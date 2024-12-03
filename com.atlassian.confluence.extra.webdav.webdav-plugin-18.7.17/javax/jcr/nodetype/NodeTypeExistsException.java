/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr.nodetype;

import javax.jcr.RepositoryException;

public class NodeTypeExistsException
extends RepositoryException {
    public NodeTypeExistsException() {
    }

    public NodeTypeExistsException(String message) {
        super(message);
    }

    public NodeTypeExistsException(String message, Throwable rootCause) {
        super(message, rootCause);
    }

    public NodeTypeExistsException(Throwable rootCause) {
        super(rootCause);
    }
}

