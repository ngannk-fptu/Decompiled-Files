/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.cache.annotation;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import org.springframework.cache.annotation.CacheAnnotationParser;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.cache.interceptor.CacheEvictOperation;
import org.springframework.cache.interceptor.CacheOperation;
import org.springframework.cache.interceptor.CachePutOperation;
import org.springframework.cache.interceptor.CacheableOperation;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

public class SpringCacheAnnotationParser
implements CacheAnnotationParser,
Serializable {
    private static final Set<Class<? extends Annotation>> CACHE_OPERATION_ANNOTATIONS = new LinkedHashSet<Class<? extends Annotation>>(8);

    @Override
    public boolean isCandidateClass(Class<?> targetClass) {
        return AnnotationUtils.isCandidateClass(targetClass, CACHE_OPERATION_ANNOTATIONS);
    }

    @Override
    @Nullable
    public Collection<CacheOperation> parseCacheAnnotations(Class<?> type) {
        DefaultCacheConfig defaultConfig = new DefaultCacheConfig(type);
        return this.parseCacheAnnotations(defaultConfig, type);
    }

    @Override
    @Nullable
    public Collection<CacheOperation> parseCacheAnnotations(Method method) {
        DefaultCacheConfig defaultConfig = new DefaultCacheConfig(method.getDeclaringClass());
        return this.parseCacheAnnotations(defaultConfig, method);
    }

    @Nullable
    private Collection<CacheOperation> parseCacheAnnotations(DefaultCacheConfig cachingConfig, AnnotatedElement ae) {
        Collection<CacheOperation> localOps;
        Collection<CacheOperation> ops = this.parseCacheAnnotations(cachingConfig, ae, false);
        if (ops != null && ops.size() > 1 && (localOps = this.parseCacheAnnotations(cachingConfig, ae, true)) != null) {
            return localOps;
        }
        return ops;
    }

    @Nullable
    private Collection<CacheOperation> parseCacheAnnotations(DefaultCacheConfig cachingConfig, AnnotatedElement ae, boolean localOnly) {
        Set<Annotation> anns;
        Set<Annotation> set = anns = localOnly ? AnnotatedElementUtils.getAllMergedAnnotations(ae, CACHE_OPERATION_ANNOTATIONS) : AnnotatedElementUtils.findAllMergedAnnotations(ae, CACHE_OPERATION_ANNOTATIONS);
        if (anns.isEmpty()) {
            return null;
        }
        ArrayList<CacheOperation> ops = new ArrayList<CacheOperation>(1);
        anns.stream().filter(ann -> ann instanceof Cacheable).forEach(ann -> ops.add(this.parseCacheableAnnotation(ae, cachingConfig, (Cacheable)ann)));
        anns.stream().filter(ann -> ann instanceof CacheEvict).forEach(ann -> ops.add(this.parseEvictAnnotation(ae, cachingConfig, (CacheEvict)ann)));
        anns.stream().filter(ann -> ann instanceof CachePut).forEach(ann -> ops.add(this.parsePutAnnotation(ae, cachingConfig, (CachePut)ann)));
        anns.stream().filter(ann -> ann instanceof Caching).forEach(ann -> this.parseCachingAnnotation(ae, cachingConfig, (Caching)ann, ops));
        return ops;
    }

    private CacheableOperation parseCacheableAnnotation(AnnotatedElement ae, DefaultCacheConfig defaultConfig, Cacheable cacheable) {
        CacheableOperation.Builder builder = new CacheableOperation.Builder();
        builder.setName(ae.toString());
        builder.setCacheNames(cacheable.cacheNames());
        builder.setCondition(cacheable.condition());
        builder.setUnless(cacheable.unless());
        builder.setKey(cacheable.key());
        builder.setKeyGenerator(cacheable.keyGenerator());
        builder.setCacheManager(cacheable.cacheManager());
        builder.setCacheResolver(cacheable.cacheResolver());
        builder.setSync(cacheable.sync());
        defaultConfig.applyDefault(builder);
        CacheableOperation op = builder.build();
        this.validateCacheOperation(ae, op);
        return op;
    }

    private CacheEvictOperation parseEvictAnnotation(AnnotatedElement ae, DefaultCacheConfig defaultConfig, CacheEvict cacheEvict) {
        CacheEvictOperation.Builder builder = new CacheEvictOperation.Builder();
        builder.setName(ae.toString());
        builder.setCacheNames(cacheEvict.cacheNames());
        builder.setCondition(cacheEvict.condition());
        builder.setKey(cacheEvict.key());
        builder.setKeyGenerator(cacheEvict.keyGenerator());
        builder.setCacheManager(cacheEvict.cacheManager());
        builder.setCacheResolver(cacheEvict.cacheResolver());
        builder.setCacheWide(cacheEvict.allEntries());
        builder.setBeforeInvocation(cacheEvict.beforeInvocation());
        defaultConfig.applyDefault(builder);
        CacheEvictOperation op = builder.build();
        this.validateCacheOperation(ae, op);
        return op;
    }

    private CacheOperation parsePutAnnotation(AnnotatedElement ae, DefaultCacheConfig defaultConfig, CachePut cachePut) {
        CachePutOperation.Builder builder = new CachePutOperation.Builder();
        builder.setName(ae.toString());
        builder.setCacheNames(cachePut.cacheNames());
        builder.setCondition(cachePut.condition());
        builder.setUnless(cachePut.unless());
        builder.setKey(cachePut.key());
        builder.setKeyGenerator(cachePut.keyGenerator());
        builder.setCacheManager(cachePut.cacheManager());
        builder.setCacheResolver(cachePut.cacheResolver());
        defaultConfig.applyDefault(builder);
        CachePutOperation op = builder.build();
        this.validateCacheOperation(ae, op);
        return op;
    }

    private void parseCachingAnnotation(AnnotatedElement ae, DefaultCacheConfig defaultConfig, Caching caching, Collection<CacheOperation> ops) {
        CachePut[] cachePuts;
        CacheEvict[] cacheEvicts;
        Cacheable[] cacheables;
        for (Cacheable cacheable : cacheables = caching.cacheable()) {
            ops.add(this.parseCacheableAnnotation(ae, defaultConfig, cacheable));
        }
        for (CacheEvict cacheEvict : cacheEvicts = caching.evict()) {
            ops.add(this.parseEvictAnnotation(ae, defaultConfig, cacheEvict));
        }
        for (CachePut cachePut : cachePuts = caching.put()) {
            ops.add(this.parsePutAnnotation(ae, defaultConfig, cachePut));
        }
    }

    private void validateCacheOperation(AnnotatedElement ae, CacheOperation operation) {
        if (StringUtils.hasText(operation.getKey()) && StringUtils.hasText(operation.getKeyGenerator())) {
            throw new IllegalStateException("Invalid cache annotation configuration on '" + ae.toString() + "'. Both 'key' and 'keyGenerator' attributes have been set. These attributes are mutually exclusive: either set the SpEL expression used tocompute the key at runtime or set the name of the KeyGenerator bean to use.");
        }
        if (StringUtils.hasText(operation.getCacheManager()) && StringUtils.hasText(operation.getCacheResolver())) {
            throw new IllegalStateException("Invalid cache annotation configuration on '" + ae.toString() + "'. Both 'cacheManager' and 'cacheResolver' attributes have been set. These attributes are mutually exclusive: the cache manager is used to configure adefault cache resolver if none is set. If a cache resolver is set, the cache managerwon't be used.");
        }
    }

    public boolean equals(@Nullable Object other) {
        return other instanceof SpringCacheAnnotationParser;
    }

    public int hashCode() {
        return SpringCacheAnnotationParser.class.hashCode();
    }

    static {
        CACHE_OPERATION_ANNOTATIONS.add(Cacheable.class);
        CACHE_OPERATION_ANNOTATIONS.add(CacheEvict.class);
        CACHE_OPERATION_ANNOTATIONS.add(CachePut.class);
        CACHE_OPERATION_ANNOTATIONS.add(Caching.class);
    }

    private static class DefaultCacheConfig {
        private final Class<?> target;
        @Nullable
        private String[] cacheNames;
        @Nullable
        private String keyGenerator;
        @Nullable
        private String cacheManager;
        @Nullable
        private String cacheResolver;
        private boolean initialized = false;

        public DefaultCacheConfig(Class<?> target) {
            this.target = target;
        }

        public void applyDefault(CacheOperation.Builder builder) {
            if (!this.initialized) {
                CacheConfig annotation = AnnotatedElementUtils.findMergedAnnotation(this.target, CacheConfig.class);
                if (annotation != null) {
                    this.cacheNames = annotation.cacheNames();
                    this.keyGenerator = annotation.keyGenerator();
                    this.cacheManager = annotation.cacheManager();
                    this.cacheResolver = annotation.cacheResolver();
                }
                this.initialized = true;
            }
            if (builder.getCacheNames().isEmpty() && this.cacheNames != null) {
                builder.setCacheNames(this.cacheNames);
            }
            if (!StringUtils.hasText(builder.getKey()) && !StringUtils.hasText(builder.getKeyGenerator()) && StringUtils.hasText(this.keyGenerator)) {
                builder.setKeyGenerator(this.keyGenerator);
            }
            if (!StringUtils.hasText(builder.getCacheManager()) && !StringUtils.hasText(builder.getCacheResolver())) {
                if (StringUtils.hasText(this.cacheResolver)) {
                    builder.setCacheResolver(this.cacheResolver);
                } else if (StringUtils.hasText(this.cacheManager)) {
                    builder.setCacheManager(this.cacheManager);
                }
            }
        }
    }
}

