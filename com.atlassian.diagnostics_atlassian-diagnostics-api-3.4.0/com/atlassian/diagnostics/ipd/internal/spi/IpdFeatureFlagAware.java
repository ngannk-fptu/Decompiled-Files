/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 */
package com.atlassian.diagnostics.ipd.internal.spi;

import com.atlassian.annotations.ExperimentalApi;

@ExperimentalApi
public interface IpdFeatureFlagAware {
    public boolean isIpdFeatureFlagEnabled();

    default public boolean isWipIpdFeatureFlagEnabled() {
        return false;
    }
}

