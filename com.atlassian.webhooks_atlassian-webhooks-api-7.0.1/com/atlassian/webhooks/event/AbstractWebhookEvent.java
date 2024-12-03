/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.webhooks.event;

import java.util.EventObject;
import java.util.Objects;
import javax.annotation.Nonnull;

public abstract class AbstractWebhookEvent
extends EventObject {
    AbstractWebhookEvent(@Nonnull Object source) {
        super(Objects.requireNonNull(source, "source"));
    }
}

