/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.aop.framework.AopProxyUtils
 *  org.springframework.aop.support.AopUtils
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.BeanFactoryAware
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.NoSuchBeanDefinitionException
 *  org.springframework.beans.factory.NoUniqueBeanDefinitionException
 *  org.springframework.beans.factory.SmartInitializingSingleton
 *  org.springframework.beans.factory.annotation.BeanFactoryAnnotationUtils
 *  org.springframework.core.BridgeMethodResolver
 *  org.springframework.expression.EvaluationContext
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.CollectionUtils
 *  org.springframework.util.LinkedMultiValueMap
 *  org.springframework.util.MultiValueMap
 *  org.springframework.util.ObjectUtils
 *  org.springframework.util.ReflectionUtils
 *  org.springframework.util.StringUtils
 *  org.springframework.util.function.SingletonSupplier
 *  org.springframework.util.function.SupplierUtils
 */
package org.springframework.cache.interceptor;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.BeanFactoryAnnotationUtils;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.AbstractCacheInvoker;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.CacheEvictOperation;
import org.springframework.cache.interceptor.CacheOperation;
import org.springframework.cache.interceptor.CacheOperationExpressionEvaluator;
import org.springframework.cache.interceptor.CacheOperationInvocationContext;
import org.springframework.cache.interceptor.CacheOperationInvoker;
import org.springframework.cache.interceptor.CacheOperationSource;
import org.springframework.cache.interceptor.CachePutOperation;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.CacheableOperation;
import org.springframework.cache.interceptor.CompositeCacheOperationSource;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleCacheErrorHandler;
import org.springframework.cache.interceptor.SimpleCacheResolver;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.cache.interceptor.VariableNotAvailableException;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.expression.EvaluationContext;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.function.SingletonSupplier;
import org.springframework.util.function.SupplierUtils;

