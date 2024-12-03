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
 *  javax.validation.valueextraction.ValueExtractor
 */
package org.hibernate.validator.internal.engine;

import java.lang.invoke.MethodHandles;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import javax.validation.ClockProvider;
import javax.validation.ConstraintValidatorFactory;
import javax.validation.MessageInterpolator;
import javax.validation.ParameterNameProvider;
import javax.validation.TraversableResolver;
import javax.validation.Validator;
import javax.validation.valueextraction.ValueExtractor;
import org.hibernate.validator.HibernateValidatorContext;
import org.hibernate.validator.internal.engine.MethodValidationConfiguration;
import org.hibernate.validator.internal.engine.ValidatorFactoryImpl;
import org.hibernate.validator.internal.engine.valueextraction.ValueExtractorDescriptor;
import org.hibernate.validator.internal.engine.valueextraction.ValueExtractorManager;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

public class ValidatorContextImpl
implements HibernateValidatorContext {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private final ValidatorFactoryImpl validatorFactory;
    private ConstraintValidatorFactory constraintValidatorFactory;
    private final ValidatorFactoryImpl.ValidatorFactoryScopedContext.Builder validatorFactoryScopedContextBuilder;
    private final ValueExtractorManager valueExtractorManager;
    private final MethodValidationConfiguration.Builder methodValidationConfigurationBuilder;
    private final Map<ValueExtractorDescriptor.Key, ValueExtractorDescriptor> valueExtractorDescriptors;

    public ValidatorContextImpl(ValidatorFactoryImpl validatorFactory) {
        this.validatorFactoryScopedContextBuilder = new ValidatorFactoryImpl.ValidatorFactoryScopedContext.Builder(validatorFactory.getValidatorFactoryScopedContext());
        this.validatorFactory = validatorFactory;
        this.constraintValidatorFactory = validatorFactory.getConstraintValidatorFactory();
        this.methodValidationConfigurationBuilder = new MethodValidationConfiguration.Builder(validatorFactory.getMethodValidationConfiguration());
        this.valueExtractorManager = validatorFactory.getValueExtractorManager();
        this.valueExtractorDescriptors = new HashMap<ValueExtractorDescriptor.Key, ValueExtractorDescriptor>();
    }

    @Override
    public HibernateValidatorContext messageInterpolator(MessageInterpolator messageInterpolator) {
        this.validatorFactoryScopedContextBuilder.setMessageInterpolator(messageInterpolator);
        return this;
    }

    @Override
    public HibernateValidatorContext traversableResolver(TraversableResolver traversableResolver) {
        this.validatorFactoryScopedContextBuilder.setTraversableResolver(traversableResolver);
        return this;
    }

    @Override
    public HibernateValidatorContext constraintValidatorFactory(ConstraintValidatorFactory factory) {
        this.constraintValidatorFactory = factory == null ? this.validatorFactory.getConstraintValidatorFactory() : factory;
        return this;
    }

    @Override
    public HibernateValidatorContext parameterNameProvider(ParameterNameProvider parameterNameProvider) {
        this.validatorFactoryScopedContextBuilder.setParameterNameProvider(parameterNameProvider);
        return this;
    }

    @Override
    public HibernateValidatorContext clockProvider(ClockProvider clockProvider) {
        this.validatorFactoryScopedContextBuilder.setClockProvider(clockProvider);
        return this;
    }

    @Override
    public HibernateValidatorContext addValueExtractor(ValueExtractor<?> extractor) {
        ValueExtractorDescriptor descriptor = new ValueExtractorDescriptor(extractor);
        ValueExtractorDescriptor previous = this.valueExtractorDescriptors.put(descriptor.getKey(), descriptor);
        if (previous != null) {
            throw LOG.getValueExtractorForTypeAndTypeUseAlreadyPresentException(extractor, previous.getValueExtractor());
        }
        return this;
    }

    @Override
    public HibernateValidatorContext failFast(boolean failFast) {
        this.validatorFactoryScopedContextBuilder.setFailFast(failFast);
        return this;
    }

    @Override
    public HibernateValidatorContext allowOverridingMethodAlterParameterConstraint(boolean allow) {
        this.methodValidationConfigurationBuilder.allowOverridingMethodAlterParameterConstraint(allow);
        return this;
    }

    @Override
    public HibernateValidatorContext allowMultipleCascadedValidationOnReturnValues(boolean allow) {
        this.methodValidationConfigurationBuilder.allowMultipleCascadedValidationOnReturnValues(allow);
        return this;
    }

    @Override
    public HibernateValidatorContext allowParallelMethodsDefineParameterConstraints(boolean allow) {
        this.methodValidationConfigurationBuilder.allowParallelMethodsDefineParameterConstraints(allow);
        return this;
    }

    @Override
    public HibernateValidatorContext enableTraversableResolverResultCache(boolean enabled) {
        this.validatorFactoryScopedContextBuilder.setTraversableResolverResultCacheEnabled(enabled);
        return this;
    }

    @Override
    public HibernateValidatorContext temporalValidationTolerance(Duration temporalValidationTolerance) {
        this.validatorFactoryScopedContextBuilder.setTemporalValidationTolerance(temporalValidationTolerance);
        return this;
    }

    @Override
    public HibernateValidatorContext constraintValidatorPayload(Object dynamicPayload) {
        this.validatorFactoryScopedContextBuilder.setConstraintValidatorPayload(dynamicPayload);
        return this;
    }

    public Validator getValidator() {
        return this.validatorFactory.createValidator(this.constraintValidatorFactory, this.valueExtractorDescriptors.isEmpty() ? this.valueExtractorManager : new ValueExtractorManager(this.valueExtractorManager, this.valueExtractorDescriptors), this.validatorFactoryScopedContextBuilder.build(), this.methodValidationConfigurationBuilder.build());
    }
}

