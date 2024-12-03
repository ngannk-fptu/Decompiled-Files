/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Lists
 *  com.google.errorprone.annotations.Immutable
 *  io.atlassian.util.concurrent.LazyReference
 *  org.apache.velocity.context.Context
 */
package com.atlassian.confluence.velocity;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.errorprone.annotations.Immutable;
import io.atlassian.util.concurrent.LazyReference;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.apache.velocity.context.Context;

@Immutable
final class ContextMapView
extends AbstractMap<String, Object> {
    private final Context context;
    private final LazyReference<Set<Map.Entry<String, Object>>> entries;

    ContextMapView(final Context context) {
        this.context = Objects.requireNonNull(context, "context");
        this.entries = new LazyReference<Set<Map.Entry<String, Object>>>(){

            protected Set<Map.Entry<String, Object>> create() {
                List<Object> keyWithNoDuplicates = Arrays.asList(context.getKeys());
                return ImmutableSet.copyOf((Collection)Lists.transform(keyWithNoDuplicates, (Function)new EntryTransformer()));
            }
        };
    }

    @Override
    public Object get(Object key) {
        return this.context.get((String)key);
    }

    @Override
    public boolean containsKey(Object key) {
        return this.context.containsKey(key);
    }

    @Override
    public Set<Map.Entry<String, Object>> entrySet() {
        return (Set)this.entries.get();
    }

    class EntryTransformer
    implements Function<Object, Map.Entry<String, Object>> {
        EntryTransformer() {
        }

        public Map.Entry<String, Object> apply(Object key) {
            return new EntryView(key);
        }
    }

    @Immutable
    class EntryView
    implements Map.Entry<String, Object> {
        private final String key;

        EntryView(Object key) {
            this.key = (String)key;
        }

        @Override
        public String getKey() {
            return this.key;
        }

        @Override
        public Object getValue() {
            return ContextMapView.this.context.get(this.key);
        }

        @Override
        public Object setValue(Object value) {
            throw new UnsupportedOperationException();
        }
    }
}

