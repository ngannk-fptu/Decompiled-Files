/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.constraints.Future
 */
package org.hibernate.validator.internal.constraintvalidators.bv.time.future;

import java.time.Duration;
import javax.validation.constraints.Future;
import org.hibernate.validator.internal.constraintvalidators.bv.time.AbstractInstantBasedTimeValidator;

public abstract class AbstractFutureInstantBasedValidator<T>
extends AbstractInstantBasedTimeValidator<Future, T> {
    @Override
    protected boolean isValid(int result) {
        return result > 0;
    }

    @Override
    protected Duration getEffectiveTemporalValidationTolerance(Duration absoluteTemporalValidationTolerance) {
        return absoluteTemporalValidationTolerance.negated();
    }
}

