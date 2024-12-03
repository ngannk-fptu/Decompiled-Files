/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.constraintvalidators.bv.time.futureorpresent;

import java.time.Instant;
import java.util.Date;
import org.hibernate.validator.internal.constraintvalidators.bv.time.futureorpresent.AbstractFutureOrPresentInstantBasedValidator;

public class FutureOrPresentValidatorForDate
extends AbstractFutureOrPresentInstantBasedValidator<Date> {
    @Override
    protected Instant getInstant(Date value) {
        return Instant.ofEpochMilli(value.getTime());
    }
}

