/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr;

import javax.jcr.RepositoryException;

public class ItemNotFoundException
extends RepositoryException {
    public ItemNotFoundException() {
    }

    public ItemNotFoundException(String message) {
        super(message);
    }

    public ItemNotFoundException(String message, Throwable rootCause) {
        super(message, rootCause);
    }

    public ItemNotFoundException(Throwable rootCause) {
        super(rootCause);
    }
}

