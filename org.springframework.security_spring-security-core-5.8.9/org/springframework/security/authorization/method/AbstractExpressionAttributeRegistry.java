/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.aopalliance.intercept.MethodInvocation
 *  org.springframework.core.MethodClassKey
 *  org.springframework.lang.NonNull
 */
package org.springframework.security.authorization.method;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.core.MethodClassKey;
import org.springframework.lang.NonNull;
import org.springframework.security.authorization.method.ExpressionAttribute;

abstract class AbstractExpressionAttributeRegistry<T extends ExpressionAttribute> {
    private final Map<MethodClassKey, T> cachedAttributes = new ConcurrentHashMap<MethodClassKey, T>();

    AbstractExpressionAttributeRegistry() {
    }

    final T getAttribute(MethodInvocation mi) {
        Method method = mi.getMethod();
        Object target = mi.getThis();
        Class<?> targetClass = target != null ? target.getClass() : null;
        return this.getAttribute(method, targetClass);
    }

    final T getAttribute(Method method, Class<?> targetClass) {
        MethodClassKey cacheKey = new MethodClassKey(method, targetClass);
        return (T)this.cachedAttributes.computeIfAbsent(cacheKey, k -> this.resolveAttribute(method, targetClass));
    }

    @NonNull
    abstract T resolveAttribute(Method var1, Class<?> var2);
}

