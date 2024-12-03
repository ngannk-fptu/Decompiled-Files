/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.money.MonetaryAmount
 *  javax.validation.ConstraintValidator
 *  javax.validation.ConstraintValidatorContext
 *  javax.validation.constraints.Positive
 */
package org.hibernate.validator.internal.constraintvalidators.bv.money;

import javax.money.MonetaryAmount;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.Positive;

public class PositiveValidatorForMonetaryAmount
implements ConstraintValidator<Positive, MonetaryAmount> {
    public boolean isValid(MonetaryAmount value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return value.signum() > 0;
    }
}

