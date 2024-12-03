/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nonnull
 */
package com.atlassian.plugin.webresource.cdn.mapper;

import com.atlassian.plugin.webresource.cdn.mapper.Mapping;
import com.atlassian.plugin.webresource.cdn.mapper.MappingSet;
import com.google.common.base.Preconditions;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import javax.annotation.Nonnull;

public class DefaultMappingSet
implements MappingSet {
    public static final MappingSet EMPTY = new DefaultMappingSet(Collections.emptyList());
    private final Map<String, Mapping> mappings;

    public DefaultMappingSet(@Nonnull Collection<Mapping> mappings) {
        Preconditions.checkNotNull(mappings, (Object)"Collection of mappings is null!");
        TreeMap tm = new TreeMap();
        mappings.stream().forEach(e -> tm.put(e.originalResource(), e));
        this.mappings = Collections.unmodifiableMap(tm);
    }

    @Override
    @Nonnull
    public Optional<Mapping> get(String originalResource) {
        return Optional.ofNullable(this.mappings.get(originalResource));
    }

    @Override
    @Nonnull
    public List<String> getMappedResources(String originalResource) {
        return this.get(originalResource).map(Mapping::mappedResources).orElseGet(Collections::emptyList);
    }

    @Override
    @Nonnull
    public Iterable<Mapping> all() {
        return this.mappings.values();
    }

    @Override
    public int size() {
        return this.mappings.size();
    }
}

