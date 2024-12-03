/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.atlassian.analytics.client.session;

import javax.annotation.Nullable;

public interface SessionIdProvider {
    @Nullable
    public String getSessionId();
}

