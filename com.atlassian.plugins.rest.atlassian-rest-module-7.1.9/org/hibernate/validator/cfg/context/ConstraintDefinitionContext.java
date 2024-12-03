/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.ConstraintValidator
 */
package org.hibernate.validator.cfg.context;

import java.lang.annotation.Annotation;
import javax.validation.ConstraintValidator;
import org.hibernate.validator.Incubating;
import org.hibernate.validator.cfg.context.ConstraintMappingTarget;

public interface ConstraintDefinitionContext<A extends Annotation>
extends ConstraintMappingTarget {
    public ConstraintDefinitionContext<A> includeExistingValidators(boolean var1);

    public ConstraintDefinitionContext<A> validatedBy(Class<? extends ConstraintValidator<A, ?>> var1);

    @Incubating
    public <T> ConstraintValidatorDefinitionContext<A, T> validateType(Class<T> var1);

    @FunctionalInterface
    @Incubating
    public static interface ValidationCallable<T> {
        public boolean isValid(T var1);
    }

    @Incubating
    public static interface ConstraintValidatorDefinitionContext<A extends Annotation, T> {
        public ConstraintDefinitionContext<A> with(ValidationCallable<T> var1);
    }
}

