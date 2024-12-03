/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.aopalliance.intercept.MethodInterceptor
 *  org.aopalliance.intercept.MethodInvocation
 */
package org.springframework.security.access.intercept.aopalliance;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.access.SecurityMetadataSource;
import org.springframework.security.access.intercept.AbstractSecurityInterceptor;
import org.springframework.security.access.intercept.InterceptorStatusToken;
import org.springframework.security.access.method.MethodSecurityMetadataSource;

@Deprecated
public class MethodSecurityInterceptor
extends AbstractSecurityInterceptor
implements MethodInterceptor {
    private MethodSecurityMetadataSource securityMetadataSource;

    @Override
    public Class<?> getSecureObjectClass() {
        return MethodInvocation.class;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object invoke(MethodInvocation mi) throws Throwable {
        Object result;
        InterceptorStatusToken token = super.beforeInvocation(mi);
        try {
            result = mi.proceed();
        }
        finally {
            super.finallyInvocation(token);
        }
        return super.afterInvocation(token, result);
    }

    public MethodSecurityMetadataSource getSecurityMetadataSource() {
        return this.securityMetadataSource;
    }

    @Override
    public SecurityMetadataSource obtainSecurityMetadataSource() {
        return this.securityMetadataSource;
    }

    public void setSecurityMetadataSource(MethodSecurityMetadataSource newSource) {
        this.securityMetadataSource = newSource;
    }
}

