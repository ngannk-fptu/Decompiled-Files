/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.metadata.provider;

import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.hibernate.validator.internal.cfg.context.DefaultConstraintMapping;
import org.hibernate.validator.internal.engine.valueextraction.ValueExtractorManager;
import org.hibernate.validator.internal.metadata.core.AnnotationProcessingOptions;
import org.hibernate.validator.internal.metadata.core.AnnotationProcessingOptionsImpl;
import org.hibernate.validator.internal.metadata.core.ConstraintHelper;
import org.hibernate.validator.internal.metadata.provider.MetaDataProvider;
import org.hibernate.validator.internal.metadata.raw.BeanConfiguration;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.hibernate.validator.internal.util.Contracts;
import org.hibernate.validator.internal.util.TypeResolutionHelper;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

public class ProgrammaticMetaDataProvider
implements MetaDataProvider {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private final Map<String, BeanConfiguration<?>> configuredBeans;
    private final AnnotationProcessingOptions annotationProcessingOptions;

    public ProgrammaticMetaDataProvider(ConstraintHelper constraintHelper, TypeResolutionHelper typeResolutionHelper, ValueExtractorManager valueExtractorManager, Set<DefaultConstraintMapping> constraintMappings) {
        Contracts.assertNotNull(constraintMappings);
        this.configuredBeans = CollectionHelper.toImmutableMap(ProgrammaticMetaDataProvider.createBeanConfigurations(constraintMappings, constraintHelper, typeResolutionHelper, valueExtractorManager));
        ProgrammaticMetaDataProvider.assertUniquenessOfConfiguredTypes(constraintMappings);
        this.annotationProcessingOptions = ProgrammaticMetaDataProvider.mergeAnnotationProcessingOptions(constraintMappings);
    }

    private static void assertUniquenessOfConfiguredTypes(Set<DefaultConstraintMapping> mappings) {
        HashSet allConfiguredTypes = CollectionHelper.newHashSet();
        for (DefaultConstraintMapping constraintMapping : mappings) {
            for (Class<?> configuredType : constraintMapping.getConfiguredTypes()) {
                if (!allConfiguredTypes.contains(configuredType)) continue;
                throw LOG.getBeanClassHasAlreadyBeConfiguredViaProgrammaticApiException(configuredType);
            }
            allConfiguredTypes.addAll(constraintMapping.getConfiguredTypes());
        }
    }

    private static Map<String, BeanConfiguration<?>> createBeanConfigurations(Set<DefaultConstraintMapping> mappings, ConstraintHelper constraintHelper, TypeResolutionHelper typeResolutionHelper, ValueExtractorManager valueExtractorManager) {
        HashMap configuredBeans = new HashMap();
        for (DefaultConstraintMapping mapping : mappings) {
            Set<BeanConfiguration<?>> beanConfigurations = mapping.getBeanConfigurations(constraintHelper, typeResolutionHelper, valueExtractorManager);
            for (BeanConfiguration<?> beanConfiguration : beanConfigurations) {
                configuredBeans.put(beanConfiguration.getBeanClass().getName(), beanConfiguration);
            }
        }
        return configuredBeans;
    }

    private static AnnotationProcessingOptions mergeAnnotationProcessingOptions(Set<DefaultConstraintMapping> mappings) {
        if (mappings.size() == 1) {
            return mappings.iterator().next().getAnnotationProcessingOptions();
        }
        AnnotationProcessingOptionsImpl options = new AnnotationProcessingOptionsImpl();
        for (DefaultConstraintMapping mapping : mappings) {
            options.merge(mapping.getAnnotationProcessingOptions());
        }
        return options;
    }

    public <T> BeanConfiguration<T> getBeanConfiguration(Class<T> beanClass) {
        return this.configuredBeans.get(beanClass.getName());
    }

    @Override
    public AnnotationProcessingOptions getAnnotationProcessingOptions() {
        return this.annotationProcessingOptions;
    }
}

