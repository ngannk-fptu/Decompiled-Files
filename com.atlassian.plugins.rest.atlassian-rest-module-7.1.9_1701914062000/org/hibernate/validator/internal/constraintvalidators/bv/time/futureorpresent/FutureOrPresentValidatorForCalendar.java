/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.constraintvalidators.bv.time.futureorpresent;

import java.time.Instant;
import java.util.Calendar;
import org.hibernate.validator.internal.constraintvalidators.bv.time.futureorpresent.AbstractFutureOrPresentInstantBasedValidator;

public class FutureOrPresentValidatorForCalendar
extends AbstractFutureOrPresentInstantBasedValidator<Calendar> {
    @Override
    protected Instant getInstant(Calendar value) {
        return value.toInstant();
    }
}

