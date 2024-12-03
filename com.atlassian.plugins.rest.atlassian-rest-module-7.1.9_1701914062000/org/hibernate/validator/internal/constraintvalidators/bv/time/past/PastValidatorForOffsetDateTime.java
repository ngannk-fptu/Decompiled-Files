/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.constraintvalidators.bv.time.past;

import java.time.Clock;
import java.time.OffsetDateTime;
import org.hibernate.validator.internal.constraintvalidators.bv.time.past.AbstractPastJavaTimeValidator;

public class PastValidatorForOffsetDateTime
extends AbstractPastJavaTimeValidator<OffsetDateTime> {
    @Override
    protected OffsetDateTime getReferenceValue(Clock reference) {
        return OffsetDateTime.now(reference);
    }
}

