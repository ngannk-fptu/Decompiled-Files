/*
 * Decompiled with CFR 0.152.
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

public class AspectJAfterAdvice
extends AbstractAspectJAdvice
implements MethodInterceptor,
AfterAdvice,
Serializable {
    public AspectJAfterAdvice(Method aspectJBeforeAdviceMethod, AspectJExpressionPointcut pointcut, AspectInstanceFactory aif) {
        super(aspectJBeforeAdviceMethod, pointcut, aif);
    }

    @Override
    public Object invoke(MethodInvocation mi) throws Throwable {
        try {
            Object object = mi.proceed();
            return object;
        }
        finally {
            this.invokeAdviceMethod(this.getJoinPointMatch(), null, null);
        }
    }

    @Override
    public boolean isBeforeAdvice() {
        return false;
    }

    @Override
    public boolean isAfterAdvice() {
        return true;
    }
}

