/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.constraintvalidators.bv.time.future;

import java.time.Instant;
import java.util.Calendar;
import org.hibernate.validator.internal.constraintvalidators.bv.time.future.AbstractFutureInstantBasedValidator;

public class FutureValidatorForCalendar
extends AbstractFutureInstantBasedValidator<Calendar> {
    @Override
    protected Instant getInstant(Calendar value) {
        return value.toInstant();
    }
}

