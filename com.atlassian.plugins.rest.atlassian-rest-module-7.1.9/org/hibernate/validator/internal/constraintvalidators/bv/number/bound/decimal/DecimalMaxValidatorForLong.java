/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.constraintvalidators.bv.number.bound.decimal;

import org.hibernate.validator.internal.constraintvalidators.bv.number.bound.decimal.AbstractDecimalMaxValidator;
import org.hibernate.validator.internal.constraintvalidators.bv.number.bound.decimal.DecimalNumberComparatorHelper;

public class DecimalMaxValidatorForLong
extends AbstractDecimalMaxValidator<Long> {
    @Override
    protected int compare(Long number) {
        return DecimalNumberComparatorHelper.compare(number, this.maxValue);
    }
}

