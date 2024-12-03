/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.webresource.api.assembler;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.webresource.api.assembler.resource.ResourcePhase;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@ExperimentalApi
public interface RequiredResources {
    @Nonnull
    public RequiredResources requireWebResource(@Nonnull String var1);

    @Nonnull
    public RequiredResources requireWebResource(@Nonnull ResourcePhase var1, @Nonnull String var2);

    @Nonnull
    public RequiredResources requireModule(@Nonnull String var1);

    @Nonnull
    public RequiredResources requireModule(@Nonnull ResourcePhase var1, @Nonnull String var2);

    @Nonnull
    public RequiredResources requireContext(@Nonnull String var1);

    @Nonnull
    public RequiredResources requireContext(@Nonnull ResourcePhase var1, @Nonnull String var2);

    @Nonnull
    public RequiredResources exclude(@Nullable Set<String> var1, @Nullable Set<String> var2);

    @Nonnull
    public RequiredResources requirePage(@Nonnull String var1);

    @Nonnull
    public RequiredResources requirePage(@Nonnull ResourcePhase var1, @Nonnull String var2);

    @Nonnull
    public RequiredResources excludeSuperbatch();

    @Nonnull
    public RequiredResources requireSuperbatch(@Nonnull ResourcePhase var1);
}

