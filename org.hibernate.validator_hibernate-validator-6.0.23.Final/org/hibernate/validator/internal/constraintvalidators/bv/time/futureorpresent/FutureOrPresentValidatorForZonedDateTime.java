/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.constraintvalidators.bv.time.futureorpresent;

import java.time.Clock;
import java.time.ZonedDateTime;
import org.hibernate.validator.internal.constraintvalidators.bv.time.futureorpresent.AbstractFutureOrPresentJavaTimeValidator;

public class FutureOrPresentValidatorForZonedDateTime
extends AbstractFutureOrPresentJavaTimeValidator<ZonedDateTime> {
    @Override
    protected ZonedDateTime getReferenceValue(Clock reference) {
        return ZonedDateTime.now(reference);
    }
}

