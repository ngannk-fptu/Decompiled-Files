/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr;

import javax.jcr.RepositoryException;

public class ItemExistsException
extends RepositoryException {
    public ItemExistsException() {
    }

    public ItemExistsException(String message) {
        super(message);
    }

    public ItemExistsException(String message, Throwable rootCause) {
        super(message, rootCause);
    }

    public ItemExistsException(Throwable rootCause) {
        super(rootCause);
    }
}

