/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package org.aopalliance.intercept;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.aopalliance.intercept.Interceptor;
import org.aopalliance.intercept.MethodInvocation;

@FunctionalInterface
public interface MethodInterceptor
extends Interceptor {
    @Nullable
    public Object invoke(@Nonnull MethodInvocation var1) throws Throwable;
}

