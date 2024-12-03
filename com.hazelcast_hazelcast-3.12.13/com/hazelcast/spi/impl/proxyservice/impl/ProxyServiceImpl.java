/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.proxyservice.impl;

import com.hazelcast.core.DistributedObject;
import com.hazelcast.core.DistributedObjectEvent;
import com.hazelcast.core.DistributedObjectListener;
import com.hazelcast.core.HazelcastInstanceNotActiveException;
import com.hazelcast.core.Member;
import com.hazelcast.internal.metrics.MetricsProvider;
import com.hazelcast.internal.metrics.MetricsRegistry;
import com.hazelcast.internal.metrics.Probe;
import com.hazelcast.internal.metrics.ProbeLevel;
import com.hazelcast.internal.util.counters.MwCounter;
import com.hazelcast.logging.ILogger;
import com.hazelcast.spi.EventPublishingService;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.PostJoinAwareService;
import com.hazelcast.spi.ProxyService;
import com.hazelcast.spi.RemoteService;
import com.hazelcast.spi.exception.DistributedObjectDestroyedException;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.impl.operationservice.InternalOperationService;
import com.hazelcast.spi.impl.proxyservice.InternalProxyService;
import com.hazelcast.spi.impl.proxyservice.impl.DistributedObjectEventPacket;
import com.hazelcast.spi.impl.proxyservice.impl.ProxyInfo;
import com.hazelcast.spi.impl.proxyservice.impl.ProxyRegistry;
import com.hazelcast.spi.impl.proxyservice.impl.operations.DistributedObjectDestroyOperation;
import com.hazelcast.spi.impl.proxyservice.impl.operations.PostJoinProxyOperation;
import com.hazelcast.util.ConcurrencyUtil;
import com.hazelcast.util.ConstructorFunction;
import com.hazelcast.util.EmptyStatement;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.FutureUtil;
import com.hazelcast.util.Preconditions;
import com.hazelcast.util.UuidUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class ProxyServiceImpl
implements InternalProxyService,
PostJoinAwareService,
EventPublishingService<DistributedObjectEventPacket, Object>,
MetricsProvider {
    public static final String SERVICE_NAME = "hz:core:proxyService";
    private static final int TRY_COUNT = 10;
    private static final long DESTROY_TIMEOUT_SECONDS = 30L;
    final NodeEngineImpl nodeEngine;
    final ILogger logger;
    final ConcurrentMap<String, DistributedObjectListener> listeners = new ConcurrentHashMap<String, DistributedObjectListener>();
    private final ConstructorFunction<String, ProxyRegistry> registryConstructor = new ConstructorFunction<String, ProxyRegistry>(){

        @Override
        public ProxyRegistry createNew(String serviceName) {
            return new ProxyRegistry(ProxyServiceImpl.this, serviceName);
        }
    };
    private final ConcurrentMap<String, ProxyRegistry> registries = new ConcurrentHashMap<String, ProxyRegistry>();
    @Probe(name="createdCount", level=ProbeLevel.MANDATORY)
    private final MwCounter createdCounter = MwCounter.newMwCounter();
    @Probe(name="destroyedCount", level=ProbeLevel.MANDATORY)
    private final MwCounter destroyedCounter = MwCounter.newMwCounter();
    private final FutureUtil.ExceptionHandler destroyProxyExceptionHandler = new FutureUtil.ExceptionHandler(){

        @Override
        public void handleException(Throwable throwable) {
            boolean causedByInactiveInstance = ExceptionUtil.peel(throwable) instanceof HazelcastInstanceNotActiveException;
            Level level = causedByInactiveInstance ? Level.FINEST : Level.WARNING;
            ProxyServiceImpl.this.logger.log(level, "Error while destroying a proxy.", throwable);
        }
    };

    public ProxyServiceImpl(NodeEngineImpl nodeEngine) {
        this.nodeEngine = nodeEngine;
        this.logger = nodeEngine.getLogger(ProxyService.class.getName());
    }

    @Override
    public void provideMetrics(MetricsRegistry registry) {
        registry.scanAndRegister(this, "proxy");
    }

    public void init() {
        this.nodeEngine.getEventService().registerListener(SERVICE_NAME, SERVICE_NAME, new Object());
    }

    @Override
    @Probe(name="proxyCount")
    public int getProxyCount() {
        int count = 0;
        for (ProxyRegistry registry : this.registries.values()) {
            count += registry.getProxyCount();
        }
        return count;
    }

    public void initializeAndPublishProxies() {
        for (ProxyRegistry registry : this.registries.values()) {
            registry.initializeAndPublishProxies();
        }
    }

    @Override
    public void initializeDistributedObject(String serviceName, String name) {
        ProxyServiceImpl.checkServiceNameNotNull(serviceName);
        ProxyServiceImpl.checkObjectNameNotNull(name);
        ProxyRegistry registry = this.getOrCreateRegistry(serviceName);
        registry.createProxy(name, true, true);
        this.createdCounter.inc();
    }

    public ProxyRegistry getOrCreateRegistry(String serviceName) {
        return ConcurrencyUtil.getOrPutIfAbsent(this.registries, serviceName, this.registryConstructor);
    }

    @Override
    public DistributedObject getDistributedObject(String serviceName, String name) {
        ProxyServiceImpl.checkServiceNameNotNull(serviceName);
        ProxyServiceImpl.checkObjectNameNotNull(name);
        ProxyRegistry registry = this.getOrCreateRegistry(serviceName);
        return registry.getOrCreateProxy(name, true);
    }

    @Override
    public void destroyDistributedObject(String serviceName, String name) {
        ProxyServiceImpl.checkServiceNameNotNull(serviceName);
        ProxyServiceImpl.checkObjectNameNotNull(name);
        InternalOperationService operationService = this.nodeEngine.getOperationService();
        Set<Member> members = this.nodeEngine.getClusterService().getMembers();
        ArrayList calls = new ArrayList(members.size());
        for (Member member : members) {
            if (member.localMember()) continue;
            DistributedObjectDestroyOperation operation = new DistributedObjectDestroyOperation(serviceName, name);
            InternalCompletableFuture f = operationService.createInvocationBuilder(SERVICE_NAME, (Operation)operation, member.getAddress()).setTryCount(10).invoke();
            calls.add(f);
        }
        this.destroyLocalDistributedObject(serviceName, name, true);
        FutureUtil.waitWithDeadline(calls, 30L, TimeUnit.SECONDS, this.destroyProxyExceptionHandler);
    }

    @Override
    public void destroyLocalDistributedObject(String serviceName, String name, boolean fireEvent) {
        ProxyRegistry registry = (ProxyRegistry)this.registries.get(serviceName);
        if (registry != null) {
            registry.destroyProxy(name, fireEvent);
            this.destroyedCounter.inc();
        }
        RemoteService service = (RemoteService)this.nodeEngine.getService(serviceName);
        service.destroyDistributedObject(name);
        String message = "DistributedObject[" + service + " -> " + name + "] has been destroyed!";
        DistributedObjectDestroyedException cause = new DistributedObjectDestroyedException(message);
        this.nodeEngine.getOperationParker().cancelParkedOperations(serviceName, name, cause);
    }

    @Override
    public Collection<DistributedObject> getDistributedObjects(String serviceName) {
        ProxyServiceImpl.checkServiceNameNotNull(serviceName);
        LinkedList<DistributedObject> result = new LinkedList<DistributedObject>();
        ProxyRegistry registry = (ProxyRegistry)this.registries.get(serviceName);
        if (registry != null) {
            registry.getDistributedObjects(result);
        }
        return result;
    }

    @Override
    public Collection<String> getDistributedObjectNames(String serviceName) {
        ProxyServiceImpl.checkServiceNameNotNull(serviceName);
        ProxyRegistry registry = (ProxyRegistry)this.registries.get(serviceName);
        if (registry == null) {
            return Collections.emptySet();
        }
        return registry.getDistributedObjectNames();
    }

    @Override
    public Collection<DistributedObject> getAllDistributedObjects() {
        LinkedList<DistributedObject> result = new LinkedList<DistributedObject>();
        for (ProxyRegistry registry : this.registries.values()) {
            registry.getDistributedObjects(result);
        }
        return result;
    }

    @Override
    public String addProxyListener(DistributedObjectListener distributedObjectListener) {
        String id = UuidUtil.newUnsecureUuidString();
        this.listeners.put(id, distributedObjectListener);
        return id;
    }

    @Override
    public boolean removeProxyListener(String registrationId) {
        return this.listeners.remove(registrationId) != null;
    }

    @Override
    public void dispatchEvent(DistributedObjectEventPacket eventPacket, Object ignore) {
        String serviceName = eventPacket.getServiceName();
        if (eventPacket.getEventType() == DistributedObjectEvent.EventType.CREATED) {
            try {
                ProxyRegistry registry = this.getOrCreateRegistry(serviceName);
                if (!registry.contains(eventPacket.getName())) {
                    registry.createProxy(eventPacket.getName(), false, true);
                }
            }
            catch (HazelcastInstanceNotActiveException ignored) {
                EmptyStatement.ignore(ignored);
            }
        } else {
            ProxyRegistry registry = (ProxyRegistry)this.registries.get(serviceName);
            if (registry != null) {
                registry.destroyProxy(eventPacket.getName(), false);
            }
        }
    }

    @Override
    public Operation getPostJoinOperation() {
        LinkedList<ProxyInfo> proxies = new LinkedList<ProxyInfo>();
        for (ProxyRegistry registry : this.registries.values()) {
            registry.getProxyInfos(proxies);
        }
        return proxies.isEmpty() ? null : new PostJoinProxyOperation(proxies);
    }

    public void shutdown() {
        for (ProxyRegistry registry : this.registries.values()) {
            registry.destroy();
        }
        this.registries.clear();
        this.listeners.clear();
    }

    private static String checkServiceNameNotNull(String serviceName) {
        return Preconditions.checkNotNull(serviceName, "Service name is required!");
    }

    private static String checkObjectNameNotNull(String name) {
        return Preconditions.checkNotNull(name, "Object name is required!");
    }
}

