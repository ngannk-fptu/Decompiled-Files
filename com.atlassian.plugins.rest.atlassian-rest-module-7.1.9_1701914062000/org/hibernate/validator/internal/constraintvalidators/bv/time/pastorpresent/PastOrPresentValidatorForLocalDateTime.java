/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.constraintvalidators.bv.time.pastorpresent;

import java.time.Clock;
import java.time.LocalDateTime;
import org.hibernate.validator.internal.constraintvalidators.bv.time.pastorpresent.AbstractPastOrPresentJavaTimeValidator;

public class PastOrPresentValidatorForLocalDateTime
extends AbstractPastOrPresentJavaTimeValidator<LocalDateTime> {
    @Override
    protected LocalDateTime getReferenceValue(Clock reference) {
        return LocalDateTime.now(reference);
    }
}

