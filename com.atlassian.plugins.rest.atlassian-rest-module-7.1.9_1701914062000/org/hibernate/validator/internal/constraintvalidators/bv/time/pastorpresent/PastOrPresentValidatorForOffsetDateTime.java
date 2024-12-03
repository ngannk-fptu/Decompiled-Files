/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.constraintvalidators.bv.time.pastorpresent;

import java.time.Clock;
import java.time.OffsetDateTime;
import org.hibernate.validator.internal.constraintvalidators.bv.time.pastorpresent.AbstractPastOrPresentJavaTimeValidator;

public class PastOrPresentValidatorForOffsetDateTime
extends AbstractPastOrPresentJavaTimeValidator<OffsetDateTime> {
    @Override
    protected OffsetDateTime getReferenceValue(Clock reference) {
        return OffsetDateTime.now(reference);
    }
}

