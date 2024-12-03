/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.constraintvalidators.bv.time.pastorpresent;

import java.time.Clock;
import java.time.OffsetTime;
import org.hibernate.validator.internal.constraintvalidators.bv.time.pastorpresent.AbstractPastOrPresentJavaTimeValidator;

public class PastOrPresentValidatorForOffsetTime
extends AbstractPastOrPresentJavaTimeValidator<OffsetTime> {
    @Override
    protected OffsetTime getReferenceValue(Clock reference) {
        return OffsetTime.now(reference);
    }
}

