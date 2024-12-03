/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler
 *  org.springframework.lang.Nullable
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

