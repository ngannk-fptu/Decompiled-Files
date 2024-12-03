/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.ConstraintValidator
 *  javax.validation.metadata.ConstraintDescriptor
 */
package org.hibernate.validator.constraintvalidation;

import java.lang.annotation.Annotation;
import javax.validation.ConstraintValidator;
import javax.validation.metadata.ConstraintDescriptor;
import org.hibernate.validator.Incubating;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorInitializationContext;

@Incubating
public interface HibernateConstraintValidator<A extends Annotation, T>
extends ConstraintValidator<A, T> {
    default public void initialize(ConstraintDescriptor<A> constraintDescriptor, HibernateConstraintValidatorInitializationContext initializationContext) {
    }
}

