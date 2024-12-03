/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.constraintvalidators.bv.time.future;

import java.time.Clock;
import java.time.chrono.HijrahDate;
import org.hibernate.validator.internal.constraintvalidators.bv.time.future.AbstractFutureJavaTimeValidator;

public class FutureValidatorForHijrahDate
extends AbstractFutureJavaTimeValidator<HijrahDate> {
    @Override
    protected HijrahDate getReferenceValue(Clock reference) {
        return HijrahDate.now(reference);
    }
}

