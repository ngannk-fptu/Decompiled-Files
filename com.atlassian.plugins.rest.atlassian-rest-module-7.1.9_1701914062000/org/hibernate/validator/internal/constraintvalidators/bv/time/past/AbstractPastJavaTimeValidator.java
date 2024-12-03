/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.constraints.Past
 */
package org.hibernate.validator.internal.constraintvalidators.bv.time.past;

import java.time.Duration;
import java.time.temporal.TemporalAccessor;
import javax.validation.constraints.Past;
import org.hibernate.validator.internal.constraintvalidators.bv.time.AbstractJavaTimeValidator;

public abstract class AbstractPastJavaTimeValidator<T extends TemporalAccessor & Comparable<? super T>>
extends AbstractJavaTimeValidator<Past, T> {
    @Override
    protected boolean isValid(int result) {
        return result < 0;
    }

    @Override
    protected Duration getEffectiveTemporalValidationTolerance(Duration absoluteTemporalValidationTolerance) {
        return absoluteTemporalValidationTolerance;
    }
}

