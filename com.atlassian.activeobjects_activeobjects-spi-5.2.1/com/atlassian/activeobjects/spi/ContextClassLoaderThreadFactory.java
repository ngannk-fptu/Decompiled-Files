/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  javax.annotation.Nonnull
 */
package com.atlassian.activeobjects.spi;

import com.atlassian.annotations.VisibleForTesting;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import javax.annotation.Nonnull;

public class ContextClassLoaderThreadFactory
implements ThreadFactory {
    private final ClassLoader contextClassLoader;

    public ContextClassLoaderThreadFactory(ClassLoader contextClassLoader) {
        this.contextClassLoader = Objects.requireNonNull(contextClassLoader);
    }

    @Override
    public Thread newThread(@Nonnull Runnable r) {
        Thread thread = Executors.defaultThreadFactory().newThread(r);
        thread.setContextClassLoader(this.contextClassLoader);
        return thread;
    }

    @VisibleForTesting
    public ClassLoader getContextClassLoader() {
        return this.contextClassLoader;
    }
}

