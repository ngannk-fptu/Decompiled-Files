/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.aop;

import java.lang.reflect.Method;
import org.springframework.aop.AfterAdvice;
import org.springframework.lang.Nullable;

public interface AfterReturningAdvice
extends AfterAdvice {
    public void afterReturning(@Nullable Object var1, Method var2, Object[] var3, @Nullable Object var4) throws Throwable;
}

