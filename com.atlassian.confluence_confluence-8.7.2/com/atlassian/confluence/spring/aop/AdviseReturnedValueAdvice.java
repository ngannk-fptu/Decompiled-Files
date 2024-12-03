/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.aopalliance.intercept.MethodInterceptor
 *  org.aopalliance.intercept.MethodInvocation
 *  org.springframework.aop.Advisor
 *  org.springframework.aop.MethodMatcher
 */
package com.atlassian.confluence.spring.aop;

import com.atlassian.confluence.util.AopUtils;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.Advisor;
import org.springframework.aop.MethodMatcher;

public final class AdviseReturnedValueAdvice
implements MethodInterceptor {
    private Advisor returnValueAdvisor;
    private Class proxyInterface;
    private MethodMatcher invokedMethodMatcher;

    public void setInvokedMethodMatcher(MethodMatcher invokedMethodMatcher) {
        this.invokedMethodMatcher = invokedMethodMatcher;
    }

    public void setReturnValueAdvisor(Advisor advisor) {
        this.returnValueAdvisor = advisor;
    }

    public void setProxyInterface(Class proxyInterface) {
        this.proxyInterface = proxyInterface;
    }

    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        Object targetReturnValue = methodInvocation.proceed();
        if (this.invocationMatches(methodInvocation)) {
            if (this.proxyInterface != null) {
                return AopUtils.createAdvisedProxy(targetReturnValue, this.returnValueAdvisor, this.proxyInterface);
            }
            return AopUtils.createAdvisedDynamicProxy(targetReturnValue, this.returnValueAdvisor);
        }
        return targetReturnValue;
    }

    private boolean invocationMatches(MethodInvocation methodInvocation) {
        return this.invokedMethodMatcher == null || this.invokedMethodMatcher.matches(methodInvocation.getMethod(), methodInvocation.getMethod().getDeclaringClass());
    }
}

