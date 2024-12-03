/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.constraintvalidators.bv.time.past;

import java.time.Clock;
import java.time.MonthDay;
import org.hibernate.validator.internal.constraintvalidators.bv.time.past.AbstractPastJavaTimeValidator;

public class PastValidatorForMonthDay
extends AbstractPastJavaTimeValidator<MonthDay> {
    @Override
    protected MonthDay getReferenceValue(Clock reference) {
        return MonthDay.now(reference);
    }
}

