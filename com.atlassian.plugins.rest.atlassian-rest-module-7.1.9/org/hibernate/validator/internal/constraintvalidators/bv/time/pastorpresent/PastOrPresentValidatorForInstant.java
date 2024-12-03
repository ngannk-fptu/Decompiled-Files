/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.constraintvalidators.bv.time.pastorpresent;

import java.time.Clock;
import java.time.Instant;
import org.hibernate.validator.internal.constraintvalidators.bv.time.pastorpresent.AbstractPastOrPresentJavaTimeValidator;

public class PastOrPresentValidatorForInstant
extends AbstractPastOrPresentJavaTimeValidator<Instant> {
    @Override
    protected Instant getReferenceValue(Clock reference) {
        return Instant.now(reference);
    }
}

