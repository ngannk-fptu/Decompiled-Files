/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.constraintvalidators.bv.number.bound.decimal;

import org.hibernate.validator.internal.constraintvalidators.bv.number.bound.decimal.AbstractDecimalMinValidator;
import org.hibernate.validator.internal.constraintvalidators.bv.number.bound.decimal.DecimalNumberComparatorHelper;

public class DecimalMinValidatorForLong
extends AbstractDecimalMinValidator<Long> {
    @Override
    protected int compare(Long number) {
        return DecimalNumberComparatorHelper.compare(number, this.minValue);
    }
}

