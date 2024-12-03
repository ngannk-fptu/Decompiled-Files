/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.aopalliance.intercept.MethodInterceptor
 *  org.aopalliance.intercept.MethodInvocation
 *  org.springframework.cache.interceptor.CacheErrorHandler
 *  org.springframework.cache.interceptor.CacheOperationInvoker
 *  org.springframework.cache.interceptor.CacheOperationInvoker$ThrowableWrapper
 *  org.springframework.cache.interceptor.SimpleCacheErrorHandler
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.function.SingletonSupplier
 */
package org.springframework.cache.jcache.interceptor;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.function.Supplier;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.CacheOperationInvoker;
import org.springframework.cache.interceptor.SimpleCacheErrorHandler;
import org.springframework.cache.jcache.interceptor.JCacheAspectSupport;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.function.SingletonSupplier;

public class JCacheInterceptor
extends JCacheAspectSupport
implements MethodInterceptor,
Serializable {
    public JCacheInterceptor() {
    }

    public JCacheInterceptor(@Nullable Supplier<CacheErrorHandler> errorHandler) {
        this.errorHandler = new SingletonSupplier(errorHandler, SimpleCacheErrorHandler::new);
    }

    @Nullable
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        CacheOperationInvoker aopAllianceInvoker = () -> {
            try {
                return invocation.proceed();
            }
            catch (Throwable ex) {
                throw new CacheOperationInvoker.ThrowableWrapper(ex);
            }
        };
        Object target = invocation.getThis();
        Assert.state((target != null ? 1 : 0) != 0, (String)"Target must not be null");
        try {
            return this.execute(aopAllianceInvoker, target, method, invocation.getArguments());
        }
        catch (CacheOperationInvoker.ThrowableWrapper th) {
            throw th.getOriginal();
        }
    }
}

