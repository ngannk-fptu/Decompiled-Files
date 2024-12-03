/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.webhooks;

import com.atlassian.webhooks.WebhookInvocation;
import java.util.Objects;
import javax.annotation.Nonnull;

public class DispatchFailedException
extends RuntimeException {
    static final long serialVersionUID = 1L;
    private final WebhookInvocation invocation;

    public DispatchFailedException(@Nonnull WebhookInvocation invocation, String message) {
        super(message);
        this.invocation = Objects.requireNonNull(invocation, "invocation");
    }

    @Nonnull
    public WebhookInvocation getInvocation() {
        return this.invocation;
    }
}

