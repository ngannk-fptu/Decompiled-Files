/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.cache.interceptor;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.cache.interceptor.CacheOperation;
import org.springframework.cache.interceptor.CacheOperationSource;
import org.springframework.core.MethodClassKey;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

public abstract class AbstractFallbackCacheOperationSource
implements CacheOperationSource {
    private static final Collection<CacheOperation> NULL_CACHING_ATTRIBUTE = Collections.emptyList();
    protected final Log logger = LogFactory.getLog(this.getClass());
    private final Map<Object, Collection<CacheOperation>> attributeCache = new ConcurrentHashMap<Object, Collection<CacheOperation>>(1024);

    @Override
    @Nullable
    public Collection<CacheOperation> getCacheOperations(Method method, @Nullable Class<?> targetClass) {
        if (method.getDeclaringClass() == Object.class) {
            return null;
        }
        Object cacheKey = this.getCacheKey(method, targetClass);
        Collection<CacheOperation> cached = this.attributeCache.get(cacheKey);
        if (cached != null) {
            return cached != NULL_CACHING_ATTRIBUTE ? cached : null;
        }
        Collection<CacheOperation> cacheOps = this.computeCacheOperations(method, targetClass);
        if (cacheOps != null) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Adding cacheable method '" + method.getName() + "' with attribute: " + cacheOps);
            }
            this.attributeCache.put(cacheKey, cacheOps);
        } else {
            this.attributeCache.put(cacheKey, NULL_CACHING_ATTRIBUTE);
        }
        return cacheOps;
    }

    protected Object getCacheKey(Method method, @Nullable Class<?> targetClass) {
        return new MethodClassKey(method, targetClass);
    }

    @Nullable
    private Collection<CacheOperation> computeCacheOperations(Method method, @Nullable Class<?> targetClass) {
        if (this.allowPublicMethodsOnly() && !Modifier.isPublic(method.getModifiers())) {
            return null;
        }
        Method specificMethod = AopUtils.getMostSpecificMethod(method, targetClass);
        Collection<CacheOperation> opDef = this.findCacheOperations(specificMethod);
        if (opDef != null) {
            return opDef;
        }
        opDef = this.findCacheOperations(specificMethod.getDeclaringClass());
        if (opDef != null && ClassUtils.isUserLevelMethod(method)) {
            return opDef;
        }
        if (specificMethod != method) {
            opDef = this.findCacheOperations(method);
            if (opDef != null) {
                return opDef;
            }
            opDef = this.findCacheOperations(method.getDeclaringClass());
            if (opDef != null && ClassUtils.isUserLevelMethod(method)) {
                return opDef;
            }
        }
        return null;
    }

    @Nullable
    protected abstract Collection<CacheOperation> findCacheOperations(Class<?> var1);

    @Nullable
    protected abstract Collection<CacheOperation> findCacheOperations(Method var1);

    protected boolean allowPublicMethodsOnly() {
        return false;
    }
}

