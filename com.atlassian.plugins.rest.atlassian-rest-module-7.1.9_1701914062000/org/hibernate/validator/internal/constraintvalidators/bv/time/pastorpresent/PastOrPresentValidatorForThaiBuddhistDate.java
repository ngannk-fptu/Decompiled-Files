/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.constraintvalidators.bv.time.pastorpresent;

import java.time.Clock;
import java.time.chrono.ThaiBuddhistDate;
import org.hibernate.validator.internal.constraintvalidators.bv.time.pastorpresent.AbstractPastOrPresentJavaTimeValidator;

public class PastOrPresentValidatorForThaiBuddhistDate
extends AbstractPastOrPresentJavaTimeValidator<ThaiBuddhistDate> {
    @Override
    protected ThaiBuddhistDate getReferenceValue(Clock reference) {
        return ThaiBuddhistDate.now(reference);
    }
}

