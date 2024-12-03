/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.springframework.beans.factory.ListableBeanFactory
 *  org.springframework.context.ApplicationContext
 *  org.springframework.context.ApplicationContextAware
 *  org.springframework.core.convert.ConversionService
 *  org.springframework.core.convert.TypeDescriptor
 *  org.springframework.core.convert.converter.ConditionalGenericConverter
 *  org.springframework.core.convert.converter.ConverterRegistry
 *  org.springframework.core.convert.converter.GenericConverter
 *  org.springframework.core.convert.converter.GenericConverter$ConvertiblePair
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.StringUtils
 */
package org.springframework.data.repository.support;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nonnull;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;
import org.springframework.core.convert.converter.ConverterRegistry;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.support.DefaultRepositoryInvokerFactory;
import org.springframework.data.repository.support.Repositories;
import org.springframework.data.repository.support.RepositoryInvoker;
import org.springframework.data.repository.support.RepositoryInvokerFactory;
import org.springframework.data.util.Lazy;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class DomainClassConverter<T extends ConversionService & ConverterRegistry>
implements ConditionalGenericConverter,
ApplicationContextAware {
    private final T conversionService;
    private Lazy<Repositories> repositories = Lazy.of(Repositories.NONE);
    private Optional<ToEntityConverter> toEntityConverter = Optional.empty();
    private Optional<ToIdConverter> toIdConverter = Optional.empty();

    public DomainClassConverter(T conversionService) {
        Assert.notNull(conversionService, (String)"ConversionService must not be null!");
        this.conversionService = conversionService;
        ((ConverterRegistry)this.conversionService).addConverter((GenericConverter)this);
    }

    @Nonnull
    public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new GenericConverter.ConvertiblePair(Object.class, Object.class));
    }

    @Nullable
    public Object convert(@Nullable Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        return this.getConverter(targetType).map(it -> it.convert(source, sourceType, targetType)).orElse(null);
    }

    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        return this.getConverter(targetType).map(it -> it.matches(sourceType, targetType)).orElse(false);
    }

    private Optional<? extends ConditionalGenericConverter> getConverter(TypeDescriptor targetType) {
        return this.repositories.get().hasRepositoryFor(targetType.getType()) ? this.toEntityConverter : this.toIdConverter;
    }

    public void setApplicationContext(ApplicationContext context) {
        this.repositories = Lazy.of(() -> {
            Repositories repositories = new Repositories((ListableBeanFactory)context);
            this.toEntityConverter = Optional.of(new ToEntityConverter(repositories, (ConversionService)this.conversionService));
            this.toIdConverter = Optional.of(new ToIdConverter(repositories, (ConversionService)this.conversionService));
            return repositories;
        });
    }

    static class ToIdConverter
    implements ConditionalGenericConverter {
        private final Repositories repositories;
        private final ConversionService conversionService;

        public ToIdConverter(Repositories repositories, ConversionService conversionService) {
            this.repositories = repositories;
            this.conversionService = conversionService;
        }

        @Nonnull
        public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
            return Collections.singleton(new GenericConverter.ConvertiblePair(Object.class, Object.class));
        }

        @Nullable
        public Object convert(@Nullable Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
            if (source == null || !StringUtils.hasText((String)source.toString())) {
                return null;
            }
            if (sourceType.equals((Object)targetType)) {
                return source;
            }
            Class domainType = sourceType.getType();
            EntityInformation entityInformation = this.repositories.getEntityInformationFor(domainType);
            return this.conversionService.convert(entityInformation.getId(source), targetType.getType());
        }

        public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
            if (sourceType.isAssignableTo(targetType)) {
                return false;
            }
            Class domainType = sourceType.getType();
            if (!this.repositories.hasRepositoryFor(domainType)) {
                return false;
            }
            Optional<RepositoryInformation> information = this.repositories.getRepositoryInformationFor(domainType);
            return information.map(it -> {
                Class<?> rawIdType = it.getIdType();
                return targetType.equals((Object)TypeDescriptor.valueOf(rawIdType)) || this.conversionService.canConvert(rawIdType, targetType.getType());
            }).orElseThrow(() -> new IllegalStateException(String.format("Couldn't find RepositoryInformation for %s!", domainType)));
        }
    }

    private static class ToEntityConverter
    implements ConditionalGenericConverter {
        private final RepositoryInvokerFactory repositoryInvokerFactory;
        private final Repositories repositories;
        private final ConversionService conversionService;

        public ToEntityConverter(Repositories repositories, ConversionService conversionService) {
            this.repositoryInvokerFactory = new DefaultRepositoryInvokerFactory(repositories, conversionService);
            this.repositories = repositories;
            this.conversionService = conversionService;
        }

        @Nonnull
        public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
            return Collections.singleton(new GenericConverter.ConvertiblePair(Object.class, Object.class));
        }

        @Nullable
        public Object convert(@Nullable Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
            if (source == null || !StringUtils.hasText((String)source.toString())) {
                return null;
            }
            if (sourceType.equals((Object)targetType)) {
                return source;
            }
            Class domainType = targetType.getType();
            RepositoryInvoker invoker = this.repositoryInvokerFactory.getInvokerFor(domainType);
            RepositoryInformation information = this.repositories.getRequiredRepositoryInformation(domainType);
            Object id = this.conversionService.convert(source, information.getIdType());
            return id == null ? null : invoker.invokeFindById(id).orElse(null);
        }

        public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
            if (sourceType.isAssignableTo(targetType)) {
                return false;
            }
            Class domainType = targetType.getType();
            if (!this.repositories.hasRepositoryFor(domainType)) {
                return false;
            }
            Optional<RepositoryInformation> repositoryInformation = this.repositories.getRepositoryInformationFor(domainType);
            return repositoryInformation.map(it -> {
                Class<?> rawIdType = it.getIdType();
                return sourceType.equals((Object)TypeDescriptor.valueOf(rawIdType)) || this.conversionService.canConvert(sourceType.getType(), rawIdType);
            }).orElseThrow(() -> new IllegalStateException(String.format("Couldn't find RepositoryInformation for %s!", domainType)));
        }
    }
}

