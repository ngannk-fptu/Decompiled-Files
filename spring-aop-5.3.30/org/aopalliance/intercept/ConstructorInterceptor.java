/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package org.aopalliance.intercept;

import javax.annotation.Nonnull;
import org.aopalliance.intercept.ConstructorInvocation;
import org.aopalliance.intercept.Interceptor;

public interface ConstructorInterceptor
extends Interceptor {
    @Nonnull
    public Object construct(ConstructorInvocation var1) throws Throwable;
}

