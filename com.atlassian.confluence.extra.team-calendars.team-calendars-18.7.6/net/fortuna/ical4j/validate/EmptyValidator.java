/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.validate;

import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.Validator;

public final class EmptyValidator<T>
implements Validator<T> {
    private static final long serialVersionUID = 1L;

    @Override
    public void validate(T target) throws ValidationException {
    }
}

