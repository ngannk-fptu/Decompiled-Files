/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.ConstraintValidator
 *  javax.validation.ConstraintValidatorFactory
 *  javax.validation.constraintvalidation.ValidationTarget
 */
package org.hibernate.validator.internal.engine.constraintvalidation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.EnumSet;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorFactory;
import javax.validation.constraintvalidation.ValidationTarget;
import org.hibernate.validator.cfg.context.ConstraintDefinitionContext;
import org.hibernate.validator.internal.engine.constraintvalidation.ClassBasedValidatorDescriptor;
import org.hibernate.validator.internal.engine.constraintvalidation.LambdaBasedValidatorDescriptor;

public interface ConstraintValidatorDescriptor<A extends Annotation> {
    public Class<? extends ConstraintValidator<A, ?>> getValidatorClass();

    public EnumSet<ValidationTarget> getValidationTargets();

    public Type getValidatedType();

    public ConstraintValidator<A, ?> newInstance(ConstraintValidatorFactory var1);

    public static <A extends Annotation> ConstraintValidatorDescriptor<A> forClass(Class<? extends ConstraintValidator<A, ?>> validatorClass, Class<? extends Annotation> constraintAnnotationType) {
        return ClassBasedValidatorDescriptor.of(validatorClass, constraintAnnotationType);
    }

    public static <A extends Annotation, T> ConstraintValidatorDescriptor<A> forLambda(Class<A> annotationType, Type validatedType, ConstraintDefinitionContext.ValidationCallable<T> lambda) {
        return new LambdaBasedValidatorDescriptor(validatedType, lambda);
    }
}

