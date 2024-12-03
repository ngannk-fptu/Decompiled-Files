/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.aop.interceptor;

import java.lang.reflect.Method;

@FunctionalInterface
public interface AsyncUncaughtExceptionHandler {
    public void handleUncaughtException(Throwable var1, Method var2, Object ... var3);
}

