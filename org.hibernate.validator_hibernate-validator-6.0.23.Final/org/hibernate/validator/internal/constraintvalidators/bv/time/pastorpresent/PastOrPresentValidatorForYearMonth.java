/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.constraintvalidators.bv.time.pastorpresent;

import java.time.Clock;
import java.time.YearMonth;
import org.hibernate.validator.internal.constraintvalidators.bv.time.pastorpresent.AbstractPastOrPresentJavaTimeValidator;

public class PastOrPresentValidatorForYearMonth
extends AbstractPastOrPresentJavaTimeValidator<YearMonth> {
    @Override
    protected YearMonth getReferenceValue(Clock reference) {
        return YearMonth.now(reference);
    }
}

