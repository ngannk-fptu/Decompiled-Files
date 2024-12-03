/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.event.spi;

import com.atlassian.event.spi.ListenerInvoker;
import javax.annotation.Nonnull;

public interface EventRunnableFactory {
    @Nonnull
    public Runnable getRunnable(ListenerInvoker var1, Object var2);
}

