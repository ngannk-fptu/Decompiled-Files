/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.webresource.api.assembler.resource.ResourcePhase
 *  javax.annotation.Nonnull
 */
package com.atlassian.plugin.webresource.impl;

import com.atlassian.webresource.api.assembler.resource.ResourcePhase;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;

public class SuperbatchConfiguration {
    private boolean enabled;
    private ResourcePhase resourcePhase;

    public SuperbatchConfiguration(boolean enabled) {
        this.enabled = enabled;
    }

    public ResourcePhase getResourcePhase() {
        return Optional.ofNullable(this.resourcePhase).orElseGet(ResourcePhase::defaultPhase);
    }

    public SuperbatchConfiguration setResourcePhase(@Nonnull ResourcePhase resourcePhase) {
        this.resourcePhase = Objects.requireNonNull(resourcePhase, "The resourcePhase is required");
        return this;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public SuperbatchConfiguration setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }
}

