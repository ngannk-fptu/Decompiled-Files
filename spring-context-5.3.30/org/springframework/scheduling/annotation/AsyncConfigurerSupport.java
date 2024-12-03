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
import org.springframework.scheduling.annotation.AsyncConfigurer;

public class AsyncConfigurerSupport
implements AsyncConfigurer {
    @Override
    public Executor getAsyncExecutor() {
        return null;
    }

    @Override
    @Nullable
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return null;
    }
}

