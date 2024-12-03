/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.idgen;

import com.hazelcast.concurrent.idgen.IdGeneratorProxy;
import com.hazelcast.core.DistributedObject;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IAtomicLong;
import com.hazelcast.spi.ManagedService;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.RemoteService;
import java.util.Properties;

public class IdGeneratorService
implements ManagedService,
RemoteService {
    public static final String SERVICE_NAME = "hz:impl:idGeneratorService";
    public static final String ATOMIC_LONG_NAME = "hz:atomic:idGenerator:";
    private NodeEngine nodeEngine;

    public IdGeneratorService(NodeEngine nodeEngine) {
        this.nodeEngine = nodeEngine;
    }

    @Override
    public void init(NodeEngine nodeEngine, Properties properties) {
        this.nodeEngine = nodeEngine;
    }

    @Override
    public void reset() {
    }

    @Override
    public void shutdown(boolean terminate) {
    }

    private IAtomicLong getBlockGenerator(String name) {
        HazelcastInstance hazelcastInstance = this.nodeEngine.getHazelcastInstance();
        return hazelcastInstance.getAtomicLong(ATOMIC_LONG_NAME + name);
    }

    @Override
    public DistributedObject createDistributedObject(String name) {
        IAtomicLong blockGenerator = this.getBlockGenerator(name);
        return new IdGeneratorProxy(blockGenerator, name, this.nodeEngine, this);
    }

    @Override
    public void destroyDistributedObject(String name) {
    }
}

