/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.servicemanager.impl;

import com.hazelcast.cache.impl.ICacheService;
import com.hazelcast.cache.impl.JCacheDetector;
import com.hazelcast.cardinality.impl.CardinalityEstimatorService;
import com.hazelcast.collection.impl.list.ListService;
import com.hazelcast.collection.impl.queue.QueueService;
import com.hazelcast.collection.impl.set.SetService;
import com.hazelcast.concurrent.atomiclong.AtomicLongService;
import com.hazelcast.concurrent.atomicreference.AtomicReferenceService;
import com.hazelcast.concurrent.countdownlatch.CountDownLatchService;
import com.hazelcast.concurrent.idgen.IdGeneratorService;
import com.hazelcast.concurrent.lock.LockServiceImpl;
import com.hazelcast.concurrent.semaphore.SemaphoreService;
import com.hazelcast.config.ServiceConfig;
import com.hazelcast.config.ServicesConfig;
import com.hazelcast.core.HazelcastException;
import com.hazelcast.crdt.CRDTReplicationMigrationService;
import com.hazelcast.crdt.pncounter.PNCounterService;
import com.hazelcast.durableexecutor.impl.DistributedDurableExecutorService;
import com.hazelcast.executor.impl.DistributedExecutorService;
import com.hazelcast.flakeidgen.impl.FlakeIdGeneratorService;
import com.hazelcast.instance.Node;
import com.hazelcast.instance.NodeExtension;
import com.hazelcast.logging.ILogger;
import com.hazelcast.map.impl.MapService;
import com.hazelcast.mapreduce.impl.MapReduceService;
import com.hazelcast.multimap.impl.MultiMapService;
import com.hazelcast.nio.ClassLoaderUtil;
import com.hazelcast.replicatedmap.impl.ReplicatedMapService;
import com.hazelcast.ringbuffer.impl.RingbufferService;
import com.hazelcast.scheduledexecutor.impl.DistributedScheduledExecutorService;
import com.hazelcast.spi.ConfigurableService;
import com.hazelcast.spi.ManagedService;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.SharedService;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.impl.servicemanager.ServiceDescriptor;
import com.hazelcast.spi.impl.servicemanager.ServiceDescriptorProvider;
import com.hazelcast.spi.impl.servicemanager.ServiceInfo;
import com.hazelcast.spi.impl.servicemanager.ServiceManager;
import com.hazelcast.topic.impl.TopicService;
import com.hazelcast.topic.impl.reliable.ReliableTopicService;
import com.hazelcast.transaction.impl.xa.XAService;
import com.hazelcast.util.EmptyStatement;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.ServiceLoader;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class ServiceManagerImpl
implements ServiceManager {
    private static final String PROVIDER_ID = ServiceDescriptorProvider.class.getName();
    private final NodeEngineImpl nodeEngine;
    private final ILogger logger;
    private final ConcurrentMap<String, ServiceInfo> services = new ConcurrentHashMap<String, ServiceInfo>(20, 0.75f, 1);

    public ServiceManagerImpl(NodeEngineImpl nodeEngine) {
        this.nodeEngine = nodeEngine;
        this.logger = nodeEngine.getLogger(ServiceManagerImpl.class);
    }

    public synchronized void start() {
        HashMap<String, Properties> serviceProps = new HashMap<String, Properties>();
        HashMap<String, Object> serviceConfigObjects = new HashMap<String, Object>();
        this.registerServices(serviceProps, serviceConfigObjects);
        this.initServices(serviceProps, serviceConfigObjects);
    }

    private void registerServices(Map<String, Properties> serviceProps, Map<String, Object> serviceConfigObjects) {
        this.registerCoreServices();
        this.registerExtensionServices();
        Node node = this.nodeEngine.getNode();
        ServicesConfig servicesConfig = node.getConfig().getServicesConfig();
        if (servicesConfig != null) {
            this.registerDefaultServices(servicesConfig);
            this.registerUserServices(servicesConfig, serviceProps, serviceConfigObjects);
        }
    }

    private void registerCoreServices() {
        this.logger.finest("Registering core services...");
        Node node = this.nodeEngine.getNode();
        this.registerService("hz:core:clusterService", node.getClusterService());
        this.registerService("hz:core:partitionService", node.getPartitionService());
        this.registerService("hz:core:proxyService", this.nodeEngine.getProxyService());
        this.registerService("hz:core:txManagerService", this.nodeEngine.getTransactionManagerService());
        this.registerService("hz:core:clientEngine", node.clientEngine);
        this.registerService("hz:impl:quorumService", this.nodeEngine.getQuorumService());
        this.registerService("hz:core:wanReplicationService", this.nodeEngine.getWanReplicationService());
        this.registerService("hz:core:eventService", this.nodeEngine.getEventService());
    }

    private void registerExtensionServices() {
        this.logger.finest("Registering extension services...");
        NodeExtension nodeExtension = this.nodeEngine.getNode().getNodeExtension();
        Map<String, Object> services = nodeExtension.createExtensionServices();
        for (Map.Entry<String, Object> entry : services.entrySet()) {
            this.registerService(entry.getKey(), entry.getValue());
        }
    }

    private void registerDefaultServices(ServicesConfig servicesConfig) {
        if (!servicesConfig.isEnableDefaults()) {
            return;
        }
        this.logger.finest("Registering default services...");
        this.registerService("hz:impl:mapService", this.createService(MapService.class));
        this.registerService("hz:impl:lockService", new LockServiceImpl(this.nodeEngine));
        this.registerService("hz:impl:queueService", new QueueService(this.nodeEngine));
        this.registerService("hz:impl:topicService", new TopicService());
        this.registerService("hz:impl:reliableTopicService", new ReliableTopicService(this.nodeEngine));
        this.registerService("hz:impl:multiMapService", new MultiMapService(this.nodeEngine));
        this.registerService("hz:impl:listService", new ListService(this.nodeEngine));
        this.registerService("hz:impl:setService", new SetService(this.nodeEngine));
        this.registerService("hz:impl:executorService", new DistributedExecutorService());
        this.registerService("hz:impl:durableExecutorService", new DistributedDurableExecutorService(this.nodeEngine));
        this.registerService("hz:impl:atomicLongService", new AtomicLongService());
        this.registerService("hz:impl:atomicReferenceService", new AtomicReferenceService());
        this.registerService("hz:impl:countDownLatchService", new CountDownLatchService());
        this.registerService("hz:impl:semaphoreService", new SemaphoreService(this.nodeEngine));
        this.registerService("hz:impl:idGeneratorService", new IdGeneratorService(this.nodeEngine));
        this.registerService("hz:impl:flakeIdGeneratorService", new FlakeIdGeneratorService(this.nodeEngine));
        this.registerService("hz:impl:mapReduceService", new MapReduceService(this.nodeEngine));
        this.registerService("hz:impl:replicatedMapService", new ReplicatedMapService(this.nodeEngine));
        this.registerService("hz:impl:ringbufferService", new RingbufferService(this.nodeEngine));
        this.registerService("hz:impl:xaService", new XAService(this.nodeEngine));
        this.registerService("hz:impl:cardinalityEstimatorService", new CardinalityEstimatorService());
        this.registerService("hz:impl:PNCounterService", new PNCounterService());
        this.registerService("hz:impl:CRDTReplicationMigrationService", new CRDTReplicationMigrationService());
        this.registerService("hz:impl:scheduledExecutorService", new DistributedScheduledExecutorService());
        this.registerCacheServiceIfAvailable();
        this.readServiceDescriptors();
    }

    private void readServiceDescriptors() {
        Node node = this.nodeEngine.getNode();
        try {
            ClassLoader classLoader = node.getConfigClassLoader();
            Iterator<Class<ServiceDescriptorProvider>> iterator = ServiceLoader.classIterator(ServiceDescriptorProvider.class, PROVIDER_ID, classLoader);
            while (iterator.hasNext()) {
                ServiceDescriptor[] services;
                Class<ServiceDescriptorProvider> clazz = iterator.next();
                Constructor<ServiceDescriptorProvider> constructor = clazz.getDeclaredConstructor(new Class[0]);
                ServiceDescriptorProvider provider = constructor.newInstance(new Object[0]);
                for (ServiceDescriptor serviceDescriptor : services = provider.createServiceDescriptors()) {
                    this.registerService(serviceDescriptor.getServiceName(), serviceDescriptor.getService(this.nodeEngine));
                }
            }
        }
        catch (Exception e) {
            throw ExceptionUtil.rethrow(e);
        }
    }

    private <T> T createService(Class<T> service) {
        Node node = this.nodeEngine.getNode();
        NodeExtension nodeExtension = node.getNodeExtension();
        return nodeExtension.createService(service);
    }

    private void registerCacheServiceIfAvailable() {
        if (JCacheDetector.isJCacheAvailable(this.nodeEngine.getConfigClassLoader(), this.logger)) {
            ICacheService service = this.createService(ICacheService.class);
            this.registerService("hz:impl:cacheService", service);
        } else {
            this.logger.finest("javax.cache api is not detected on classpath. Skipping CacheService...");
        }
    }

    private void initServices(Map<String, Properties> serviceProps, Map<String, Object> serviceConfigObjects) {
        for (ServiceInfo serviceInfo : this.services.values()) {
            this.initService(serviceProps, serviceConfigObjects, serviceInfo);
        }
    }

    private void initService(Map<String, Properties> serviceProps, Map<String, Object> serviceConfigObjects, ServiceInfo serviceInfo) {
        Object service = serviceInfo.getService();
        if (serviceInfo.isConfigurableService()) {
            try {
                if (this.logger.isFinestEnabled()) {
                    this.logger.finest("Configuring service -> " + service);
                }
                Object configObject = serviceConfigObjects.get(serviceInfo.getName());
                ((ConfigurableService)service).configure(configObject);
            }
            catch (Throwable t) {
                this.logger.severe("Error while configuring service: " + t.getMessage(), t);
            }
        }
        if (serviceInfo.isManagedService()) {
            try {
                if (this.logger.isFinestEnabled()) {
                    this.logger.finest("Initializing service -> " + service);
                }
                Properties props = serviceProps.get(serviceInfo.getName());
                ((ManagedService)service).init(this.nodeEngine, props != null ? props : new Properties());
            }
            catch (Throwable t) {
                this.logger.severe("Error while initializing service: " + t.getMessage(), t);
            }
        }
    }

    private void registerUserServices(ServicesConfig servicesConfig, Map<String, Properties> serviceProps, Map<String, Object> serviceConfigObjects) {
        this.logger.finest("Registering user defined services...");
        Collection<ServiceConfig> serviceConfigs = servicesConfig.getServiceConfigs();
        for (ServiceConfig serviceConfig : serviceConfigs) {
            this.registerUserService(serviceProps, serviceConfigObjects, serviceConfig);
        }
    }

    private void registerUserService(Map<String, Properties> serviceProps, Map<String, Object> serviceConfigObjects, ServiceConfig serviceConfig) {
        if (!serviceConfig.isEnabled()) {
            return;
        }
        Object service = serviceConfig.getImplementation();
        if (service == null) {
            service = this.createServiceObject(serviceConfig.getClassName());
        }
        if (service != null) {
            this.registerService(serviceConfig.getName(), service);
            serviceProps.put(serviceConfig.getName(), serviceConfig.getProperties());
            if (serviceConfig.getConfigObject() != null) {
                serviceConfigObjects.put(serviceConfig.getName(), serviceConfig.getConfigObject());
            }
        }
    }

    private Object createServiceObject(String className) {
        try {
            ClassLoader classLoader = this.nodeEngine.getConfigClassLoader();
            Class<?> serviceClass = ClassLoaderUtil.loadClass(classLoader, className);
            try {
                Constructor<?> constructor = serviceClass.getConstructor(NodeEngine.class);
                return constructor.newInstance(this.nodeEngine);
            }
            catch (NoSuchMethodException ignored) {
                EmptyStatement.ignore(ignored);
                Constructor<?> constructor = serviceClass.getDeclaredConstructor(new Class[0]);
                if (!constructor.isAccessible()) {
                    constructor.setAccessible(true);
                }
                return constructor.newInstance(new Object[0]);
            }
        }
        catch (Exception e) {
            this.logger.severe(e);
            return null;
        }
    }

    public synchronized void shutdown(boolean terminate) {
        this.logger.finest("Stopping services...");
        List<ManagedService> managedServices = this.getServices(ManagedService.class);
        Collections.reverse(managedServices);
        this.services.clear();
        for (ManagedService service : managedServices) {
            this.shutdownService(service, terminate);
        }
    }

    private void shutdownService(ManagedService service, boolean terminate) {
        try {
            if (this.logger.isFinestEnabled()) {
                this.logger.finest("Shutting down service -> " + service);
            }
            service.shutdown(terminate);
        }
        catch (Throwable t) {
            this.logger.severe("Error while shutting down service[" + service + "]: " + t.getMessage(), t);
        }
    }

    public synchronized void registerService(String serviceName, Object service) {
        ServiceInfo serviceInfo;
        ServiceInfo currentServiceInfo;
        if (this.logger.isFinestEnabled()) {
            this.logger.finest("Registering service: '" + serviceName + "'");
        }
        if ((currentServiceInfo = this.services.putIfAbsent(serviceName, serviceInfo = new ServiceInfo(serviceName, service))) != null) {
            this.logger.warning("Replacing " + currentServiceInfo + " with " + serviceInfo);
            if (currentServiceInfo.isCoreService()) {
                throw new HazelcastException("Can not replace a CoreService! Name: " + serviceName + ", Service: " + currentServiceInfo.getService());
            }
            if (currentServiceInfo.isManagedService()) {
                this.shutdownService((ManagedService)currentServiceInfo.getService(), false);
            }
            this.services.put(serviceName, serviceInfo);
        }
    }

    @Override
    public ServiceInfo getServiceInfo(String serviceName) {
        return (ServiceInfo)this.services.get(serviceName);
    }

    @Override
    public <S> List<S> getServices(Class<S> serviceClass) {
        LinkedList result = new LinkedList();
        for (ServiceInfo serviceInfo : this.services.values()) {
            if (!serviceInfo.isInstanceOf(serviceClass)) continue;
            Object service = serviceInfo.getService();
            if (serviceInfo.isCoreService()) {
                result.addFirst(service);
                continue;
            }
            result.addLast(service);
        }
        return result;
    }

    @Override
    public <T> T getService(String serviceName) {
        ServiceInfo serviceInfo = this.getServiceInfo(serviceName);
        return serviceInfo != null ? (T)serviceInfo.getService() : null;
    }

    @Override
    public <T extends SharedService> T getSharedService(String serviceName) {
        T service = this.getService(serviceName);
        if (service == null) {
            return null;
        }
        if (service instanceof SharedService) {
            return (T)((SharedService)service);
        }
        throw new IllegalArgumentException("No SharedService registered with name: " + serviceName);
    }

    @Override
    public List<ServiceInfo> getServiceInfos(Class serviceClass) {
        LinkedList<ServiceInfo> result = new LinkedList<ServiceInfo>();
        for (ServiceInfo serviceInfo : this.services.values()) {
            if (!serviceInfo.isInstanceOf(serviceClass)) continue;
            if (serviceInfo.isCoreService()) {
                result.addFirst(serviceInfo);
                continue;
            }
            result.addLast(serviceInfo);
        }
        return result;
    }
}

