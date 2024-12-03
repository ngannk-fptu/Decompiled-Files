/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.ConstraintValidator
 *  javax.validation.ConstraintValidatorContext
 *  javax.validation.constraints.Max
 */
package org.hibernate.validator.internal.constraintvalidators.bv.number.bound;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.Max;

public abstract class AbstractMaxValidator<T>
implements ConstraintValidator<Max, T> {
    protected long maxValue;

    public void initialize(Max maxValue) {
        this.maxValue = maxValue.value();
    }

    public boolean isValid(T value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return true;
        }
        return this.compare(value) <= 0;
    }

    protected abstract int compare(T var1);
}

