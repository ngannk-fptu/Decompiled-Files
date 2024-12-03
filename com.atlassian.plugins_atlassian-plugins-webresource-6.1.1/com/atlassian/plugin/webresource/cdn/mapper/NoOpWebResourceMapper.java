/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.plugin.webresource.cdn.mapper;

import com.atlassian.plugin.webresource.cdn.mapper.DefaultMappingSet;
import com.atlassian.plugin.webresource.cdn.mapper.MappingSet;
import com.atlassian.plugin.webresource.cdn.mapper.WebResourceMapper;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NoOpWebResourceMapper
implements WebResourceMapper {
    private final MappingSet mappings = new DefaultMappingSet(Collections.emptyList());
    private final Optional<Exception> reason;

    public NoOpWebResourceMapper(@Nullable Exception reason) {
        this.reason = Optional.ofNullable(reason);
    }

    @Override
    @Nonnull
    public List<String> map(@Nonnull String resourceUrl) {
        return Collections.emptyList();
    }

    @Override
    @Nonnull
    public Optional<String> mapSingle(@Nonnull String resourceUrl) {
        return Optional.empty();
    }

    @Override
    @Nonnull
    public MappingSet mappings() {
        return this.mappings;
    }

    @Nonnull
    public Optional<Exception> reason() {
        return this.reason;
    }
}

