/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl;

import com.hazelcast.map.impl.DefaultMapServiceFactory;
import com.hazelcast.map.impl.MapService;
import com.hazelcast.map.impl.MapServiceContextImpl;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.util.ConstructorFunction;

public final class MapServiceConstructor {
    private static final ConstructorFunction<NodeEngine, MapService> DEFAULT_MAP_SERVICE_CONSTRUCTOR = new ConstructorFunction<NodeEngine, MapService>(){

        @Override
        public MapService createNew(NodeEngine nodeEngine) {
            MapServiceContextImpl defaultMapServiceContext = new MapServiceContextImpl(nodeEngine);
            DefaultMapServiceFactory factory = new DefaultMapServiceFactory(nodeEngine, defaultMapServiceContext);
            return factory.createMapService();
        }
    };

    private MapServiceConstructor() {
    }

    public static ConstructorFunction<NodeEngine, MapService> getDefaultMapServiceConstructor() {
        return DEFAULT_MAP_SERVICE_CONSTRUCTOR;
    }
}

