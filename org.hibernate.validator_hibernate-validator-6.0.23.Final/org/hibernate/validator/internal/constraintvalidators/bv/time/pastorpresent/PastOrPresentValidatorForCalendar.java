/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.constraintvalidators.bv.time.pastorpresent;

import java.time.Instant;
import java.util.Calendar;
import org.hibernate.validator.internal.constraintvalidators.bv.time.pastorpresent.AbstractPastOrPresentInstantBasedValidator;

public class PastOrPresentValidatorForCalendar
extends AbstractPastOrPresentInstantBasedValidator<Calendar> {
    @Override
    protected Instant getInstant(Calendar value) {
        return value.toInstant();
    }
}

