/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr;

import javax.jcr.RepositoryException;

public class InvalidItemStateException
extends RepositoryException {
    public InvalidItemStateException() {
    }

    public InvalidItemStateException(String message) {
        super(message);
    }

    public InvalidItemStateException(String message, Throwable rootCause) {
        super(message, rootCause);
    }

    public InvalidItemStateException(Throwable rootCause) {
        super(rootCause);
    }
}

