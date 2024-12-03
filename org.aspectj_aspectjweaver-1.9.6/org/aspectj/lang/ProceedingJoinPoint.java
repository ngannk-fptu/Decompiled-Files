/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.lang;

import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.internal.AroundClosure;

public interface ProceedingJoinPoint
extends JoinPoint {
    public void set$AroundClosure(AroundClosure var1);

    default public void stack$AroundClosure(AroundClosure arc) {
        throw new UnsupportedOperationException();
    }

    public Object proceed() throws Throwable;

    public Object proceed(Object[] var1) throws Throwable;
}

