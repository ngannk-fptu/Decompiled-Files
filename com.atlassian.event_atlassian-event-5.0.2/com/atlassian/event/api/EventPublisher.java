/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.event.api;

import com.atlassian.event.api.EventListenerRegistrar;
import javax.annotation.Nonnull;

public interface EventPublisher
extends EventListenerRegistrar {
    public void publish(@Nonnull Object var1);
}