public abstract class CacheAspectSupport
extends AbstractCacheInvoker
implements BeanFactoryAware,
InitializingBean,
SmartInitializingSingleton {
    protected final Log logger = LogFactory.getLog(this.getClass());
    private final Map<CacheOperationCacheKey, CacheOperationMetadata> metadataCache = new ConcurrentHashMap<CacheOperationCacheKey, CacheOperationMetadata>(1024);
    private final CacheOperationExpressionEvaluator evaluator = new CacheOperationExpressionEvaluator();
    @Nullable
    private CacheOperationSource cacheOperationSource;
    private SingletonSupplier<KeyGenerator> keyGenerator = SingletonSupplier.of(SimpleKeyGenerator::new);
    @Nullable
    private SingletonSupplier<CacheResolver> cacheResolver;
    @Nullable
    private BeanFactory beanFactory;
    private boolean initialized = false;

    public void configure(@Nullable Supplier<CacheErrorHandler> errorHandler, @Nullable Supplier<KeyGenerator> keyGenerator, @Nullable Supplier<CacheResolver> cacheResolver, @Nullable Supplier<CacheManager> cacheManager) {
        this.errorHandler = new SingletonSupplier(errorHandler, SimpleCacheErrorHandler::new);
        this.keyGenerator = new SingletonSupplier(keyGenerator, SimpleKeyGenerator::new);
        this.cacheResolver = new SingletonSupplier(cacheResolver, () -> SimpleCacheResolver.of((CacheManager)SupplierUtils.resolve((Supplier)cacheManager)));
    }

    public void setCacheOperationSources(CacheOperationSource ... cacheOperationSources) {
        Assert.notEmpty((Object[])cacheOperationSources, (String)"At least 1 CacheOperationSource needs to be specified");
        this.cacheOperationSource = cacheOperationSources.length > 1 ? new CompositeCacheOperationSource(cacheOperationSources) : cacheOperationSources[0];
    }

    public void setCacheOperationSource(@Nullable CacheOperationSource cacheOperationSource) {
        this.cacheOperationSource = cacheOperationSource;
    }

    @Nullable
    public CacheOperationSource getCacheOperationSource() {
        return this.cacheOperationSource;
    }

    public void setKeyGenerator(KeyGenerator keyGenerator) {
        this.keyGenerator = SingletonSupplier.of((Object)keyGenerator);
    }

    public KeyGenerator getKeyGenerator() {
        return (KeyGenerator)this.keyGenerator.obtain();
    }

    public void setCacheResolver(@Nullable CacheResolver cacheResolver) {
        this.cacheResolver = SingletonSupplier.ofNullable((Object)cacheResolver);
    }

    @Nullable
    public CacheResolver getCacheResolver() {
        return (CacheResolver)SupplierUtils.resolve(this.cacheResolver);
    }

    public void setCacheManager(CacheManager cacheManager) {
        this.cacheResolver = SingletonSupplier.of((Object)new SimpleCacheResolver(cacheManager));
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public void afterPropertiesSet() {
        Assert.state((this.getCacheOperationSource() != null ? 1 : 0) != 0, (String)"The 'cacheOperationSources' property is required: If there are no cacheable methods, then don't use a cache aspect.");
    }

    public void afterSingletonsInstantiated() {
        if (this.getCacheResolver() == null) {
            Assert.state((this.beanFactory != null ? 1 : 0) != 0, (String)"CacheResolver or BeanFactory must be set on cache aspect");
            try {
                this.setCacheManager((CacheManager)this.beanFactory.getBean(CacheManager.class));
            }
            catch (NoUniqueBeanDefinitionException ex) {
                throw new IllegalStateException("No CacheResolver specified, and no unique bean of type CacheManager found. Mark one as primary or declare a specific CacheManager to use.", ex);
            }
            catch (NoSuchBeanDefinitionException ex) {
                throw new IllegalStateException("No CacheResolver specified, and no bean of type CacheManager found. Register a CacheManager bean or remove the @EnableCaching annotation from your configuration.", ex);
            }
        }
        this.initialized = true;
    }

    protected String methodIdentification(Method method, Class<?> targetClass) {
        Method specificMethod = ClassUtils.getMostSpecificMethod((Method)method, targetClass);
        return ClassUtils.getQualifiedMethodName((Method)specificMethod);
    }

    protected Collection<? extends Cache> getCaches(CacheOperationInvocationContext<CacheOperation> context, CacheResolver cacheResolver) {
        Collection<? extends Cache> caches = cacheResolver.resolveCaches(context);
        if (caches.isEmpty()) {
            throw new IllegalStateException("No cache could be resolved for '" + context.getOperation() + "' using resolver '" + cacheResolver + "'. At least one cache should be provided per cache operation.");
        }
        return caches;
    }

    protected CacheOperationContext getOperationContext(CacheOperation operation, Method method, Object[] args, Object target, Class<?> targetClass) {
        CacheOperationMetadata metadata = this.getCacheOperationMetadata(operation, method, targetClass);
        return new CacheOperationContext(metadata, args, target);
    }

    protected CacheOperationMetadata getCacheOperationMetadata(CacheOperation operation, Method method, Class<?> targetClass) {
        CacheOperationCacheKey cacheKey = new CacheOperationCacheKey(operation, method, targetClass);
        CacheOperationMetadata metadata = this.metadataCache.get(cacheKey);
        if (metadata == null) {
            CacheResolver operationCacheResolver;
            KeyGenerator operationKeyGenerator = StringUtils.hasText((String)operation.getKeyGenerator()) ? this.getBean(operation.getKeyGenerator(), KeyGenerator.class) : this.getKeyGenerator();
            if (StringUtils.hasText((String)operation.getCacheResolver())) {
                operationCacheResolver = this.getBean(operation.getCacheResolver(), CacheResolver.class);
            } else if (StringUtils.hasText((String)operation.getCacheManager())) {
                CacheManager cacheManager = this.getBean(operation.getCacheManager(), CacheManager.class);
                operationCacheResolver = new SimpleCacheResolver(cacheManager);
            } else {
                operationCacheResolver = this.getCacheResolver();
                Assert.state((operationCacheResolver != null ? 1 : 0) != 0, (String)"No CacheResolver/CacheManager set");
            }
            metadata = new CacheOperationMetadata(operation, method, targetClass, operationKeyGenerator, operationCacheResolver);
            this.metadataCache.put(cacheKey, metadata);
        }
        return metadata;
    }

    protected <T> T getBean(String name, Class<T> serviceType) {
        if (this.beanFactory == null) {
            throw new IllegalStateException("BeanFactory must be set on cache aspect for " + serviceType.getSimpleName() + " retrieval");
        }
        return (T)BeanFactoryAnnotationUtils.qualifiedBeanOfType((BeanFactory)this.beanFactory, serviceType, (String)name);
    }

    protected void clearMetadataCache() {
        this.metadataCache.clear();
        this.evaluator.clear();
    }

    @Nullable
    protected Object execute(CacheOperationInvoker invoker, Object target, Method method, Object[] args) {
        if (this.initialized) {
            Collection<CacheOperation> operations;
            Class<?> targetClass = this.getTargetClass(target);
            CacheOperationSource cacheOperationSource = this.getCacheOperationSource();
            if (cacheOperationSource != null && !CollectionUtils.isEmpty(operations = cacheOperationSource.getCacheOperations(method, targetClass))) {
                return this.execute(invoker, method, new CacheOperationContexts(operations, method, args, target, targetClass));
            }
        }
        return invoker.invoke();
    }

    @Nullable
    protected Object invokeOperation(CacheOperationInvoker invoker) {
        return invoker.invoke();
    }

    private Class<?> getTargetClass(Object target) {
        return AopProxyUtils.ultimateTargetClass((Object)target);
    }

    @Nullable
    private Object execute(CacheOperationInvoker invoker, Method method, CacheOperationContexts contexts) {
        Object returnValue;
        Object cacheValue;
        if (contexts.isSynchronized()) {
            CacheOperationContext context = contexts.get(CacheableOperation.class).iterator().next();
            if (this.isConditionPassing(context, CacheOperationExpressionEvaluator.NO_RESULT)) {
                Object key = this.generateKey(context, CacheOperationExpressionEvaluator.NO_RESULT);
                Cache cache = context.getCaches().iterator().next();
                try {
                    return this.wrapCacheValue(method, this.handleSynchronizedGet(invoker, key, cache));
                }
                catch (Cache.ValueRetrievalException ex) {
                    ReflectionUtils.rethrowRuntimeException((Throwable)ex.getCause());
                }
            } else {
                return this.invokeOperation(invoker);
            }
        }
        this.processCacheEvicts(contexts.get(CacheEvictOperation.class), true, CacheOperationExpressionEvaluator.NO_RESULT);
        Cache.ValueWrapper cacheHit = this.findCachedItem(contexts.get(CacheableOperation.class));
        ArrayList<CachePutRequest> cachePutRequests = new ArrayList<CachePutRequest>(1);
        if (cacheHit == null) {
            this.collectPutRequests(contexts.get(CacheableOperation.class), CacheOperationExpressionEvaluator.NO_RESULT, cachePutRequests);
        }
        if (cacheHit != null && !this.hasCachePut(contexts)) {
            cacheValue = cacheHit.get();
            returnValue = this.wrapCacheValue(method, cacheValue);
        } else {
            returnValue = this.invokeOperation(invoker);
            cacheValue = this.unwrapReturnValue(returnValue);
        }
        this.collectPutRequests(contexts.get(CachePutOperation.class), cacheValue, cachePutRequests);
        for (CachePutRequest cachePutRequest : cachePutRequests) {
            cachePutRequest.apply(cacheValue);
        }
        this.processCacheEvicts(contexts.get(CacheEvictOperation.class), false, cacheValue);
        return returnValue;
    }

    @Nullable
    private Object handleSynchronizedGet(CacheOperationInvoker invoker, Object key, Cache cache) {
        InvocationAwareResult invocationResult = new InvocationAwareResult();
        Object result = cache.get(key, () -> {
            invocationResult.invoked = true;
            if (this.logger.isTraceEnabled()) {
                this.logger.trace((Object)("No cache entry for key '" + key + "' in cache " + cache.getName()));
            }
            return this.unwrapReturnValue(this.invokeOperation(invoker));
        });
        if (!invocationResult.invoked && this.logger.isTraceEnabled()) {
            this.logger.trace((Object)("Cache entry for key '" + key + "' found in cache '" + cache.getName() + "'"));
        }
        return result;
    }

    @Nullable
    private Object wrapCacheValue(Method method, @Nullable Object cacheValue) {
        if (method.getReturnType() == Optional.class && (cacheValue == null || cacheValue.getClass() != Optional.class)) {
            return Optional.ofNullable(cacheValue);
        }
        return cacheValue;
    }

    @Nullable
    private Object unwrapReturnValue(@Nullable Object returnValue) {
        return ObjectUtils.unwrapOptional((Object)returnValue);
    }

    private boolean hasCachePut(CacheOperationContexts contexts) {
        Collection<CacheOperationContext> cachePutContexts = contexts.get(CachePutOperation.class);
        ArrayList<CacheOperationContext> excluded = new ArrayList<CacheOperationContext>(1);
        for (CacheOperationContext context : cachePutContexts) {
            try {
                if (context.isConditionPassing(CacheOperationExpressionEvaluator.RESULT_UNAVAILABLE)) continue;
                excluded.add(context);
            }
            catch (VariableNotAvailableException variableNotAvailableException) {}
        }
        return cachePutContexts.size() != excluded.size();
    }

    private void processCacheEvicts(Collection<CacheOperationContext> contexts, boolean beforeInvocation, @Nullable Object result) {
        for (CacheOperationContext context : contexts) {
            CacheEvictOperation operation = (CacheEvictOperation)context.metadata.operation;
            if (beforeInvocation != operation.isBeforeInvocation() || !this.isConditionPassing(context, result)) continue;
            this.performCacheEvict(context, operation, result);
        }
    }

    private void performCacheEvict(CacheOperationContext context, CacheEvictOperation operation, @Nullable Object result) {
        Object key = null;
        for (Cache cache : context.getCaches()) {
            if (operation.isCacheWide()) {
                this.logInvalidating(context, operation, null);
                this.doClear(cache, operation.isBeforeInvocation());
                continue;
            }
            if (key == null) {
                key = this.generateKey(context, result);
            }
            this.logInvalidating(context, operation, key);
            this.doEvict(cache, key, operation.isBeforeInvocation());
        }
    }

    private void logInvalidating(CacheOperationContext context, CacheEvictOperation operation, @Nullable Object key) {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace((Object)("Invalidating " + (key != null ? "cache key [" + key + "]" : "entire cache") + " for operation " + operation + " on method " + context.metadata.method));
        }
    }

    @Nullable
    private Cache.ValueWrapper findCachedItem(Collection<CacheOperationContext> contexts) {
        Object result = CacheOperationExpressionEvaluator.NO_RESULT;
        for (CacheOperationContext context : contexts) {
            if (!this.isConditionPassing(context, result)) continue;
            Object key = this.generateKey(context, result);
            Cache.ValueWrapper cached = this.findInCaches(context, key);
            if (cached != null) {
                return cached;
            }
            if (!this.logger.isTraceEnabled()) continue;
            this.logger.trace((Object)("No cache entry for key '" + key + "' in cache(s) " + context.getCacheNames()));
        }
        return null;
    }

    private void collectPutRequests(Collection<CacheOperationContext> contexts, @Nullable Object result, Collection<CachePutRequest> putRequests) {
        for (CacheOperationContext context : contexts) {
            if (!this.isConditionPassing(context, result)) continue;
            Object key = this.generateKey(context, result);
            putRequests.add(new CachePutRequest(context, key));
        }
    }

    @Nullable
    private Cache.ValueWrapper findInCaches(CacheOperationContext context, Object key) {
        for (Cache cache : context.getCaches()) {
            Cache.ValueWrapper wrapper = this.doGet(cache, key);
            if (wrapper == null) continue;
            if (this.logger.isTraceEnabled()) {
                this.logger.trace((Object)("Cache entry for key '" + key + "' found in cache '" + cache.getName() + "'"));
            }
            return wrapper;
        }
        return null;
    }

    private boolean isConditionPassing(CacheOperationContext context, @Nullable Object result) {
        boolean passing = context.isConditionPassing(result);
        if (!passing && this.logger.isTraceEnabled()) {
            this.logger.trace((Object)("Cache condition failed on method " + context.metadata.method + " for operation " + context.metadata.operation));
        }
        return passing;
    }

    private Object generateKey(CacheOperationContext context, @Nullable Object result) {
        Object key = context.generateKey(result);
        if (key == null) {
            throw new IllegalArgumentException("Null key returned for cache operation (maybe you are using named params on classes without debug info?) " + context.metadata.operation);
        }
        if (this.logger.isTraceEnabled()) {
            this.logger.trace((Object)("Computed cache key '" + key + "' for operation " + context.metadata.operation));
        }
        return key;
    }

    private static class InvocationAwareResult {
        boolean invoked;

        private InvocationAwareResult() {
        }
    }

    private static final class CacheOperationCacheKey
    implements Comparable<CacheOperationCacheKey> {
        private final CacheOperation cacheOperation;
        private final AnnotatedElementKey methodCacheKey;

        private CacheOperationCacheKey(CacheOperation cacheOperation, Method method, Class<?> targetClass) {
            this.cacheOperation = cacheOperation;
            this.methodCacheKey = new AnnotatedElementKey(method, targetClass);
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof CacheOperationCacheKey)) {
                return false;
            }
            CacheOperationCacheKey otherKey = (CacheOperationCacheKey)other;
            return this.cacheOperation.equals(otherKey.cacheOperation) && this.methodCacheKey.equals(otherKey.methodCacheKey);
        }

        public int hashCode() {
            return this.cacheOperation.hashCode() * 31 + this.methodCacheKey.hashCode();
        }

        public String toString() {
            return this.cacheOperation + " on " + this.methodCacheKey;
        }

        @Override
        public int compareTo(CacheOperationCacheKey other) {
            int result = this.cacheOperation.getName().compareTo(other.cacheOperation.getName());
            if (result == 0) {
                result = this.methodCacheKey.compareTo(other.methodCacheKey);
            }
            return result;
        }
    }

    private class CachePutRequest {
        private final CacheOperationContext context;
        private final Object key;

        public CachePutRequest(CacheOperationContext context, Object key) {
            this.context = context;
            this.key = key;
        }

        public void apply(@Nullable Object result) {
            if (this.context.canPutToCache(result)) {
                for (Cache cache : this.context.getCaches()) {
                    CacheAspectSupport.this.doPut(cache, this.key, result);
                }
            }
        }
    }

    protected class CacheOperationContext
    implements CacheOperationInvocationContext<CacheOperation> {
        private final CacheOperationMetadata metadata;
        private final Object[] args;
        private final Object target;
        private final Collection<? extends Cache> caches;
        private final Collection<String> cacheNames;
        @Nullable
        private Boolean conditionPassing;

        public CacheOperationContext(CacheOperationMetadata metadata, Object[] args, Object target) {
            this.metadata = metadata;
            this.args = this.extractArgs(metadata.method, args);
            this.target = target;
            this.caches = CacheAspectSupport.this.getCaches(this, metadata.cacheResolver);
            this.cacheNames = this.prepareCacheNames(this.caches);
        }

        @Override
        public CacheOperation getOperation() {
            return this.metadata.operation;
        }

        @Override
        public Object getTarget() {
            return this.target;
        }

        @Override
        public Method getMethod() {
            return this.metadata.method;
        }

        @Override
        public Object[] getArgs() {
            return this.args;
        }

        private Object[] extractArgs(Method method, Object[] args) {
            if (!method.isVarArgs()) {
                return args;
            }
            Object[] varArgs = ObjectUtils.toObjectArray((Object)args[args.length - 1]);
            Object[] combinedArgs = new Object[args.length - 1 + varArgs.length];
            System.arraycopy(args, 0, combinedArgs, 0, args.length - 1);
            System.arraycopy(varArgs, 0, combinedArgs, args.length - 1, varArgs.length);
            return combinedArgs;
        }

        protected boolean isConditionPassing(@Nullable Object result) {
            if (this.conditionPassing == null) {
                if (StringUtils.hasText((String)this.metadata.operation.getCondition())) {
                    EvaluationContext evaluationContext = this.createEvaluationContext(result);
                    this.conditionPassing = CacheAspectSupport.this.evaluator.condition(this.metadata.operation.getCondition(), this.metadata.methodKey, evaluationContext);
                } else {
                    this.conditionPassing = true;
                }
            }
            return this.conditionPassing;
        }

        protected boolean canPutToCache(@Nullable Object value) {
            String unless = "";
            if (this.metadata.operation instanceof CacheableOperation) {
                unless = ((CacheableOperation)this.metadata.operation).getUnless();
            } else if (this.metadata.operation instanceof CachePutOperation) {
                unless = ((CachePutOperation)this.metadata.operation).getUnless();
            }
            if (StringUtils.hasText((String)unless)) {
                EvaluationContext evaluationContext = this.createEvaluationContext(value);
                return !CacheAspectSupport.this.evaluator.unless(unless, this.metadata.methodKey, evaluationContext);
            }
            return true;
        }

        @Nullable
        protected Object generateKey(@Nullable Object result) {
            if (StringUtils.hasText((String)this.metadata.operation.getKey())) {
                EvaluationContext evaluationContext = this.createEvaluationContext(result);
                return CacheAspectSupport.this.evaluator.key(this.metadata.operation.getKey(), this.metadata.methodKey, evaluationContext);
            }
            return this.metadata.keyGenerator.generate(this.target, this.metadata.method, this.args);
        }

        private EvaluationContext createEvaluationContext(@Nullable Object result) {
            return CacheAspectSupport.this.evaluator.createEvaluationContext(this.caches, this.metadata.method, this.args, this.target, this.metadata.targetClass, this.metadata.targetMethod, result, CacheAspectSupport.this.beanFactory);
        }

        protected Collection<? extends Cache> getCaches() {
            return this.caches;
        }

        protected Collection<String> getCacheNames() {
            return this.cacheNames;
        }

        private Collection<String> prepareCacheNames(Collection<? extends Cache> caches) {
            ArrayList<String> names = new ArrayList<String>(caches.size());
            for (Cache cache : caches) {
                names.add(cache.getName());
            }
            return names;
        }
    }

    protected static class CacheOperationMetadata {
        private final CacheOperation operation;
        private final Method method;
        private final Class<?> targetClass;
        private final Method targetMethod;
        private final AnnotatedElementKey methodKey;
        private final KeyGenerator keyGenerator;
        private final CacheResolver cacheResolver;

        public CacheOperationMetadata(CacheOperation operation, Method method, Class<?> targetClass, KeyGenerator keyGenerator, CacheResolver cacheResolver) {
            this.operation = operation;
            this.method = BridgeMethodResolver.findBridgedMethod((Method)method);
            this.targetClass = targetClass;
            this.targetMethod = !Proxy.isProxyClass(targetClass) ? AopUtils.getMostSpecificMethod((Method)method, targetClass) : this.method;
            this.methodKey = new AnnotatedElementKey(this.targetMethod, targetClass);
            this.keyGenerator = keyGenerator;
            this.cacheResolver = cacheResolver;
        }
    }

    private class CacheOperationContexts {
        private final MultiValueMap<Class<? extends CacheOperation>, CacheOperationContext> contexts;
        private final boolean sync;

        public CacheOperationContexts(Collection<? extends CacheOperation> operations, Method method, Object[] args, Object target, Class<?> targetClass) {
            this.contexts = new LinkedMultiValueMap(operations.size());
            for (CacheOperation cacheOperation : operations) {
                this.contexts.add(cacheOperation.getClass(), (Object)CacheAspectSupport.this.getOperationContext(cacheOperation, method, args, target, targetClass));
            }
            this.sync = this.determineSyncFlag(method);
        }

        public Collection<CacheOperationContext> get(Class<? extends CacheOperation> operationClass) {
            List<CacheOperationContext> result = (List<CacheOperationContext>)this.contexts.get(operationClass);
            return result != null ? result : Collections.emptyList();
        }

        public boolean isSynchronized() {
            return this.sync;
        }

        private boolean determineSyncFlag(Method method) {
            List cacheOperationContexts = (List)this.contexts.get(CacheableOperation.class);
            if (cacheOperationContexts == null) {
                return false;
            }
            boolean syncEnabled = false;
            for (CacheOperationContext cacheOperationContext : cacheOperationContexts) {
                if (!((CacheableOperation)cacheOperationContext.getOperation()).isSync()) continue;
                syncEnabled = true;
                break;
            }
            if (syncEnabled) {
                if (this.contexts.size() > 1) {
                    throw new IllegalStateException("A sync=true operation cannot be combined with other cache operations on '" + method + "'");
                }
                if (cacheOperationContexts.size() > 1) {
                    throw new IllegalStateException("Only one sync=true operation is allowed on '" + method + "'");
                }
                CacheOperationContext cacheOperationContext = (CacheOperationContext)cacheOperationContexts.iterator().next();
                CacheOperation operation = cacheOperationContext.getOperation();
                if (cacheOperationContext.getCaches().size() > 1) {
                    throw new IllegalStateException("A sync=true operation is restricted to a single cache on '" + operation + "'");
                }
                if (operation instanceof CacheableOperation && StringUtils.hasText((String)((CacheableOperation)operation).getUnless())) {
                    throw new IllegalStateException("A sync=true operation does not support the unless attribute on '" + operation + "'");
                }
                return true;
            }
            return false;
        }
    }
}

