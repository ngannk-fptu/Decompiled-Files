/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.constraintvalidators.bv.time.past;

import java.time.Clock;
import java.time.OffsetTime;
import org.hibernate.validator.internal.constraintvalidators.bv.time.past.AbstractPastJavaTimeValidator;

public class PastValidatorForOffsetTime
extends AbstractPastJavaTimeValidator<OffsetTime> {
    @Override
    protected OffsetTime getReferenceValue(Clock reference) {
        return OffsetTime.now(reference);
    }
}

