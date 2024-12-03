/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.constraintvalidators.bv.time.future;

import java.time.Clock;
import java.time.Instant;
import org.hibernate.validator.internal.constraintvalidators.bv.time.future.AbstractFutureJavaTimeValidator;

public class FutureValidatorForInstant
extends AbstractFutureJavaTimeValidator<Instant> {
    @Override
    protected Instant getReferenceValue(Clock reference) {
        return Instant.now(reference);
    }
}

