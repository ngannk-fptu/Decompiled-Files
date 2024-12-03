/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.cache.annotation;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import org.springframework.cache.annotation.CacheAnnotationParser;
import org.springframework.cache.annotation.SpringCacheAnnotationParser;
import org.springframework.cache.interceptor.AbstractFallbackCacheOperationSource;
import org.springframework.cache.interceptor.CacheOperation;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class AnnotationCacheOperationSource
extends AbstractFallbackCacheOperationSource
implements Serializable {
    private final boolean publicMethodsOnly;
    private final Set<CacheAnnotationParser> annotationParsers;

    public AnnotationCacheOperationSource() {
        this(true);
    }

    public AnnotationCacheOperationSource(boolean publicMethodsOnly) {
        this.publicMethodsOnly = publicMethodsOnly;
        this.annotationParsers = Collections.singleton(new SpringCacheAnnotationParser());
    }

    public AnnotationCacheOperationSource(CacheAnnotationParser annotationParser) {
        this.publicMethodsOnly = true;
        Assert.notNull((Object)annotationParser, (String)"CacheAnnotationParser must not be null");
        this.annotationParsers = Collections.singleton(annotationParser);
    }

    public AnnotationCacheOperationSource(CacheAnnotationParser ... annotationParsers) {
        this.publicMethodsOnly = true;
        Assert.notEmpty((Object[])annotationParsers, (String)"At least one CacheAnnotationParser needs to be specified");
        this.annotationParsers = new LinkedHashSet<CacheAnnotationParser>(Arrays.asList(annotationParsers));
    }

    public AnnotationCacheOperationSource(Set<CacheAnnotationParser> annotationParsers) {
        this.publicMethodsOnly = true;
        Assert.notEmpty(annotationParsers, (String)"At least one CacheAnnotationParser needs to be specified");
        this.annotationParsers = annotationParsers;
    }

    @Override
    public boolean isCandidateClass(Class<?> targetClass) {
        for (CacheAnnotationParser parser : this.annotationParsers) {
            if (!parser.isCandidateClass(targetClass)) continue;
            return true;
        }
        return false;
    }

    @Override
    @Nullable
    protected Collection<CacheOperation> findCacheOperations(Class<?> clazz) {
        return this.determineCacheOperations(parser -> parser.parseCacheAnnotations(clazz));
    }

    @Override
    @Nullable
    protected Collection<CacheOperation> findCacheOperations(Method method) {
        return this.determineCacheOperations(parser -> parser.parseCacheAnnotations(method));
    }

    @Nullable
    protected Collection<CacheOperation> determineCacheOperations(CacheOperationProvider provider) {
        Collection<CacheOperation> ops = null;
        for (CacheAnnotationParser parser : this.annotationParsers) {
            Collection<CacheOperation> annOps = provider.getCacheOperations(parser);
            if (annOps == null) continue;
            if (ops == null) {
                ops = annOps;
                continue;
            }
            ArrayList<CacheOperation> combined = new ArrayList<CacheOperation>(ops.size() + annOps.size());
            combined.addAll(ops);
            combined.addAll(annOps);
            ops = combined;
        }
        return ops;
    }

    @Override
    protected boolean allowPublicMethodsOnly() {
        return this.publicMethodsOnly;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof AnnotationCacheOperationSource)) {
            return false;
        }
        AnnotationCacheOperationSource otherCos = (AnnotationCacheOperationSource)other;
        return this.annotationParsers.equals(otherCos.annotationParsers) && this.publicMethodsOnly == otherCos.publicMethodsOnly;
    }

    public int hashCode() {
        return this.annotationParsers.hashCode();
    }

    @FunctionalInterface
    protected static interface CacheOperationProvider {
        @Nullable
        public Collection<CacheOperation> getCacheOperations(CacheAnnotationParser var1);
    }
}

