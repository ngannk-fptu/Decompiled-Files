/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.aop.framework.adapter;

import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.Advisor;

public interface AdvisorAdapter {
    public boolean supportsAdvice(Advice var1);

    public MethodInterceptor getInterceptor(Advisor var1);
}

