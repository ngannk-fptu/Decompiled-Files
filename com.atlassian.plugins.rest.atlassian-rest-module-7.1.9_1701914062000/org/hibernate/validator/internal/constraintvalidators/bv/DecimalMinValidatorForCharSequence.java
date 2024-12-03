/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.ConstraintValidator
 *  javax.validation.ConstraintValidatorContext
 *  javax.validation.constraints.DecimalMin
 */
package org.hibernate.validator.internal.constraintvalidators.bv;

import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.DecimalMin;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

public class DecimalMinValidatorForCharSequence
implements ConstraintValidator<DecimalMin, CharSequence> {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private BigDecimal minValue;
    private boolean inclusive;

    public void initialize(DecimalMin minValue) {
        try {
            this.minValue = new BigDecimal(minValue.value());
        }
        catch (NumberFormatException nfe) {
            throw LOG.getInvalidBigDecimalFormatException(minValue.value(), nfe);
        }
        this.inclusive = minValue.inclusive();
    }

    public boolean isValid(CharSequence value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return true;
        }
        try {
            int comparisonResult = new BigDecimal(value.toString()).compareTo(this.minValue);
            return this.inclusive ? comparisonResult >= 0 : comparisonResult > 0;
        }
        catch (NumberFormatException nfe) {
            return false;
        }
    }
}

