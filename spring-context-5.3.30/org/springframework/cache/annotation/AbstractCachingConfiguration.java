/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.ObjectProvider
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.core.annotation.AnnotationAttributes
 *  org.springframework.core.type.AnnotationMetadata
 *  org.springframework.lang.Nullable
 *  org.springframework.util.CollectionUtils
 *  org.springframework.util.function.SingletonSupplier
 */
package org.springframework.cache.annotation;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.function.SingletonSupplier;

@Configuration(proxyBeanMethods=false)
public abstract class AbstractCachingConfiguration
implements ImportAware {
    @Nullable
    protected AnnotationAttributes enableCaching;
    @Nullable
    protected Supplier<CacheManager> cacheManager;
    @Nullable
    protected Supplier<CacheResolver> cacheResolver;
    @Nullable
    protected Supplier<KeyGenerator> keyGenerator;
    @Nullable
    protected Supplier<CacheErrorHandler> errorHandler;

    @Override
    public void setImportMetadata(AnnotationMetadata importMetadata) {
        this.enableCaching = AnnotationAttributes.fromMap((Map)importMetadata.getAnnotationAttributes(EnableCaching.class.getName()));
        if (this.enableCaching == null) {
            throw new IllegalArgumentException("@EnableCaching is not present on importing class " + importMetadata.getClassName());
        }
    }

    @Autowired
    void setConfigurers(ObjectProvider<CachingConfigurer> configurers) {
        Supplier<CachingConfigurer> configurer = () -> {
            List candidates = configurers.stream().collect(Collectors.toList());
            if (CollectionUtils.isEmpty(candidates)) {
                return null;
            }
            if (candidates.size() > 1) {
                throw new IllegalStateException(candidates.size() + " implementations of CachingConfigurer were found when only 1 was expected. Refactor the configuration such that CachingConfigurer is implemented only once or not at all.");
            }
            return (CachingConfigurer)candidates.get(0);
        };
        this.useCachingConfigurer(new CachingConfigurerSupplier(configurer));
    }

    protected void useCachingConfigurer(CachingConfigurerSupplier cachingConfigurerSupplier) {
        this.cacheManager = cachingConfigurerSupplier.adapt(CachingConfigurer::cacheManager);
        this.cacheResolver = cachingConfigurerSupplier.adapt(CachingConfigurer::cacheResolver);
        this.keyGenerator = cachingConfigurerSupplier.adapt(CachingConfigurer::keyGenerator);
        this.errorHandler = cachingConfigurerSupplier.adapt(CachingConfigurer::errorHandler);
    }

    protected static class CachingConfigurerSupplier {
        private final Supplier<CachingConfigurer> supplier;

        public CachingConfigurerSupplier(Supplier<CachingConfigurer> supplier) {
            this.supplier = SingletonSupplier.of(supplier);
        }

        @Nullable
        public <T> Supplier<T> adapt(Function<CachingConfigurer, T> provider) {
            return () -> {
                CachingConfigurer cachingConfigurer = this.supplier.get();
                return cachingConfigurer != null ? provider.apply(cachingConfigurer) : null;
            };
        }
    }
}

