/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.aop.aspectj;

import java.io.Serializable;
import java.lang.reflect.Method;
import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.aop.aspectj.AbstractAspectJAdvice;
import org.springframework.aop.aspectj.AspectInstanceFactory;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.lang.Nullable;

public class AspectJMethodBeforeAdvice
extends AbstractAspectJAdvice
implements MethodBeforeAdvice,
Serializable {
    public AspectJMethodBeforeAdvice(Method aspectJBeforeAdviceMethod, AspectJExpressionPointcut pointcut, AspectInstanceFactory aif) {
        super(aspectJBeforeAdviceMethod, pointcut, aif);
    }

    @Override
    public void before(Method method, Object[] args, @Nullable Object target) throws Throwable {
        this.invokeAdviceMethod(this.getJoinPointMatch(), null, null);
    }

    @Override
    public boolean isBeforeAdvice() {
        return true;
    }

    @Override
    public boolean isAfterAdvice() {
        return false;
    }
}

