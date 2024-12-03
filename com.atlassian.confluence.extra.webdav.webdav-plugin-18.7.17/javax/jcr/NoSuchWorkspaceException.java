/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr;

import javax.jcr.RepositoryException;

public class NoSuchWorkspaceException
extends RepositoryException {
    public NoSuchWorkspaceException() {
    }

    public NoSuchWorkspaceException(String message) {
        super(message);
    }

    public NoSuchWorkspaceException(String message, Throwable rootCause) {
        super(message, rootCause);
    }

    public NoSuchWorkspaceException(Throwable rootCause) {
        super(rootCause);
    }
}

