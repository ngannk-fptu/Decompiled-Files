/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.constraintvalidators.bv.time.past;

import java.time.Clock;
import java.time.LocalDateTime;
import org.hibernate.validator.internal.constraintvalidators.bv.time.past.AbstractPastJavaTimeValidator;

public class PastValidatorForLocalDateTime
extends AbstractPastJavaTimeValidator<LocalDateTime> {
    @Override
    protected LocalDateTime getReferenceValue(Clock reference) {
        return LocalDateTime.now(reference);
    }
}

