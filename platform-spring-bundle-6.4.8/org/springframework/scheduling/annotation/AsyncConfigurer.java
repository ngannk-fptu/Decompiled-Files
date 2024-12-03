/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.scheduling.annotation;

import java.util.concurrent.Executor;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.lang.Nullable;

public interface AsyncConfigurer {
    @Nullable
    default public Executor getAsyncExecutor() {
        return null;
    }

    @Nullable
    default public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return null;
    }
}

