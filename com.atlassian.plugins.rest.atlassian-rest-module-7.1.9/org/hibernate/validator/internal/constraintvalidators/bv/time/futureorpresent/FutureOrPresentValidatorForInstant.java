/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.constraintvalidators.bv.time.futureorpresent;

import java.time.Clock;
import java.time.Instant;
import org.hibernate.validator.internal.constraintvalidators.bv.time.futureorpresent.AbstractFutureOrPresentJavaTimeValidator;

public class FutureOrPresentValidatorForInstant
extends AbstractFutureOrPresentJavaTimeValidator<Instant> {
    @Override
    protected Instant getReferenceValue(Clock reference) {
        return Instant.now(reference);
    }
}

