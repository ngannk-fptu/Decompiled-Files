/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.metadata;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import org.hibernate.validator.internal.engine.MethodValidationConfiguration;
import org.hibernate.validator.internal.engine.groups.ValidationOrderGenerator;
import org.hibernate.validator.internal.engine.valueextraction.ValueExtractorManager;
import org.hibernate.validator.internal.metadata.aggregated.BeanMetaData;
import org.hibernate.validator.internal.metadata.aggregated.BeanMetaDataImpl;
import org.hibernate.validator.internal.metadata.core.AnnotationProcessingOptions;
import org.hibernate.validator.internal.metadata.core.AnnotationProcessingOptionsImpl;
import org.hibernate.validator.internal.metadata.core.ConstraintHelper;
import org.hibernate.validator.internal.metadata.provider.AnnotationMetaDataProvider;
import org.hibernate.validator.internal.metadata.provider.MetaDataProvider;
import org.hibernate.validator.internal.metadata.raw.BeanConfiguration;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.hibernate.validator.internal.util.ConcurrentReferenceHashMap;
import org.hibernate.validator.internal.util.Contracts;
import org.hibernate.validator.internal.util.ExecutableHelper;
import org.hibernate.validator.internal.util.ExecutableParameterNameProvider;
import org.hibernate.validator.internal.util.TypeResolutionHelper;
import org.hibernate.validator.internal.util.classhierarchy.ClassHierarchyHelper;
import org.hibernate.validator.internal.util.classhierarchy.Filter;
import org.hibernate.validator.internal.util.logging.Messages;
import org.hibernate.validator.metadata.BeanMetaDataClassNormalizer;

public class BeanMetaDataManager {
    private static final int DEFAULT_INITIAL_CAPACITY = 16;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;
    private static final int DEFAULT_CONCURRENCY_LEVEL = 16;
    private final List<MetaDataProvider> metaDataProviders;
    private final ConstraintHelper constraintHelper;
    private final TypeResolutionHelper typeResolutionHelper;
    private final ValueExtractorManager valueExtractorManager;
    private final ExecutableParameterNameProvider parameterNameProvider;
    private final ConcurrentReferenceHashMap<Class<?>, BeanMetaData<?>> beanMetaDataCache;
    private final ExecutableHelper executableHelper;
    private final BeanMetaDataClassNormalizer beanMetaDataClassNormalizer;
    private final ValidationOrderGenerator validationOrderGenerator;
    private final MethodValidationConfiguration methodValidationConfiguration;

    public BeanMetaDataManager(ConstraintHelper constraintHelper, ExecutableHelper executableHelper, TypeResolutionHelper typeResolutionHelper, ExecutableParameterNameProvider parameterNameProvider, ValueExtractorManager valueExtractorManager, BeanMetaDataClassNormalizer beanMetaDataClassNormalizer, ValidationOrderGenerator validationOrderGenerator, List<MetaDataProvider> optionalMetaDataProviders, MethodValidationConfiguration methodValidationConfiguration) {
        this.constraintHelper = constraintHelper;
        this.executableHelper = executableHelper;
        this.typeResolutionHelper = typeResolutionHelper;
        this.valueExtractorManager = valueExtractorManager;
        this.parameterNameProvider = parameterNameProvider;
        this.beanMetaDataClassNormalizer = beanMetaDataClassNormalizer;
        this.validationOrderGenerator = validationOrderGenerator;
        this.metaDataProviders = CollectionHelper.newArrayList();
        this.metaDataProviders.addAll(optionalMetaDataProviders);
        this.methodValidationConfiguration = methodValidationConfiguration;
        this.beanMetaDataCache = new ConcurrentReferenceHashMap(16, 0.75f, 16, ConcurrentReferenceHashMap.ReferenceType.SOFT, ConcurrentReferenceHashMap.ReferenceType.SOFT, EnumSet.of(ConcurrentReferenceHashMap.Option.IDENTITY_COMPARISONS));
        AnnotationProcessingOptions annotationProcessingOptions = this.getAnnotationProcessingOptionsFromNonDefaultProviders();
        AnnotationMetaDataProvider defaultProvider = new AnnotationMetaDataProvider(constraintHelper, typeResolutionHelper, valueExtractorManager, annotationProcessingOptions);
        this.metaDataProviders.add(defaultProvider);
    }

    public <T> BeanMetaData<T> getBeanMetaData(Class<T> beanClass) {
        Contracts.assertNotNull(beanClass, Messages.MESSAGES.beanTypeCannotBeNull());
        Class<T> normalizedBeanClass = this.beanMetaDataClassNormalizer.normalize(beanClass);
        BeanMetaData<?> beanMetaData = this.beanMetaDataCache.get(normalizedBeanClass);
        if (beanMetaData != null) {
            return beanMetaData;
        }
        beanMetaData = this.createBeanMetaData(normalizedBeanClass);
        BeanMetaData<?> previousBeanMetaData = this.beanMetaDataCache.putIfAbsent(normalizedBeanClass, beanMetaData);
        if (previousBeanMetaData != null) {
            return previousBeanMetaData;
        }
        return beanMetaData;
    }

    public void clear() {
        this.beanMetaDataCache.clear();
    }

    public int numberOfCachedBeanMetaDataInstances() {
        return this.beanMetaDataCache.size();
    }

    private <T> BeanMetaDataImpl<T> createBeanMetaData(Class<T> clazz) {
        BeanMetaDataImpl.BeanMetaDataBuilder<T> builder = BeanMetaDataImpl.BeanMetaDataBuilder.getInstance(this.constraintHelper, this.executableHelper, this.typeResolutionHelper, this.valueExtractorManager, this.parameterNameProvider, this.validationOrderGenerator, clazz, this.methodValidationConfiguration);
        for (MetaDataProvider provider : this.metaDataProviders) {
            for (BeanConfiguration<T> beanConfiguration : this.getBeanConfigurationForHierarchy(provider, clazz)) {
                builder.add(beanConfiguration);
            }
        }
        return builder.build();
    }

    private AnnotationProcessingOptions getAnnotationProcessingOptionsFromNonDefaultProviders() {
        AnnotationProcessingOptionsImpl options = new AnnotationProcessingOptionsImpl();
        for (MetaDataProvider metaDataProvider : this.metaDataProviders) {
            options.merge(metaDataProvider.getAnnotationProcessingOptions());
        }
        return options;
    }

    private <T> List<BeanConfiguration<? super T>> getBeanConfigurationForHierarchy(MetaDataProvider provider, Class<T> beanClass) {
        ArrayList<BeanConfiguration<T>> configurations = CollectionHelper.newArrayList();
        for (Class<T> clazz : ClassHierarchyHelper.getHierarchy(beanClass, new Filter[0])) {
            BeanConfiguration<T> configuration = provider.getBeanConfiguration(clazz);
            if (configuration == null) continue;
            configurations.add(configuration);
        }
        return configurations;
    }
}

