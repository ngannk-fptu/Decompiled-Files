/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Maps
 *  com.google.inject.ConfigurationException
 *  com.google.inject.Key
 *  com.google.inject.TypeLiteral
 *  com.google.inject.spi.Message
 */
package com.google.inject.assistedinject;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.inject.ConfigurationException;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.Message;
import java.util.Collections;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
class BindingCollector {
    private final Map<Key<?>, TypeLiteral<?>> bindings = Maps.newHashMap();

    BindingCollector() {
    }

    public BindingCollector addBinding(Key<?> key, TypeLiteral<?> target) {
        if (this.bindings.containsKey(key)) {
            throw new ConfigurationException((Iterable)ImmutableSet.of((Object)new Message("Only one implementation can be specified for " + key)));
        }
        this.bindings.put(key, target);
        return this;
    }

    public Map<Key<?>, TypeLiteral<?>> getBindings() {
        return Collections.unmodifiableMap(this.bindings);
    }
}

