/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.proxyservice.impl;

import com.hazelcast.core.DistributedObject;
import com.hazelcast.core.DistributedObjectEvent;
import com.hazelcast.core.HazelcastException;
import com.hazelcast.core.HazelcastInstanceNotActiveException;
import com.hazelcast.spi.AbstractDistributedObject;
import com.hazelcast.spi.EventRegistration;
import com.hazelcast.spi.InitializingObject;
import com.hazelcast.spi.RemoteService;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.impl.eventservice.InternalEventService;
import com.hazelcast.spi.impl.proxyservice.impl.DistributedObjectEventPacket;
import com.hazelcast.spi.impl.proxyservice.impl.DistributedObjectFuture;
import com.hazelcast.spi.impl.proxyservice.impl.ProxyEventProcessor;
import com.hazelcast.spi.impl.proxyservice.impl.ProxyInfo;
import com.hazelcast.spi.impl.proxyservice.impl.ProxyServiceImpl;
import com.hazelcast.util.EmptyStatement;
import com.hazelcast.util.ExceptionUtil;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class ProxyRegistry {
    private final ProxyServiceImpl proxyService;
    private final String serviceName;
    private final RemoteService service;
    private final ConcurrentMap<String, DistributedObjectFuture> proxies = new ConcurrentHashMap<String, DistributedObjectFuture>();

    ProxyRegistry(ProxyServiceImpl proxyService, String serviceName) {
        this.proxyService = proxyService;
        this.serviceName = serviceName;
        this.service = this.getService(proxyService.nodeEngine, serviceName);
    }

    private RemoteService getService(NodeEngineImpl nodeEngine, String serviceName) {
        try {
            return (RemoteService)nodeEngine.getService(serviceName);
        }
        catch (HazelcastException e) {
            if (!nodeEngine.isRunning()) {
                throw new HazelcastInstanceNotActiveException(e.getMessage());
            }
            throw e;
        }
    }

    public String getServiceName() {
        return this.serviceName;
    }

    public int getProxyCount() {
        return this.proxies.size();
    }

    boolean contains(String name) {
        return this.proxies.containsKey(name);
    }

    public Collection<String> getDistributedObjectNames() {
        return this.proxies.keySet();
    }

    public void getProxyInfos(Collection<ProxyInfo> result) {
        for (Map.Entry entry : this.proxies.entrySet()) {
            DistributedObjectFuture future = (DistributedObjectFuture)entry.getValue();
            if (!future.isSetAndInitialized()) continue;
            String proxyName = (String)entry.getKey();
            result.add(new ProxyInfo(this.serviceName, proxyName));
        }
    }

    public void getDistributedObjects(Collection<DistributedObject> result) {
        Collection futures = this.proxies.values();
        for (DistributedObjectFuture future : futures) {
            if (!future.isSetAndInitialized()) continue;
            try {
                DistributedObject object = future.get();
                result.add(object);
            }
            catch (Throwable ignored) {
                EmptyStatement.ignore(ignored);
            }
        }
    }

    public DistributedObject getOrCreateProxy(String name, boolean publishEvent) {
        DistributedObjectFuture proxyFuture = this.getOrCreateProxyFuture(name, publishEvent, true);
        return proxyFuture.get();
    }

    public DistributedObjectFuture getOrCreateProxyFuture(String name, boolean publishEvent, boolean initialize) {
        DistributedObjectFuture proxyFuture = (DistributedObjectFuture)this.proxies.get(name);
        if (proxyFuture == null) {
            if (!this.proxyService.nodeEngine.isRunning()) {
                throw new HazelcastInstanceNotActiveException();
            }
            proxyFuture = this.createProxy(name, publishEvent, initialize);
            if (proxyFuture == null) {
                return this.getOrCreateProxyFuture(name, publishEvent, initialize);
            }
        }
        return proxyFuture;
    }

    public DistributedObjectFuture createProxy(String name, boolean publishEvent, boolean initialize) {
        if (this.proxies.containsKey(name)) {
            return null;
        }
        if (!this.proxyService.nodeEngine.isRunning()) {
            throw new HazelcastInstanceNotActiveException();
        }
        DistributedObjectFuture proxyFuture = new DistributedObjectFuture();
        if (this.proxies.putIfAbsent(name, proxyFuture) != null) {
            return null;
        }
        return this.doCreateProxy(name, publishEvent, initialize, proxyFuture);
    }

    private DistributedObjectFuture doCreateProxy(String name, boolean publishEvent, boolean initialize, DistributedObjectFuture proxyFuture) {
        DistributedObject proxy;
        try {
            proxy = this.service.createDistributedObject(name);
            if (initialize && proxy instanceof InitializingObject) {
                try {
                    ((InitializingObject)((Object)proxy)).initialize();
                }
                catch (Exception e) {
                    this.proxyService.logger.warning("Error while initializing proxy: " + proxy, e);
                    throw e;
                }
            }
            proxyFuture.set(proxy, initialize);
        }
        catch (Throwable e) {
            proxyFuture.setError(e);
            this.proxies.remove(name);
            throw ExceptionUtil.rethrow(e);
        }
        InternalEventService eventService = this.proxyService.nodeEngine.getEventService();
        ProxyEventProcessor callback = new ProxyEventProcessor(this.proxyService.listeners.values(), DistributedObjectEvent.EventType.CREATED, this.serviceName, name, proxy);
        eventService.executeEventCallback(callback);
        if (publishEvent) {
            this.publish(new DistributedObjectEventPacket(DistributedObjectEvent.EventType.CREATED, this.serviceName, name));
        }
        return proxyFuture;
    }

    void destroyProxy(String name, boolean publishEvent) {
        DistributedObject proxy;
        DistributedObjectFuture proxyFuture = (DistributedObjectFuture)this.proxies.remove(name);
        if (proxyFuture == null) {
            return;
        }
        try {
            proxy = proxyFuture.get();
        }
        catch (Throwable t) {
            this.proxyService.logger.warning("Cannot destroy proxy [" + this.serviceName + ":" + name + "], since its creation is failed with " + t.getClass().getName() + ": " + t.getMessage());
            return;
        }
        InternalEventService eventService = this.proxyService.nodeEngine.getEventService();
        ProxyEventProcessor callback = new ProxyEventProcessor(this.proxyService.listeners.values(), DistributedObjectEvent.EventType.DESTROYED, this.serviceName, name, proxy);
        eventService.executeEventCallback(callback);
        if (publishEvent) {
            this.publish(new DistributedObjectEventPacket(DistributedObjectEvent.EventType.DESTROYED, this.serviceName, name));
        }
    }

    private void publish(DistributedObjectEventPacket event) {
        InternalEventService eventService = this.proxyService.nodeEngine.getEventService();
        Collection<EventRegistration> registrations = eventService.getRegistrations("hz:core:proxyService", "hz:core:proxyService");
        eventService.publishRemoteEvent("hz:core:proxyService", registrations, event, event.getName().hashCode());
    }

    void destroy() {
        for (DistributedObjectFuture future : this.proxies.values()) {
            if (!future.isSetAndInitialized()) continue;
            DistributedObject distributedObject = this.extractDistributedObject(future);
            this.invalidate(distributedObject);
        }
        this.proxies.clear();
    }

    private DistributedObject extractDistributedObject(DistributedObjectFuture future) {
        try {
            return future.get();
        }
        catch (Throwable ex) {
            EmptyStatement.ignore(ex);
            return null;
        }
    }

    private void invalidate(DistributedObject distributedObject) {
        if (distributedObject != null && distributedObject instanceof AbstractDistributedObject) {
            ((AbstractDistributedObject)distributedObject).invalidate();
        }
    }

    void initializeAndPublishProxies() {
        for (Map.Entry entry : this.proxies.entrySet()) {
            String name = (String)entry.getKey();
            DistributedObjectFuture future = (DistributedObjectFuture)entry.getValue();
            if (future.isSetAndInitialized()) continue;
            try {
                future.get();
            }
            catch (Throwable e) {
                this.proxyService.logger.warning("Error while initializing proxy: " + name, e);
                future.setError(e);
                this.proxies.remove(entry.getKey());
                throw ExceptionUtil.rethrow(e);
            }
            this.publish(new DistributedObjectEventPacket(DistributedObjectEvent.EventType.CREATED, this.serviceName, name));
        }
    }
}

