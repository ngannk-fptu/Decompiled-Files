/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.scheduling.annotation;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.util.CollectionUtils;
import org.springframework.util.function.SingletonSupplier;

@Configuration(proxyBeanMethods=false)
public abstract class AbstractAsyncConfiguration
implements ImportAware {
    @Nullable
    protected AnnotationAttributes enableAsync;
    @Nullable
    protected Supplier<Executor> executor;
    @Nullable
    protected Supplier<AsyncUncaughtExceptionHandler> exceptionHandler;

    @Override
    public void setImportMetadata(AnnotationMetadata importMetadata) {
        this.enableAsync = AnnotationAttributes.fromMap(importMetadata.getAnnotationAttributes(EnableAsync.class.getName()));
        if (this.enableAsync == null) {
            throw new IllegalArgumentException("@EnableAsync is not present on importing class " + importMetadata.getClassName());
        }
    }

    @Autowired
    void setConfigurers(ObjectProvider<AsyncConfigurer> configurers) {
        SingletonSupplier<AsyncConfigurer> configurer = SingletonSupplier.of(() -> {
            List candidates = configurers.stream().collect(Collectors.toList());
            if (CollectionUtils.isEmpty(candidates)) {
                return null;
            }
            if (candidates.size() > 1) {
                throw new IllegalStateException("Only one AsyncConfigurer may exist");
            }
            return (AsyncConfigurer)candidates.get(0);
        });
        this.executor = this.adapt(configurer, AsyncConfigurer::getAsyncExecutor);
        this.exceptionHandler = this.adapt(configurer, AsyncConfigurer::getAsyncUncaughtExceptionHandler);
    }

    private <T> Supplier<T> adapt(Supplier<AsyncConfigurer> supplier, Function<AsyncConfigurer, T> provider) {
        return () -> {
            AsyncConfigurer configurer = (AsyncConfigurer)supplier.get();
            return configurer != null ? provider.apply(configurer) : null;
        };
    }
}

