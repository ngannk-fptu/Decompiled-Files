/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.constraintvalidators.bv.time.past;

import java.time.Instant;
import java.util.Calendar;
import org.hibernate.validator.internal.constraintvalidators.bv.time.past.AbstractPastInstantBasedValidator;

public class PastValidatorForCalendar
extends AbstractPastInstantBasedValidator<Calendar> {
    @Override
    protected Instant getInstant(Calendar value) {
        return value.toInstant();
    }
}

