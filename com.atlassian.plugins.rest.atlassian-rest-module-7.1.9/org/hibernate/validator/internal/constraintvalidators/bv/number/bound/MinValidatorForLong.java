/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.constraintvalidators.bv.number.bound;

import org.hibernate.validator.internal.constraintvalidators.bv.number.bound.AbstractMinValidator;
import org.hibernate.validator.internal.constraintvalidators.bv.number.bound.NumberComparatorHelper;

public class MinValidatorForLong
extends AbstractMinValidator<Long> {
    @Override
    protected int compare(Long number) {
        return NumberComparatorHelper.compare(number, this.minValue);
    }
}

