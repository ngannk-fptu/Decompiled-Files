/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.instance;

import com.hazelcast.config.Config;
import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.core.DuplicateInstanceNameException;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.LifecycleEvent;
import com.hazelcast.core.Member;
import com.hazelcast.instance.DefaultNodeContext;
import com.hazelcast.instance.HazelcastInstanceImpl;
import com.hazelcast.instance.HazelcastInstanceProxy;
import com.hazelcast.instance.Node;
import com.hazelcast.instance.NodeContext;
import com.hazelcast.instance.OutOfMemoryErrorDispatcher;
import com.hazelcast.internal.jmx.ManagementService;
import com.hazelcast.internal.util.ModularJavaUtils;
import com.hazelcast.logging.Logger;
import com.hazelcast.spi.annotation.PrivateApi;
import com.hazelcast.spi.properties.GroupProperty;
import com.hazelcast.util.EmptyStatement;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.Preconditions;
import com.hazelcast.util.SetUtil;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@PrivateApi
public final class HazelcastInstanceFactory {
    private static final int ADDITIONAL_SLEEP_SECONDS_FOR_NON_FIRST_MEMBERS = 4;
    private static final AtomicInteger FACTORY_ID_GEN = new AtomicInteger();
    private static final ConcurrentMap<String, InstanceFuture<HazelcastInstanceProxy>> INSTANCE_MAP = new ConcurrentHashMap<String, InstanceFuture<HazelcastInstanceProxy>>(5);

    private HazelcastInstanceFactory() {
    }

    public static Set<HazelcastInstance> getAllHazelcastInstances() {
        Set<HazelcastInstance> result = SetUtil.createHashSet(INSTANCE_MAP.size());
        for (InstanceFuture f : INSTANCE_MAP.values()) {
            result.add((HazelcastInstance)f.get());
        }
        return result;
    }

    public static HazelcastInstance getHazelcastInstance(String instanceName) {
        InstanceFuture instanceFuture = (InstanceFuture)INSTANCE_MAP.get(instanceName);
        if (instanceFuture == null) {
            return null;
        }
        try {
            return (HazelcastInstance)instanceFuture.get();
        }
        catch (IllegalStateException t) {
            return null;
        }
    }

    public static HazelcastInstance getOrCreateHazelcastInstance(Config config) {
        if (config == null) {
            config = new XmlConfigBuilder().build();
        }
        String name = config.getInstanceName();
        Preconditions.checkHasText(name, "instanceName must contain text");
        InstanceFuture<HazelcastInstanceProxy> future = (InstanceFuture<HazelcastInstanceProxy>)INSTANCE_MAP.get(name);
        if (future != null) {
            return (HazelcastInstance)future.get();
        }
        future = new InstanceFuture<HazelcastInstanceProxy>();
        InstanceFuture found = INSTANCE_MAP.putIfAbsent(name, future);
        if (found != null) {
            return (HazelcastInstance)found.get();
        }
        try {
            return HazelcastInstanceFactory.constructHazelcastInstance(config, name, new DefaultNodeContext(), future);
        }
        catch (Throwable t) {
            INSTANCE_MAP.remove(name, future);
            future.setFailure(t);
            throw ExceptionUtil.rethrow(t);
        }
    }

    public static HazelcastInstance newHazelcastInstance(Config config) {
        if (config == null) {
            config = Config.load();
        }
        return HazelcastInstanceFactory.newHazelcastInstance(config, config.getInstanceName(), new DefaultNodeContext());
    }

    public static String createInstanceName(Config config) {
        return "_hzInstance_" + FACTORY_ID_GEN.incrementAndGet() + "_" + config.getGroupConfig().getName();
    }

    public static String getInstanceName(String instanceName, Config config) {
        String name = instanceName;
        if (name == null || name.trim().length() == 0) {
            name = HazelcastInstanceFactory.createInstanceName(config);
        }
        return name;
    }

    public static HazelcastInstance newHazelcastInstance(Config config, String instanceName, NodeContext nodeContext) {
        InstanceFuture<HazelcastInstanceProxy> future;
        String name;
        if (config == null) {
            config = new XmlConfigBuilder().build();
        }
        if (INSTANCE_MAP.putIfAbsent(name = HazelcastInstanceFactory.getInstanceName(instanceName, config), future = new InstanceFuture<HazelcastInstanceProxy>()) != null) {
            throw new DuplicateInstanceNameException("HazelcastInstance with name '" + name + "' already exists!");
        }
        try {
            return HazelcastInstanceFactory.constructHazelcastInstance(config, name, nodeContext, future);
        }
        catch (Throwable t) {
            INSTANCE_MAP.remove(name, future);
            future.setFailure(t);
            throw ExceptionUtil.rethrow(t);
        }
    }

    private static HazelcastInstanceProxy newHazelcastProxy(HazelcastInstanceImpl hazelcastInstance) {
        return new HazelcastInstanceProxy(hazelcastInstance);
    }

