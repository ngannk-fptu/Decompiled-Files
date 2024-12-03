/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.annotation.CacheDefaults
 *  javax.cache.annotation.CacheKeyGenerator
 *  javax.cache.annotation.CacheMethodDetails
 *  javax.cache.annotation.CachePut
 *  javax.cache.annotation.CacheRemove
 *  javax.cache.annotation.CacheRemoveAll
 *  javax.cache.annotation.CacheResolver
 *  javax.cache.annotation.CacheResolverFactory
 *  javax.cache.annotation.CacheResult
 *  org.springframework.cache.interceptor.CacheResolver
 *  org.springframework.cache.interceptor.KeyGenerator
 *  org.springframework.lang.Nullable
 *  org.springframework.util.StringUtils
 */
package org.springframework.cache.jcache.interceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import javax.cache.annotation.CacheDefaults;
import javax.cache.annotation.CacheKeyGenerator;
import javax.cache.annotation.CacheMethodDetails;
import javax.cache.annotation.CachePut;
import javax.cache.annotation.CacheRemove;
import javax.cache.annotation.CacheRemoveAll;
import javax.cache.annotation.CacheResolver;
import javax.cache.annotation.CacheResolverFactory;
import javax.cache.annotation.CacheResult;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.jcache.interceptor.AbstractFallbackJCacheOperationSource;
import org.springframework.cache.jcache.interceptor.CachePutOperation;
import org.springframework.cache.jcache.interceptor.CacheRemoveAllOperation;
import org.springframework.cache.jcache.interceptor.CacheRemoveOperation;
import org.springframework.cache.jcache.interceptor.CacheResolverAdapter;
import org.springframework.cache.jcache.interceptor.CacheResultOperation;
import org.springframework.cache.jcache.interceptor.DefaultCacheMethodDetails;
import org.springframework.cache.jcache.interceptor.JCacheOperation;
import org.springframework.cache.jcache.interceptor.JCacheOperationSource;
import org.springframework.cache.jcache.interceptor.KeyGeneratorAdapter;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

