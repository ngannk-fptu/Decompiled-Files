/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.jmx;

import com.hazelcast.core.DistributedObject;
import com.hazelcast.core.IAtomicLong;
import com.hazelcast.core.IAtomicReference;
import com.hazelcast.core.ICountDownLatch;
import com.hazelcast.core.IExecutorService;
import com.hazelcast.core.IList;
import com.hazelcast.core.ILock;
import com.hazelcast.core.IMap;
import com.hazelcast.core.IQueue;
import com.hazelcast.core.ISemaphore;
import com.hazelcast.core.ISet;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.MultiMap;
import com.hazelcast.internal.jmx.AtomicLongMBean;
import com.hazelcast.internal.jmx.AtomicReferenceMBean;
import com.hazelcast.internal.jmx.CountDownLatchMBean;
import com.hazelcast.internal.jmx.ExecutorServiceMBean;
import com.hazelcast.internal.jmx.HazelcastMBean;
import com.hazelcast.internal.jmx.ListMBean;
import com.hazelcast.internal.jmx.LockMBean;
import com.hazelcast.internal.jmx.ManagementService;
import com.hazelcast.internal.jmx.MapMBean;
import com.hazelcast.internal.jmx.MultiMapMBean;
import com.hazelcast.internal.jmx.QueueMBean;
import com.hazelcast.internal.jmx.ReliableTopicMBean;
import com.hazelcast.internal.jmx.ReplicatedMapMBean;
import com.hazelcast.internal.jmx.SemaphoreMBean;
import com.hazelcast.internal.jmx.SetMBean;
import com.hazelcast.internal.jmx.TopicMBean;
import com.hazelcast.replicatedmap.impl.ReplicatedMapProxy;
import com.hazelcast.topic.impl.reliable.ReliableTopicProxy;
import com.hazelcast.util.MapUtil;
import java.util.concurrent.ConcurrentMap;

final class MBeans {
    private static final ConcurrentMap<String, MBeanFactory> MBEAN_FACTORY_TYPES_REGISTRY;

    private MBeans() {
    }

    static HazelcastMBean createHazelcastMBeanOrNull(DistributedObject distributedObject, ManagementService managementService) {
        MBeanFactory mBeanFactory = MBeans.getMBeanFactory(distributedObject.getServiceName());
        return mBeanFactory == null ? null : mBeanFactory.createNew(distributedObject, managementService);
    }

    static String getObjectTypeOrNull(String serviceName) {
        MBeanFactory mBeanFactory = MBeans.getMBeanFactory(serviceName);
        return mBeanFactory == null ? null : mBeanFactory.getObjectType();
    }

    private static MBeanFactory getMBeanFactory(String serviceName) {
        return (MBeanFactory)((Object)MBEAN_FACTORY_TYPES_REGISTRY.get(serviceName));
    }

    static {
        MBeanFactory[] mBeanFactories;
        MBEAN_FACTORY_TYPES_REGISTRY = MapUtil.createConcurrentHashMap(MBeanFactory.values().length);
        for (MBeanFactory mBeanFactory : mBeanFactories = MBeanFactory.values()) {
            MBEAN_FACTORY_TYPES_REGISTRY.put(mBeanFactory.getServiceName(), mBeanFactory);
        }
    }

