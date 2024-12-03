/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.constraintvalidators.bv.time.futureorpresent;

import java.time.Clock;
import java.time.LocalDateTime;
import org.hibernate.validator.internal.constraintvalidators.bv.time.futureorpresent.AbstractFutureOrPresentJavaTimeValidator;

public class FutureOrPresentValidatorForLocalDateTime
extends AbstractFutureOrPresentJavaTimeValidator<LocalDateTime> {
    @Override
    protected LocalDateTime getReferenceValue(Clock reference) {
        return LocalDateTime.now(reference);
    }
}

