/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.constraints.PastOrPresent
 */
package org.hibernate.validator.internal.constraintvalidators.bv.time.pastorpresent;

import java.time.Duration;
import javax.validation.constraints.PastOrPresent;
import org.hibernate.validator.internal.constraintvalidators.bv.time.AbstractInstantBasedTimeValidator;

public abstract class AbstractPastOrPresentInstantBasedValidator<T>
extends AbstractInstantBasedTimeValidator<PastOrPresent, T> {
    @Override
    protected boolean isValid(int result) {
        return result <= 0;
    }

    @Override
    protected Duration getEffectiveTemporalValidationTolerance(Duration absoluteTemporalValidationTolerance) {
        return absoluteTemporalValidationTolerance;
    }
}

