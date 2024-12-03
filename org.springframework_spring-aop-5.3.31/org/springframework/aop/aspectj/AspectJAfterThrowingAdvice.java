/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.aop.aspectj;

import java.io.Serializable;
import java.lang.reflect.Method;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.AfterAdvice;
import org.springframework.aop.aspectj.AbstractAspectJAdvice;
import org.springframework.aop.aspectj.AspectInstanceFactory;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.lang.Nullable;

public class AspectJAfterThrowingAdvice
extends AbstractAspectJAdvice
implements MethodInterceptor,
AfterAdvice,
Serializable {
    public AspectJAfterThrowingAdvice(Method aspectJBeforeAdviceMethod, AspectJExpressionPointcut pointcut, AspectInstanceFactory aif) {
        super(aspectJBeforeAdviceMethod, pointcut, aif);
    }

    @Override
    public boolean isBeforeAdvice() {
        return false;
    }

    @Override
    public boolean isAfterAdvice() {
        return true;
    }

    @Override
    public void setThrowingName(String name) {
        this.setThrowingNameNoCheck(name);
    }

    @Override
    @Nullable
    public Object invoke(MethodInvocation mi) throws Throwable {
        try {
            return mi.proceed();
        }
        catch (Throwable ex) {
            if (this.shouldInvokeOnThrowing(ex)) {
                this.invokeAdviceMethod(this.getJoinPointMatch(), null, ex);
            }
            throw ex;
        }
    }

    private boolean shouldInvokeOnThrowing(Throwable ex) {
        return this.getDiscoveredThrowingType().isAssignableFrom(ex.getClass());
    }
}

