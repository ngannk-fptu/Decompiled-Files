/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.event.spi;

import java.util.concurrent.Executor;
import javax.annotation.Nonnull;

public interface EventExecutorFactory {
    @Nonnull
    public Executor getExecutor();
}

