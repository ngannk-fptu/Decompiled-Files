/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.aop.interceptor.AsyncExecutionInterceptor
 *  org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler
 *  org.springframework.core.annotation.AnnotatedElementUtils
 *  org.springframework.lang.Nullable
 */
package org.springframework.scheduling.annotation;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.concurrent.Executor;
import org.springframework.aop.interceptor.AsyncExecutionInterceptor;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Async;

public class AnnotationAsyncExecutionInterceptor
extends AsyncExecutionInterceptor {
    public AnnotationAsyncExecutionInterceptor(@Nullable Executor defaultExecutor) {
        super(defaultExecutor);
    }

    public AnnotationAsyncExecutionInterceptor(@Nullable Executor defaultExecutor, AsyncUncaughtExceptionHandler exceptionHandler) {
        super(defaultExecutor, exceptionHandler);
    }

    @Nullable
    protected String getExecutorQualifier(Method method) {
        Async async = (Async)AnnotatedElementUtils.findMergedAnnotation((AnnotatedElement)method, Async.class);
        if (async == null) {
            async = (Async)AnnotatedElementUtils.findMergedAnnotation(method.getDeclaringClass(), Async.class);
        }
        return async != null ? async.value() : null;
    }
}