    private static HazelcastInstanceProxy constructHazelcastInstance(Config config, String instanceName, NodeContext nodeContext, InstanceFuture<HazelcastInstanceProxy> future) {
        HazelcastInstanceProxy proxy;
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try {
            if (classLoader == null) {
                Thread.currentThread().setContextClassLoader(HazelcastInstanceFactory.class.getClassLoader());
            }
            HazelcastInstanceImpl hazelcastInstance = new HazelcastInstanceImpl(instanceName, config, nodeContext);
            OutOfMemoryErrorDispatcher.registerServer(hazelcastInstance);
            proxy = HazelcastInstanceFactory.newHazelcastProxy(hazelcastInstance);
            Node node = hazelcastInstance.node;
            boolean firstMember = HazelcastInstanceFactory.isFirstMember(node);
            long initialWaitSeconds = node.getProperties().getSeconds(GroupProperty.INITIAL_WAIT_SECONDS);
            if (initialWaitSeconds > 0L) {
                hazelcastInstance.logger.info(String.format("Waiting %d seconds before completing HazelcastInstance startup...", initialWaitSeconds));
                try {
                    TimeUnit.SECONDS.sleep(initialWaitSeconds);
                    if (firstMember) {
                        node.partitionService.firstArrangement();
                    } else {
                        TimeUnit.SECONDS.sleep(4L);
                    }
                }
                catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                }
            }
            HazelcastInstanceFactory.awaitMinimalClusterSize(hazelcastInstance, node, firstMember);
            future.set(proxy);
            hazelcastInstance.lifecycleService.fireLifecycleEvent(LifecycleEvent.LifecycleState.STARTED);
        }
        catch (Throwable t) {
            throw ExceptionUtil.rethrow(t);
        }
        finally {
            Thread.currentThread().setContextClassLoader(classLoader);
        }
        return proxy;
    }

    private static boolean isFirstMember(Node node) {
        Iterator<Member> iterator = node.getClusterService().getMembers().iterator();
        return iterator.hasNext() && iterator.next().localMember();
    }

    private static void awaitMinimalClusterSize(HazelcastInstanceImpl hazelcastInstance, Node node, boolean firstMember) throws InterruptedException {
        int initialMinClusterSize = node.getProperties().getInteger(GroupProperty.INITIAL_MIN_CLUSTER_SIZE);
        while (node.getClusterService().getSize() < initialMinClusterSize) {
            try {
                hazelcastInstance.logger.info("HazelcastInstance waiting for cluster size of " + initialMinClusterSize);
                TimeUnit.SECONDS.sleep(1L);
            }
            catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
        }
        if (initialMinClusterSize > 1) {
            if (firstMember) {
                node.partitionService.firstArrangement();
            } else {
                TimeUnit.SECONDS.sleep(3L);
            }
            hazelcastInstance.logger.info("HazelcastInstance starting after waiting for cluster size of " + initialMinClusterSize);
        }
    }

    public static void shutdownAll() {
        HazelcastInstanceFactory.shutdownAll(false);
    }

    public static void terminateAll() {
        HazelcastInstanceFactory.shutdownAll(true);
    }

    private static void shutdownAll(boolean terminate) {
        LinkedList<HazelcastInstanceProxy> instances = new LinkedList<HazelcastInstanceProxy>();
        for (InstanceFuture future : INSTANCE_MAP.values()) {
            try {
                HazelcastInstanceProxy instanceProxy = (HazelcastInstanceProxy)future.get();
                instances.add(instanceProxy);
            }
            catch (RuntimeException ignored) {
                EmptyStatement.ignore(ignored);
            }
        }
        INSTANCE_MAP.clear();
        OutOfMemoryErrorDispatcher.clearServers();
        ManagementService.shutdownAll(instances);
        Collections.sort(instances, new Comparator<HazelcastInstanceProxy>(){

            @Override
            public int compare(HazelcastInstanceProxy o1, HazelcastInstanceProxy o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        for (HazelcastInstanceProxy proxy : instances) {
            if (terminate) {
                proxy.getLifecycleService().terminate();
            } else {
                proxy.getLifecycleService().shutdown();
            }
            proxy.original = null;
        }
    }

    public static void remove(HazelcastInstanceImpl instance) {
        OutOfMemoryErrorDispatcher.deregisterServer(instance);
        InstanceFuture future = (InstanceFuture)INSTANCE_MAP.remove(instance.getName());
        if (future != null && future.isSet()) {
            ((HazelcastInstanceProxy)future.get()).original = null;
        }
        if (INSTANCE_MAP.size() == 0) {
            ManagementService.shutdown(instance.getName());
        }
    }

    static {
        ModularJavaUtils.checkJavaInternalAccess(Logger.getLogger(HazelcastInstanceFactory.class));
    }

    public static class InstanceFuture<T> {
        private volatile T hz;
        private volatile Throwable throwable;

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public T get() {
            if (this.hz != null) {
                return this.hz;
            }
            boolean restoreInterrupt = false;
            InstanceFuture instanceFuture = this;
            synchronized (instanceFuture) {
                while (this.hz == null && this.throwable == null) {
                    try {
                        this.wait();
                    }
                    catch (InterruptedException ignored) {
                        restoreInterrupt = true;
                    }
                }
            }
            if (restoreInterrupt) {
                Thread.currentThread().interrupt();
            }
            if (this.hz != null) {
                return this.hz;
            }
            throw new IllegalStateException(this.throwable);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void set(T proxy) {
            InstanceFuture instanceFuture = this;
            synchronized (instanceFuture) {
                this.hz = proxy;
                this.notifyAll();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void setFailure(Throwable throwable) {
            InstanceFuture instanceFuture = this;
            synchronized (instanceFuture) {
                this.throwable = throwable;
                this.notifyAll();
            }
        }

        public boolean isSet() {
            return this.hz != null;
        }
    }
}

