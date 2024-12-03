/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.cache.jcache.interceptor;

import java.util.Collection;
import java.util.function.Supplier;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.CacheOperationInvocationContext;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleCacheResolver;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.cache.jcache.interceptor.AnnotationJCacheOperationSource;
import org.springframework.cache.jcache.interceptor.JCacheOperationSource;
import org.springframework.cache.jcache.interceptor.KeyGeneratorAdapter;
import org.springframework.cache.jcache.interceptor.SimpleExceptionCacheResolver;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.function.SingletonSupplier;
import org.springframework.util.function.SupplierUtils;

public class DefaultJCacheOperationSource
extends AnnotationJCacheOperationSource
implements BeanFactoryAware,
SmartInitializingSingleton {
    @Nullable
    private SingletonSupplier<CacheManager> cacheManager;
    @Nullable
    private SingletonSupplier<CacheResolver> cacheResolver;
    @Nullable
    private SingletonSupplier<CacheResolver> exceptionCacheResolver;
    private SingletonSupplier<KeyGenerator> keyGenerator;
    private final SingletonSupplier<KeyGenerator> adaptedKeyGenerator = SingletonSupplier.of(() -> new KeyGeneratorAdapter((JCacheOperationSource)this, this.getKeyGenerator()));
    @Nullable
    private BeanFactory beanFactory;

    public DefaultJCacheOperationSource() {
        this.keyGenerator = SingletonSupplier.of(SimpleKeyGenerator::new);
    }

    public DefaultJCacheOperationSource(@Nullable Supplier<CacheManager> cacheManager, @Nullable Supplier<CacheResolver> cacheResolver, @Nullable Supplier<CacheResolver> exceptionCacheResolver, @Nullable Supplier<KeyGenerator> keyGenerator) {
        this.cacheManager = SingletonSupplier.ofNullable(cacheManager);
        this.cacheResolver = SingletonSupplier.ofNullable(cacheResolver);
        this.exceptionCacheResolver = SingletonSupplier.ofNullable(exceptionCacheResolver);
        this.keyGenerator = new SingletonSupplier<KeyGenerator>(keyGenerator, SimpleKeyGenerator::new);
    }

    public void setCacheManager(@Nullable CacheManager cacheManager) {
        this.cacheManager = SingletonSupplier.ofNullable(cacheManager);
    }

    @Nullable
    public CacheManager getCacheManager() {
        return SupplierUtils.resolve(this.cacheManager);
    }

    public void setCacheResolver(@Nullable CacheResolver cacheResolver) {
        this.cacheResolver = SingletonSupplier.ofNullable(cacheResolver);
    }

    @Nullable
    public CacheResolver getCacheResolver() {
        return SupplierUtils.resolve(this.cacheResolver);
    }

    public void setExceptionCacheResolver(@Nullable CacheResolver exceptionCacheResolver) {
        this.exceptionCacheResolver = SingletonSupplier.ofNullable(exceptionCacheResolver);
    }

    @Nullable
    public CacheResolver getExceptionCacheResolver() {
        return SupplierUtils.resolve(this.exceptionCacheResolver);
    }

    public void setKeyGenerator(KeyGenerator keyGenerator) {
        this.keyGenerator = SingletonSupplier.of(keyGenerator);
    }

    public KeyGenerator getKeyGenerator() {
        return this.keyGenerator.obtain();
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public void afterSingletonsInstantiated() {
        Assert.notNull((Object)this.getDefaultCacheResolver(), "Cache resolver should have been initialized");
    }

    @Override
    protected <T> T getBean(Class<T> type) {
        Assert.state(this.beanFactory != null, () -> "BeanFactory required for resolution of [" + type + "]");
        try {
            return this.beanFactory.getBean(type);
        }
        catch (NoUniqueBeanDefinitionException ex) {
            throw new IllegalStateException("No unique [" + type.getName() + "] bean found in application context - mark one as primary, or declare a more specific implementation type for your cache", ex);
        }
        catch (NoSuchBeanDefinitionException ex) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)("No bean of type [" + type.getName() + "] found in application context"), (Throwable)ex);
            }
            return BeanUtils.instantiateClass(type);
        }
    }

    protected CacheManager getDefaultCacheManager() {
        if (this.getCacheManager() == null) {
            Assert.state(this.beanFactory != null, "BeanFactory required for default CacheManager resolution");
            try {
                this.cacheManager = SingletonSupplier.of(this.beanFactory.getBean(CacheManager.class));
            }
            catch (NoUniqueBeanDefinitionException ex) {
                throw new IllegalStateException("No unique bean of type CacheManager found. Mark one as primary or declare a specific CacheManager to use.");
            }
            catch (NoSuchBeanDefinitionException ex) {
                throw new IllegalStateException("No bean of type CacheManager found. Register a CacheManager bean or remove the @EnableCaching annotation from your configuration.");
            }
        }
        return this.getCacheManager();
    }

    @Override
    protected CacheResolver getDefaultCacheResolver() {
        if (this.getCacheResolver() == null) {
            this.cacheResolver = SingletonSupplier.of(new SimpleCacheResolver(this.getDefaultCacheManager()));
        }
        return this.getCacheResolver();
    }

    @Override
    protected CacheResolver getDefaultExceptionCacheResolver() {
        if (this.getExceptionCacheResolver() == null) {
            this.exceptionCacheResolver = SingletonSupplier.of(new LazyCacheResolver());
        }
        return this.getExceptionCacheResolver();
    }

    @Override
    protected KeyGenerator getDefaultKeyGenerator() {
        return this.adaptedKeyGenerator.obtain();
    }

    class LazyCacheResolver
    implements CacheResolver {
        private final SingletonSupplier<CacheResolver> cacheResolver = SingletonSupplier.of(() -> new SimpleExceptionCacheResolver(DefaultJCacheOperationSource.this.getDefaultCacheManager()));

        LazyCacheResolver() {
        }

        @Override
        public Collection<? extends Cache> resolveCaches(CacheOperationInvocationContext<?> context) {
            return this.cacheResolver.obtain().resolveCaches(context);
        }
    }
}

