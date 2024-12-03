/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.springframework.cache.jcache.interceptor;

import java.lang.reflect.Method;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.interceptor.AbstractCacheInvoker;
import org.springframework.cache.interceptor.CacheOperationInvocationContext;
import org.springframework.cache.interceptor.CacheOperationInvoker;
import org.springframework.cache.jcache.interceptor.CachePutInterceptor;
import org.springframework.cache.jcache.interceptor.CachePutOperation;
import org.springframework.cache.jcache.interceptor.CacheRemoveAllInterceptor;
import org.springframework.cache.jcache.interceptor.CacheRemoveAllOperation;
import org.springframework.cache.jcache.interceptor.CacheRemoveEntryInterceptor;
import org.springframework.cache.jcache.interceptor.CacheRemoveOperation;
import org.springframework.cache.jcache.interceptor.CacheResultInterceptor;
import org.springframework.cache.jcache.interceptor.CacheResultOperation;
import org.springframework.cache.jcache.interceptor.DefaultCacheInvocationContext;
import org.springframework.cache.jcache.interceptor.JCacheOperation;
import org.springframework.cache.jcache.interceptor.JCacheOperationSource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class JCacheAspectSupport
extends AbstractCacheInvoker
implements InitializingBean {
    protected final Log logger = LogFactory.getLog(this.getClass());
    @Nullable
    private JCacheOperationSource cacheOperationSource;
    @Nullable
    private CacheResultInterceptor cacheResultInterceptor;
    @Nullable
    private CachePutInterceptor cachePutInterceptor;
    @Nullable
    private CacheRemoveEntryInterceptor cacheRemoveEntryInterceptor;
    @Nullable
    private CacheRemoveAllInterceptor cacheRemoveAllInterceptor;
    private boolean initialized = false;

    public void setCacheOperationSource(JCacheOperationSource cacheOperationSource) {
        Assert.notNull((Object)cacheOperationSource, "JCacheOperationSource must not be null");
        this.cacheOperationSource = cacheOperationSource;
    }

    public JCacheOperationSource getCacheOperationSource() {
        Assert.state(this.cacheOperationSource != null, "The 'cacheOperationSource' property is required: If there are no cacheable methods, then don't use a cache aspect.");
        return this.cacheOperationSource;
    }

    @Override
    public void afterPropertiesSet() {
        this.getCacheOperationSource();
        this.cacheResultInterceptor = new CacheResultInterceptor(this.getErrorHandler());
        this.cachePutInterceptor = new CachePutInterceptor(this.getErrorHandler());
        this.cacheRemoveEntryInterceptor = new CacheRemoveEntryInterceptor(this.getErrorHandler());
        this.cacheRemoveAllInterceptor = new CacheRemoveAllInterceptor(this.getErrorHandler());
        this.initialized = true;
    }

    @Nullable
    protected Object execute(CacheOperationInvoker invoker, Object target, Method method, Object[] args) {
        if (this.initialized) {
            Class<?> targetClass = AopProxyUtils.ultimateTargetClass(target);
            JCacheOperation<?> operation = this.getCacheOperationSource().getCacheOperation(method, targetClass);
            if (operation != null) {
                CacheOperationInvocationContext<?> context = this.createCacheOperationInvocationContext(target, args, operation);
                return this.execute(context, invoker);
            }
        }
        return invoker.invoke();
    }

    private CacheOperationInvocationContext<?> createCacheOperationInvocationContext(Object target, Object[] args, JCacheOperation<?> operation) {
        return new DefaultCacheInvocationContext(operation, target, args);
    }

    @Nullable
    private Object execute(CacheOperationInvocationContext<?> context, CacheOperationInvoker invoker) {
        CacheOperationInvokerAdapter adapter = new CacheOperationInvokerAdapter(invoker);
        Object operation = context.getOperation();
        if (operation instanceof CacheResultOperation) {
            Assert.state(this.cacheResultInterceptor != null, "No CacheResultInterceptor");
            return this.cacheResultInterceptor.invoke(context, (CacheOperationInvoker)adapter);
        }
        if (operation instanceof CachePutOperation) {
            Assert.state(this.cachePutInterceptor != null, "No CachePutInterceptor");
            return this.cachePutInterceptor.invoke((CacheOperationInvocationContext<CachePutOperation>)context, (CacheOperationInvoker)adapter);
        }
        if (operation instanceof CacheRemoveOperation) {
            Assert.state(this.cacheRemoveEntryInterceptor != null, "No CacheRemoveEntryInterceptor");
            return this.cacheRemoveEntryInterceptor.invoke((CacheOperationInvocationContext<CacheRemoveOperation>)context, (CacheOperationInvoker)adapter);
        }
        if (operation instanceof CacheRemoveAllOperation) {
            Assert.state(this.cacheRemoveAllInterceptor != null, "No CacheRemoveAllInterceptor");
            return this.cacheRemoveAllInterceptor.invoke((CacheOperationInvocationContext<CacheRemoveAllOperation>)context, (CacheOperationInvoker)adapter);
        }
        throw new IllegalArgumentException("Cannot handle " + operation);
    }

    @Nullable
    protected Object invokeOperation(CacheOperationInvoker invoker) {
        return invoker.invoke();
    }

    private class CacheOperationInvokerAdapter
    implements CacheOperationInvoker {
        private final CacheOperationInvoker delegate;

        public CacheOperationInvokerAdapter(CacheOperationInvoker delegate) {
            this.delegate = delegate;
        }

        @Override
        public Object invoke() throws CacheOperationInvoker.ThrowableWrapper {
            return JCacheAspectSupport.this.invokeOperation(this.delegate);
        }
    }
}

