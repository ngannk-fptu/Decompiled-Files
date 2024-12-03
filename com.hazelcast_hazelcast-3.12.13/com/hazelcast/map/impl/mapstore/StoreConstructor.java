/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.mapstore;

import com.hazelcast.config.MapStoreConfig;
import com.hazelcast.core.MapStoreFactory;
import com.hazelcast.nio.ClassLoaderUtil;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.StringUtil;
import java.util.Properties;

final class StoreConstructor {
    private StoreConstructor() {
    }

    static Object createStore(String name, MapStoreConfig mapStoreConfig, ClassLoader classLoader) {
        Object store = StoreConstructor.getStoreFromFactoryOrNull(name, mapStoreConfig, classLoader);
        if (store == null) {
            store = StoreConstructor.getStoreFromImplementationOrNull(mapStoreConfig);
        }
        if (store == null) {
            store = StoreConstructor.getStoreFromClassOrNull(mapStoreConfig, classLoader);
        }
        return store;
    }

    private static Object getStoreFromFactoryOrNull(String name, MapStoreConfig mapStoreConfig, ClassLoader classLoader) {
        MapStoreFactory factory = (MapStoreFactory)mapStoreConfig.getFactoryImplementation();
        if (factory == null) {
            String factoryClassName = mapStoreConfig.getFactoryClassName();
            if (StringUtil.isNullOrEmpty(factoryClassName)) {
                return null;
            }
            try {
                factory = (MapStoreFactory)ClassLoaderUtil.newInstance(classLoader, factoryClassName);
            }
            catch (Exception e) {
                throw ExceptionUtil.rethrow(e);
            }
        }
        if (factory == null) {
            return null;
        }
        Properties properties = mapStoreConfig.getProperties();
        return factory.newMapStore(name, properties);
    }

    private static Object getStoreFromImplementationOrNull(MapStoreConfig mapStoreConfig) {
        return mapStoreConfig.getImplementation();
    }

    private static Object getStoreFromClassOrNull(MapStoreConfig mapStoreConfig, ClassLoader classLoader) {
        Object store;
        String mapStoreClassName = mapStoreConfig.getClassName();
        try {
            store = ClassLoaderUtil.newInstance(classLoader, mapStoreClassName);
        }
        catch (Exception e) {
            throw ExceptionUtil.rethrow(e);
        }
        return store;
    }
}

