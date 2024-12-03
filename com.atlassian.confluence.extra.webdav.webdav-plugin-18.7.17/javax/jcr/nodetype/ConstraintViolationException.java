/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr.nodetype;

import javax.jcr.RepositoryException;

public class ConstraintViolationException
extends RepositoryException {
    public ConstraintViolationException() {
    }

    public ConstraintViolationException(String message) {
        super(message);
    }

    public ConstraintViolationException(String message, Throwable rootCause) {
        super(message, rootCause);
    }

    public ConstraintViolationException(Throwable rootCause) {
        super(rootCause);
    }
}

