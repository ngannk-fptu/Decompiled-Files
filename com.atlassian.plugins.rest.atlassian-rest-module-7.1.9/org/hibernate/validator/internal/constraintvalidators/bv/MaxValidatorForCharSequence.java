/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.ConstraintValidator
 *  javax.validation.ConstraintValidatorContext
 *  javax.validation.constraints.Max
 */
package org.hibernate.validator.internal.constraintvalidators.bv;

import java.math.BigDecimal;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.Max;

public class MaxValidatorForCharSequence
implements ConstraintValidator<Max, CharSequence> {
    private BigDecimal maxValue;

    public void initialize(Max maxValue) {
        this.maxValue = BigDecimal.valueOf(maxValue.value());
    }

    public boolean isValid(CharSequence value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return true;
        }
        try {
            return new BigDecimal(value.toString()).compareTo(this.maxValue) != 1;
        }
        catch (NumberFormatException nfe) {
            return false;
        }
    }
}

