/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.servicemanager;

import com.hazelcast.spi.NodeEngine;

public interface ServiceDescriptor {
    public String getServiceName();

    public Object getService(NodeEngine var1);
}

