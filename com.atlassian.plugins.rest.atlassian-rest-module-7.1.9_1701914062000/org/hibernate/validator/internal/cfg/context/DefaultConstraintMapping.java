/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.Constraint
 */
package org.hibernate.validator.internal.cfg.context;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandles;
import java.util.HashSet;
import java.util.Set;
import javax.validation.Constraint;
import org.hibernate.validator.cfg.ConstraintMapping;
import org.hibernate.validator.cfg.context.ConstraintDefinitionContext;
import org.hibernate.validator.cfg.context.TypeConstraintMappingContext;
import org.hibernate.validator.internal.cfg.context.ConstraintDefinitionContextImpl;
import org.hibernate.validator.internal.cfg.context.TypeConstraintMappingContextImpl;
import org.hibernate.validator.internal.engine.constraintdefinition.ConstraintDefinitionContribution;
import org.hibernate.validator.internal.engine.valueextraction.ValueExtractorManager;
import org.hibernate.validator.internal.metadata.core.AnnotationProcessingOptionsImpl;
import org.hibernate.validator.internal.metadata.core.ConstraintHelper;
import org.hibernate.validator.internal.metadata.raw.BeanConfiguration;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.hibernate.validator.internal.util.Contracts;
import org.hibernate.validator.internal.util.TypeResolutionHelper;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.hibernate.validator.internal.util.logging.Messages;

public class DefaultConstraintMapping
implements ConstraintMapping {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private final AnnotationProcessingOptionsImpl annotationProcessingOptions = new AnnotationProcessingOptionsImpl();
    private final Set<Class<?>> configuredTypes = CollectionHelper.newHashSet();
    private final Set<TypeConstraintMappingContextImpl<?>> typeContexts = CollectionHelper.newHashSet();
    private final Set<Class<?>> definedConstraints = CollectionHelper.newHashSet();
    private final Set<ConstraintDefinitionContextImpl<?>> constraintContexts = CollectionHelper.newHashSet();

    @Override
    public final <C> TypeConstraintMappingContext<C> type(Class<C> type) {
        Contracts.assertNotNull(type, Messages.MESSAGES.beanTypeMustNotBeNull());
        if (this.configuredTypes.contains(type)) {
            throw LOG.getBeanClassHasAlreadyBeConfiguredViaProgrammaticApiException(type);
        }
        TypeConstraintMappingContextImpl<C> typeContext = new TypeConstraintMappingContextImpl<C>(this, type);
        this.typeContexts.add(typeContext);
        this.configuredTypes.add(type);
        return typeContext;
    }

    public final AnnotationProcessingOptionsImpl getAnnotationProcessingOptions() {
        return this.annotationProcessingOptions;
    }

    public Set<Class<?>> getConfiguredTypes() {
        return this.configuredTypes;
    }

    public Set<BeanConfiguration<?>> getBeanConfigurations(ConstraintHelper constraintHelper, TypeResolutionHelper typeResolutionHelper, ValueExtractorManager valueExtractorManager) {
        HashSet<BeanConfiguration<?>> configurations = CollectionHelper.newHashSet();
        for (TypeConstraintMappingContextImpl<?> typeContext : this.typeContexts) {
            configurations.add(typeContext.build(constraintHelper, typeResolutionHelper, valueExtractorManager));
        }
        return configurations;
    }

    @Override
    public <A extends Annotation> ConstraintDefinitionContext<A> constraintDefinition(Class<A> annotationClass) {
        Contracts.assertNotNull(annotationClass, Messages.MESSAGES.annotationTypeMustNotBeNull());
        Contracts.assertTrue(annotationClass.isAnnotationPresent(Constraint.class), Messages.MESSAGES.annotationTypeMustBeAnnotatedWithConstraint());
        if (this.definedConstraints.contains(annotationClass)) {
            throw LOG.getConstraintHasAlreadyBeenConfiguredViaProgrammaticApiException(annotationClass);
        }
        ConstraintDefinitionContextImpl<A> constraintContext = new ConstraintDefinitionContextImpl<A>(this, annotationClass);
        this.constraintContexts.add(constraintContext);
        this.definedConstraints.add(annotationClass);
        return constraintContext;
    }

    public Set<ConstraintDefinitionContribution<?>> getConstraintDefinitionContributions() {
        HashSet<ConstraintDefinitionContribution<?>> contributions = CollectionHelper.newHashSet();
        for (ConstraintDefinitionContextImpl<?> constraintContext : this.constraintContexts) {
            contributions.add(constraintContext.build());
        }
        return contributions;
    }
}

