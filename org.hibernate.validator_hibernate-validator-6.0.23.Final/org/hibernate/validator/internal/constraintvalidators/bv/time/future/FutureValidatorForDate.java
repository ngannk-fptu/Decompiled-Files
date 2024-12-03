/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.constraintvalidators.bv.time.future;

import java.time.Instant;
import java.util.Date;
import org.hibernate.validator.internal.constraintvalidators.bv.time.future.AbstractFutureInstantBasedValidator;

public class FutureValidatorForDate
extends AbstractFutureInstantBasedValidator<Date> {
    @Override
    protected Instant getInstant(Date value) {
        return Instant.ofEpochMilli(value.getTime());
    }
}

