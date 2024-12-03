/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.constraintvalidators.bv.time.futureorpresent;

import java.time.Clock;
import java.time.chrono.HijrahDate;
import org.hibernate.validator.internal.constraintvalidators.bv.time.futureorpresent.AbstractFutureOrPresentJavaTimeValidator;

public class FutureOrPresentValidatorForHijrahDate
extends AbstractFutureOrPresentJavaTimeValidator<HijrahDate> {
    @Override
    protected HijrahDate getReferenceValue(Clock reference) {
        return HijrahDate.now(reference);
    }
}

