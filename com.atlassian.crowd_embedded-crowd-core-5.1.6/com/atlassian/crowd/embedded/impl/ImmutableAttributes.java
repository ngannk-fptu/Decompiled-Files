/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Attributes
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Iterables
 *  javax.annotation.Nonnull
 */
package com.atlassian.crowd.embedded.impl;

import com.atlassian.crowd.embedded.api.Attributes;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nonnull;

public final class ImmutableAttributes
implements Attributes,
Serializable {
    private static final long serialVersionUID = -356796362241352598L;
    private final Map<String, Set<String>> map;

    public ImmutableAttributes() {
        this.map = Collections.emptyMap();
    }

    public ImmutableAttributes(@Nonnull Map<String, Set<String>> attributesMap) {
        this.map = ImmutableAttributes.immutableCopyOf(attributesMap);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ImmutableAttributes that = (ImmutableAttributes)o;
        return Objects.equals(this.map, that.map);
    }

    public int hashCode() {
        return Objects.hash(this.map);
    }

    public ImmutableAttributes(@Nonnull Attributes attributes) {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        for (String key : attributes.getKeys()) {
            builder.put((Object)key, (Object)ImmutableSet.copyOf((Collection)attributes.getValues(key)));
        }
        this.map = builder.build();
    }

    public Set<String> getValues(String key) {
        return this.map.get(key);
    }

    public String getValue(String key) {
        Set<String> values = this.getValues(key);
        if (values == null) {
            return null;
        }
        return (String)Iterables.getFirst(values, null);
    }

    public Set<String> getKeys() {
        return this.map.keySet();
    }

    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    private static Map<String, Set<String>> immutableCopyOf(Map<String, Set<String>> source) {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        for (Map.Entry<String, Set<String>> entry : source.entrySet()) {
            builder.put((Object)entry.getKey(), (Object)ImmutableSet.copyOf((Collection)entry.getValue()));
        }
        return builder.build();
    }
}

