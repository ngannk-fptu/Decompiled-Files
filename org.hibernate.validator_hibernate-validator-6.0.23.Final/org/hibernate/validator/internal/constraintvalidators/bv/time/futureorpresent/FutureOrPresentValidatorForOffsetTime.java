/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.constraintvalidators.bv.time.futureorpresent;

import java.time.Clock;
import java.time.OffsetTime;
import org.hibernate.validator.internal.constraintvalidators.bv.time.futureorpresent.AbstractFutureOrPresentJavaTimeValidator;

public class FutureOrPresentValidatorForOffsetTime
extends AbstractFutureOrPresentJavaTimeValidator<OffsetTime> {
    @Override
    protected OffsetTime getReferenceValue(Clock reference) {
        return OffsetTime.now(reference);
    }
}

