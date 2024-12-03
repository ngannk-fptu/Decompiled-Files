/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.webhooks.history;

import com.atlassian.webhooks.history.InvocationOutcome;
import javax.annotation.Nonnull;

public interface InvocationResult {
    @Nonnull
    public InvocationOutcome getOutcome();

    @Nonnull
    public String getDescription();
}

