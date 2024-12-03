/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.ClockProvider
 *  javax.validation.ConstraintValidatorFactory
 *  javax.validation.MessageInterpolator
 *  javax.validation.ParameterNameProvider
 *  javax.validation.TraversableResolver
 *  javax.validation.Validator
 *  javax.validation.spi.ConfigurationState
 */
package org.hibernate.validator.internal.engine;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandles;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.validation.ClockProvider;
import javax.validation.ConstraintValidatorFactory;
import javax.validation.MessageInterpolator;
import javax.validation.ParameterNameProvider;
import javax.validation.TraversableResolver;
import javax.validation.Validator;
import javax.validation.spi.ConfigurationState;
import org.hibernate.validator.HibernateValidatorContext;
import org.hibernate.validator.HibernateValidatorFactory;
import org.hibernate.validator.cfg.ConstraintMapping;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorInitializationContext;
import org.hibernate.validator.internal.cfg.context.DefaultConstraintMapping;
import org.hibernate.validator.internal.engine.ConfigurationImpl;
import org.hibernate.validator.internal.engine.MethodValidationConfiguration;
import org.hibernate.validator.internal.engine.ServiceLoaderBasedConstraintMappingContributor;
import org.hibernate.validator.internal.engine.ValidatorContextImpl;
import org.hibernate.validator.internal.engine.ValidatorImpl;
import org.hibernate.validator.internal.engine.constraintdefinition.ConstraintDefinitionContribution;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorManager;
import org.hibernate.validator.internal.engine.constraintvalidation.HibernateConstraintValidatorInitializationContextImpl;
import org.hibernate.validator.internal.engine.groups.ValidationOrderGenerator;
import org.hibernate.validator.internal.engine.scripting.DefaultScriptEvaluatorFactory;
import org.hibernate.validator.internal.engine.valueextraction.ValueExtractorManager;
import org.hibernate.validator.internal.metadata.BeanMetaDataManager;
import org.hibernate.validator.internal.metadata.DefaultBeanMetaDataClassNormalizer;
import org.hibernate.validator.internal.metadata.core.ConstraintHelper;
import org.hibernate.validator.internal.metadata.provider.MetaDataProvider;
import org.hibernate.validator.internal.metadata.provider.ProgrammaticMetaDataProvider;
import org.hibernate.validator.internal.metadata.provider.XmlMetaDataProvider;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.hibernate.validator.internal.util.Contracts;
import org.hibernate.validator.internal.util.ExecutableHelper;
import org.hibernate.validator.internal.util.ExecutableParameterNameProvider;
import org.hibernate.validator.internal.util.StringHelper;
import org.hibernate.validator.internal.util.TypeResolutionHelper;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.hibernate.validator.internal.util.privilegedactions.GetClassLoader;
import org.hibernate.validator.internal.util.privilegedactions.LoadClass;
import org.hibernate.validator.internal.util.privilegedactions.NewInstance;
import org.hibernate.validator.metadata.BeanMetaDataClassNormalizer;
import org.hibernate.validator.spi.cfg.ConstraintMappingContributor;
import org.hibernate.validator.spi.scripting.ScriptEvaluatorFactory;

