/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.event.api;

import javax.annotation.Nonnull;

public interface EventListenerRegistrar {
    public void register(@Nonnull Object var1);

    public void unregister(@Nonnull Object var1);

    public void unregisterAll();
}

