/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.annotation.CacheResult
 *  org.springframework.cache.Cache
 *  org.springframework.cache.Cache$ValueWrapper
 *  org.springframework.cache.interceptor.CacheErrorHandler
 *  org.springframework.cache.interceptor.CacheOperationInvocationContext
 *  org.springframework.cache.interceptor.CacheOperationInvoker
 *  org.springframework.cache.interceptor.CacheOperationInvoker$ThrowableWrapper
 *  org.springframework.cache.interceptor.CacheResolver
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ExceptionTypeFilter
 *  org.springframework.util.SerializationUtils
 */
package org.springframework.cache.jcache.interceptor;

import javax.cache.annotation.CacheResult;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.CacheOperationInvocationContext;
import org.springframework.cache.interceptor.CacheOperationInvoker;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.jcache.interceptor.AbstractKeyCacheInterceptor;
import org.springframework.cache.jcache.interceptor.CacheResultOperation;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ExceptionTypeFilter;
import org.springframework.util.SerializationUtils;

class CacheResultInterceptor
extends AbstractKeyCacheInterceptor<CacheResultOperation, CacheResult> {
    public CacheResultInterceptor(CacheErrorHandler errorHandler) {
        super(errorHandler);
    }

    @Override
    @Nullable
    protected Object invoke(CacheOperationInvocationContext<CacheResultOperation> context, CacheOperationInvoker invoker) {
        CacheResultOperation operation = (CacheResultOperation)context.getOperation();
        Object cacheKey = this.generateKey(context);
        Cache cache = this.resolveCache(context);
        Cache exceptionCache = this.resolveExceptionCache(context);
        if (!operation.isAlwaysInvoked()) {
            Cache.ValueWrapper cachedValue = this.doGet(cache, cacheKey);
            if (cachedValue != null) {
                return cachedValue.get();
            }
            this.checkForCachedException(exceptionCache, cacheKey);
        }
        try {
            Object invocationResult = invoker.invoke();
            this.doPut(cache, cacheKey, invocationResult);
            return invocationResult;
        }
        catch (CacheOperationInvoker.ThrowableWrapper ex) {
            Throwable original = ex.getOriginal();
            this.cacheException(exceptionCache, operation.getExceptionTypeFilter(), cacheKey, original);
            throw ex;
        }
    }

    protected void checkForCachedException(@Nullable Cache exceptionCache, Object cacheKey) {
        if (exceptionCache == null) {
            return;
        }
        Cache.ValueWrapper result = this.doGet(exceptionCache, cacheKey);
        if (result != null) {
            Throwable ex = (Throwable)result.get();
            Assert.state((ex != null ? 1 : 0) != 0, (String)"No exception in cache");
            throw CacheResultInterceptor.rewriteCallStack(ex, this.getClass().getName(), "invoke");
        }
    }

    protected void cacheException(@Nullable Cache exceptionCache, ExceptionTypeFilter filter, Object cacheKey, Throwable ex) {
        if (exceptionCache == null) {
            return;
        }
        if (filter.match(ex.getClass())) {
            this.doPut(exceptionCache, cacheKey, ex);
        }
    }

    @Nullable
    private Cache resolveExceptionCache(CacheOperationInvocationContext<CacheResultOperation> context) {
        CacheResolver exceptionCacheResolver = ((CacheResultOperation)context.getOperation()).getExceptionCacheResolver();
        if (exceptionCacheResolver != null) {
            return CacheResultInterceptor.extractFrom(((CacheResultOperation)context.getOperation()).getExceptionCacheResolver().resolveCaches(context));
        }
        return null;
    }

    private static CacheOperationInvoker.ThrowableWrapper rewriteCallStack(Throwable exception, String className, String methodName) {
        Throwable clone = CacheResultInterceptor.cloneException(exception);
        if (clone == null) {
            return new CacheOperationInvoker.ThrowableWrapper(exception);
        }
        StackTraceElement[] callStack = new Exception().getStackTrace();
        StackTraceElement[] cachedCallStack = exception.getStackTrace();
        int index = CacheResultInterceptor.findCommonAncestorIndex(callStack, className, methodName);
        int cachedIndex = CacheResultInterceptor.findCommonAncestorIndex(cachedCallStack, className, methodName);
        if (index == -1 || cachedIndex == -1) {
            return new CacheOperationInvoker.ThrowableWrapper(exception);
        }
        StackTraceElement[] result = new StackTraceElement[cachedIndex + callStack.length - index];
        System.arraycopy(cachedCallStack, 0, result, 0, cachedIndex);
        System.arraycopy(callStack, index, result, cachedIndex, callStack.length - index);
        clone.setStackTrace(result);
        return new CacheOperationInvoker.ThrowableWrapper(clone);
    }

    @Nullable
    private static <T extends Throwable> T cloneException(T exception) {
        try {
            return (T)((Throwable)SerializationUtils.deserialize((byte[])SerializationUtils.serialize(exception)));
        }
        catch (Exception ex) {
            return null;
        }
    }

    private static int findCommonAncestorIndex(StackTraceElement[] callStack, String className, String methodName) {
        for (int i = 0; i < callStack.length; ++i) {
            StackTraceElement element = callStack[i];
            if (!className.equals(element.getClassName()) || !methodName.equals(element.getMethodName())) continue;
            return i;
        }
        return -1;
    }
}

