/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 */
package com.atlassian.applinks.internal.common.capabilities;

import com.google.common.annotations.VisibleForTesting;

public enum ApplinksCapabilities {
    STATUS_API,
    MIGRATION_API;

    private static final String CAPABILITY_SYSPROP_PREFIX = "atlassian.applinks.capability.";
    @VisibleForTesting
    public final String key = "atlassian.applinks.capability." + this.name();
}

