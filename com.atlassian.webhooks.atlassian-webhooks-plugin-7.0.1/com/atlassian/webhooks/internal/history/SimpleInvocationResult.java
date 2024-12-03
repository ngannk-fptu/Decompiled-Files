/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.webhooks.history.InvocationOutcome
 *  com.atlassian.webhooks.history.InvocationResult
 *  javax.annotation.Nonnull
 */
package com.atlassian.webhooks.internal.history;

import com.atlassian.webhooks.history.InvocationOutcome;
import com.atlassian.webhooks.history.InvocationResult;
import java.util.Objects;
import javax.annotation.Nonnull;

public class SimpleInvocationResult
implements InvocationResult {
    private final String description;
    private final InvocationOutcome outcome;

    public SimpleInvocationResult(String description, InvocationOutcome outcome) {
        this.description = Objects.requireNonNull(description, "description");
        this.outcome = Objects.requireNonNull(outcome, "outcome");
    }

    @Nonnull
    public String getDescription() {
        return this.description;
    }

    @Nonnull
    public InvocationOutcome getOutcome() {
        return this.outcome;
    }
}

