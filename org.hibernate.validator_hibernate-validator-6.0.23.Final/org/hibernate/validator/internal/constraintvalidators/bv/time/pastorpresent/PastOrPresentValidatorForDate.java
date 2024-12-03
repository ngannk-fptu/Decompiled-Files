/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.constraintvalidators.bv.time.pastorpresent;

import java.time.Instant;
import java.util.Date;
import org.hibernate.validator.internal.constraintvalidators.bv.time.pastorpresent.AbstractPastOrPresentInstantBasedValidator;

public class PastOrPresentValidatorForDate
extends AbstractPastOrPresentInstantBasedValidator<Date> {
    @Override
    protected Instant getInstant(Date value) {
        return Instant.ofEpochMilli(value.getTime());
    }
}

