/*
 * Decompiled with CFR 0.152.
 */
package javax.validation;

public class ValidationException
extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }

    public ValidationException() {
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ValidationException(Throwable cause) {
        super(cause);
    }
}

