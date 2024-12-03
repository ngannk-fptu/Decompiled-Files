/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.constraintvalidators.bv.number.bound.decimal;

import java.math.BigDecimal;
import org.hibernate.validator.internal.constraintvalidators.bv.number.bound.decimal.AbstractDecimalMinValidator;
import org.hibernate.validator.internal.constraintvalidators.bv.number.bound.decimal.DecimalNumberComparatorHelper;

public class DecimalMinValidatorForBigDecimal
extends AbstractDecimalMinValidator<BigDecimal> {
    @Override
    protected int compare(BigDecimal number) {
        return DecimalNumberComparatorHelper.compare(number, this.minValue);
    }
}

