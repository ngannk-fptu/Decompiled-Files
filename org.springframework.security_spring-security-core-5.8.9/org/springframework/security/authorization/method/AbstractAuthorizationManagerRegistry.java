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
import org.springframework.security.authorization.AuthorizationManager;

abstract class AbstractAuthorizationManagerRegistry {
    static final AuthorizationManager<MethodInvocation> NULL_MANAGER = (a, o) -> null;
    private final Map<MethodClassKey, AuthorizationManager<MethodInvocation>> cachedManagers = new ConcurrentHashMap<MethodClassKey, AuthorizationManager<MethodInvocation>>();

    AbstractAuthorizationManagerRegistry() {
    }

    final AuthorizationManager<MethodInvocation> getManager(MethodInvocation methodInvocation) {
        Method method = methodInvocation.getMethod();
        Object target = methodInvocation.getThis();
        Class<?> targetClass = target != null ? target.getClass() : null;
        MethodClassKey cacheKey = new MethodClassKey(method, targetClass);
        return this.cachedManagers.computeIfAbsent(cacheKey, k -> this.resolveManager(method, targetClass));
    }

    @NonNull
    abstract AuthorizationManager<MethodInvocation> resolveManager(Method var1, Class<?> var2);
}

