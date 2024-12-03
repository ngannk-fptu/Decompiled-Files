/*
 * Decompiled with CFR 0.152.
 */
package javax.validation;

import javax.validation.ValidationException;

public class ConstraintDeclarationException
extends ValidationException {
    public ConstraintDeclarationException(String message) {
        super(message);
    }

    public ConstraintDeclarationException() {
    }

    public ConstraintDeclarationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConstraintDeclarationException(Throwable cause) {
        super(cause);
    }
}

