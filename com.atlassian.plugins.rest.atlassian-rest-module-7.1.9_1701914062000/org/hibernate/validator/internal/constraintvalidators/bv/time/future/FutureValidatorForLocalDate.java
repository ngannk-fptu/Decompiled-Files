/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.constraintvalidators.bv.time.future;

import java.time.Clock;
import java.time.LocalDate;
import org.hibernate.validator.internal.constraintvalidators.bv.time.future.AbstractFutureJavaTimeValidator;

public class FutureValidatorForLocalDate
extends AbstractFutureJavaTimeValidator<LocalDate> {
    @Override
    protected LocalDate getReferenceValue(Clock reference) {
        return LocalDate.now(reference);
    }
}

