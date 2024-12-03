/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.constraintvalidators.bv.time.future;

import java.time.Clock;
import java.time.MonthDay;
import org.hibernate.validator.internal.constraintvalidators.bv.time.future.AbstractFutureJavaTimeValidator;

public class FutureValidatorForMonthDay
extends AbstractFutureJavaTimeValidator<MonthDay> {
    @Override
    protected MonthDay getReferenceValue(Clock reference) {
        return MonthDay.now(reference);
    }
}

