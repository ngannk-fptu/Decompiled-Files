/*
 * Decompiled with CFR 0.152.
 */
package org.aopalliance.intercept;

import org.aopalliance.intercept.ConstructorInvocation;
import org.aopalliance.intercept.Interceptor;

public interface ConstructorInterceptor
extends Interceptor {
    public Object construct(ConstructorInvocation var1) throws Throwable;
}

