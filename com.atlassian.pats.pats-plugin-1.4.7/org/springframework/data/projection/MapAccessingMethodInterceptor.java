/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.aopalliance.intercept.MethodInterceptor
 *  org.aopalliance.intercept.MethodInvocation
 *  org.springframework.util.Assert
 *  org.springframework.util.ReflectionUtils
 */
package org.springframework.data.projection;

import java.lang.reflect.Method;
import java.util.Map;
import javax.annotation.Nullable;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.data.projection.Accessor;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

class MapAccessingMethodInterceptor
implements MethodInterceptor {
    private final Map<String, Object> map;

    MapAccessingMethodInterceptor(Map<String, Object> map) {
        Assert.notNull(map, (String)"Map must not be null");
        this.map = map;
    }

    @Nullable
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        if (ReflectionUtils.isObjectMethod((Method)method)) {
            return invocation.proceed();
        }
        Accessor accessor = new Accessor(method);
        if (accessor.isGetter()) {
            return this.map.get(accessor.getPropertyName());
        }
        if (accessor.isSetter()) {
            this.map.put(accessor.getPropertyName(), invocation.getArguments()[0]);
            return null;
        }
        throw new IllegalStateException("Should never get here!");
    }
}

