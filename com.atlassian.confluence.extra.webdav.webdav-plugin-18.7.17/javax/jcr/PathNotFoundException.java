/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr;

import javax.jcr.RepositoryException;

public class PathNotFoundException
extends RepositoryException {
    public PathNotFoundException() {
    }

    public PathNotFoundException(String message) {
        super(message);
    }

    public PathNotFoundException(String message, Throwable rootCause) {
        super(message, rootCause);
    }

    public PathNotFoundException(Throwable rootCause) {
        super(rootCause);
    }
}

