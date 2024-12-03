/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 */
package com.atlassian.ratelimiting.dev;

import com.atlassian.annotations.VisibleForTesting;

@VisibleForTesting
public interface SettingsInvalidationService {
    public void resetAllSettings();

    public void reloadSettings();
}

