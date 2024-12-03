/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.constraintvalidators.bv.time.future;

import java.time.Clock;
import java.time.chrono.MinguoDate;
import org.hibernate.validator.internal.constraintvalidators.bv.time.future.AbstractFutureJavaTimeValidator;

public class FutureValidatorForMinguoDate
extends AbstractFutureJavaTimeValidator<MinguoDate> {
    @Override
    protected MinguoDate getReferenceValue(Clock reference) {
        return MinguoDate.now(reference);
    }
}

