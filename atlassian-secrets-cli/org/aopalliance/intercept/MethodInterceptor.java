/*
 * Decompiled with CFR 0.152.
 */
package org.aopalliance.intercept;

import org.aopalliance.intercept.Interceptor;
import org.aopalliance.intercept.MethodInvocation;

@FunctionalInterface
public interface MethodInterceptor
extends Interceptor {
    public Object invoke(MethodInvocation var1) throws Throwable;
}

