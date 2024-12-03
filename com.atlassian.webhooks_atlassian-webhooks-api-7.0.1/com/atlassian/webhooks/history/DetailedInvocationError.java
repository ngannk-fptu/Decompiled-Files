/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.webhooks.history;

import com.atlassian.webhooks.history.DetailedInvocationResult;
import javax.annotation.Nonnull;

public interface DetailedInvocationError
extends DetailedInvocationResult {
    @Nonnull
    public String getContent();

    @Nonnull
    default public String getErrorMessage() {
        return this.getDescription();
    }
}

