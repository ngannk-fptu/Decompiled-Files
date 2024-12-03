/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.constraintvalidators.bv.time.past;

import java.time.Clock;
import java.time.YearMonth;
import org.hibernate.validator.internal.constraintvalidators.bv.time.past.AbstractPastJavaTimeValidator;

public class PastValidatorForYearMonth
extends AbstractPastJavaTimeValidator<YearMonth> {
    @Override
    protected YearMonth getReferenceValue(Clock reference) {
        return YearMonth.now(reference);
    }
}

