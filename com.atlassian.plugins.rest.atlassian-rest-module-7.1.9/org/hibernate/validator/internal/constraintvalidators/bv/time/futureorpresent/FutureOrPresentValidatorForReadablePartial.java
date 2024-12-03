/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.time.Instant
 *  org.joda.time.ReadableInstant
 *  org.joda.time.ReadablePartial
 */
package org.hibernate.validator.internal.constraintvalidators.bv.time.futureorpresent;

import java.time.Clock;
import org.hibernate.validator.internal.constraintvalidators.bv.time.futureorpresent.AbstractFutureOrPresentEpochBasedValidator;
import org.joda.time.Instant;
import org.joda.time.ReadableInstant;
import org.joda.time.ReadablePartial;

public class FutureOrPresentValidatorForReadablePartial
extends AbstractFutureOrPresentEpochBasedValidator<ReadablePartial> {
    @Override
    protected long getEpochMillis(ReadablePartial value, Clock reference) {
        return value.toDateTime((ReadableInstant)new Instant(reference.millis())).getMillis();
    }
}

