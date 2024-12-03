/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.constraintvalidators.bv.time.futureorpresent;

import java.time.Clock;
import java.time.chrono.ThaiBuddhistDate;
import org.hibernate.validator.internal.constraintvalidators.bv.time.futureorpresent.AbstractFutureOrPresentJavaTimeValidator;

public class FutureOrPresentValidatorForThaiBuddhistDate
extends AbstractFutureOrPresentJavaTimeValidator<ThaiBuddhistDate> {
    @Override
    protected ThaiBuddhistDate getReferenceValue(Clock reference) {
        return ThaiBuddhistDate.now(reference);
    }
}

