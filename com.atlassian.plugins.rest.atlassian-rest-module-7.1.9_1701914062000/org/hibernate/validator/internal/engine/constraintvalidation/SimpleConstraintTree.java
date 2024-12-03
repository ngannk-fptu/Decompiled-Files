/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.ConstraintValidator
 *  javax.validation.ConstraintViolation
 */
package org.hibernate.validator.internal.engine.constraintvalidation;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Type;
import java.util.Set;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintViolation;
import org.hibernate.validator.internal.engine.ValidationContext;
import org.hibernate.validator.internal.engine.ValueContext;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintTree;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorContextImpl;
import org.hibernate.validator.internal.metadata.descriptor.ConstraintDescriptorImpl;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

class SimpleConstraintTree<B extends Annotation>
extends ConstraintTree<B> {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());

    public SimpleConstraintTree(ConstraintDescriptorImpl<B> descriptor, Type validatedValueType) {
        super(descriptor, validatedValueType);
    }

    @Override
    protected <T> void validateConstraints(ValidationContext<T> validationContext, ValueContext<?, ?> valueContext, Set<ConstraintViolation<T>> constraintViolations) {
        if (LOG.isTraceEnabled()) {
            LOG.tracef("Validating value %s against constraint defined by %s.", valueContext.getCurrentValidatedValue(), (Object)this.descriptor);
        }
        ConstraintValidator validator = this.getInitializedConstraintValidator(validationContext, valueContext);
        ConstraintValidatorContextImpl constraintValidatorContext = new ConstraintValidatorContextImpl(validationContext.getParameterNames(), validationContext.getClockProvider(), valueContext.getPropertyPath(), this.descriptor, validationContext.getConstraintValidatorPayload());
        constraintViolations.addAll(this.validateSingleConstraint(validationContext, valueContext, constraintValidatorContext, validator));
    }
}

