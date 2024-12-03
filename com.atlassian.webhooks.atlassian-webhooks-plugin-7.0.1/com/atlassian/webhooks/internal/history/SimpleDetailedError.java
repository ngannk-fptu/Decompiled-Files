/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.webhooks.history.DetailedInvocationError
 *  com.atlassian.webhooks.history.InvocationOutcome
 *  javax.annotation.Nonnull
 */
package com.atlassian.webhooks.internal.history;

import com.atlassian.webhooks.history.DetailedInvocationError;
import com.atlassian.webhooks.history.InvocationOutcome;
import java.util.Objects;
import javax.annotation.Nonnull;

public class SimpleDetailedError
implements DetailedInvocationError {
    private final String content;
    private final String description;

    public SimpleDetailedError(@Nonnull String content, @Nonnull String description) {
        this.content = Objects.requireNonNull(content, "content");
        this.description = Objects.requireNonNull(description, "description");
    }

    @Nonnull
    public String getContent() {
        return this.content;
    }

    @Nonnull
    public String getDescription() {
        return this.description;
    }

    @Nonnull
    public InvocationOutcome getOutcome() {
        return InvocationOutcome.ERROR;
    }
}

