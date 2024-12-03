/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.constraintvalidators.bv.time.past;

import java.time.Clock;
import java.time.LocalTime;
import org.hibernate.validator.internal.constraintvalidators.bv.time.past.AbstractPastJavaTimeValidator;

public class PastValidatorForLocalTime
extends AbstractPastJavaTimeValidator<LocalTime> {
    @Override
    protected LocalTime getReferenceValue(Clock reference) {
        return LocalTime.now(reference);
    }
}

