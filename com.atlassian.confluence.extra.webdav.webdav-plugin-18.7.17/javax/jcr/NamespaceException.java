/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr;

import javax.jcr.RepositoryException;

public class NamespaceException
extends RepositoryException {
    public NamespaceException() {
    }

    public NamespaceException(String message) {
        super(message);
    }

    public NamespaceException(String message, Throwable rootCause) {
        super(message, rootCause);
    }

    public NamespaceException(Throwable rootCause) {
        super(rootCause);
    }
}

