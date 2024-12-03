/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.ConcurrencyThrottleSupport
 */
package org.springframework.aop.interceptor;

import java.io.Serializable;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.lang.Nullable;
import org.springframework.util.ConcurrencyThrottleSupport;

public class ConcurrencyThrottleInterceptor
extends ConcurrencyThrottleSupport
implements MethodInterceptor,
Serializable {
    public ConcurrencyThrottleInterceptor() {
        this.setConcurrencyLimit(1);
    }

    @Override
    @Nullable
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        this.beforeAccess();
        try {
            Object object = methodInvocation.proceed();
            return object;
        }
        finally {
            this.afterAccess();
        }
    }
}

