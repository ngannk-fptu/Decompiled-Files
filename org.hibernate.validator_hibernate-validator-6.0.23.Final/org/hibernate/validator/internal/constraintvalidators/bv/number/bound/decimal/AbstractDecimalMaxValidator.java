/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.ConstraintValidator
 *  javax.validation.ConstraintValidatorContext
 *  javax.validation.constraints.DecimalMax
 */
package org.hibernate.validator.internal.constraintvalidators.bv.number.bound.decimal;

import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.DecimalMax;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

public abstract class AbstractDecimalMaxValidator<T>
implements ConstraintValidator<DecimalMax, T> {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    protected BigDecimal maxValue;
    private boolean inclusive;

    public void initialize(DecimalMax maxValue) {
        try {
            this.maxValue = new BigDecimal(maxValue.value());
        }
        catch (NumberFormatException nfe) {
            throw LOG.getInvalidBigDecimalFormatException(maxValue.value(), nfe);
        }
        this.inclusive = maxValue.inclusive();
    }

    public boolean isValid(T value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return true;
        }
        int comparisonResult = this.compare(value);
        return this.inclusive ? comparisonResult <= 0 : comparisonResult < 0;
    }

    protected abstract int compare(T var1);
}

