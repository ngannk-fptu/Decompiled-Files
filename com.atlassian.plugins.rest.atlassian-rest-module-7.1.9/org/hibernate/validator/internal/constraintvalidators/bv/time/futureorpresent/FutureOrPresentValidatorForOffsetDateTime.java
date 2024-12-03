/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.constraintvalidators.bv.time.futureorpresent;

import java.time.Clock;
import java.time.OffsetDateTime;
import org.hibernate.validator.internal.constraintvalidators.bv.time.futureorpresent.AbstractFutureOrPresentJavaTimeValidator;

public class FutureOrPresentValidatorForOffsetDateTime
extends AbstractFutureOrPresentJavaTimeValidator<OffsetDateTime> {
    @Override
    protected OffsetDateTime getReferenceValue(Clock reference) {
        return OffsetDateTime.now(reference);
    }
}