    static enum MBeanFactory {
        MAP{

            @Override
            public HazelcastMBean createNew(DistributedObject distributedObject, ManagementService managementService) {
                return new MapMBean((IMap)distributedObject, managementService);
            }

            @Override
            public String getObjectType() {
                return "IMap";
            }

            @Override
            public String getServiceName() {
                return "hz:impl:mapService";
            }
        }
        ,
        LIST{

            @Override
            public HazelcastMBean createNew(DistributedObject distributedObject, ManagementService managementService) {
                return new ListMBean((IList)distributedObject, managementService);
            }

            @Override
            public String getObjectType() {
                return "IList";
            }

            @Override
            public String getServiceName() {
                return "hz:impl:listService";
            }
        }
        ,
        ATOMIC_LONG{

            @Override
            public HazelcastMBean createNew(DistributedObject distributedObject, ManagementService managementService) {
                return new AtomicLongMBean((IAtomicLong)distributedObject, managementService);
            }

            @Override
            public String getObjectType() {
                return "IAtomicLong";
            }

            @Override
            public String getServiceName() {
                return "hz:impl:atomicLongService";
            }
        }
        ,
        ATOMIC_REFERENCE{

            @Override
            public HazelcastMBean createNew(DistributedObject distributedObject, ManagementService managementService) {
                return new AtomicReferenceMBean((IAtomicReference)distributedObject, managementService);
            }

            @Override
            public String getObjectType() {
                return "IAtomicReference";
            }

            @Override
            public String getServiceName() {
                return "hz:impl:atomicReferenceService";
            }
        }
        ,
        COUNT_DOWN_LATCH{

            @Override
            public HazelcastMBean createNew(DistributedObject distributedObject, ManagementService managementService) {
                return new CountDownLatchMBean((ICountDownLatch)distributedObject, managementService);
            }

            @Override
            public String getObjectType() {
                return "ICountDownLatch";
            }

            @Override
            public String getServiceName() {
                return "hz:impl:countDownLatchService";
            }
        }
        ,
        LOCK{

            @Override
            public HazelcastMBean createNew(DistributedObject distributedObject, ManagementService managementService) {
                return new LockMBean((ILock)distributedObject, managementService);
            }

            @Override
            public String getObjectType() {
                return "ILock";
            }

            @Override
            public String getServiceName() {
                return "hz:impl:lockService";
            }
        }
        ,
        MULTI_MAP{

            @Override
            public HazelcastMBean createNew(DistributedObject distributedObject, ManagementService managementService) {
                return new MultiMapMBean((MultiMap)distributedObject, managementService);
            }

            @Override
            public String getObjectType() {
                return "MultiMap";
            }

            @Override
            public String getServiceName() {
                return "hz:impl:multiMapService";
            }
        }
        ,
        QUEUE{

            @Override
            public HazelcastMBean createNew(DistributedObject distributedObject, ManagementService managementService) {
                return new QueueMBean((IQueue)distributedObject, managementService);
            }

            @Override
            public String getObjectType() {
                return "IQueue";
            }

            @Override
            public String getServiceName() {
                return "hz:impl:queueService";
            }
        }
        ,
        SEMAPHORE{

            @Override
            public HazelcastMBean createNew(DistributedObject distributedObject, ManagementService managementService) {
                return new SemaphoreMBean((ISemaphore)distributedObject, managementService);
            }

            @Override
            public String getObjectType() {
                return "ISemaphore";
            }

            @Override
            public String getServiceName() {
                return "hz:impl:semaphoreService";
            }
        }
        ,
        EXECUTOR_SERVICE{

            @Override
            public HazelcastMBean createNew(DistributedObject distributedObject, ManagementService managementService) {
                return new ExecutorServiceMBean((IExecutorService)distributedObject, managementService);
            }

            @Override
            public String getObjectType() {
                return "IExecutorService";
            }

            @Override
            public String getServiceName() {
                return "hz:impl:executorService";
            }
        }
        ,
        SET{

            @Override
            public HazelcastMBean createNew(DistributedObject distributedObject, ManagementService managementService) {
                return new SetMBean((ISet)distributedObject, managementService);
            }

            @Override
            public String getObjectType() {
                return "ISet";
            }

            @Override
            public String getServiceName() {
                return "hz:impl:setService";
            }
        }
        ,
        TOPIC{

            @Override
            public HazelcastMBean createNew(DistributedObject distributedObject, ManagementService managementService) {
                return new TopicMBean((ITopic)distributedObject, managementService);
            }

            @Override
            public String getObjectType() {
                return "ITopic";
            }

            @Override
            public String getServiceName() {
                return "hz:impl:topicService";
            }
        }
        ,
        REPLICATED_MAP{

            @Override
            public HazelcastMBean createNew(DistributedObject distributedObject, ManagementService managementService) {
                return new ReplicatedMapMBean((ReplicatedMapProxy)distributedObject, managementService);
            }

            @Override
            public String getObjectType() {
                return "ReplicatedMap";
            }

            @Override
            public String getServiceName() {
                return "hz:impl:replicatedMapService";
            }
        }
        ,
        RELIABLE_TOPIC{

            @Override
            public HazelcastMBean createNew(DistributedObject distributedObject, ManagementService managementService) {
                return new ReliableTopicMBean((ReliableTopicProxy)distributedObject, managementService);
            }

            @Override
            public String getObjectType() {
                return "ReliableTopic";
            }

            @Override
            public String getServiceName() {
                return "hz:impl:reliableTopicService";
            }
        };


        abstract HazelcastMBean createNew(DistributedObject var1, ManagementService var2);

        abstract String getObjectType();

        abstract String getServiceName();
    }
}

