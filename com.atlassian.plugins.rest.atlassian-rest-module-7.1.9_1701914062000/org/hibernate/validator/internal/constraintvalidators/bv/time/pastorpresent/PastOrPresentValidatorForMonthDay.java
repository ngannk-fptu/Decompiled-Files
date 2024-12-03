/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.constraintvalidators.bv.time.pastorpresent;

import java.time.Clock;
import java.time.MonthDay;
import org.hibernate.validator.internal.constraintvalidators.bv.time.pastorpresent.AbstractPastOrPresentJavaTimeValidator;

public class PastOrPresentValidatorForMonthDay
extends AbstractPastOrPresentJavaTimeValidator<MonthDay> {
    @Override
    protected MonthDay getReferenceValue(Clock reference) {
        return MonthDay.now(reference);
    }
}

