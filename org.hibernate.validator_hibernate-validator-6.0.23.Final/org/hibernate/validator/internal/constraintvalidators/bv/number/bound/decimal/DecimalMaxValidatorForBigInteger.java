/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.constraintvalidators.bv.number.bound.decimal;

import java.math.BigInteger;
import org.hibernate.validator.internal.constraintvalidators.bv.number.bound.decimal.AbstractDecimalMaxValidator;
import org.hibernate.validator.internal.constraintvalidators.bv.number.bound.decimal.DecimalNumberComparatorHelper;

public class DecimalMaxValidatorForBigInteger
extends AbstractDecimalMaxValidator<BigInteger> {
    @Override
    protected int compare(BigInteger number) {
        return DecimalNumberComparatorHelper.compare(number, this.maxValue);
    }
}

