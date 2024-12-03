/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.constraintvalidators.bv.time.futureorpresent;

import java.time.Clock;
import java.time.LocalDate;
import org.hibernate.validator.internal.constraintvalidators.bv.time.futureorpresent.AbstractFutureOrPresentJavaTimeValidator;

public class FutureOrPresentValidatorForLocalDate
extends AbstractFutureOrPresentJavaTimeValidator<LocalDate> {
    @Override
    protected LocalDate getReferenceValue(Clock reference) {
        return LocalDate.now(reference);
    }
}

