/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.ConstraintValidator
 *  javax.validation.ConstraintValidatorContext
 *  javax.validation.constraints.Null
 */
package org.hibernate.validator.internal.constraintvalidators.bv;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.Null;

public class NullValidator
implements ConstraintValidator<Null, Object> {
    public boolean isValid(Object object, ConstraintValidatorContext constraintValidatorContext) {
        return object == null;
    }
}

