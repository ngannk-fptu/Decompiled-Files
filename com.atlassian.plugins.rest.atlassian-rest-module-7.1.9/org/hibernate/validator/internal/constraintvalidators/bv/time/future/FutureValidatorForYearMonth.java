/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.constraintvalidators.bv.time.future;

import java.time.Clock;
import java.time.YearMonth;
import org.hibernate.validator.internal.constraintvalidators.bv.time.future.AbstractFutureJavaTimeValidator;

public class FutureValidatorForYearMonth
extends AbstractFutureJavaTimeValidator<YearMonth> {
    @Override
    protected YearMonth getReferenceValue(Clock reference) {
        return YearMonth.now(reference);
    }
}

