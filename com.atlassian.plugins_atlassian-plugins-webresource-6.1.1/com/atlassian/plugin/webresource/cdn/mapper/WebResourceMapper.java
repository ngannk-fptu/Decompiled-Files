/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.plugin.webresource.cdn.mapper;

import com.atlassian.plugin.webresource.cdn.mapper.MappingSet;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;

public interface WebResourceMapper {
    @Nonnull
    public List<String> map(@Nonnull String var1);

    @Nonnull
    public Optional<String> mapSingle(@Nonnull String var1);

    @Nonnull
    public MappingSet mappings();
}

