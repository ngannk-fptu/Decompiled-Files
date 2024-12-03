/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.webresource.api.assembler.resource.ResourcePhase
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.plugin.webresource.impl.helpers;

import com.atlassian.plugin.webresource.impl.RequestState;
import com.atlassian.webresource.api.assembler.resource.ResourcePhase;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ResourceGenerationInfo {
    private final ResourcePhase resourcePhase;
    private final RequestState data;

    public ResourceGenerationInfo(@Nullable ResourcePhase resourcePhase, @Nonnull RequestState data) {
        this.resourcePhase = resourcePhase;
        this.data = Objects.requireNonNull(data);
    }

    @Nonnull
    public Optional<ResourcePhase> getResourcePhase() {
        return Optional.ofNullable(this.resourcePhase);
    }

    @Nonnull
    public RequestState getData() {
        return this.data;
    }
}

