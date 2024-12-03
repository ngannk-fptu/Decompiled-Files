/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.constraints.FutureOrPresent
 */
package org.hibernate.validator.internal.constraintvalidators.bv.time.futureorpresent;

import java.time.Duration;
import java.time.temporal.TemporalAccessor;
import javax.validation.constraints.FutureOrPresent;
import org.hibernate.validator.internal.constraintvalidators.bv.time.AbstractJavaTimeValidator;

public abstract class AbstractFutureOrPresentJavaTimeValidator<T extends TemporalAccessor & Comparable<? super T>>
extends AbstractJavaTimeValidator<FutureOrPresent, T> {
    @Override
    protected boolean isValid(int result) {
        return result >= 0;
    }

    @Override
    protected Duration getEffectiveTemporalValidationTolerance(Duration absoluteTemporalValidationTolerance) {
        return absoluteTemporalValidationTolerance.negated();
    }
}