public class ValidatorFactoryImpl
implements HibernateValidatorFactory {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private final ValidatorFactoryScopedContext validatorFactoryScopedContext;
    private final ConstraintValidatorManager constraintValidatorManager;
    private final Set<DefaultConstraintMapping> constraintMappings;
    private final ConstraintHelper constraintHelper;
    private final TypeResolutionHelper typeResolutionHelper;
    private final ExecutableHelper executableHelper;
    private final MethodValidationConfiguration methodValidationConfiguration;
    private final XmlMetaDataProvider xmlMetaDataProvider;
    private final ConcurrentMap<BeanMetaDataManagerKey, BeanMetaDataManager> beanMetaDataManagers;
    private final ValueExtractorManager valueExtractorManager;
    private final BeanMetaDataClassNormalizer beanMetadataClassNormalizer;
    private final ValidationOrderGenerator validationOrderGenerator;

    public ValidatorFactoryImpl(ConfigurationState configurationState) {
        ClassLoader externalClassLoader = ValidatorFactoryImpl.getExternalClassLoader(configurationState);
        this.valueExtractorManager = new ValueExtractorManager(configurationState.getValueExtractors());
        this.beanMetaDataManagers = new ConcurrentHashMap<BeanMetaDataManagerKey, BeanMetaDataManager>();
        this.constraintHelper = new ConstraintHelper();
        this.typeResolutionHelper = new TypeResolutionHelper();
        this.executableHelper = new ExecutableHelper(this.typeResolutionHelper);
        ConfigurationImpl hibernateSpecificConfig = null;
        if (configurationState instanceof ConfigurationImpl) {
            hibernateSpecificConfig = (ConfigurationImpl)configurationState;
        }
        this.xmlMetaDataProvider = configurationState.getMappingStreams().isEmpty() ? null : new XmlMetaDataProvider(this.constraintHelper, this.typeResolutionHelper, this.valueExtractorManager, configurationState.getMappingStreams(), externalClassLoader);
        this.constraintMappings = Collections.unmodifiableSet(ValidatorFactoryImpl.getConstraintMappings(this.typeResolutionHelper, configurationState, externalClassLoader));
        ValidatorFactoryImpl.registerCustomConstraintValidators(this.constraintMappings, this.constraintHelper);
        Map properties = configurationState.getProperties();
        this.methodValidationConfiguration = new MethodValidationConfiguration.Builder().allowOverridingMethodAlterParameterConstraint(ValidatorFactoryImpl.getAllowOverridingMethodAlterParameterConstraint(hibernateSpecificConfig, properties)).allowMultipleCascadedValidationOnReturnValues(ValidatorFactoryImpl.getAllowMultipleCascadedValidationOnReturnValues(hibernateSpecificConfig, properties)).allowParallelMethodsDefineParameterConstraints(ValidatorFactoryImpl.getAllowParallelMethodsDefineParameterConstraints(hibernateSpecificConfig, properties)).build();
        this.validatorFactoryScopedContext = new ValidatorFactoryScopedContext(configurationState.getMessageInterpolator(), configurationState.getTraversableResolver(), new ExecutableParameterNameProvider(configurationState.getParameterNameProvider()), configurationState.getClockProvider(), this.getTemporalValidationTolerance(configurationState, properties), ValidatorFactoryImpl.getScriptEvaluatorFactory(configurationState, properties, externalClassLoader), ValidatorFactoryImpl.getFailFast(hibernateSpecificConfig, properties), ValidatorFactoryImpl.getTraversableResolverResultCacheEnabled(hibernateSpecificConfig, properties), this.getConstraintValidatorPayload(hibernateSpecificConfig));
        this.constraintValidatorManager = new ConstraintValidatorManager(configurationState.getConstraintValidatorFactory(), this.validatorFactoryScopedContext.getConstraintValidatorInitializationContext());
        this.validationOrderGenerator = new ValidationOrderGenerator();
        BeanMetaDataClassNormalizer beanMetaDataClassNormalizer = this.beanMetadataClassNormalizer = hibernateSpecificConfig != null && hibernateSpecificConfig.getBeanMetaDataClassNormalizer() != null ? hibernateSpecificConfig.getBeanMetaDataClassNormalizer() : new DefaultBeanMetaDataClassNormalizer();
        if (LOG.isDebugEnabled()) {
            ValidatorFactoryImpl.logValidatorFactoryScopedConfiguration(this.validatorFactoryScopedContext);
        }
    }

    private static ClassLoader getExternalClassLoader(ConfigurationState configurationState) {
        return configurationState instanceof ConfigurationImpl ? ((ConfigurationImpl)configurationState).getExternalClassLoader() : null;
    }

    private static Set<DefaultConstraintMapping> getConstraintMappings(TypeResolutionHelper typeResolutionHelper, ConfigurationState configurationState, ClassLoader externalClassLoader) {
        HashSet<DefaultConstraintMapping> constraintMappings = CollectionHelper.newHashSet();
        if (configurationState instanceof ConfigurationImpl) {
            ConfigurationImpl hibernateConfiguration = (ConfigurationImpl)configurationState;
            constraintMappings.addAll(hibernateConfiguration.getProgrammaticMappings());
            ServiceLoaderBasedConstraintMappingContributor serviceLoaderBasedContributor = new ServiceLoaderBasedConstraintMappingContributor(typeResolutionHelper, externalClassLoader != null ? externalClassLoader : ValidatorFactoryImpl.run(GetClassLoader.fromContext()));
            DefaultConstraintMappingBuilder builder = new DefaultConstraintMappingBuilder(constraintMappings);
            serviceLoaderBasedContributor.createConstraintMappings(builder);
        }
        List<ConstraintMappingContributor> contributors = ValidatorFactoryImpl.getPropertyConfiguredConstraintMappingContributors(configurationState.getProperties(), externalClassLoader);
        for (ConstraintMappingContributor contributor : contributors) {
            DefaultConstraintMappingBuilder builder = new DefaultConstraintMappingBuilder(constraintMappings);
            contributor.createConstraintMappings(builder);
        }
        return constraintMappings;
    }

    public Validator getValidator() {
        return this.createValidator(this.constraintValidatorManager.getDefaultConstraintValidatorFactory(), this.valueExtractorManager, this.validatorFactoryScopedContext, this.methodValidationConfiguration);
    }

    public MessageInterpolator getMessageInterpolator() {
        return this.validatorFactoryScopedContext.getMessageInterpolator();
    }

    public TraversableResolver getTraversableResolver() {
        return this.validatorFactoryScopedContext.getTraversableResolver();
    }

    public ConstraintValidatorFactory getConstraintValidatorFactory() {
        return this.constraintValidatorManager.getDefaultConstraintValidatorFactory();
    }

    public ParameterNameProvider getParameterNameProvider() {
        return this.validatorFactoryScopedContext.getParameterNameProvider().getDelegate();
    }

    public ExecutableParameterNameProvider getExecutableParameterNameProvider() {
        return this.validatorFactoryScopedContext.getParameterNameProvider();
    }

    public ClockProvider getClockProvider() {
        return this.validatorFactoryScopedContext.getClockProvider();
    }

    @Override
    public ScriptEvaluatorFactory getScriptEvaluatorFactory() {
        return this.validatorFactoryScopedContext.getScriptEvaluatorFactory();
    }

    @Override
    public Duration getTemporalValidationTolerance() {
        return this.validatorFactoryScopedContext.getTemporalValidationTolerance();
    }

    public boolean isFailFast() {
        return this.validatorFactoryScopedContext.isFailFast();
    }

    MethodValidationConfiguration getMethodValidationConfiguration() {
        return this.methodValidationConfiguration;
    }

    public boolean isTraversableResolverResultCacheEnabled() {
        return this.validatorFactoryScopedContext.isTraversableResolverResultCacheEnabled();
    }

    ValueExtractorManager getValueExtractorManager() {
        return this.valueExtractorManager;
    }

    public <T> T unwrap(Class<T> type) {
        if (type.isAssignableFrom(HibernateValidatorFactory.class)) {
            return type.cast(this);
        }
        throw LOG.getTypeNotSupportedForUnwrappingException(type);
    }

    @Override
    public HibernateValidatorContext usingContext() {
        return new ValidatorContextImpl(this);
    }

    public void close() {
        this.constraintValidatorManager.clear();
        this.constraintHelper.clear();
        for (BeanMetaDataManager beanMetaDataManager : this.beanMetaDataManagers.values()) {
            beanMetaDataManager.clear();
        }
        this.validatorFactoryScopedContext.getScriptEvaluatorFactory().clear();
        this.valueExtractorManager.clear();
    }

    public ValidatorFactoryScopedContext getValidatorFactoryScopedContext() {
        return this.validatorFactoryScopedContext;
    }

    Validator createValidator(ConstraintValidatorFactory constraintValidatorFactory, ValueExtractorManager valueExtractorManager, ValidatorFactoryScopedContext validatorFactoryScopedContext, MethodValidationConfiguration methodValidationConfiguration) {
        BeanMetaDataManager beanMetaDataManager = this.beanMetaDataManagers.computeIfAbsent(new BeanMetaDataManagerKey(validatorFactoryScopedContext.getParameterNameProvider(), valueExtractorManager, methodValidationConfiguration), key -> new BeanMetaDataManager(this.constraintHelper, this.executableHelper, this.typeResolutionHelper, validatorFactoryScopedContext.getParameterNameProvider(), valueExtractorManager, this.beanMetadataClassNormalizer, this.validationOrderGenerator, this.buildDataProviders(), methodValidationConfiguration));
        return new ValidatorImpl(constraintValidatorFactory, beanMetaDataManager, valueExtractorManager, this.constraintValidatorManager, this.validationOrderGenerator, validatorFactoryScopedContext);
    }

    private List<MetaDataProvider> buildDataProviders() {
        ArrayList<MetaDataProvider> metaDataProviders = CollectionHelper.newArrayList();
        if (this.xmlMetaDataProvider != null) {
            metaDataProviders.add(this.xmlMetaDataProvider);
        }
        if (!this.constraintMappings.isEmpty()) {
            metaDataProviders.add(new ProgrammaticMetaDataProvider(this.constraintHelper, this.typeResolutionHelper, this.valueExtractorManager, this.constraintMappings));
        }
        return metaDataProviders;
    }

    private static boolean checkPropertiesForBoolean(Map<String, String> properties, String propertyKey, boolean programmaticValue) {
        boolean value = programmaticValue;
        String propertyStringValue = properties.get(propertyKey);
        if (propertyStringValue != null) {
            value = Boolean.valueOf(propertyStringValue);
        }
        return value;
    }

    private static List<ConstraintMappingContributor> getPropertyConfiguredConstraintMappingContributors(Map<String, String> properties, ClassLoader externalClassLoader) {
        String deprecatedPropertyValue = properties.get("hibernate.validator.constraint_mapping_contributor");
        String propertyValue = properties.get("hibernate.validator.constraint_mapping_contributors");
        if (StringHelper.isNullOrEmptyString(deprecatedPropertyValue) && StringHelper.isNullOrEmptyString(propertyValue)) {
            return Collections.emptyList();
        }
        StringBuilder assembledPropertyValue = new StringBuilder();
        if (!StringHelper.isNullOrEmptyString(deprecatedPropertyValue)) {
            assembledPropertyValue.append(deprecatedPropertyValue);
        }
        if (!StringHelper.isNullOrEmptyString(propertyValue)) {
            if (assembledPropertyValue.length() > 0) {
                assembledPropertyValue.append(",");
            }
            assembledPropertyValue.append(propertyValue);
        }
        String[] contributorNames = assembledPropertyValue.toString().split(",");
        ArrayList<ConstraintMappingContributor> contributors = CollectionHelper.newArrayList(contributorNames.length);
        for (String contributorName : contributorNames) {
            Class contributorType = (Class)ValidatorFactoryImpl.run(LoadClass.action(contributorName, externalClassLoader));
            contributors.add((ConstraintMappingContributor)ValidatorFactoryImpl.run(NewInstance.action(contributorType, "constraint mapping contributor class")));
        }
        return contributors;
    }

    private static boolean getAllowParallelMethodsDefineParameterConstraints(ConfigurationImpl hibernateSpecificConfig, Map<String, String> properties) {
        return ValidatorFactoryImpl.checkPropertiesForBoolean(properties, "hibernate.validator.allow_parallel_method_parameter_constraint", hibernateSpecificConfig != null ? hibernateSpecificConfig.getMethodValidationConfiguration().isAllowParallelMethodsDefineParameterConstraints() : false);
    }

    private static boolean getAllowMultipleCascadedValidationOnReturnValues(ConfigurationImpl hibernateSpecificConfig, Map<String, String> properties) {
        return ValidatorFactoryImpl.checkPropertiesForBoolean(properties, "hibernate.validator.allow_multiple_cascaded_validation_on_result", hibernateSpecificConfig != null ? hibernateSpecificConfig.getMethodValidationConfiguration().isAllowMultipleCascadedValidationOnReturnValues() : false);
    }

    private static boolean getAllowOverridingMethodAlterParameterConstraint(ConfigurationImpl hibernateSpecificConfig, Map<String, String> properties) {
        return ValidatorFactoryImpl.checkPropertiesForBoolean(properties, "hibernate.validator.allow_parameter_constraint_override", hibernateSpecificConfig != null ? hibernateSpecificConfig.getMethodValidationConfiguration().isAllowOverridingMethodAlterParameterConstraint() : false);
    }

    private static boolean getTraversableResolverResultCacheEnabled(ConfigurationImpl configuration, Map<String, String> properties) {
        return ValidatorFactoryImpl.checkPropertiesForBoolean(properties, "hibernate.validator.enable_traversable_resolver_result_cache", configuration != null ? configuration.isTraversableResolverResultCacheEnabled() : true);
    }

    private static boolean getFailFast(ConfigurationImpl configuration, Map<String, String> properties) {
        boolean tmpFailFast = configuration != null ? configuration.getFailFast() : false;
        String propertyStringValue = properties.get("hibernate.validator.fail_fast");
        if (propertyStringValue != null) {
            boolean configurationValue = Boolean.valueOf(propertyStringValue);
            if (tmpFailFast && !configurationValue) {
                throw LOG.getInconsistentFailFastConfigurationException();
            }
            tmpFailFast = configurationValue;
        }
        return tmpFailFast;
    }

    private static ScriptEvaluatorFactory getScriptEvaluatorFactory(ConfigurationState configurationState, Map<String, String> properties, ClassLoader externalClassLoader) {
        ConfigurationImpl hibernateSpecificConfig;
        if (configurationState instanceof ConfigurationImpl && (hibernateSpecificConfig = (ConfigurationImpl)configurationState).getScriptEvaluatorFactory() != null) {
            LOG.usingScriptEvaluatorFactory(hibernateSpecificConfig.getScriptEvaluatorFactory().getClass());
            return hibernateSpecificConfig.getScriptEvaluatorFactory();
        }
        String scriptEvaluatorFactoryFqcn = properties.get("hibernate.validator.script_evaluator_factory");
        if (scriptEvaluatorFactoryFqcn != null) {
            try {
                Class clazz = (Class)ValidatorFactoryImpl.run(LoadClass.action(scriptEvaluatorFactoryFqcn, externalClassLoader));
                ScriptEvaluatorFactory scriptEvaluatorFactory = (ScriptEvaluatorFactory)ValidatorFactoryImpl.run(NewInstance.action(clazz, "script evaluator factory class"));
                LOG.usingScriptEvaluatorFactory(clazz);
                return scriptEvaluatorFactory;
            }
            catch (Exception e) {
                throw LOG.getUnableToInstantiateScriptEvaluatorFactoryClassException(scriptEvaluatorFactoryFqcn, e);
            }
        }
        return new DefaultScriptEvaluatorFactory(externalClassLoader);
    }

    private Duration getTemporalValidationTolerance(ConfigurationState configurationState, Map<String, String> properties) {
        ConfigurationImpl hibernateSpecificConfig;
        if (configurationState instanceof ConfigurationImpl && (hibernateSpecificConfig = (ConfigurationImpl)configurationState).getTemporalValidationTolerance() != null) {
            LOG.logTemporalValidationTolerance(hibernateSpecificConfig.getTemporalValidationTolerance());
            return hibernateSpecificConfig.getTemporalValidationTolerance();
        }
        String temporalValidationToleranceProperty = properties.get("hibernate.validator.temporal_validation_tolerance");
        if (temporalValidationToleranceProperty != null) {
            try {
                Duration tolerance = Duration.ofMillis(Long.parseLong(temporalValidationToleranceProperty)).abs();
                LOG.logTemporalValidationTolerance(tolerance);
                return tolerance;
            }
            catch (Exception e) {
                throw LOG.getUnableToParseTemporalValidationToleranceException(temporalValidationToleranceProperty, e);
            }
        }
        return Duration.ZERO;
    }

    private Object getConstraintValidatorPayload(ConfigurationState configurationState) {
        ConfigurationImpl hibernateSpecificConfig;
        if (configurationState instanceof ConfigurationImpl && (hibernateSpecificConfig = (ConfigurationImpl)configurationState).getConstraintValidatorPayload() != null) {
            LOG.logConstraintValidatorPayload(hibernateSpecificConfig.getConstraintValidatorPayload());
            return hibernateSpecificConfig.getConstraintValidatorPayload();
        }
        return null;
    }

    private static void registerCustomConstraintValidators(Set<DefaultConstraintMapping> constraintMappings, ConstraintHelper constraintHelper) {
        HashSet<Class<?>> definedConstraints = CollectionHelper.newHashSet();
        for (DefaultConstraintMapping constraintMapping : constraintMappings) {
            for (ConstraintDefinitionContribution<?> contribution : constraintMapping.getConstraintDefinitionContributions()) {
                ValidatorFactoryImpl.processConstraintDefinitionContribution(contribution, constraintHelper, definedConstraints);
            }
        }
    }

    private static <A extends Annotation> void processConstraintDefinitionContribution(ConstraintDefinitionContribution<A> constraintDefinitionContribution, ConstraintHelper constraintHelper, Set<Class<?>> definedConstraints) {
        Class<A> constraintType = constraintDefinitionContribution.getConstraintType();
        if (definedConstraints.contains(constraintType)) {
            throw LOG.getConstraintHasAlreadyBeenConfiguredViaProgrammaticApiException(constraintType);
        }
        definedConstraints.add(constraintType);
        constraintHelper.putValidatorDescriptors(constraintType, constraintDefinitionContribution.getValidatorDescriptors(), constraintDefinitionContribution.includeExisting());
    }

    private static void logValidatorFactoryScopedConfiguration(ValidatorFactoryScopedContext context) {
        LOG.logValidatorFactoryScopedConfiguration(context.getMessageInterpolator().getClass(), "message interpolator");
        LOG.logValidatorFactoryScopedConfiguration(context.getTraversableResolver().getClass(), "traversable resolver");
        LOG.logValidatorFactoryScopedConfiguration(context.getParameterNameProvider().getClass(), "parameter name provider");
        LOG.logValidatorFactoryScopedConfiguration(context.getClockProvider().getClass(), "clock provider");
        LOG.logValidatorFactoryScopedConfiguration(context.getScriptEvaluatorFactory().getClass(), "script evaluator factory");
    }

    private static <T> T run(PrivilegedAction<T> action) {
        return System.getSecurityManager() != null ? AccessController.doPrivileged(action) : action.run();
    }

    static class ValidatorFactoryScopedContext {
        private final MessageInterpolator messageInterpolator;
        private final TraversableResolver traversableResolver;
        private final ExecutableParameterNameProvider parameterNameProvider;
        private final ClockProvider clockProvider;
        private final Duration temporalValidationTolerance;
        private final ScriptEvaluatorFactory scriptEvaluatorFactory;
        private final boolean failFast;
        private final boolean traversableResolverResultCacheEnabled;
        private final Object constraintValidatorPayload;
        private final HibernateConstraintValidatorInitializationContextImpl constraintValidatorInitializationContext;

        private ValidatorFactoryScopedContext(MessageInterpolator messageInterpolator, TraversableResolver traversableResolver, ExecutableParameterNameProvider parameterNameProvider, ClockProvider clockProvider, Duration temporalValidationTolerance, ScriptEvaluatorFactory scriptEvaluatorFactory, boolean failFast, boolean traversableResolverResultCacheEnabled, Object constraintValidatorPayload) {
            this(messageInterpolator, traversableResolver, parameterNameProvider, clockProvider, temporalValidationTolerance, scriptEvaluatorFactory, failFast, traversableResolverResultCacheEnabled, constraintValidatorPayload, new HibernateConstraintValidatorInitializationContextImpl(scriptEvaluatorFactory, clockProvider, temporalValidationTolerance));
        }

        private ValidatorFactoryScopedContext(MessageInterpolator messageInterpolator, TraversableResolver traversableResolver, ExecutableParameterNameProvider parameterNameProvider, ClockProvider clockProvider, Duration temporalValidationTolerance, ScriptEvaluatorFactory scriptEvaluatorFactory, boolean failFast, boolean traversableResolverResultCacheEnabled, Object constraintValidatorPayload, HibernateConstraintValidatorInitializationContextImpl constraintValidatorInitializationContext) {
            this.messageInterpolator = messageInterpolator;
            this.traversableResolver = traversableResolver;
            this.parameterNameProvider = parameterNameProvider;
            this.clockProvider = clockProvider;
            this.temporalValidationTolerance = temporalValidationTolerance;
            this.scriptEvaluatorFactory = scriptEvaluatorFactory;
            this.failFast = failFast;
            this.traversableResolverResultCacheEnabled = traversableResolverResultCacheEnabled;
            this.constraintValidatorPayload = constraintValidatorPayload;
            this.constraintValidatorInitializationContext = constraintValidatorInitializationContext;
        }

        public MessageInterpolator getMessageInterpolator() {
            return this.messageInterpolator;
        }

        public TraversableResolver getTraversableResolver() {
            return this.traversableResolver;
        }

        public ExecutableParameterNameProvider getParameterNameProvider() {
            return this.parameterNameProvider;
        }

        public ClockProvider getClockProvider() {
            return this.clockProvider;
        }

        public Duration getTemporalValidationTolerance() {
            return this.temporalValidationTolerance;
        }

        public ScriptEvaluatorFactory getScriptEvaluatorFactory() {
            return this.scriptEvaluatorFactory;
        }

        public boolean isFailFast() {
            return this.failFast;
        }

        public boolean isTraversableResolverResultCacheEnabled() {
            return this.traversableResolverResultCacheEnabled;
        }

        public Object getConstraintValidatorPayload() {
            return this.constraintValidatorPayload;
        }

        public HibernateConstraintValidatorInitializationContext getConstraintValidatorInitializationContext() {
            return this.constraintValidatorInitializationContext;
        }

        static class Builder {
            private final ValidatorFactoryScopedContext defaultContext;
            private MessageInterpolator messageInterpolator;
            private TraversableResolver traversableResolver;
            private ExecutableParameterNameProvider parameterNameProvider;
            private ClockProvider clockProvider;
            private ScriptEvaluatorFactory scriptEvaluatorFactory;
            private Duration temporalValidationTolerance;
            private boolean failFast;
            private boolean traversableResolverResultCacheEnabled;
            private Object constraintValidatorPayload;
            private HibernateConstraintValidatorInitializationContextImpl constraintValidatorInitializationContext;

            Builder(ValidatorFactoryScopedContext defaultContext) {
                Contracts.assertNotNull(defaultContext, "Default context cannot be null.");
                this.defaultContext = defaultContext;
                this.messageInterpolator = defaultContext.messageInterpolator;
                this.traversableResolver = defaultContext.traversableResolver;
                this.parameterNameProvider = defaultContext.parameterNameProvider;
                this.clockProvider = defaultContext.clockProvider;
                this.scriptEvaluatorFactory = defaultContext.scriptEvaluatorFactory;
                this.temporalValidationTolerance = defaultContext.temporalValidationTolerance;
                this.failFast = defaultContext.failFast;
                this.traversableResolverResultCacheEnabled = defaultContext.traversableResolverResultCacheEnabled;
                this.constraintValidatorPayload = defaultContext.constraintValidatorPayload;
                this.constraintValidatorInitializationContext = defaultContext.constraintValidatorInitializationContext;
            }

            public Builder setMessageInterpolator(MessageInterpolator messageInterpolator) {
                this.messageInterpolator = messageInterpolator == null ? this.defaultContext.messageInterpolator : messageInterpolator;
                return this;
            }

            public Builder setTraversableResolver(TraversableResolver traversableResolver) {
                this.traversableResolver = traversableResolver == null ? this.defaultContext.traversableResolver : traversableResolver;
                return this;
            }

            public Builder setParameterNameProvider(ParameterNameProvider parameterNameProvider) {
                this.parameterNameProvider = parameterNameProvider == null ? this.defaultContext.parameterNameProvider : new ExecutableParameterNameProvider(parameterNameProvider);
                return this;
            }

            public Builder setClockProvider(ClockProvider clockProvider) {
                this.clockProvider = clockProvider == null ? this.defaultContext.clockProvider : clockProvider;
                return this;
            }

            public Builder setTemporalValidationTolerance(Duration temporalValidationTolerance) {
                this.temporalValidationTolerance = temporalValidationTolerance == null ? Duration.ZERO : temporalValidationTolerance.abs();
                return this;
            }

            public Builder setScriptEvaluatorFactory(ScriptEvaluatorFactory scriptEvaluatorFactory) {
                this.scriptEvaluatorFactory = scriptEvaluatorFactory == null ? this.defaultContext.scriptEvaluatorFactory : scriptEvaluatorFactory;
                return this;
            }

            public Builder setFailFast(boolean failFast) {
                this.failFast = failFast;
                return this;
            }

            public Builder setTraversableResolverResultCacheEnabled(boolean traversableResolverResultCacheEnabled) {
                this.traversableResolverResultCacheEnabled = traversableResolverResultCacheEnabled;
                return this;
            }

            public Builder setConstraintValidatorPayload(Object constraintValidatorPayload) {
                this.constraintValidatorPayload = constraintValidatorPayload;
                return this;
            }

            public ValidatorFactoryScopedContext build() {
                return new ValidatorFactoryScopedContext(this.messageInterpolator, this.traversableResolver, this.parameterNameProvider, this.clockProvider, this.temporalValidationTolerance, this.scriptEvaluatorFactory, this.failFast, this.traversableResolverResultCacheEnabled, this.constraintValidatorPayload, HibernateConstraintValidatorInitializationContextImpl.of(this.constraintValidatorInitializationContext, this.scriptEvaluatorFactory, this.clockProvider, this.temporalValidationTolerance));
            }
        }
    }

    private static class BeanMetaDataManagerKey {
        private final ExecutableParameterNameProvider parameterNameProvider;
        private final ValueExtractorManager valueExtractorManager;
        private final MethodValidationConfiguration methodValidationConfiguration;
        private final int hashCode;

        public BeanMetaDataManagerKey(ExecutableParameterNameProvider parameterNameProvider, ValueExtractorManager valueExtractorManager, MethodValidationConfiguration methodValidationConfiguration) {
            this.parameterNameProvider = parameterNameProvider;
            this.valueExtractorManager = valueExtractorManager;
            this.methodValidationConfiguration = methodValidationConfiguration;
            this.hashCode = BeanMetaDataManagerKey.buildHashCode(parameterNameProvider, valueExtractorManager, methodValidationConfiguration);
        }

        private static int buildHashCode(ExecutableParameterNameProvider parameterNameProvider, ValueExtractorManager valueExtractorManager, MethodValidationConfiguration methodValidationConfiguration) {
            int prime = 31;
            int result = 1;
            result = 31 * result + (methodValidationConfiguration == null ? 0 : methodValidationConfiguration.hashCode());
            result = 31 * result + (parameterNameProvider == null ? 0 : parameterNameProvider.hashCode());
            result = 31 * result + (valueExtractorManager == null ? 0 : valueExtractorManager.hashCode());
            return result;
        }

        public int hashCode() {
            return this.hashCode;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            BeanMetaDataManagerKey other = (BeanMetaDataManagerKey)obj;
            return this.methodValidationConfiguration.equals(other.methodValidationConfiguration) && this.parameterNameProvider.equals(other.parameterNameProvider) && this.valueExtractorManager.equals(other.valueExtractorManager);
        }

        public String toString() {
            return "BeanMetaDataManagerKey [parameterNameProvider=" + this.parameterNameProvider + ", valueExtractorManager=" + this.valueExtractorManager + ", methodValidationConfiguration=" + this.methodValidationConfiguration + "]";
        }
    }

    private static class DefaultConstraintMappingBuilder
    implements ConstraintMappingContributor.ConstraintMappingBuilder {
        private final Set<DefaultConstraintMapping> mappings;

        public DefaultConstraintMappingBuilder(Set<DefaultConstraintMapping> mappings) {
            this.mappings = mappings;
        }

        @Override
        public ConstraintMapping addConstraintMapping() {
            DefaultConstraintMapping mapping = new DefaultConstraintMapping();
            this.mappings.add(mapping);
            return mapping;
        }
    }
}

