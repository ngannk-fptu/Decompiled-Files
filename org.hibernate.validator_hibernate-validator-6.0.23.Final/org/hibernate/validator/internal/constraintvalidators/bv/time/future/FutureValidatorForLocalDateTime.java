/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.constraintvalidators.bv.time.future;

import java.time.Clock;
import java.time.LocalDateTime;
import org.hibernate.validator.internal.constraintvalidators.bv.time.future.AbstractFutureJavaTimeValidator;

public class FutureValidatorForLocalDateTime
extends AbstractFutureJavaTimeValidator<LocalDateTime> {
    @Override
    protected LocalDateTime getReferenceValue(Clock reference) {
        return LocalDateTime.now(reference);
    }
}

