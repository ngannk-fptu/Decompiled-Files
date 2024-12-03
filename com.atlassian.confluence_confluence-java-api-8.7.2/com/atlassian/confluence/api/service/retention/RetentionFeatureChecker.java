/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 */
package com.atlassian.confluence.api.service.retention;

import com.atlassian.annotations.ExperimentalApi;

@ExperimentalApi
public interface RetentionFeatureChecker {
    public boolean isFeatureAvailable();

    public boolean isDryRunModeEnabled();
}

