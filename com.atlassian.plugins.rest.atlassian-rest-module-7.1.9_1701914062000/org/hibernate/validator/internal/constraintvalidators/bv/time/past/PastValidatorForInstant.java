/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.constraintvalidators.bv.time.past;

import java.time.Clock;
import java.time.Instant;
import org.hibernate.validator.internal.constraintvalidators.bv.time.past.AbstractPastJavaTimeValidator;

public class PastValidatorForInstant
extends AbstractPastJavaTimeValidator<Instant> {
    @Override
    protected Instant getReferenceValue(Clock reference) {
        return Instant.now(reference);
    }
}

