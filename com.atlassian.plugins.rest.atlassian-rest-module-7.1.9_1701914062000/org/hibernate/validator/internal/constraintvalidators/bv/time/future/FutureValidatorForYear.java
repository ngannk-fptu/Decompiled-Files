/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.constraintvalidators.bv.time.future;

import java.time.Clock;
import java.time.Year;
import org.hibernate.validator.internal.constraintvalidators.bv.time.future.AbstractFutureJavaTimeValidator;

public class FutureValidatorForYear
extends AbstractFutureJavaTimeValidator<Year> {
    @Override
    protected Year getReferenceValue(Clock reference) {
        return Year.now(reference);
    }
}

