/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.aop.ClassFilter
 *  org.springframework.aop.support.StaticMethodMatcherPointcut
 *  org.springframework.lang.Nullable
 *  org.springframework.util.CollectionUtils
 *  org.springframework.util.ObjectUtils
 */
package org.springframework.cache.interceptor;

import java.io.Serializable;
import java.lang.reflect.Method;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.CacheOperationSource;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

abstract class CacheOperationSourcePointcut
extends StaticMethodMatcherPointcut
implements Serializable {
    protected CacheOperationSourcePointcut() {
        this.setClassFilter(new CacheOperationSourceClassFilter());
    }

    public boolean matches(Method method, Class<?> targetClass) {
        CacheOperationSource cas = this.getCacheOperationSource();
        return cas != null && !CollectionUtils.isEmpty(cas.getCacheOperations(method, targetClass));
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof CacheOperationSourcePointcut)) {
            return false;
        }
        CacheOperationSourcePointcut otherPc = (CacheOperationSourcePointcut)other;
        return ObjectUtils.nullSafeEquals((Object)this.getCacheOperationSource(), (Object)otherPc.getCacheOperationSource());
    }

    public int hashCode() {
        return CacheOperationSourcePointcut.class.hashCode();
    }

    public String toString() {
        return this.getClass().getName() + ": " + this.getCacheOperationSource();
    }

    @Nullable
    protected abstract CacheOperationSource getCacheOperationSource();

    private class CacheOperationSourceClassFilter
    implements ClassFilter {
        private CacheOperationSourceClassFilter() {
        }

        public boolean matches(Class<?> clazz) {
            if (CacheManager.class.isAssignableFrom(clazz)) {
                return false;
            }
            CacheOperationSource cas = CacheOperationSourcePointcut.this.getCacheOperationSource();
            return cas == null || cas.isCandidateClass(clazz);
        }
    }
}

