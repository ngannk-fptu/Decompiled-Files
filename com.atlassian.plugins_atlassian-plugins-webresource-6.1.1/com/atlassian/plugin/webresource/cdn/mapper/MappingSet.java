/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.plugin.webresource.cdn.mapper;

import com.atlassian.plugin.webresource.cdn.mapper.Mapping;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;

public interface MappingSet {
    @Nonnull
    public Optional<Mapping> get(String var1);

    @Nonnull
    public List<String> getMappedResources(String var1);

    @Nonnull
    public Iterable<Mapping> all();

    public int size();
}

