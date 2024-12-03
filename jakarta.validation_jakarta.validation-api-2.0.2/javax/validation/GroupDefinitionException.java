/*
 * Decompiled with CFR 0.152.
 */
package javax.validation;

import javax.validation.ValidationException;

public class GroupDefinitionException
extends ValidationException {
    public GroupDefinitionException(String message) {
        super(message);
    }

    public GroupDefinitionException() {
    }

    public GroupDefinitionException(String message, Throwable cause) {
        super(message, cause);
    }

    public GroupDefinitionException(Throwable cause) {
        super(cause);
    }
}

