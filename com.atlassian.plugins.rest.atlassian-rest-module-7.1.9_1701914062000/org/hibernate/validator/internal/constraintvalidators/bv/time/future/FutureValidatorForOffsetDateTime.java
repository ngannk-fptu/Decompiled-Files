/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.constraintvalidators.bv.time.future;

import java.time.Clock;
import java.time.OffsetDateTime;
import org.hibernate.validator.internal.constraintvalidators.bv.time.future.AbstractFutureJavaTimeValidator;

public class FutureValidatorForOffsetDateTime
extends AbstractFutureJavaTimeValidator<OffsetDateTime> {
    @Override
    protected OffsetDateTime getReferenceValue(Clock reference) {
        return OffsetDateTime.now(reference);
    }
}

