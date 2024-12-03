/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal;

import com.hazelcast.cp.internal.RaftService;
import com.hazelcast.cp.internal.session.RaftSessionService;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.impl.servicemanager.ServiceDescriptor;
import com.hazelcast.spi.impl.servicemanager.ServiceDescriptorProvider;

public class RaftServiceDescriptorProvider
implements ServiceDescriptorProvider {
    @Override
    public ServiceDescriptor[] createServiceDescriptors() {
        return new ServiceDescriptor[]{new RaftServiceDescriptor(), new RaftSessionServiceDescriptor()};
    }

    private static class RaftSessionServiceDescriptor
    implements ServiceDescriptor {
        private RaftSessionServiceDescriptor() {
        }

        @Override
        public String getServiceName() {
            return "hz:core:raftSession";
        }

        @Override
        public Object getService(NodeEngine nodeEngine) {
            return new RaftSessionService(nodeEngine);
        }
    }

    private static class RaftServiceDescriptor
    implements ServiceDescriptor {
        private RaftServiceDescriptor() {
        }

        @Override
        public String getServiceName() {
            return "hz:core:raft";
        }

        @Override
        public Object getService(NodeEngine nodeEngine) {
            return new RaftService(nodeEngine);
        }
    }
}

