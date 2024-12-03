/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.constraintvalidators.bv.time.pastorpresent;

import java.time.Clock;
import java.time.LocalDate;
import org.hibernate.validator.internal.constraintvalidators.bv.time.pastorpresent.AbstractPastOrPresentJavaTimeValidator;

public class PastOrPresentValidatorForLocalDate
extends AbstractPastOrPresentJavaTimeValidator<LocalDate> {
    @Override
    protected LocalDate getReferenceValue(Clock reference) {
        return LocalDate.now(reference);
    }
}

