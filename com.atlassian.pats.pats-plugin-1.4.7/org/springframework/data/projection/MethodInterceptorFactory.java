/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.aopalliance.intercept.MethodInterceptor
 */
package org.springframework.data.projection;

import org.aopalliance.intercept.MethodInterceptor;

public interface MethodInterceptorFactory {
    public MethodInterceptor createMethodInterceptor(Object var1, Class<?> var2);

    public boolean supports(Object var1, Class<?> var2);
}

