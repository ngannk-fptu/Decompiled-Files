/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr;

import javax.jcr.RepositoryException;

public class UnsupportedRepositoryOperationException
extends RepositoryException {
    public UnsupportedRepositoryOperationException() {
    }

    public UnsupportedRepositoryOperationException(String message) {
        super(message);
    }

    public UnsupportedRepositoryOperationException(String message, Throwable rootCause) {
        super(message, rootCause);
    }

    public UnsupportedRepositoryOperationException(Throwable rootCause) {
        super(rootCause);
    }
}

