/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleCompleteKey
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.confluence.plugins.createcontent;

import com.atlassian.plugin.ModuleCompleteKey;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface AoBackedManager<O, A> {
    @Nullable
    public O getById(@Nonnull UUID var1);

    @Nullable
    public A getAoById(@Nonnull UUID var1);

    @Nullable
    public O getCloneByModuleCompleteKey(@Nonnull ModuleCompleteKey var1);

    @Nonnull
    public List<O> getNonClonesByModuleCompleteKey(@Nonnull ModuleCompleteKey var1);

    @Nonnull
    public List<O> getAll();

    @Nonnull
    public O create(@Nonnull O var1);

    @Nonnull
    public A createAo(@Nonnull O var1);

    @Nonnull
    public O update(@Nonnull O var1);

    @Nonnull
    public A updateAo(@Nonnull O var1);

    public boolean delete(@Nonnull UUID var1);

    public void delete(@Nonnull A var1);

    public int deleteAll();
}

