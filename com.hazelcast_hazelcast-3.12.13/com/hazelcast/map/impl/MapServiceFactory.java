/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl;

import com.hazelcast.map.impl.MapService;
import com.hazelcast.map.impl.MapServiceContext;
import com.hazelcast.spi.NodeEngine;

public interface MapServiceFactory {
    public NodeEngine getNodeEngine();

    public MapServiceContext getMapServiceContext();

    public MapService createMapService();
}

