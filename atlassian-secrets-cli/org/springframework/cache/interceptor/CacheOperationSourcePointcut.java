/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.cache.interceptor;

import java.io.Serializable;
import java.lang.reflect.Method;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.cache.interceptor.CacheOperationSource;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

abstract class CacheOperationSourcePointcut
extends StaticMethodMatcherPointcut
implements Serializable {
    CacheOperationSourcePointcut() {
    }

    @Override
    public boolean matches(Method method, @Nullable Class<?> targetClass) {
        CacheOperationSource cas = this.getCacheOperationSource();
        return cas != null && !CollectionUtils.isEmpty(cas.getCacheOperations(method, targetClass));
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof CacheOperationSourcePointcut)) {
            return false;
        }
        CacheOperationSourcePointcut otherPc = (CacheOperationSourcePointcut)other;
        return ObjectUtils.nullSafeEquals(this.getCacheOperationSource(), otherPc.getCacheOperationSource());
    }

    public int hashCode() {
        return CacheOperationSourcePointcut.class.hashCode();
    }

    public String toString() {
        return this.getClass().getName() + ": " + this.getCacheOperationSource();
    }

    @Nullable
    protected abstract CacheOperationSource getCacheOperationSource();
}

