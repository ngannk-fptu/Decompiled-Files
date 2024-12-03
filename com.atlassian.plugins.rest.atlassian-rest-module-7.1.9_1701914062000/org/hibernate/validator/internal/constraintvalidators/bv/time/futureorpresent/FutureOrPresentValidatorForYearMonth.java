/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.constraintvalidators.bv.time.futureorpresent;

import java.time.Clock;
import java.time.YearMonth;
import org.hibernate.validator.internal.constraintvalidators.bv.time.futureorpresent.AbstractFutureOrPresentJavaTimeValidator;

public class FutureOrPresentValidatorForYearMonth
extends AbstractFutureOrPresentJavaTimeValidator<YearMonth> {
    @Override
    protected YearMonth getReferenceValue(Clock reference) {
        return YearMonth.now(reference);
    }
}

