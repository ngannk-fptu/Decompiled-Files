/*
 * Decompiled with CFR 0.152.
 */
package javax.validation;

import javax.validation.ConstraintDeclarationException;

public class UnexpectedTypeException
extends ConstraintDeclarationException {
    public UnexpectedTypeException(String message) {
        super(message);
    }

    public UnexpectedTypeException() {
    }

    public UnexpectedTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnexpectedTypeException(Throwable cause) {
        super(cause);
    }
}

