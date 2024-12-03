/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.aopalliance.intercept.MethodInvocation
 *  org.aspectj.lang.JoinPoint
 *  org.aspectj.lang.ProceedingJoinPoint
 *  org.aspectj.lang.reflect.CodeSignature
 *  org.springframework.util.Assert
 */
package org.springframework.security.access.intercept.aspectj;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import org.aopalliance.intercept.MethodInvocation;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.CodeSignature;
import org.springframework.util.Assert;

@Deprecated
public final class MethodInvocationAdapter
implements MethodInvocation {
    private final ProceedingJoinPoint jp;
    private final Method method;
    private final Object target;

    MethodInvocationAdapter(JoinPoint jp) {
        this.jp = (ProceedingJoinPoint)jp;
        this.target = jp.getTarget() != null ? jp.getTarget() : jp.getSignature().getDeclaringType();
        String targetMethodName = jp.getStaticPart().getSignature().getName();
        Class[] types = ((CodeSignature)jp.getStaticPart().getSignature()).getParameterTypes();
        Class declaringType = jp.getStaticPart().getSignature().getDeclaringType();
        this.method = this.findMethod(targetMethodName, declaringType, types);
        Assert.notNull((Object)this.method, () -> "Could not obtain target method from JoinPoint: '" + jp + "'");
    }

    private Method findMethod(String name, Class<?> declaringType, Class<?>[] params) {
        Method method = null;
        try {
            method = declaringType.getMethod(name, params);
        }
        catch (NoSuchMethodException noSuchMethodException) {
            // empty catch block
        }
        if (method == null) {
            try {
                method = declaringType.getDeclaredMethod(name, params);
            }
            catch (NoSuchMethodException noSuchMethodException) {
                // empty catch block
            }
        }
        return method;
    }

    public Method getMethod() {
        return this.method;
    }

    public Object[] getArguments() {
        return this.jp.getArgs();
    }

    public AccessibleObject getStaticPart() {
        return this.method;
    }

    public Object getThis() {
        return this.target;
    }

    public Object proceed() throws Throwable {
        return this.jp.proceed();
    }
}

