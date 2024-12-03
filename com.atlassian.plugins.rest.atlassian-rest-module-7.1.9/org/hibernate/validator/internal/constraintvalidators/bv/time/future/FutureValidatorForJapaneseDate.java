/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.constraintvalidators.bv.time.future;

import java.time.Clock;
import java.time.chrono.JapaneseDate;
import org.hibernate.validator.internal.constraintvalidators.bv.time.future.AbstractFutureJavaTimeValidator;

public class FutureValidatorForJapaneseDate
extends AbstractFutureJavaTimeValidator<JapaneseDate> {
    @Override
    protected JapaneseDate getReferenceValue(Clock reference) {
        return JapaneseDate.now(reference);
    }
}

