/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.aop.support.StaticMethodMatcherPointcut
 *  org.springframework.lang.Nullable
 *  org.springframework.util.ObjectUtils
 */
package org.springframework.cache.jcache.interceptor;

import java.io.Serializable;
import java.lang.reflect.Method;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.cache.jcache.interceptor.JCacheOperationSource;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

public abstract class JCacheOperationSourcePointcut
extends StaticMethodMatcherPointcut
implements Serializable {
    public boolean matches(Method method, Class<?> targetClass) {
        JCacheOperationSource cas = this.getCacheOperationSource();
        return cas != null && cas.getCacheOperation(method, targetClass) != null;
    }

    @Nullable
    protected abstract JCacheOperationSource getCacheOperationSource();

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof JCacheOperationSourcePointcut)) {
            return false;
        }
        JCacheOperationSourcePointcut otherPc = (JCacheOperationSourcePointcut)other;
        return ObjectUtils.nullSafeEquals((Object)this.getCacheOperationSource(), (Object)otherPc.getCacheOperationSource());
    }

    public int hashCode() {
        return JCacheOperationSourcePointcut.class.hashCode();
    }

    public String toString() {
        return this.getClass().getName() + ": " + this.getCacheOperationSource();
    }
}

