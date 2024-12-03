/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.aop;

import java.lang.reflect.Method;
import org.springframework.aop.BeforeAdvice;
import org.springframework.lang.Nullable;

public interface MethodBeforeAdvice
extends BeforeAdvice {
    public void before(Method var1, Object[] var2, @Nullable Object var3) throws Throwable;
}

