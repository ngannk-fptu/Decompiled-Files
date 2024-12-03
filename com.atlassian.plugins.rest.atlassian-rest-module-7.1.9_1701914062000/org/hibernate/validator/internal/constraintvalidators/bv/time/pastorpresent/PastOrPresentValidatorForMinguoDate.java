/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.constraintvalidators.bv.time.pastorpresent;

import java.time.Clock;
import java.time.chrono.MinguoDate;
import org.hibernate.validator.internal.constraintvalidators.bv.time.pastorpresent.AbstractPastOrPresentJavaTimeValidator;

public class PastOrPresentValidatorForMinguoDate
extends AbstractPastOrPresentJavaTimeValidator<MinguoDate> {
    @Override
    protected MinguoDate getReferenceValue(Clock reference) {
        return MinguoDate.now(reference);
    }
}

