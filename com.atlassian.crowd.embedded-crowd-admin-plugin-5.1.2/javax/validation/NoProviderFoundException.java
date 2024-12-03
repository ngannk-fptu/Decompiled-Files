/*
 * Decompiled with CFR 0.152.
 */
package javax.validation;

import javax.validation.ValidationException;

public class NoProviderFoundException
extends ValidationException {
    public NoProviderFoundException() {
    }

    public NoProviderFoundException(String message) {
        super(message);
    }

    public NoProviderFoundException(Throwable cause) {
        super(cause);
    }

    public NoProviderFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

