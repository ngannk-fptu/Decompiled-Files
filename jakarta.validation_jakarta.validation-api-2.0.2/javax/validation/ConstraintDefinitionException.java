/*
 * Decompiled with CFR 0.152.
 */
package javax.validation;

import javax.validation.ValidationException;

public class ConstraintDefinitionException
extends ValidationException {
    public ConstraintDefinitionException(String message) {
        super(message);
    }

    public ConstraintDefinitionException() {
    }

    public ConstraintDefinitionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConstraintDefinitionException(Throwable cause) {
        super(cause);
    }
}

