/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.constraintvalidators.bv.time.pastorpresent;

import java.time.Clock;
import java.time.ZonedDateTime;
import org.hibernate.validator.internal.constraintvalidators.bv.time.pastorpresent.AbstractPastOrPresentJavaTimeValidator;

public class PastOrPresentValidatorForZonedDateTime
extends AbstractPastOrPresentJavaTimeValidator<ZonedDateTime> {
    @Override
    protected ZonedDateTime getReferenceValue(Clock reference) {
        return ZonedDateTime.now(reference);
    }
}

