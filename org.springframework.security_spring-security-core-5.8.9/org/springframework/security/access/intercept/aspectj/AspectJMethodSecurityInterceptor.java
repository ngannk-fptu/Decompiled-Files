/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.aspectj.lang.JoinPoint
 */
package org.springframework.security.access.intercept.aspectj;

import org.aspectj.lang.JoinPoint;
import org.springframework.security.access.intercept.InterceptorStatusToken;
import org.springframework.security.access.intercept.aopalliance.MethodSecurityInterceptor;
import org.springframework.security.access.intercept.aspectj.AspectJCallback;
import org.springframework.security.access.intercept.aspectj.MethodInvocationAdapter;

@Deprecated
public final class AspectJMethodSecurityInterceptor
extends MethodSecurityInterceptor {
    public Object invoke(JoinPoint jp) throws Throwable {
        return super.invoke(new MethodInvocationAdapter(jp));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object invoke(JoinPoint jp, AspectJCallback advisorProceed) {
        Object result;
        InterceptorStatusToken token = super.beforeInvocation(new MethodInvocationAdapter(jp));
        try {
            result = advisorProceed.proceedWithObject();
        }
        finally {
            super.finallyInvocation(token);
        }
        return super.afterInvocation(token, result);
    }
}

