/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.TypeUtils
 */
package org.springframework.aop.aspectj;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import org.springframework.aop.AfterAdvice;
import org.springframework.aop.AfterReturningAdvice;
import org.springframework.aop.aspectj.AbstractAspectJAdvice;
import org.springframework.aop.aspectj.AspectInstanceFactory;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.TypeUtils;

public class AspectJAfterReturningAdvice
extends AbstractAspectJAdvice
implements AfterReturningAdvice,
AfterAdvice,
Serializable {
    public AspectJAfterReturningAdvice(Method aspectJBeforeAdviceMethod, AspectJExpressionPointcut pointcut, AspectInstanceFactory aif) {
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
    public void setReturningName(String name) {
        this.setReturningNameNoCheck(name);
    }

    @Override
    public void afterReturning(@Nullable Object returnValue, Method method, Object[] args, @Nullable Object target) throws Throwable {
        if (this.shouldInvokeOnReturnValueOf(method, returnValue)) {
            this.invokeAdviceMethod(this.getJoinPointMatch(), returnValue, null);
        }
    }

    private boolean shouldInvokeOnReturnValueOf(Method method, @Nullable Object returnValue) {
        Class<?> type = this.getDiscoveredReturningType();
        Type genericType = this.getDiscoveredReturningGenericType();
        return this.matchesReturnValue(type, method, returnValue) && (genericType == null || genericType == type || TypeUtils.isAssignable((Type)genericType, (Type)method.getGenericReturnType()));
    }

    private boolean matchesReturnValue(Class<?> type, Method method, @Nullable Object returnValue) {
        if (returnValue != null) {
            return ClassUtils.isAssignableValue(type, (Object)returnValue);
        }
        if (Object.class == type && Void.TYPE == method.getReturnType()) {
            return true;
        }
        return ClassUtils.isAssignable(type, method.getReturnType());
    }
}

