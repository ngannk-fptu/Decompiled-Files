/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.constraintvalidators.bv.number.bound;

import java.math.BigDecimal;
import org.hibernate.validator.internal.constraintvalidators.bv.number.bound.AbstractMinValidator;
import org.hibernate.validator.internal.constraintvalidators.bv.number.bound.NumberComparatorHelper;

public class MinValidatorForBigDecimal
extends AbstractMinValidator<BigDecimal> {
    @Override
    protected int compare(BigDecimal number) {
        return NumberComparatorHelper.compare(number, this.minValue);
    }
}

