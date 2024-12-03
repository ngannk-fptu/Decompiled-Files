/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr.query;

import javax.jcr.RepositoryException;

public class InvalidQueryException
extends RepositoryException {
    public InvalidQueryException() {
    }

    public InvalidQueryException(String message) {
        super(message);
    }

    public InvalidQueryException(String message, Throwable rootCause) {
        super(message, rootCause);
    }

    public InvalidQueryException(Throwable rootCause) {
        super(rootCause);
    }
}

