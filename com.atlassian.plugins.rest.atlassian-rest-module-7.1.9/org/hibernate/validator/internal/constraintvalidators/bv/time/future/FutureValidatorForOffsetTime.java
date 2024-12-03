/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.constraintvalidators.bv.time.future;

import java.time.Clock;
import java.time.OffsetTime;
import org.hibernate.validator.internal.constraintvalidators.bv.time.future.AbstractFutureJavaTimeValidator;

public class FutureValidatorForOffsetTime
extends AbstractFutureJavaTimeValidator<OffsetTime> {
    @Override
    protected OffsetTime getReferenceValue(Clock reference) {
        return OffsetTime.now(reference);
    }
}

