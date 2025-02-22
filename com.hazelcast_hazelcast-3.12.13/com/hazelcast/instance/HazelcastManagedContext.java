/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.instance;

import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.core.ManagedContext;
import com.hazelcast.instance.HazelcastInstanceImpl;
import com.hazelcast.spi.NodeAware;
import com.hazelcast.spi.annotation.PrivateApi;
import com.hazelcast.spi.serialization.SerializationServiceAware;

@PrivateApi
public final class HazelcastManagedContext
implements ManagedContext {
    private final HazelcastInstanceImpl instance;
    private final ManagedContext externalContext;
    private final boolean hasExternalContext;

    public HazelcastManagedContext(HazelcastInstanceImpl instance, ManagedContext externalContext) {
        this.instance = instance;
        this.externalContext = externalContext;
        this.hasExternalContext = externalContext != null;
    }

    @Override
    public Object initialize(Object obj) {
        if (obj instanceof HazelcastInstanceAware) {
            ((HazelcastInstanceAware)obj).setHazelcastInstance(this.instance);
        }
        if (obj instanceof NodeAware) {
            ((NodeAware)obj).setNode(this.instance.node);
        }
        if (obj instanceof SerializationServiceAware) {
            ((SerializationServiceAware)obj).setSerializationService(this.instance.node.getSerializationService());
        }
        if (this.hasExternalContext) {
            return this.externalContext.initialize(obj);
        }
        return obj;
    }
}

