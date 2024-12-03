/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.constraintvalidators.bv.time.pastorpresent;

import java.time.Clock;
import java.time.chrono.JapaneseDate;
import org.hibernate.validator.internal.constraintvalidators.bv.time.pastorpresent.AbstractPastOrPresentJavaTimeValidator;

public class PastOrPresentValidatorForJapaneseDate
extends AbstractPastOrPresentJavaTimeValidator<JapaneseDate> {
    @Override
    protected JapaneseDate getReferenceValue(Clock reference) {
        return JapaneseDate.now(reference);
    }
}

