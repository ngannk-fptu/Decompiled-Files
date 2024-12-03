/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.constraintvalidators.bv.time.futureorpresent;

import java.time.Clock;
import java.time.Year;
import org.hibernate.validator.internal.constraintvalidators.bv.time.futureorpresent.AbstractFutureOrPresentJavaTimeValidator;

public class FutureOrPresentValidatorForYear
extends AbstractFutureOrPresentJavaTimeValidator<Year> {
    @Override
    protected Year getReferenceValue(Clock reference) {
        return Year.now(reference);
    }
}

