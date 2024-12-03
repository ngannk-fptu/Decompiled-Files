/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.annotation.CacheInvocationParameter
 *  javax.cache.annotation.CacheKeyGenerator
 *  javax.cache.annotation.CacheKeyInvocationContext
 */
package org.springframework.cache.jcache.interceptor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import javax.cache.annotation.CacheInvocationParameter;
import javax.cache.annotation.CacheKeyGenerator;
import javax.cache.annotation.CacheKeyInvocationContext;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.jcache.interceptor.AbstractJCacheKeyOperation;
import org.springframework.cache.jcache.interceptor.DefaultCacheKeyInvocationContext;
import org.springframework.cache.jcache.interceptor.JCacheOperation;
import org.springframework.cache.jcache.interceptor.JCacheOperationSource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

class KeyGeneratorAdapter
implements KeyGenerator {
    private final JCacheOperationSource cacheOperationSource;
    @Nullable
    private KeyGenerator keyGenerator;
    @Nullable
    private CacheKeyGenerator cacheKeyGenerator;

    public KeyGeneratorAdapter(JCacheOperationSource cacheOperationSource, KeyGenerator target) {
        Assert.notNull((Object)cacheOperationSource, "JCacheOperationSource must not be null");
        Assert.notNull((Object)target, "KeyGenerator must not be null");
        this.cacheOperationSource = cacheOperationSource;
        this.keyGenerator = target;
    }

    public KeyGeneratorAdapter(JCacheOperationSource cacheOperationSource, CacheKeyGenerator target) {
        Assert.notNull((Object)cacheOperationSource, "JCacheOperationSource must not be null");
        Assert.notNull((Object)target, "CacheKeyGenerator must not be null");
        this.cacheOperationSource = cacheOperationSource;
        this.cacheKeyGenerator = target;
    }

    public Object getTarget() {
        if (this.cacheKeyGenerator != null) {
            return this.cacheKeyGenerator;
        }
        Assert.state(this.keyGenerator != null, "No key generator");
        return this.keyGenerator;
    }

    @Override
    public Object generate(Object target, Method method, Object ... params) {
        JCacheOperation<?> operation = this.cacheOperationSource.getCacheOperation(method, target.getClass());
        if (!(operation instanceof AbstractJCacheKeyOperation)) {
            throw new IllegalStateException("Invalid operation, should be a key-based operation " + operation);
        }
        CacheKeyInvocationContext<?> invocationContext = this.createCacheKeyInvocationContext(target, operation, params);
        if (this.cacheKeyGenerator != null) {
            return this.cacheKeyGenerator.generateCacheKey(invocationContext);
        }
        Assert.state(this.keyGenerator != null, "No key generator");
        return KeyGeneratorAdapter.doGenerate(this.keyGenerator, invocationContext);
    }

    private static Object doGenerate(KeyGenerator keyGenerator, CacheKeyInvocationContext<?> context) {
        ArrayList<Object> parameters = new ArrayList<Object>();
        for (CacheInvocationParameter param : context.getKeyParameters()) {
            Object value = param.getValue();
            if (param.getParameterPosition() == context.getAllParameters().length - 1 && context.getMethod().isVarArgs()) {
                parameters.addAll(CollectionUtils.arrayToList(value));
                continue;
            }
            parameters.add(value);
        }
        return keyGenerator.generate(context.getTarget(), context.getMethod(), parameters.toArray());
    }

    private CacheKeyInvocationContext<?> createCacheKeyInvocationContext(Object target, JCacheOperation<?> operation, Object[] params) {
        AbstractJCacheKeyOperation keyCacheOperation = (AbstractJCacheKeyOperation)operation;
        return new DefaultCacheKeyInvocationContext(keyCacheOperation, target, params);
    }
}

