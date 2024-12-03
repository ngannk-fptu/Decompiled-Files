/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.springframework.cache.jcache.interceptor;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.cache.jcache.interceptor.JCacheOperation;
import org.springframework.cache.jcache.interceptor.JCacheOperationSource;
import org.springframework.core.MethodClassKey;
import org.springframework.lang.Nullable;

public abstract class AbstractFallbackJCacheOperationSource
implements JCacheOperationSource {
    private static final Object NULL_CACHING_ATTRIBUTE = new Object();
    protected final Log logger = LogFactory.getLog(this.getClass());
    private final Map<MethodClassKey, Object> cache = new ConcurrentHashMap<MethodClassKey, Object>(1024);

    @Override
    public JCacheOperation<?> getCacheOperation(Method method, @Nullable Class<?> targetClass) {
        MethodClassKey cacheKey = new MethodClassKey(method, targetClass);
        Object cached = this.cache.get(cacheKey);
        if (cached != null) {
            return cached != NULL_CACHING_ATTRIBUTE ? (JCacheOperation)cached : null;
        }
        JCacheOperation<?> operation = this.computeCacheOperation(method, targetClass);
        if (operation != null) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)("Adding cacheable method '" + method.getName() + "' with operation: " + operation));
            }
            this.cache.put(cacheKey, operation);
        } else {
            this.cache.put(cacheKey, NULL_CACHING_ATTRIBUTE);
        }
        return operation;
    }

    @Nullable
    private JCacheOperation<?> computeCacheOperation(Method method, @Nullable Class<?> targetClass) {
        if (this.allowPublicMethodsOnly() && !Modifier.isPublic(method.getModifiers())) {
            return null;
        }
        Method specificMethod = AopUtils.getMostSpecificMethod(method, targetClass);
        JCacheOperation<?> operation = this.findCacheOperation(specificMethod, targetClass);
        if (operation != null) {
            return operation;
        }
        if (specificMethod != method && (operation = this.findCacheOperation(method, targetClass)) != null) {
            return operation;
        }
        return null;
    }

    @Nullable
    protected abstract JCacheOperation<?> findCacheOperation(Method var1, @Nullable Class<?> var2);

    protected boolean allowPublicMethodsOnly() {
        return false;
    }
}

