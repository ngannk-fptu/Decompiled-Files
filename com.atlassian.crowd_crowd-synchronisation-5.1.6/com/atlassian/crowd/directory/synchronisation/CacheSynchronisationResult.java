/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  javax.annotation.Nullable
 */
package com.atlassian.crowd.directory.synchronisation;

import com.google.common.base.Strings;
import javax.annotation.Nullable;

public class CacheSynchronisationResult {
    private final boolean success;
    private final String syncStatusToken;
    public static final CacheSynchronisationResult FAILURE = new CacheSynchronisationResult(false, null);

    public CacheSynchronisationResult(boolean success, @Nullable String syncStatusToken) {
        this.success = success;
        this.syncStatusToken = Strings.nullToEmpty((String)syncStatusToken);
    }

    public boolean isSuccess() {
        return this.success;
    }

    public String getSyncStatusToken() {
        return this.syncStatusToken;
    }
}

