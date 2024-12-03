/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.constraintvalidators.bv.time.pastorpresent;

import java.time.Clock;
import java.time.chrono.HijrahDate;
import org.hibernate.validator.internal.constraintvalidators.bv.time.pastorpresent.AbstractPastOrPresentJavaTimeValidator;

public class PastOrPresentValidatorForHijrahDate
extends AbstractPastOrPresentJavaTimeValidator<HijrahDate> {
    @Override
    protected HijrahDate getReferenceValue(Clock reference) {
        return HijrahDate.now(reference);
    }
}

