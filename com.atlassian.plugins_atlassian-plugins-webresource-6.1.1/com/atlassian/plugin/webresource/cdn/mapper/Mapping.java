/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.plugin.webresource.cdn.mapper;

import java.util.List;
import javax.annotation.Nonnull;

public interface Mapping {
    @Nonnull
    public String originalResource();

    @Nonnull
    public List<String> mappedResources();
}

