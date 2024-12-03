/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.constraintvalidators.bv.time.future;

import java.time.Clock;
import java.time.LocalTime;
import org.hibernate.validator.internal.constraintvalidators.bv.time.future.AbstractFutureJavaTimeValidator;

public class FutureValidatorForLocalTime
extends AbstractFutureJavaTimeValidator<LocalTime> {
    @Override
    protected LocalTime getReferenceValue(Clock reference) {
        return LocalTime.now(reference);
    }
}

