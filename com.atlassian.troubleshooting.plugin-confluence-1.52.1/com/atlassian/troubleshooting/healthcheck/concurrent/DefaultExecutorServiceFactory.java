/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.troubleshooting.healthcheck.concurrent;

import com.atlassian.troubleshooting.api.healthcheck.ExecutorServiceFactory;
import com.atlassian.util.concurrent.ThreadFactories;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import javax.annotation.Nonnull;

public class DefaultExecutorServiceFactory
implements ExecutorServiceFactory {
    @Override
    @Nonnull
    public ExecutorService newFixedSizeThreadPool(int size, @Nonnull String prefix) {
        Objects.requireNonNull(prefix);
        ThreadFactory threadFactory = ThreadFactories.namedThreadFactory(prefix, ThreadFactories.Type.DAEMON);
        return Executors.newFixedThreadPool(size, threadFactory);
    }
}

