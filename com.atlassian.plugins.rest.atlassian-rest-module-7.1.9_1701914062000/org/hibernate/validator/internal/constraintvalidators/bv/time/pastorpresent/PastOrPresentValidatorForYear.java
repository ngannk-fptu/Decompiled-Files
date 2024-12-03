/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.constraintvalidators.bv.time.pastorpresent;

import java.time.Clock;
import java.time.Year;
import org.hibernate.validator.internal.constraintvalidators.bv.time.pastorpresent.AbstractPastOrPresentJavaTimeValidator;

public class PastOrPresentValidatorForYear
extends AbstractPastOrPresentJavaTimeValidator<Year> {
    @Override
    protected Year getReferenceValue(Clock reference) {
        return Year.now(reference);
    }
}

