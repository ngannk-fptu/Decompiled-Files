/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.constraintvalidators.bv.time.pastorpresent;

import java.time.Clock;
import java.time.LocalTime;
import org.hibernate.validator.internal.constraintvalidators.bv.time.pastorpresent.AbstractPastOrPresentJavaTimeValidator;

public class PastOrPresentValidatorForLocalTime
extends AbstractPastOrPresentJavaTimeValidator<LocalTime> {
    @Override
    protected LocalTime getReferenceValue(Clock reference) {
        return LocalTime.now(reference);
    }
}

