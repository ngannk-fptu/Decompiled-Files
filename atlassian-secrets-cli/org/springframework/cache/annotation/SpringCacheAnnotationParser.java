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
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

public class SpringCacheAnnotationParser
implements CacheAnnotationParser,
Serializable {
    @Override
    @Nullable
    public Collection<CacheOperation> parseCacheAnnotations(Class<?> type) {
        DefaultCacheConfig defaultConfig = this.getDefaultCacheConfig(type);
        return this.parseCacheAnnotations(defaultConfig, type);
    }

    @Override
    @Nullable
    public Collection<CacheOperation> parseCacheAnnotations(Method method) {
        DefaultCacheConfig defaultConfig = this.getDefaultCacheConfig(method.getDeclaringClass());
        return this.parseCacheAnnotations(defaultConfig, method);
    }

    @Nullable
    private Collection<CacheOperation> parseCacheAnnotations(DefaultCacheConfig cachingConfig, AnnotatedElement ae) {
        Collection<CacheOperation> localOps;
        Collection<CacheOperation> ops = this.parseCacheAnnotations(cachingConfig, ae, false);
        if (ops != null && ops.size() > 1 && ae.getAnnotations().length > 0 && (localOps = this.parseCacheAnnotations(cachingConfig, ae, true)) != null) {
            return localOps;
        }
        return ops;
    }

    @Nullable
    private Collection<CacheOperation> parseCacheAnnotations(DefaultCacheConfig cachingConfig, AnnotatedElement ae, boolean localOnly) {
        Set<Caching> set;
        Set<CachePut> set2;
        Set<CacheEvict> evicts;
        Set<Cacheable> cacheables;
        Collection<CacheOperation> ops = null;
        Set<Cacheable> set3 = cacheables = localOnly ? AnnotatedElementUtils.getAllMergedAnnotations(ae, Cacheable.class) : AnnotatedElementUtils.findAllMergedAnnotations(ae, Cacheable.class);
        if (!cacheables.isEmpty()) {
            ops = this.lazyInit(null);
            for (Cacheable cacheable : cacheables) {
                ops.add(this.parseCacheableAnnotation(ae, cachingConfig, cacheable));
            }
        }
        Set<CacheEvict> set4 = evicts = localOnly ? AnnotatedElementUtils.getAllMergedAnnotations(ae, CacheEvict.class) : AnnotatedElementUtils.findAllMergedAnnotations(ae, CacheEvict.class);
        if (!evicts.isEmpty()) {
            ops = this.lazyInit(ops);
            for (CacheEvict cacheEvict : evicts) {
                ops.add(this.parseEvictAnnotation(ae, cachingConfig, cacheEvict));
            }
        }
        Set<CachePut> set5 = set2 = localOnly ? AnnotatedElementUtils.getAllMergedAnnotations(ae, CachePut.class) : AnnotatedElementUtils.findAllMergedAnnotations(ae, CachePut.class);
        if (!set2.isEmpty()) {
            ops = this.lazyInit(ops);
            for (CachePut put : set2) {
                ops.add(this.parsePutAnnotation(ae, cachingConfig, put));
            }
        }
        Set<Caching> set6 = set = localOnly ? AnnotatedElementUtils.getAllMergedAnnotations(ae, Caching.class) : AnnotatedElementUtils.findAllMergedAnnotations(ae, Caching.class);
        if (!set.isEmpty()) {
            ops = this.lazyInit(ops);
            for (Caching caching : set) {
                Collection<CacheOperation> cachingOps = this.parseCachingAnnotation(ae, cachingConfig, caching);
                if (cachingOps == null) continue;
                ops.addAll(cachingOps);
            }
        }
        return ops;
    }

    private <T extends Annotation> Collection<CacheOperation> lazyInit(@Nullable Collection<CacheOperation> ops) {
        return ops != null ? ops : new ArrayList(1);
    }

    CacheableOperation parseCacheableAnnotation(AnnotatedElement ae, DefaultCacheConfig defaultConfig, Cacheable cacheable) {
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

    CacheEvictOperation parseEvictAnnotation(AnnotatedElement ae, DefaultCacheConfig defaultConfig, CacheEvict cacheEvict) {
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

    CacheOperation parsePutAnnotation(AnnotatedElement ae, DefaultCacheConfig defaultConfig, CachePut cachePut) {
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

    @Nullable
    Collection<CacheOperation> parseCachingAnnotation(AnnotatedElement ae, DefaultCacheConfig defaultConfig, Caching caching) {
        Object[] cachePuts;
        Object[] cacheEvicts;
        Collection<CacheOperation> ops = null;
        Object[] cacheables = caching.cacheable();
        if (!ObjectUtils.isEmpty(cacheables)) {
            ops = this.lazyInit(null);
            for (Object cacheable : cacheables) {
                ops.add(this.parseCacheableAnnotation(ae, defaultConfig, (Cacheable)cacheable));
            }
        }
        if (!ObjectUtils.isEmpty(cacheEvicts = caching.evict())) {
            ops = this.lazyInit(ops);
            for (Object cacheEvict : cacheEvicts) {
                ops.add(this.parseEvictAnnotation(ae, defaultConfig, (CacheEvict)cacheEvict));
            }
        }
        if (!ObjectUtils.isEmpty(cachePuts = caching.put())) {
            ops = this.lazyInit(ops);
            for (Object cachePut : cachePuts) {
                ops.add(this.parsePutAnnotation(ae, defaultConfig, (CachePut)cachePut));
            }
        }
        return ops;
    }

    DefaultCacheConfig getDefaultCacheConfig(Class<?> target) {
        CacheConfig annotation = AnnotatedElementUtils.findMergedAnnotation(target, CacheConfig.class);
        if (annotation != null) {
            return new DefaultCacheConfig(annotation.cacheNames(), annotation.keyGenerator(), annotation.cacheManager(), annotation.cacheResolver());
        }
        return new DefaultCacheConfig();
    }

    private void validateCacheOperation(AnnotatedElement ae, CacheOperation operation) {
        if (StringUtils.hasText(operation.getKey()) && StringUtils.hasText(operation.getKeyGenerator())) {
            throw new IllegalStateException("Invalid cache annotation configuration on '" + ae.toString() + "'. Both 'key' and 'keyGenerator' attributes have been set. These attributes are mutually exclusive: either set the SpEL expression used tocompute the key at runtime or set the name of the KeyGenerator bean to use.");
        }
        if (StringUtils.hasText(operation.getCacheManager()) && StringUtils.hasText(operation.getCacheResolver())) {
            throw new IllegalStateException("Invalid cache annotation configuration on '" + ae.toString() + "'. Both 'cacheManager' and 'cacheResolver' attributes have been set. These attributes are mutually exclusive: the cache manager is used to configure adefault cache resolver if none is set. If a cache resolver is set, the cache managerwon't be used.");
        }
    }

    public boolean equals(Object other) {
        return this == other || other instanceof SpringCacheAnnotationParser;
    }

    public int hashCode() {
        return SpringCacheAnnotationParser.class.hashCode();
    }

    static class DefaultCacheConfig {
        @Nullable
        private final String[] cacheNames;
        @Nullable
        private final String keyGenerator;
        @Nullable
        private final String cacheManager;
        @Nullable
        private final String cacheResolver;

        public DefaultCacheConfig() {
            this(null, null, null, null);
        }

        private DefaultCacheConfig(@Nullable String[] cacheNames, @Nullable String keyGenerator, @Nullable String cacheManager, @Nullable String cacheResolver) {
            this.cacheNames = cacheNames;
            this.keyGenerator = keyGenerator;
            this.cacheManager = cacheManager;
            this.cacheResolver = cacheResolver;
        }

        public void applyDefault(CacheOperation.Builder builder) {
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

