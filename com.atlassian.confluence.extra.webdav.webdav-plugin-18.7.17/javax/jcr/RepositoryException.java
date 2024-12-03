/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr;

public class RepositoryException
extends Exception {
    public RepositoryException() {
    }

    public RepositoryException(String message) {
        super(message);
    }

    public RepositoryException(String message, Throwable rootCause) {
        super(message, rootCause);
    }

    public RepositoryException(Throwable rootCause) {
        super(rootCause);
    }
}

