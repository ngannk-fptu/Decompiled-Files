/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.constraintvalidators.bv.time.past;

import java.time.Clock;
import java.time.LocalDate;
import org.hibernate.validator.internal.constraintvalidators.bv.time.past.AbstractPastJavaTimeValidator;

public class PastValidatorForLocalDate
extends AbstractPastJavaTimeValidator<LocalDate> {
    @Override
    protected LocalDate getReferenceValue(Clock reference) {
        return LocalDate.now(reference);
    }
}

