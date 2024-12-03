/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.constraints.FutureOrPresent
 */
package org.hibernate.validator.internal.constraintvalidators.bv.time.futureorpresent;

import java.time.Duration;
import javax.validation.constraints.FutureOrPresent;
import org.hibernate.validator.internal.constraintvalidators.bv.time.AbstractInstantBasedTimeValidator;

public abstract class AbstractFutureOrPresentInstantBasedValidator<T>
extends AbstractInstantBasedTimeValidator<FutureOrPresent, T> {
    @Override
    protected boolean isValid(int result) {
        return result >= 0;
    }

    @Override
    protected Duration getEffectiveTemporalValidationTolerance(Duration absoluteTemporalValidationTolerance) {
        return absoluteTemporalValidationTolerance.negated();
    }
}

