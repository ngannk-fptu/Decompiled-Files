/*
 * Decompiled with CFR 0.152.
 */
package javax.persistence;

import javax.persistence.PersistenceException;

public class EntityExistsException
extends PersistenceException {
    public EntityExistsException() {
    }

    public EntityExistsException(String message) {
        super(message);
    }

    public EntityExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public EntityExistsException(Throwable cause) {
        super(cause);
    }
}

