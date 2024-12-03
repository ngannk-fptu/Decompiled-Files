/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.constraintvalidators.bv.number.bound.decimal;

import org.hibernate.validator.internal.constraintvalidators.bv.number.bound.decimal.AbstractDecimalMaxValidator;
import org.hibernate.validator.internal.constraintvalidators.bv.number.bound.decimal.DecimalNumberComparatorHelper;

public class DecimalMaxValidatorForNumber
extends AbstractDecimalMaxValidator<Number> {
    @Override
    protected int compare(Number number) {
        return DecimalNumberComparatorHelper.compare(number, this.maxValue);
    }
}

