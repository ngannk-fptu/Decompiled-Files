/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.annotation.CacheInvocationContext
 *  javax.cache.annotation.CacheInvocationParameter
 */
package org.springframework.cache.jcache.interceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;
import javax.cache.annotation.CacheInvocationContext;
import javax.cache.annotation.CacheInvocationParameter;
import org.springframework.cache.interceptor.CacheOperationInvocationContext;
import org.springframework.cache.jcache.interceptor.JCacheOperation;

class DefaultCacheInvocationContext<A extends Annotation>
implements CacheInvocationContext<A>,
CacheOperationInvocationContext<JCacheOperation<A>> {
    private final JCacheOperation<A> operation;
    private final Object target;
    private final Object[] args;
    private final CacheInvocationParameter[] allParameters;

    public DefaultCacheInvocationContext(JCacheOperation<A> operation, Object target, Object[] args) {
        this.operation = operation;
        this.target = target;
        this.args = args;
        this.allParameters = operation.getAllParameters(args);
    }

    @Override
    public JCacheOperation<A> getOperation() {
        return this.operation;
    }

    @Override
    public Method getMethod() {
        return this.operation.getMethod();
    }

    @Override
    public Object[] getArgs() {
        return (Object[])this.args.clone();
    }

    public Set<Annotation> getAnnotations() {
        return this.operation.getAnnotations();
    }

    public A getCacheAnnotation() {
        return (A)this.operation.getCacheAnnotation();
    }

    public String getCacheName() {
        return this.operation.getCacheName();
    }

    @Override
    public Object getTarget() {
        return this.target;
    }

    public CacheInvocationParameter[] getAllParameters() {
        return (CacheInvocationParameter[])this.allParameters.clone();
    }

    public <T> T unwrap(Class<T> cls) {
        throw new IllegalArgumentException("Cannot unwrap to " + cls);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("CacheInvocationContext{");
        sb.append("operation=").append(this.operation);
        sb.append(", target=").append(this.target);
        sb.append(", args=").append(Arrays.toString(this.args));
        sb.append(", allParameters=").append(Arrays.toString(this.allParameters));
        sb.append('}');
        return sb.toString();
    }
}

