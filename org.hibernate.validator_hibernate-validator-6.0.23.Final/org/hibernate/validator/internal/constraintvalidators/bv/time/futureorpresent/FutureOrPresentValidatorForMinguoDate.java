/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.constraintvalidators.bv.time.futureorpresent;

import java.time.Clock;
import java.time.chrono.MinguoDate;
import org.hibernate.validator.internal.constraintvalidators.bv.time.futureorpresent.AbstractFutureOrPresentJavaTimeValidator;

public class FutureOrPresentValidatorForMinguoDate
extends AbstractFutureOrPresentJavaTimeValidator<MinguoDate> {
    @Override
    protected MinguoDate getReferenceValue(Clock reference) {
        return MinguoDate.now(reference);
    }
}

