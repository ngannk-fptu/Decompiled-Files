/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.constraintvalidators.bv.time.past;

import java.time.Instant;
import java.util.Date;
import org.hibernate.validator.internal.constraintvalidators.bv.time.past.AbstractPastInstantBasedValidator;

public class PastValidatorForDate
extends AbstractPastInstantBasedValidator<Date> {
    @Override
    protected Instant getInstant(Date value) {
        return Instant.ofEpochMilli(value.getTime());
    }
}

