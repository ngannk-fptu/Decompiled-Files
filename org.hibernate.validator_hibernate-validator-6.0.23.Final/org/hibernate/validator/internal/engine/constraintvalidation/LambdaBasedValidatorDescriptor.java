/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.ConstraintValidator
 *  javax.validation.ConstraintValidatorContext
 *  javax.validation.ConstraintValidatorFactory
 *  javax.validation.constraintvalidation.ValidationTarget
 */
package org.hibernate.validator.internal.engine.constraintvalidation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.EnumSet;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintValidatorFactory;
import javax.validation.constraintvalidation.ValidationTarget;
import org.hibernate.validator.cfg.context.ConstraintDefinitionContext;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorDescriptor;

class LambdaBasedValidatorDescriptor<A extends Annotation>
implements ConstraintValidatorDescriptor<A> {
    private static final long serialVersionUID = 5129757824081595723L;
    private final Type validatedType;
    private final ConstraintDefinitionContext.ValidationCallable<?> lambda;

    public LambdaBasedValidatorDescriptor(Type validatedType, ConstraintDefinitionContext.ValidationCallable<?> lambda) {
        this.validatedType = validatedType;
        this.lambda = lambda;
    }

    @Override
    public Class<? extends ConstraintValidator<A, ?>> getValidatorClass() {
        Class<LambdaExecutor> clazz = LambdaExecutor.class;
        return clazz;
    }

    @Override
    public EnumSet<ValidationTarget> getValidationTargets() {
        return EnumSet.of(ValidationTarget.ANNOTATED_ELEMENT);
    }

    @Override
    public Type getValidatedType() {
        return this.validatedType;
    }

    @Override
    public ConstraintValidator<A, ?> newInstance(ConstraintValidatorFactory constraintValidatorFactory) {
        return new LambdaExecutor(this.lambda);
    }

    private static class LambdaExecutor<A extends Annotation, T>
    implements ConstraintValidator<A, T> {
        private final ConstraintDefinitionContext.ValidationCallable<T> lambda;

        public LambdaExecutor(ConstraintDefinitionContext.ValidationCallable<T> lambda) {
            this.lambda = lambda;
        }

        public boolean isValid(T value, ConstraintValidatorContext context) {
            return this.lambda.isValid(value);
        }
    }
}

