/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.money.MonetaryAmount
 *  javax.validation.ConstraintValidator
 *  javax.validation.ConstraintValidatorContext
 *  javax.validation.constraints.DecimalMin
 */
package org.hibernate.validator.internal.constraintvalidators.bv.money;

import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import javax.money.MonetaryAmount;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.DecimalMin;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

public class DecimalMinValidatorForMonetaryAmount
implements ConstraintValidator<DecimalMin, MonetaryAmount> {
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

    public boolean isValid(MonetaryAmount value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        int comparisonResult = ((BigDecimal)value.getNumber().numberValueExact(BigDecimal.class)).compareTo(this.minValue);
        return this.inclusive ? comparisonResult >= 0 : comparisonResult > 0;
    }
}

