/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.annotation.CacheInvocationParameter
 *  javax.cache.annotation.CacheKey
 *  javax.cache.annotation.CacheMethodDetails
 *  javax.cache.annotation.CacheValue
 */
package org.springframework.cache.jcache.interceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.cache.annotation.CacheInvocationParameter;
import javax.cache.annotation.CacheKey;
import javax.cache.annotation.CacheMethodDetails;
import javax.cache.annotation.CacheValue;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.jcache.interceptor.JCacheOperation;
import org.springframework.util.Assert;
import org.springframework.util.ExceptionTypeFilter;

abstract class AbstractJCacheOperation<A extends Annotation>
implements JCacheOperation<A> {
    private final CacheMethodDetails<A> methodDetails;
    private final CacheResolver cacheResolver;
    protected final List<CacheParameterDetail> allParameterDetails;

    protected AbstractJCacheOperation(CacheMethodDetails<A> methodDetails, CacheResolver cacheResolver) {
        Assert.notNull(methodDetails, "CacheMethodDetails must not be null");
        Assert.notNull((Object)cacheResolver, "CacheResolver must not be null");
        this.methodDetails = methodDetails;
        this.cacheResolver = cacheResolver;
        this.allParameterDetails = AbstractJCacheOperation.initializeAllParameterDetails(methodDetails.getMethod());
    }

    private static List<CacheParameterDetail> initializeAllParameterDetails(Method method) {
        int parameterCount = method.getParameterCount();
        ArrayList<CacheParameterDetail> result = new ArrayList<CacheParameterDetail>(parameterCount);
        for (int i2 = 0; i2 < parameterCount; ++i2) {
            CacheParameterDetail detail = new CacheParameterDetail(method, i2);
            result.add(detail);
        }
        return result;
    }

    public Method getMethod() {
        return this.methodDetails.getMethod();
    }

    public Set<Annotation> getAnnotations() {
        return this.methodDetails.getAnnotations();
    }

    public A getCacheAnnotation() {
        return (A)this.methodDetails.getCacheAnnotation();
    }

    public String getCacheName() {
        return this.methodDetails.getCacheName();
    }

    @Override
    public Set<String> getCacheNames() {
        return Collections.singleton(this.getCacheName());
    }

    @Override
    public CacheResolver getCacheResolver() {
        return this.cacheResolver;
    }

    @Override
    public CacheInvocationParameter[] getAllParameters(Object ... values) {
        if (this.allParameterDetails.size() != values.length) {
            throw new IllegalStateException("Values mismatch, operation has " + this.allParameterDetails.size() + " parameter(s) but got " + values.length + " value(s)");
        }
        ArrayList<CacheInvocationParameter> result = new ArrayList<CacheInvocationParameter>();
        for (int i2 = 0; i2 < this.allParameterDetails.size(); ++i2) {
            result.add(this.allParameterDetails.get(i2).toCacheInvocationParameter(values[i2]));
        }
        return result.toArray(new CacheInvocationParameter[0]);
    }

    public abstract ExceptionTypeFilter getExceptionTypeFilter();

    protected ExceptionTypeFilter createExceptionTypeFilter(Class<? extends Throwable>[] includes, Class<? extends Throwable>[] excludes) {
        return new ExceptionTypeFilter((Collection<? extends Class<? extends Throwable>>)Arrays.asList(includes), (Collection<? extends Class<? extends Throwable>>)Arrays.asList(excludes), true);
    }

    public String toString() {
        return this.getOperationDescription().append(']').toString();
    }

    protected StringBuilder getOperationDescription() {
        StringBuilder result = new StringBuilder();
        result.append(this.getClass().getSimpleName());
        result.append('[');
        result.append(this.methodDetails);
        return result;
    }

    protected static class CacheInvocationParameterImpl
    implements CacheInvocationParameter {
        private final CacheParameterDetail detail;
        private final Object value;

        public CacheInvocationParameterImpl(CacheParameterDetail detail, Object value) {
            this.detail = detail;
            this.value = value;
        }

        public Class<?> getRawType() {
            return this.detail.rawType;
        }

        public Object getValue() {
            return this.value;
        }

        public Set<Annotation> getAnnotations() {
            return this.detail.annotations;
        }

        public int getParameterPosition() {
            return this.detail.parameterPosition;
        }
    }

    protected static class CacheParameterDetail {
        private final Class<?> rawType;
        private final Set<Annotation> annotations;
        private final int parameterPosition;
        private final boolean isKey;
        private final boolean isValue;

        public CacheParameterDetail(Method method, int parameterPosition) {
            this.rawType = method.getParameterTypes()[parameterPosition];
            this.annotations = new LinkedHashSet<Annotation>();
            boolean foundKeyAnnotation = false;
            boolean foundValueAnnotation = false;
            for (Annotation annotation : method.getParameterAnnotations()[parameterPosition]) {
                this.annotations.add(annotation);
                if (CacheKey.class.isAssignableFrom(annotation.annotationType())) {
                    foundKeyAnnotation = true;
                }
                if (!CacheValue.class.isAssignableFrom(annotation.annotationType())) continue;
                foundValueAnnotation = true;
            }
            this.parameterPosition = parameterPosition;
            this.isKey = foundKeyAnnotation;
            this.isValue = foundValueAnnotation;
        }

        public int getParameterPosition() {
            return this.parameterPosition;
        }

        protected boolean isKey() {
            return this.isKey;
        }

        protected boolean isValue() {
            return this.isValue;
        }

        public CacheInvocationParameter toCacheInvocationParameter(Object value) {
            return new CacheInvocationParameterImpl(this, value);
        }
    }
}

