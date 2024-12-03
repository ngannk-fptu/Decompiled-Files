/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures;

import com.hazelcast.cp.internal.datastructures.atomiclong.RaftAtomicLongService;
import com.hazelcast.cp.internal.datastructures.atomicref.RaftAtomicRefService;
import com.hazelcast.cp.internal.datastructures.countdownlatch.RaftCountDownLatchService;
import com.hazelcast.cp.internal.datastructures.lock.RaftLockService;
import com.hazelcast.cp.internal.datastructures.semaphore.RaftSemaphoreService;
import com.hazelcast.cp.internal.session.ProxySessionManagerService;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.impl.servicemanager.ServiceDescriptor;
import com.hazelcast.spi.impl.servicemanager.ServiceDescriptorProvider;

public class RaftDataServiceDescriptorProvider
implements ServiceDescriptorProvider {
    @Override
    public ServiceDescriptor[] createServiceDescriptors() {
        return new ServiceDescriptor[]{new RaftAtomicLongServiceDescriptor(), new RaftLockServiceDescriptor(), new RaftSessionManagerServiceDescriptor(), new RaftAtomicRefServiceDescriptor(), new RaftSemaphoreServiceDescriptor(), new RaftCountDownLatcherviceDescriptor()};
    }

    private static class RaftCountDownLatcherviceDescriptor
    implements ServiceDescriptor {
        private RaftCountDownLatcherviceDescriptor() {
        }

        @Override
        public String getServiceName() {
            return "hz:raft:countDownLatchService";
        }

        @Override
        public Object getService(NodeEngine nodeEngine) {
            return new RaftCountDownLatchService(nodeEngine);
        }
    }

    private static class RaftSemaphoreServiceDescriptor
    implements ServiceDescriptor {
        private RaftSemaphoreServiceDescriptor() {
        }

        @Override
        public String getServiceName() {
            return "hz:raft:semaphoreService";
        }

        @Override
        public Object getService(NodeEngine nodeEngine) {
            return new RaftSemaphoreService(nodeEngine);
        }
    }

    private static class RaftAtomicRefServiceDescriptor
    implements ServiceDescriptor {
        private RaftAtomicRefServiceDescriptor() {
        }

        @Override
        public String getServiceName() {
            return "hz:raft:atomicRefService";
        }

        @Override
        public Object getService(NodeEngine nodeEngine) {
            return new RaftAtomicRefService(nodeEngine);
        }
    }

    private static class RaftLockServiceDescriptor
    implements ServiceDescriptor {
        private RaftLockServiceDescriptor() {
        }

        @Override
        public String getServiceName() {
            return "hz:raft:lockService";
        }

        @Override
        public Object getService(NodeEngine nodeEngine) {
            return new RaftLockService(nodeEngine);
        }
    }

    private static class RaftSessionManagerServiceDescriptor
    implements ServiceDescriptor {
        private RaftSessionManagerServiceDescriptor() {
        }

        @Override
        public String getServiceName() {
            return "hz:raft:proxySessionManagerService";
        }

        @Override
        public Object getService(NodeEngine nodeEngine) {
            return new ProxySessionManagerService(nodeEngine);
        }
    }

    private static class RaftAtomicLongServiceDescriptor
    implements ServiceDescriptor {
        private RaftAtomicLongServiceDescriptor() {
        }

        @Override
        public String getServiceName() {
            return "hz:raft:atomicLongService";
        }

        @Override
        public Object getService(NodeEngine nodeEngine) {
            return new RaftAtomicLongService(nodeEngine);
        }
    }
}

