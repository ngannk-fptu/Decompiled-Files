/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.constraintvalidators.bv.time.futureorpresent;

import java.time.Clock;
import java.time.LocalTime;
import org.hibernate.validator.internal.constraintvalidators.bv.time.futureorpresent.AbstractFutureOrPresentJavaTimeValidator;

public class FutureOrPresentValidatorForLocalTime
extends AbstractFutureOrPresentJavaTimeValidator<LocalTime> {
    @Override
    protected LocalTime getReferenceValue(Clock reference) {
        return LocalTime.now(reference);
    }
}

