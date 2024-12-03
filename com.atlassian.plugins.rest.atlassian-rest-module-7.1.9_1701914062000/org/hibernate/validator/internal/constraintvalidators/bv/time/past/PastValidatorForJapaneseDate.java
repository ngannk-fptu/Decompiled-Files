/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.constraintvalidators.bv.time.past;

import java.time.Clock;
import java.time.chrono.JapaneseDate;
import org.hibernate.validator.internal.constraintvalidators.bv.time.past.AbstractPastJavaTimeValidator;

public class PastValidatorForJapaneseDate
extends AbstractPastJavaTimeValidator<JapaneseDate> {
    @Override
    protected JapaneseDate getReferenceValue(Clock reference) {
        return JapaneseDate.now(reference);
    }
}

