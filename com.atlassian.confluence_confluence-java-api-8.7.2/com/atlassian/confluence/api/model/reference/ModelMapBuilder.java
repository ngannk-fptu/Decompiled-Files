/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSet$Builder
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.api.model.reference;

import com.atlassian.confluence.api.model.reference.CollapsedMap;
import com.atlassian.confluence.api.model.reference.EnrichableMap;
import com.atlassian.confluence.api.nav.Navigation;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ModelMapBuilder<K, V> {
    private Map<K, V> delegate = new LinkedHashMap();
    @Deprecated
    ImmutableSet.Builder<K> collapsedEntries = ImmutableSet.builder();
    CollapsedMap<K, V> collapsedMap;
    Navigation.Builder navBuilder = null;
    private boolean isExpanded = false;

    private ModelMapBuilder() {
    }

    public static <K, V> ModelMapBuilder<K, V> newInstance() {
        return new ModelMapBuilder<K, V>();
    }

    public static <K, V> ModelMapBuilder<K, V> newInstance(Map<? extends K, ? extends V> map) {
        return new ModelMapBuilder<K, V>().putAll(map);
    }

    public static <K, V> ModelMapBuilder<K, V> newExpandedInstance() {
        ModelMapBuilder<K, V> mapBuilder = ModelMapBuilder.newInstance();
        super.setExpanded(true);
        return mapBuilder;
    }

    public ModelMapBuilder<K, V> put(K key, V value) {
        this.setExpanded(true);
        if (key != null && value != null) {
            this.delegate.put(key, value);
        }
        return this;
    }

    public ModelMapBuilder<K, V> putAll(Map<? extends K, ? extends V> map) {
        this.setExpanded(true);
        this.delegate.putAll(map);
        return this;
    }

    public ModelMapBuilder<K, V> addCollapsedEntry(K key) {
        this.setExpanded(true);
        this.collapsedEntries.add(key);
        return this;
    }

    @Deprecated
    public ModelMapBuilder<K, V> addCollapsedEntries(Set<? extends K> collapsedEntries) {
        return this.addCollapsedEntries((Iterable<? extends K>)collapsedEntries);
    }

    public ModelMapBuilder<K, V> addCollapsedEntries(Iterable<? extends K> collapsedEntries) {
        this.setExpanded(true);
        this.collapsedEntries.addAll(collapsedEntries);
        return this;
    }

    public ModelMapBuilder<K, V> copy(@NonNull Map<? extends K, ? extends V> map) {
        boolean expanded = !(map instanceof CollapsedMap);
        this.setExpanded(expanded);
        if (expanded) {
            this.delegate.clear();
            this.putAll(map);
            if (map instanceof EnrichableMap) {
                EnrichableMap enrichableMap = (EnrichableMap)map;
                this.addCollapsedEntries(enrichableMap.getCollapsedEntries());
                this.collapsedMap = null;
                this.navBuilder = enrichableMap.getNavigationBuilder();
            }
        } else {
            this.collapsedMap = (CollapsedMap)map;
            this.navBuilder = null;
        }
        return this;
    }

    public ModelMapBuilder<K, V> navigable(Navigation.Builder navBuilder) {
        this.navBuilder = navBuilder;
        return this;
    }

    private void setExpanded(boolean expanded) {
        this.isExpanded = expanded;
    }

    public Map<K, V> build() {
        if (this.isExpanded) {
            return new EnrichableMap(this, this.navBuilder);
        }
        if (this.collapsedMap != null) {
            return this.collapsedMap;
        }
        return new CollapsedMap();
    }

    @Deprecated
    protected ImmutableMap<K, V> buildDelegate() {
        return ImmutableMap.copyOf(this.buildFromDelegate());
    }

    protected Map<K, V> buildFromDelegate() {
        return Collections.unmodifiableMap(this.delegate);
    }
}

