/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.ConstraintValidator
 */
package org.hibernate.validator.internal.cfg.context;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;
import javax.validation.ConstraintValidator;
import org.hibernate.validator.cfg.context.ConstraintDefinitionContext;
import org.hibernate.validator.internal.cfg.context.ConstraintContextImplBase;
import org.hibernate.validator.internal.cfg.context.DefaultConstraintMapping;
import org.hibernate.validator.internal.engine.constraintdefinition.ConstraintDefinitionContribution;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorDescriptor;
import org.hibernate.validator.internal.util.CollectionHelper;

class ConstraintDefinitionContextImpl<A extends Annotation>
extends ConstraintContextImplBase
implements ConstraintDefinitionContext<A> {
    private final Class<A> annotationType;
    private boolean includeExistingValidators = true;
    private final Set<ConstraintValidatorDescriptor<A>> validatorDescriptors = new HashSet<ConstraintValidatorDescriptor<A>>();

    ConstraintDefinitionContextImpl(DefaultConstraintMapping mapping, Class<A> annotationType) {
        super(mapping);
        this.annotationType = annotationType;
    }

    @Override
    public ConstraintDefinitionContext<A> includeExistingValidators(boolean includeExistingValidators) {
        this.includeExistingValidators = includeExistingValidators;
        return this;
    }

    @Override
    public ConstraintDefinitionContext<A> validatedBy(Class<? extends ConstraintValidator<A, ?>> validator) {
        this.validatorDescriptors.add(ConstraintValidatorDescriptor.forClass(validator, this.annotationType));
        return this;
    }

    @Override
    public <T> ConstraintDefinitionContext.ConstraintValidatorDefinitionContext<A, T> validateType(Class<T> type) {
        return new ConstraintValidatorDefinitionContextImpl<T>(type);
    }

    ConstraintDefinitionContribution<A> build() {
        return new ConstraintDefinitionContribution<A>(this.annotationType, CollectionHelper.newArrayList(this.validatorDescriptors), this.includeExistingValidators);
    }

    private class ConstraintValidatorDefinitionContextImpl<T>
    implements ConstraintDefinitionContext.ConstraintValidatorDefinitionContext<A, T> {
        private final Class<T> type;

        public ConstraintValidatorDefinitionContextImpl(Class<T> type) {
            this.type = type;
        }

        @Override
        public ConstraintDefinitionContext<A> with(ConstraintDefinitionContext.ValidationCallable<T> vc) {
            ConstraintDefinitionContextImpl.this.validatorDescriptors.add(ConstraintValidatorDescriptor.forLambda(ConstraintDefinitionContextImpl.this.annotationType, this.type, vc));
            return ConstraintDefinitionContextImpl.this;
        }
    }
}

