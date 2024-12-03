/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.set;

import com.hazelcast.collection.impl.collection.AbstractCollectionProxyImpl;
import com.hazelcast.collection.impl.set.SetService;
import com.hazelcast.config.CollectionConfig;
import com.hazelcast.core.ISet;
import com.hazelcast.spi.NodeEngine;

public class SetProxyImpl<E>
extends AbstractCollectionProxyImpl<SetService, E>
implements ISet<E> {
    public SetProxyImpl(String name, NodeEngine nodeEngine, SetService service) {
        super(name, nodeEngine, service);
    }

    @Override
    protected CollectionConfig getConfig(NodeEngine nodeEngine) {
        return nodeEngine.getConfig().findSetConfig(this.name);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:setService";
    }
}

