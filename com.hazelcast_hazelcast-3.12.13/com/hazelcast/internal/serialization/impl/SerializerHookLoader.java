/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.serialization.impl;

import com.hazelcast.config.SerializationConfig;
import com.hazelcast.config.SerializerConfig;
import com.hazelcast.logging.Logger;
import com.hazelcast.nio.ClassLoaderUtil;
import com.hazelcast.nio.serialization.HazelcastSerializationException;
import com.hazelcast.nio.serialization.Serializer;
import com.hazelcast.nio.serialization.SerializerHook;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.ServiceLoader;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

final class SerializerHookLoader {
    private static final String FACTORY_ID = "com.hazelcast.SerializerHook";
    private final boolean useDefaultConstructorOnly = Boolean.getBoolean("hazelcast.compat.serializers.use.default.constructor.only");
    private final Map<Class, Object> serializers = new HashMap<Class, Object>();
    private final Collection<SerializerConfig> serializerConfigs;
    private final ClassLoader classLoader;

    SerializerHookLoader(SerializationConfig serializationConfig, ClassLoader classLoader) {
        this.serializerConfigs = serializationConfig != null ? serializationConfig.getSerializerConfigs() : null;
        this.classLoader = classLoader;
        this.load();
    }

    private void load() {
        try {
            Iterator<SerializerHook> hooks = ServiceLoader.iterator(SerializerHook.class, FACTORY_ID, this.classLoader);
            while (hooks.hasNext()) {
                SerializerHook hook = hooks.next();
                Class serializationType = hook.getSerializationType();
                if (serializationType == null) continue;
                this.serializers.put(serializationType, hook);
            }
        }
        catch (Exception e) {
            throw ExceptionUtil.rethrow(e);
        }
        if (this.serializerConfigs != null) {
            for (SerializerConfig serializerConfig : this.serializerConfigs) {
                Serializer serializer = serializerConfig.getImplementation();
                Class<?> serializationType = serializerConfig.getTypeClass();
                if (serializationType == null) {
                    try {
                        serializationType = ClassLoaderUtil.loadClass(this.classLoader, serializerConfig.getTypeClassName());
                    }
                    catch (ClassNotFoundException e) {
                        throw new HazelcastSerializationException(e);
                    }
                }
                if (serializer == null) {
                    serializer = this.createSerializerInstance(serializerConfig, serializationType);
                }
                this.register(serializationType, serializer);
            }
        }
    }

    private Serializer createSerializerInstance(SerializerConfig serializerConfig, Class serializationType) {
        try {
            String className = serializerConfig.getClassName();
            if (this.useDefaultConstructorOnly) {
                return (Serializer)ClassLoaderUtil.newInstance(this.classLoader, className);
            }
            return this.createSerializerInstanceWithFallback(serializationType, className);
        }
        catch (Exception e) {
            throw new HazelcastSerializationException(e);
        }
    }

    private Serializer createSerializerInstanceWithFallback(Class serializationType, String className) throws Exception {
        Class<?> clazz = ClassLoaderUtil.loadClass(this.classLoader, className);
        try {
            Constructor<?> constructor = clazz.getDeclaredConstructor(Class.class);
            constructor.setAccessible(true);
            return (Serializer)constructor.newInstance(serializationType);
        }
        catch (NoSuchMethodException e) {
            Constructor<?> constructor = clazz.getDeclaredConstructor(new Class[0]);
            constructor.setAccessible(true);
            return (Serializer)constructor.newInstance(new Object[0]);
        }
    }

    Map<Class, Object> getSerializers() {
        return this.serializers;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private void register(Class serializationType, Serializer serializer) {
        Object current = this.serializers.get(serializationType);
        if (current != null) {
            if (current.equals(serializer)) {
                Logger.getLogger(this.getClass()).warning("Serializer[" + serializationType.toString() + "] is already registered! Skipping " + serializer);
                return;
            } else {
                if (!(current instanceof SerializerHook) || !((SerializerHook)current).isOverwritable()) throw new IllegalArgumentException("Serializer[" + serializationType.toString() + "] is already registered! " + current + " -> " + serializer);
                this.serializers.put(serializationType, serializer);
            }
            return;
        } else {
            this.serializers.put(serializationType, serializer);
        }
    }
}

