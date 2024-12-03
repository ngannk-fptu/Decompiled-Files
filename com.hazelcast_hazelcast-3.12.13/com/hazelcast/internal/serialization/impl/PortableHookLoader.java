/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.serialization.impl;

import com.hazelcast.internal.serialization.PortableHook;
import com.hazelcast.logging.Logger;
import com.hazelcast.nio.serialization.ClassDefinition;
import com.hazelcast.nio.serialization.PortableFactory;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.ServiceLoader;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

final class PortableHookLoader {
    private static final String FACTORY_ID = "com.hazelcast.PortableHook";
    private final Map<Integer, ? extends PortableFactory> configuredFactories;
    private final Map<Integer, PortableFactory> factories = new HashMap<Integer, PortableFactory>();
    private final Collection<ClassDefinition> definitions = new HashSet<ClassDefinition>();
    private final ClassLoader classLoader;

    PortableHookLoader(Map<Integer, ? extends PortableFactory> configuredFactories, ClassLoader classLoader) {
        this.configuredFactories = configuredFactories;
        this.classLoader = classLoader;
        this.load();
    }

    private void load() {
        try {
            Iterator<PortableHook> hooks = ServiceLoader.iterator(PortableHook.class, FACTORY_ID, this.classLoader);
            while (hooks.hasNext()) {
                Collection<ClassDefinition> defs;
                PortableHook hook = hooks.next();
                PortableFactory factory = hook.createFactory();
                if (factory != null) {
                    this.register(hook.getFactoryId(), factory);
                }
                if ((defs = hook.getBuiltinDefinitions()) == null || defs.isEmpty()) continue;
                this.definitions.addAll(defs);
            }
        }
        catch (Exception e) {
            throw ExceptionUtil.rethrow(e);
        }
        if (this.configuredFactories != null) {
            for (Map.Entry<Integer, ? extends PortableFactory> entry : this.configuredFactories.entrySet()) {
                this.register(entry.getKey(), entry.getValue());
            }
        }
    }

    Map<Integer, PortableFactory> getFactories() {
        return this.factories;
    }

    Collection<ClassDefinition> getDefinitions() {
        return this.definitions;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private void register(int factoryId, PortableFactory factory) {
        PortableFactory current = this.factories.get(factoryId);
        if (current != null) {
            if (!current.equals(factory)) throw new IllegalArgumentException("PortableFactory[" + factoryId + "] is already registered! " + current + " -> " + factory);
            Logger.getLogger(this.getClass()).warning("PortableFactory[" + factoryId + "] is already registered! Skipping " + factory);
            return;
        } else {
            this.factories.put(factoryId, factory);
        }
    }
}

