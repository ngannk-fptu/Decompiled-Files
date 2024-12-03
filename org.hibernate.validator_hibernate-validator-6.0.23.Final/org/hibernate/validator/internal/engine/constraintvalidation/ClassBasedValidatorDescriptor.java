/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.ConstraintValidator
 *  javax.validation.ConstraintValidatorFactory
 *  javax.validation.constraintvalidation.SupportedValidationTarget
 *  javax.validation.constraintvalidation.ValidationTarget
 */
package org.hibernate.validator.internal.engine.constraintvalidation;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.EnumSet;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorFactory;
import javax.validation.constraintvalidation.SupportedValidationTarget;
import javax.validation.constraintvalidation.ValidationTarget;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorDescriptor;
import org.hibernate.validator.internal.util.TypeHelper;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

class ClassBasedValidatorDescriptor<A extends Annotation>
implements ConstraintValidatorDescriptor<A> {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private final Class<? extends ConstraintValidator<A, ?>> validatorClass;
    private final Type validatedType;
    private final EnumSet<ValidationTarget> validationTargets;

    private ClassBasedValidatorDescriptor(Class<? extends ConstraintValidator<A, ?>> validatorClass) {
        this.validatorClass = validatorClass;
        this.validatedType = TypeHelper.extractValidatedType(validatorClass);
        this.validationTargets = ClassBasedValidatorDescriptor.determineValidationTargets(validatorClass);
    }

    public static <T extends Annotation> ClassBasedValidatorDescriptor<T> of(Class<? extends ConstraintValidator<T, ?>> validatorClass, Class<? extends Annotation> registeredConstraintAnnotationType) {
        Type definedConstraintAnnotationType = TypeHelper.extractConstraintType(validatorClass);
        if (!registeredConstraintAnnotationType.equals(definedConstraintAnnotationType)) {
            throw LOG.getConstraintValidatorDefinitionConstraintMismatchException(validatorClass, registeredConstraintAnnotationType, definedConstraintAnnotationType);
        }
        return new ClassBasedValidatorDescriptor(validatorClass);
    }

    private static EnumSet<ValidationTarget> determineValidationTargets(Class<? extends ConstraintValidator<?, ?>> validatorClass) {
        SupportedValidationTarget supportedTargetAnnotation = validatorClass.getAnnotation(SupportedValidationTarget.class);
        if (supportedTargetAnnotation == null) {
            return EnumSet.of(ValidationTarget.ANNOTATED_ELEMENT);
        }
        return EnumSet.copyOf(Arrays.asList(supportedTargetAnnotation.value()));
    }

    @Override
    public Class<? extends ConstraintValidator<A, ?>> getValidatorClass() {
        return this.validatorClass;
    }

    @Override
    public ConstraintValidator<A, ?> newInstance(ConstraintValidatorFactory constraintValidatorFactory) {
        ConstraintValidator constraintValidator = constraintValidatorFactory.getInstance(this.validatorClass);
        if (constraintValidator == null) {
            throw LOG.getConstraintValidatorFactoryMustNotReturnNullException(this.validatorClass);
        }
        return constraintValidator;
    }

    @Override
    public Type getValidatedType() {
        return this.validatedType;
    }

    @Override
    public EnumSet<ValidationTarget> getValidationTargets() {
        return this.validationTargets;
    }
}

