/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.Configuration
 *  javax.validation.valueextraction.ValueExtractor
 */
package org.hibernate.validator;

import java.time.Duration;
import java.util.Set;
import javax.validation.Configuration;
import javax.validation.valueextraction.ValueExtractor;
import org.hibernate.validator.Incubating;
import org.hibernate.validator.cfg.ConstraintMapping;
import org.hibernate.validator.metadata.BeanMetaDataClassNormalizer;
import org.hibernate.validator.spi.resourceloading.ResourceBundleLocator;
import org.hibernate.validator.spi.scripting.ScriptEvaluatorFactory;

public interface HibernateValidatorConfiguration
extends Configuration<HibernateValidatorConfiguration> {
    public static final String FAIL_FAST = "hibernate.validator.fail_fast";
    public static final String ALLOW_PARAMETER_CONSTRAINT_OVERRIDE = "hibernate.validator.allow_parameter_constraint_override";
    public static final String ALLOW_MULTIPLE_CASCADED_VALIDATION_ON_RESULT = "hibernate.validator.allow_multiple_cascaded_validation_on_result";
    public static final String ALLOW_PARALLEL_METHODS_DEFINE_PARAMETER_CONSTRAINTS = "hibernate.validator.allow_parallel_method_parameter_constraint";
    @Deprecated
    public static final String CONSTRAINT_MAPPING_CONTRIBUTOR = "hibernate.validator.constraint_mapping_contributor";
    public static final String CONSTRAINT_MAPPING_CONTRIBUTORS = "hibernate.validator.constraint_mapping_contributors";
    public static final String ENABLE_TRAVERSABLE_RESOLVER_RESULT_CACHE = "hibernate.validator.enable_traversable_resolver_result_cache";
    @Incubating
    public static final String SCRIPT_EVALUATOR_FACTORY_CLASSNAME = "hibernate.validator.script_evaluator_factory";
    @Incubating
    public static final String TEMPORAL_VALIDATION_TOLERANCE = "hibernate.validator.temporal_validation_tolerance";

    public ResourceBundleLocator getDefaultResourceBundleLocator();

    public ConstraintMapping createConstraintMapping();

    @Incubating
    public Set<ValueExtractor<?>> getDefaultValueExtractors();

    public HibernateValidatorConfiguration addMapping(ConstraintMapping var1);

    public HibernateValidatorConfiguration failFast(boolean var1);

    public HibernateValidatorConfiguration externalClassLoader(ClassLoader var1);

    public HibernateValidatorConfiguration allowOverridingMethodAlterParameterConstraint(boolean var1);

    public HibernateValidatorConfiguration allowMultipleCascadedValidationOnReturnValues(boolean var1);

    public HibernateValidatorConfiguration allowParallelMethodsDefineParameterConstraints(boolean var1);

    public HibernateValidatorConfiguration enableTraversableResolverResultCache(boolean var1);

    @Incubating
    public HibernateValidatorConfiguration scriptEvaluatorFactory(ScriptEvaluatorFactory var1);

    @Incubating
    public HibernateValidatorConfiguration temporalValidationTolerance(Duration var1);

    @Incubating
    public HibernateValidatorConfiguration constraintValidatorPayload(Object var1);

    @Incubating
    public HibernateValidatorConfiguration beanMetaDataClassNormalizer(BeanMetaDataClassNormalizer var1);
}