public abstract class AnnotationJCacheOperationSource
extends AbstractFallbackJCacheOperationSource {
    @Override
    protected JCacheOperation<?> findCacheOperation(Method method, @Nullable Class<?> targetType) {
        CacheResult cacheResult = method.getAnnotation(CacheResult.class);
        CachePut cachePut = method.getAnnotation(CachePut.class);
        CacheRemove cacheRemove = method.getAnnotation(CacheRemove.class);
        CacheRemoveAll cacheRemoveAll = method.getAnnotation(CacheRemoveAll.class);
        int found = this.countNonNull(cacheResult, cachePut, cacheRemove, cacheRemoveAll);
        if (found == 0) {
            return null;
        }
        if (found > 1) {
            throw new IllegalStateException("More than one cache annotation found on '" + method + "'");
        }
        CacheDefaults defaults = this.getCacheDefaults(method, targetType);
        if (cacheResult != null) {
            return this.createCacheResultOperation(method, defaults, cacheResult);
        }
        if (cachePut != null) {
            return this.createCachePutOperation(method, defaults, cachePut);
        }
        if (cacheRemove != null) {
            return this.createCacheRemoveOperation(method, defaults, cacheRemove);
        }
        return this.createCacheRemoveAllOperation(method, defaults, cacheRemoveAll);
    }

    @Nullable
    protected CacheDefaults getCacheDefaults(Method method, @Nullable Class<?> targetType) {
        CacheDefaults annotation = method.getDeclaringClass().getAnnotation(CacheDefaults.class);
        if (annotation != null) {
            return annotation;
        }
        return targetType != null ? targetType.getAnnotation(CacheDefaults.class) : null;
    }

    protected CacheResultOperation createCacheResultOperation(Method method, @Nullable CacheDefaults defaults, CacheResult ann) {
        String cacheName = this.determineCacheName(method, defaults, ann.cacheName());
        CacheResolverFactory cacheResolverFactory = this.determineCacheResolverFactory(defaults, ann.cacheResolverFactory());
        KeyGenerator keyGenerator = this.determineKeyGenerator(defaults, ann.cacheKeyGenerator());
        CacheMethodDetails<CacheResult> methodDetails = this.createMethodDetails(method, ann, cacheName);
        org.springframework.cache.interceptor.CacheResolver cacheResolver = this.getCacheResolver(cacheResolverFactory, methodDetails);
        org.springframework.cache.interceptor.CacheResolver exceptionCacheResolver = null;
        String exceptionCacheName = ann.exceptionCacheName();
        if (StringUtils.hasText((String)exceptionCacheName)) {
            exceptionCacheResolver = this.getExceptionCacheResolver(cacheResolverFactory, methodDetails);
        }
        return new CacheResultOperation(methodDetails, cacheResolver, keyGenerator, exceptionCacheResolver);
    }

    protected CachePutOperation createCachePutOperation(Method method, @Nullable CacheDefaults defaults, CachePut ann) {
        String cacheName = this.determineCacheName(method, defaults, ann.cacheName());
        CacheResolverFactory cacheResolverFactory = this.determineCacheResolverFactory(defaults, ann.cacheResolverFactory());
        KeyGenerator keyGenerator = this.determineKeyGenerator(defaults, ann.cacheKeyGenerator());
        CacheMethodDetails<CachePut> methodDetails = this.createMethodDetails(method, ann, cacheName);
        org.springframework.cache.interceptor.CacheResolver cacheResolver = this.getCacheResolver(cacheResolverFactory, methodDetails);
        return new CachePutOperation(methodDetails, cacheResolver, keyGenerator);
    }

    protected CacheRemoveOperation createCacheRemoveOperation(Method method, @Nullable CacheDefaults defaults, CacheRemove ann) {
        String cacheName = this.determineCacheName(method, defaults, ann.cacheName());
        CacheResolverFactory cacheResolverFactory = this.determineCacheResolverFactory(defaults, ann.cacheResolverFactory());
        KeyGenerator keyGenerator = this.determineKeyGenerator(defaults, ann.cacheKeyGenerator());
        CacheMethodDetails<CacheRemove> methodDetails = this.createMethodDetails(method, ann, cacheName);
        org.springframework.cache.interceptor.CacheResolver cacheResolver = this.getCacheResolver(cacheResolverFactory, methodDetails);
        return new CacheRemoveOperation(methodDetails, cacheResolver, keyGenerator);
    }

    protected CacheRemoveAllOperation createCacheRemoveAllOperation(Method method, @Nullable CacheDefaults defaults, CacheRemoveAll ann) {
        String cacheName = this.determineCacheName(method, defaults, ann.cacheName());
        CacheResolverFactory cacheResolverFactory = this.determineCacheResolverFactory(defaults, ann.cacheResolverFactory());
        CacheMethodDetails<CacheRemoveAll> methodDetails = this.createMethodDetails(method, ann, cacheName);
        org.springframework.cache.interceptor.CacheResolver cacheResolver = this.getCacheResolver(cacheResolverFactory, methodDetails);
        return new CacheRemoveAllOperation(methodDetails, cacheResolver);
    }

    private <A extends Annotation> CacheMethodDetails<A> createMethodDetails(Method method, A annotation, String cacheName) {
        return new DefaultCacheMethodDetails<A>(method, annotation, cacheName);
    }

    protected org.springframework.cache.interceptor.CacheResolver getCacheResolver(@Nullable CacheResolverFactory factory, CacheMethodDetails<?> details) {
        if (factory != null) {
            CacheResolver cacheResolver = factory.getCacheResolver(details);
            return new CacheResolverAdapter(cacheResolver);
        }
        return this.getDefaultCacheResolver();
    }

    protected org.springframework.cache.interceptor.CacheResolver getExceptionCacheResolver(@Nullable CacheResolverFactory factory, CacheMethodDetails<CacheResult> details) {
        if (factory != null) {
            CacheResolver cacheResolver = factory.getExceptionCacheResolver(details);
            return new CacheResolverAdapter(cacheResolver);
        }
        return this.getDefaultExceptionCacheResolver();
    }

    @Nullable
    protected CacheResolverFactory determineCacheResolverFactory(@Nullable CacheDefaults defaults, Class<? extends CacheResolverFactory> candidate) {
        if (candidate != CacheResolverFactory.class) {
            return this.getBean(candidate);
        }
        if (defaults != null && defaults.cacheResolverFactory() != CacheResolverFactory.class) {
            return (CacheResolverFactory)this.getBean(defaults.cacheResolverFactory());
        }
        return null;
    }

    protected KeyGenerator determineKeyGenerator(@Nullable CacheDefaults defaults, Class<? extends CacheKeyGenerator> candidate) {
        if (candidate != CacheKeyGenerator.class) {
            return new KeyGeneratorAdapter((JCacheOperationSource)this, this.getBean(candidate));
        }
        if (defaults != null && CacheKeyGenerator.class != defaults.cacheKeyGenerator()) {
            return new KeyGeneratorAdapter((JCacheOperationSource)this, (CacheKeyGenerator)this.getBean(defaults.cacheKeyGenerator()));
        }
        return this.getDefaultKeyGenerator();
    }

    protected String determineCacheName(Method method, @Nullable CacheDefaults defaults, String candidate) {
        if (StringUtils.hasText((String)candidate)) {
            return candidate;
        }
        if (defaults != null && StringUtils.hasText((String)defaults.cacheName())) {
            return defaults.cacheName();
        }
        return this.generateDefaultCacheName(method);
    }

    protected String generateDefaultCacheName(Method method) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        ArrayList<String> parameters = new ArrayList<String>(parameterTypes.length);
        for (Class<?> parameterType : parameterTypes) {
            parameters.add(parameterType.getName());
        }
        return method.getDeclaringClass().getName() + '.' + method.getName() + '(' + StringUtils.collectionToCommaDelimitedString(parameters) + ')';
    }

    private int countNonNull(Object ... instances) {
        int result = 0;
        for (Object instance : instances) {
            if (instance == null) continue;
            ++result;
        }
        return result;
    }

    protected abstract <T> T getBean(Class<T> var1);

    protected abstract org.springframework.cache.interceptor.CacheResolver getDefaultCacheResolver();

    protected abstract org.springframework.cache.interceptor.CacheResolver getDefaultExceptionCacheResolver();

    protected abstract KeyGenerator getDefaultKeyGenerator();
}

