/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.constraintvalidators.bv.time.futureorpresent;

import java.time.Clock;
import java.time.MonthDay;
import org.hibernate.validator.internal.constraintvalidators.bv.time.futureorpresent.AbstractFutureOrPresentJavaTimeValidator;

public class FutureOrPresentValidatorForMonthDay
extends AbstractFutureOrPresentJavaTimeValidator<MonthDay> {
    @Override
    protected MonthDay getReferenceValue(Clock reference) {
        return MonthDay.now(reference);
    }
}

