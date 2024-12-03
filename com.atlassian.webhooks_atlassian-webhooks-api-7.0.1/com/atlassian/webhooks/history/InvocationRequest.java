/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.webhooks.history;

import com.atlassian.webhooks.request.Method;
import javax.annotation.Nonnull;

public interface InvocationRequest {
    @Nonnull
    public Method getMethod();

    @Nonnull
    public String getUrl();
}

