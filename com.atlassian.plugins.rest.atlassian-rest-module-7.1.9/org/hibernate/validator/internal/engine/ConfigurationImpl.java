/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.BootstrapConfiguration
 *  javax.validation.ClockProvider
 *  javax.validation.ConstraintValidatorFactory
 *  javax.validation.MessageInterpolator
 *  javax.validation.ParameterNameProvider
 *  javax.validation.TraversableResolver
 *  javax.validation.ValidationProviderResolver
 *  javax.validation.ValidatorFactory
 *  javax.validation.spi.BootstrapState
 *  javax.validation.spi.ConfigurationState
 *  javax.validation.spi.ValidationProvider
 *  javax.validation.valueextraction.ValueExtractor
 */
package org.hibernate.validator.internal.engine;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.BootstrapConfiguration;
import javax.validation.ClockProvider;
import javax.validation.ConstraintValidatorFactory;
import javax.validation.MessageInterpolator;
import javax.validation.ParameterNameProvider;
import javax.validation.TraversableResolver;
import javax.validation.ValidationProviderResolver;
import javax.validation.ValidatorFactory;
import javax.validation.spi.BootstrapState;
import javax.validation.spi.ConfigurationState;
import javax.validation.spi.ValidationProvider;
import javax.validation.valueextraction.ValueExtractor;
import org.hibernate.validator.HibernateValidatorConfiguration;
import org.hibernate.validator.cfg.ConstraintMapping;
import org.hibernate.validator.internal.cfg.context.DefaultConstraintMapping;
import org.hibernate.validator.internal.engine.DefaultClockProvider;
import org.hibernate.validator.internal.engine.DefaultParameterNameProvider;
import org.hibernate.validator.internal.engine.MethodValidationConfiguration;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorFactoryImpl;
import org.hibernate.validator.internal.engine.resolver.TraversableResolvers;
import org.hibernate.validator.internal.engine.valueextraction.ValueExtractorDescriptor;
import org.hibernate.validator.internal.engine.valueextraction.ValueExtractorManager;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.hibernate.validator.internal.util.Contracts;
import org.hibernate.validator.internal.util.Version;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.hibernate.validator.internal.util.logging.Messages;
import org.hibernate.validator.internal.util.privilegedactions.GetClassLoader;
import org.hibernate.validator.internal.util.privilegedactions.GetInstancesFromServiceLoader;
import org.hibernate.validator.internal.util.privilegedactions.SetContextClassLoader;
import org.hibernate.validator.internal.xml.config.ValidationBootstrapParameters;
import org.hibernate.validator.internal.xml.config.ValidationXmlParser;
import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.hibernate.validator.metadata.BeanMetaDataClassNormalizer;
import org.hibernate.validator.resourceloading.PlatformResourceBundleLocator;
import org.hibernate.validator.spi.resourceloading.ResourceBundleLocator;
import org.hibernate.validator.spi.scripting.ScriptEvaluatorFactory;

