/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.time.ReadableInstant
 */
package org.hibernate.validator.internal.constraintvalidators.bv.time.past;

import java.time.Clock;
import org.hibernate.validator.internal.constraintvalidators.bv.time.past.AbstractPastEpochBasedValidator;
import org.joda.time.ReadableInstant;

public class PastValidatorForReadableInstant
extends AbstractPastEpochBasedValidator<ReadableInstant> {
    @Override
    protected long getEpochMillis(ReadableInstant value, Clock reference) {
        return value.getMillis();
    }
}

