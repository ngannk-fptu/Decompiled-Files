/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.event.internal;

import com.atlassian.event.spi.ListenerInvoker;
import javax.annotation.Nonnull;

public interface InvokerTransformer {
    @Nonnull
    public Iterable<ListenerInvoker> transformAll(@Nonnull Iterable<ListenerInvoker> var1, @Nonnull Object var2);
}

