/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.LoggerFactory
 */
package net.fortuna.ical4j.validate;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.function.Predicate;
import net.fortuna.ical4j.validate.ValidationException;
import org.slf4j.LoggerFactory;

public interface Validator<T>
extends Serializable {
    public static <T> void assertFalse(Predicate<T> predicate, String message, boolean warn, T components, Object ... messageParams) {
        if (predicate.test(components)) {
            if (warn) {
                LoggerFactory.getLogger(Validator.class).warn(MessageFormat.format(message, messageParams));
            } else {
                throw new ValidationException(message, messageParams);
            }
        }
    }

    public void validate(T var1) throws ValidationException;
}

