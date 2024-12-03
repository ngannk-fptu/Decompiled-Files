/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.constraintvalidators.bv.time.futureorpresent;

import java.time.Clock;
import java.time.chrono.JapaneseDate;
import org.hibernate.validator.internal.constraintvalidators.bv.time.futureorpresent.AbstractFutureOrPresentJavaTimeValidator;

public class FutureOrPresentValidatorForJapaneseDate
extends AbstractFutureOrPresentJavaTimeValidator<JapaneseDate> {
    @Override
    protected JapaneseDate getReferenceValue(Clock reference) {
        return JapaneseDate.now(reference);
    }
}