public class ConfigurationImpl
implements HibernateValidatorConfiguration,
ConfigurationState {
    private static final Log LOG;
    private final ResourceBundleLocator defaultResourceBundleLocator;
    private MessageInterpolator defaultMessageInterpolator;
    private MessageInterpolator messageInterpolator;
    private final TraversableResolver defaultTraversableResolver;
    private final ConstraintValidatorFactory defaultConstraintValidatorFactory;
    private final ParameterNameProvider defaultParameterNameProvider;
    private final ClockProvider defaultClockProvider;
    private ValidationProviderResolver providerResolver;
    private final ValidationBootstrapParameters validationBootstrapParameters;
    private boolean ignoreXmlConfiguration = false;
    private final Set<InputStream> configurationStreams = CollectionHelper.newHashSet();
    private BootstrapConfiguration bootstrapConfiguration;
    private final Map<ValueExtractorDescriptor.Key, ValueExtractorDescriptor> valueExtractorDescriptors = new HashMap<ValueExtractorDescriptor.Key, ValueExtractorDescriptor>();
    private final Set<DefaultConstraintMapping> programmaticMappings = CollectionHelper.newHashSet();
    private boolean failFast;
    private ClassLoader externalClassLoader;
    private final MethodValidationConfiguration.Builder methodValidationConfigurationBuilder = new MethodValidationConfiguration.Builder();
    private boolean traversableResolverResultCacheEnabled = true;
    private ScriptEvaluatorFactory scriptEvaluatorFactory;
    private Duration temporalValidationTolerance;
    private Object constraintValidatorPayload;
    private BeanMetaDataClassNormalizer beanMetaDataClassNormalizer;

    public ConfigurationImpl(BootstrapState state) {
        this();
        this.providerResolver = state.getValidationProviderResolver() == null ? state.getDefaultValidationProviderResolver() : state.getValidationProviderResolver();
    }

    public ConfigurationImpl(ValidationProvider<?> provider) {
        this();
        if (provider == null) {
            throw LOG.getInconsistentConfigurationException();
        }
        this.providerResolver = null;
        this.validationBootstrapParameters.setProvider(provider);
    }

    private ConfigurationImpl() {
        this.validationBootstrapParameters = new ValidationBootstrapParameters();
        this.defaultResourceBundleLocator = new PlatformResourceBundleLocator("ValidationMessages");
        this.defaultTraversableResolver = TraversableResolvers.getDefault();
        this.defaultConstraintValidatorFactory = new ConstraintValidatorFactoryImpl();
        this.defaultParameterNameProvider = new DefaultParameterNameProvider();
        this.defaultClockProvider = DefaultClockProvider.INSTANCE;
    }

    public final HibernateValidatorConfiguration ignoreXmlConfiguration() {
        this.ignoreXmlConfiguration = true;
        return this;
    }

    public final ConfigurationImpl messageInterpolator(MessageInterpolator interpolator) {
        if (LOG.isDebugEnabled() && interpolator != null) {
            LOG.debug("Setting custom MessageInterpolator of type " + interpolator.getClass().getName());
        }
        this.validationBootstrapParameters.setMessageInterpolator(interpolator);
        return this;
    }

    public final ConfigurationImpl traversableResolver(TraversableResolver resolver) {
        if (LOG.isDebugEnabled() && resolver != null) {
            LOG.debug("Setting custom TraversableResolver of type " + resolver.getClass().getName());
        }
        this.validationBootstrapParameters.setTraversableResolver(resolver);
        return this;
    }

    @Override
    public final ConfigurationImpl enableTraversableResolverResultCache(boolean enabled) {
        this.traversableResolverResultCacheEnabled = enabled;
        return this;
    }

    public final boolean isTraversableResolverResultCacheEnabled() {
        return this.traversableResolverResultCacheEnabled;
    }

    public final ConfigurationImpl constraintValidatorFactory(ConstraintValidatorFactory constraintValidatorFactory) {
        if (LOG.isDebugEnabled() && constraintValidatorFactory != null) {
            LOG.debug("Setting custom ConstraintValidatorFactory of type " + constraintValidatorFactory.getClass().getName());
        }
        this.validationBootstrapParameters.setConstraintValidatorFactory(constraintValidatorFactory);
        return this;
    }

    public HibernateValidatorConfiguration parameterNameProvider(ParameterNameProvider parameterNameProvider) {
        if (LOG.isDebugEnabled() && parameterNameProvider != null) {
            LOG.debug("Setting custom ParameterNameProvider of type " + parameterNameProvider.getClass().getName());
        }
        this.validationBootstrapParameters.setParameterNameProvider(parameterNameProvider);
        return this;
    }

    public HibernateValidatorConfiguration clockProvider(ClockProvider clockProvider) {
        if (LOG.isDebugEnabled() && clockProvider != null) {
            LOG.debug("Setting custom ClockProvider of type " + clockProvider.getClass().getName());
        }
        this.validationBootstrapParameters.setClockProvider(clockProvider);
        return this;
    }

    public HibernateValidatorConfiguration addValueExtractor(ValueExtractor<?> extractor) {
        Contracts.assertNotNull(extractor, Messages.MESSAGES.parameterMustNotBeNull("extractor"));
        ValueExtractorDescriptor descriptor = new ValueExtractorDescriptor(extractor);
        ValueExtractorDescriptor previous = this.valueExtractorDescriptors.put(descriptor.getKey(), descriptor);
        if (previous != null) {
            throw LOG.getValueExtractorForTypeAndTypeUseAlreadyPresentException(extractor, previous.getValueExtractor());
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Adding value extractor " + extractor);
        }
        return this;
    }

    public final HibernateValidatorConfiguration addMapping(InputStream stream) {
        Contracts.assertNotNull(stream, Messages.MESSAGES.inputStreamCannotBeNull());
        this.validationBootstrapParameters.addMapping(stream.markSupported() ? stream : new BufferedInputStream(stream));
        return this;
    }

    @Override
    public final HibernateValidatorConfiguration failFast(boolean failFast) {
        this.failFast = failFast;
        return this;
    }

    @Override
    public HibernateValidatorConfiguration allowOverridingMethodAlterParameterConstraint(boolean allow) {
        this.methodValidationConfigurationBuilder.allowOverridingMethodAlterParameterConstraint(allow);
        return this;
    }

    public boolean isAllowOverridingMethodAlterParameterConstraint() {
        return this.methodValidationConfigurationBuilder.isAllowOverridingMethodAlterParameterConstraint();
    }

    @Override
    public HibernateValidatorConfiguration allowMultipleCascadedValidationOnReturnValues(boolean allow) {
        this.methodValidationConfigurationBuilder.allowMultipleCascadedValidationOnReturnValues(allow);
        return this;
    }

    public boolean isAllowMultipleCascadedValidationOnReturnValues() {
        return this.methodValidationConfigurationBuilder.isAllowMultipleCascadedValidationOnReturnValues();
    }

    @Override
    public HibernateValidatorConfiguration allowParallelMethodsDefineParameterConstraints(boolean allow) {
        this.methodValidationConfigurationBuilder.allowParallelMethodsDefineParameterConstraints(allow);
        return this;
    }

    @Override
    public HibernateValidatorConfiguration scriptEvaluatorFactory(ScriptEvaluatorFactory scriptEvaluatorFactory) {
        Contracts.assertNotNull(scriptEvaluatorFactory, Messages.MESSAGES.parameterMustNotBeNull("scriptEvaluatorFactory"));
        this.scriptEvaluatorFactory = scriptEvaluatorFactory;
        return this;
    }

    @Override
    public HibernateValidatorConfiguration temporalValidationTolerance(Duration temporalValidationTolerance) {
        Contracts.assertNotNull(temporalValidationTolerance, Messages.MESSAGES.parameterMustNotBeNull("temporalValidationTolerance"));
        this.temporalValidationTolerance = temporalValidationTolerance.abs();
        return this;
    }

    @Override
    public HibernateValidatorConfiguration constraintValidatorPayload(Object constraintValidatorPayload) {
        Contracts.assertNotNull(constraintValidatorPayload, Messages.MESSAGES.parameterMustNotBeNull("constraintValidatorPayload"));
        this.constraintValidatorPayload = constraintValidatorPayload;
        return this;
    }

    public boolean isAllowParallelMethodsDefineParameterConstraints() {
        return this.methodValidationConfigurationBuilder.isAllowParallelMethodsDefineParameterConstraints();
    }

    public MethodValidationConfiguration getMethodValidationConfiguration() {
        return this.methodValidationConfigurationBuilder.build();
    }

    @Override
    public final DefaultConstraintMapping createConstraintMapping() {
        return new DefaultConstraintMapping();
    }

    @Override
    public final HibernateValidatorConfiguration addMapping(ConstraintMapping mapping) {
        Contracts.assertNotNull(mapping, Messages.MESSAGES.parameterMustNotBeNull("mapping"));
        this.programmaticMappings.add((DefaultConstraintMapping)mapping);
        return this;
    }

    public final HibernateValidatorConfiguration addProperty(String name, String value) {
        if (value != null) {
            this.validationBootstrapParameters.addConfigProperty(name, value);
        }
        return this;
    }

    @Override
    public HibernateValidatorConfiguration externalClassLoader(ClassLoader externalClassLoader) {
        Contracts.assertNotNull(externalClassLoader, Messages.MESSAGES.parameterMustNotBeNull("externalClassLoader"));
        this.externalClassLoader = externalClassLoader;
        this.messageInterpolator = null;
        return this;
    }

    @Override
    public HibernateValidatorConfiguration beanMetaDataClassNormalizer(BeanMetaDataClassNormalizer beanMetaDataClassNormalizer) {
        if (LOG.isDebugEnabled() && beanMetaDataClassNormalizer != null) {
            LOG.debug("Setting custom BeanMetaDataClassNormalizer of type " + beanMetaDataClassNormalizer.getClass().getName());
        }
        this.beanMetaDataClassNormalizer = beanMetaDataClassNormalizer;
        return this;
    }

    public BeanMetaDataClassNormalizer getBeanMetaDataClassNormalizer() {
        return this.beanMetaDataClassNormalizer;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final ValidatorFactory buildValidatorFactory() {
        this.loadValueExtractorsFromServiceLoader();
        this.parseValidationXml();
        for (ValueExtractorDescriptor valueExtractorDescriptor : this.valueExtractorDescriptors.values()) {
            this.validationBootstrapParameters.addValueExtractorDescriptor(valueExtractorDescriptor);
        }
        ValidatorFactory factory = null;
        try {
            if (this.isSpecificProvider()) {
                factory = this.validationBootstrapParameters.getProvider().buildValidatorFactory((ConfigurationState)this);
            } else {
                Class<ValidationProvider<?>> providerClass = this.validationBootstrapParameters.getProviderClass();
                if (providerClass != null) {
                    for (ValidationProvider provider : this.providerResolver.getValidationProviders()) {
                        if (!providerClass.isAssignableFrom(provider.getClass())) continue;
                        factory = provider.buildValidatorFactory((ConfigurationState)this);
                        break;
                    }
                    if (factory == null) {
                        throw LOG.getUnableToFindProviderException(providerClass);
                    }
                } else {
                    List providers = this.providerResolver.getValidationProviders();
                    assert (providers.size() != 0);
                    factory = ((ValidationProvider)providers.get(0)).buildValidatorFactory((ConfigurationState)this);
                }
            }
        }
        finally {
            for (InputStream in : this.configurationStreams) {
                try {
                    in.close();
                }
                catch (IOException io) {
                    LOG.unableToCloseInputStream();
                }
            }
        }
        return factory;
    }

    public final boolean isIgnoreXmlConfiguration() {
        return this.ignoreXmlConfiguration;
    }

    public final MessageInterpolator getMessageInterpolator() {
        MessageInterpolator selectedInterpolator = this.validationBootstrapParameters.getMessageInterpolator();
        if (selectedInterpolator != null) {
            return selectedInterpolator;
        }
        if (this.messageInterpolator == null) {
            this.messageInterpolator = this.getDefaultMessageInterpolatorConfiguredWithClassLoader();
        }
        return this.messageInterpolator;
    }

    public final Set<InputStream> getMappingStreams() {
        return this.validationBootstrapParameters.getMappings();
    }

    public final boolean getFailFast() {
        return this.failFast;
    }

    public final ConstraintValidatorFactory getConstraintValidatorFactory() {
        return this.validationBootstrapParameters.getConstraintValidatorFactory();
    }

    public final TraversableResolver getTraversableResolver() {
        return this.validationBootstrapParameters.getTraversableResolver();
    }

    public BootstrapConfiguration getBootstrapConfiguration() {
        if (this.bootstrapConfiguration == null) {
            this.bootstrapConfiguration = new ValidationXmlParser(this.externalClassLoader).parseValidationXml();
        }
        return this.bootstrapConfiguration;
    }

    public ParameterNameProvider getParameterNameProvider() {
        return this.validationBootstrapParameters.getParameterNameProvider();
    }

    public ClockProvider getClockProvider() {
        return this.validationBootstrapParameters.getClockProvider();
    }

    public ScriptEvaluatorFactory getScriptEvaluatorFactory() {
        return this.scriptEvaluatorFactory;
    }

    public Duration getTemporalValidationTolerance() {
        return this.temporalValidationTolerance;
    }

    public Object getConstraintValidatorPayload() {
        return this.constraintValidatorPayload;
    }

    public Set<ValueExtractor<?>> getValueExtractors() {
        return this.validationBootstrapParameters.getValueExtractorDescriptors().values().stream().map(ValueExtractorDescriptor::getValueExtractor).collect(Collectors.toSet());
    }

    public final Map<String, String> getProperties() {
        return this.validationBootstrapParameters.getConfigProperties();
    }

    public ClassLoader getExternalClassLoader() {
        return this.externalClassLoader;
    }

    public final MessageInterpolator getDefaultMessageInterpolator() {
        if (this.defaultMessageInterpolator == null) {
            this.defaultMessageInterpolator = new ResourceBundleMessageInterpolator(this.defaultResourceBundleLocator);
        }
        return this.defaultMessageInterpolator;
    }

    public final TraversableResolver getDefaultTraversableResolver() {
        return this.defaultTraversableResolver;
    }

    public final ConstraintValidatorFactory getDefaultConstraintValidatorFactory() {
        return this.defaultConstraintValidatorFactory;
    }

    @Override
    public final ResourceBundleLocator getDefaultResourceBundleLocator() {
        return this.defaultResourceBundleLocator;
    }

    public ParameterNameProvider getDefaultParameterNameProvider() {
        return this.defaultParameterNameProvider;
    }

    public ClockProvider getDefaultClockProvider() {
        return this.defaultClockProvider;
    }

    @Override
    public Set<ValueExtractor<?>> getDefaultValueExtractors() {
        return ValueExtractorManager.getDefaultValueExtractors();
    }

    public final Set<DefaultConstraintMapping> getProgrammaticMappings() {
        return this.programmaticMappings;
    }

    private boolean isSpecificProvider() {
        return this.validationBootstrapParameters.getProvider() != null;
    }

    private void parseValidationXml() {
        if (this.ignoreXmlConfiguration) {
            LOG.ignoringXmlConfiguration();
            if (this.validationBootstrapParameters.getTraversableResolver() == null) {
                this.validationBootstrapParameters.setTraversableResolver(this.defaultTraversableResolver);
            }
            if (this.validationBootstrapParameters.getConstraintValidatorFactory() == null) {
                this.validationBootstrapParameters.setConstraintValidatorFactory(this.defaultConstraintValidatorFactory);
            }
            if (this.validationBootstrapParameters.getParameterNameProvider() == null) {
                this.validationBootstrapParameters.setParameterNameProvider(this.defaultParameterNameProvider);
            }
            if (this.validationBootstrapParameters.getClockProvider() == null) {
                this.validationBootstrapParameters.setClockProvider(this.defaultClockProvider);
            }
        } else {
            ValidationBootstrapParameters xmlParameters = new ValidationBootstrapParameters(this.getBootstrapConfiguration(), this.externalClassLoader);
            this.applyXmlSettings(xmlParameters);
        }
    }

    private void loadValueExtractorsFromServiceLoader() {
        List valueExtractors = (List)ConfigurationImpl.run(GetInstancesFromServiceLoader.action(this.externalClassLoader != null ? this.externalClassLoader : ConfigurationImpl.run(GetClassLoader.fromContext()), ValueExtractor.class));
        for (ValueExtractor valueExtractor : valueExtractors) {
            this.validationBootstrapParameters.addValueExtractorDescriptor(new ValueExtractorDescriptor(valueExtractor));
        }
    }

    private void applyXmlSettings(ValidationBootstrapParameters xmlParameters) {
        this.validationBootstrapParameters.setProviderClass(xmlParameters.getProviderClass());
        if (this.validationBootstrapParameters.getMessageInterpolator() == null && xmlParameters.getMessageInterpolator() != null) {
            this.validationBootstrapParameters.setMessageInterpolator(xmlParameters.getMessageInterpolator());
        }
        if (this.validationBootstrapParameters.getTraversableResolver() == null) {
            if (xmlParameters.getTraversableResolver() != null) {
                this.validationBootstrapParameters.setTraversableResolver(xmlParameters.getTraversableResolver());
            } else {
                this.validationBootstrapParameters.setTraversableResolver(this.defaultTraversableResolver);
            }
        }
        if (this.validationBootstrapParameters.getConstraintValidatorFactory() == null) {
            if (xmlParameters.getConstraintValidatorFactory() != null) {
                this.validationBootstrapParameters.setConstraintValidatorFactory(xmlParameters.getConstraintValidatorFactory());
            } else {
                this.validationBootstrapParameters.setConstraintValidatorFactory(this.defaultConstraintValidatorFactory);
            }
        }
        if (this.validationBootstrapParameters.getParameterNameProvider() == null) {
            if (xmlParameters.getParameterNameProvider() != null) {
                this.validationBootstrapParameters.setParameterNameProvider(xmlParameters.getParameterNameProvider());
            } else {
                this.validationBootstrapParameters.setParameterNameProvider(this.defaultParameterNameProvider);
            }
        }
        if (this.validationBootstrapParameters.getClockProvider() == null) {
            if (xmlParameters.getClockProvider() != null) {
                this.validationBootstrapParameters.setClockProvider(xmlParameters.getClockProvider());
            } else {
                this.validationBootstrapParameters.setClockProvider(this.defaultClockProvider);
            }
        }
        for (ValueExtractorDescriptor valueExtractorDescriptor : xmlParameters.getValueExtractorDescriptors().values()) {
            this.validationBootstrapParameters.addValueExtractorDescriptor(valueExtractorDescriptor);
        }
        this.validationBootstrapParameters.addAllMappings(xmlParameters.getMappings());
        this.configurationStreams.addAll(xmlParameters.getMappings());
        for (Map.Entry entry : xmlParameters.getConfigProperties().entrySet()) {
            if (this.validationBootstrapParameters.getConfigProperties().get(entry.getKey()) != null) continue;
            this.validationBootstrapParameters.addConfigProperty((String)entry.getKey(), (String)entry.getValue());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private MessageInterpolator getDefaultMessageInterpolatorConfiguredWithClassLoader() {
        if (this.externalClassLoader != null) {
            PlatformResourceBundleLocator userResourceBundleLocator = new PlatformResourceBundleLocator("ValidationMessages", this.externalClassLoader);
            PlatformResourceBundleLocator contributorResourceBundleLocator = new PlatformResourceBundleLocator("ContributorValidationMessages", this.externalClassLoader, true);
            ClassLoader originalContextClassLoader = ConfigurationImpl.run(GetClassLoader.fromContext());
            try {
                ConfigurationImpl.run(SetContextClassLoader.action(this.externalClassLoader));
                ResourceBundleMessageInterpolator resourceBundleMessageInterpolator = new ResourceBundleMessageInterpolator((ResourceBundleLocator)userResourceBundleLocator, contributorResourceBundleLocator);
                return resourceBundleMessageInterpolator;
            }
            finally {
                ConfigurationImpl.run(SetContextClassLoader.action(originalContextClassLoader));
            }
        }
        return this.getDefaultMessageInterpolator();
    }

    private static <T> T run(PrivilegedAction<T> action) {
        return System.getSecurityManager() != null ? AccessController.doPrivileged(action) : action.run();
    }

    static {
        Version.touch();
        LOG = LoggerFactory.make(MethodHandles.lookup());
    }
}

