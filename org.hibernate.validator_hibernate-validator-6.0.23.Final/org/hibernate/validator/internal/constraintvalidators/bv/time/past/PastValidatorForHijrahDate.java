/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.constraintvalidators.bv.time.past;

import java.time.Clock;
import java.time.chrono.HijrahDate;
import org.hibernate.validator.internal.constraintvalidators.bv.time.past.AbstractPastJavaTimeValidator;

public class PastValidatorForHijrahDate
extends AbstractPastJavaTimeValidator<HijrahDate> {
    @Override
    protected HijrahDate getReferenceValue(Clock reference) {
        return HijrahDate.now(reference);
    }
}

