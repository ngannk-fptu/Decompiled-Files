/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.constraintvalidators.bv.number.bound;

import java.math.BigInteger;
import org.hibernate.validator.internal.constraintvalidators.bv.number.bound.AbstractMinValidator;
import org.hibernate.validator.internal.constraintvalidators.bv.number.bound.NumberComparatorHelper;

public class MinValidatorForBigInteger
extends AbstractMinValidator<BigInteger> {
    @Override
    protected int compare(BigInteger number) {
        return NumberComparatorHelper.compare(number, this.minValue);
    }
}

