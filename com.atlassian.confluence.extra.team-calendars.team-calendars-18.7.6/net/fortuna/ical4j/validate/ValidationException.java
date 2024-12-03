/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.validate;

import java.text.MessageFormat;

public class ValidationException
extends RuntimeException {
    private static final long serialVersionUID = 309245291364742896L;

    public ValidationException() {
    }

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Object[] args) {
        super(MessageFormat.format(message, args));
    }
}

