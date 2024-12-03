/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.ConstraintValidator
 *  javax.validation.ConstraintValidatorContext
 *  javax.validation.constraints.Min
 */
package org.hibernate.validator.internal.constraintvalidators.bv.number.bound;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.Min;

public abstract class AbstractMinValidator<T>
implements ConstraintValidator<Min, T> {
    protected long minValue;

    public void initialize(Min maxValue) {
        this.minValue = maxValue.value();
    }

    public boolean isValid(T value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return true;
        }
        return this.compare(value) >= 0;
    }

    protected abstract int compare(T var1);
}

