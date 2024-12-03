/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr.nodetype;

import javax.jcr.RepositoryException;

public class NoSuchNodeTypeException
extends RepositoryException {
    public NoSuchNodeTypeException() {
    }

    public NoSuchNodeTypeException(String message) {
        super(message);
    }

    public NoSuchNodeTypeException(String message, Throwable rootCause) {
        super(message, rootCause);
    }

    public NoSuchNodeTypeException(Throwable rootCause) {
        super(rootCause);
    }
}

