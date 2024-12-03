/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.constraintvalidators.bv.time.past;

import java.time.Clock;
import java.time.Year;
import org.hibernate.validator.internal.constraintvalidators.bv.time.past.AbstractPastJavaTimeValidator;

public class PastValidatorForYear
extends AbstractPastJavaTimeValidator<Year> {
    @Override
    protected Year getReferenceValue(Clock reference) {
        return Year.now(reference);
    }
}

