/*
 * Decompiled with CFR 0.152.
 */
package javax.persistence;

import javax.persistence.PersistenceException;

public class EntityNotFoundException
extends PersistenceException {
    public EntityNotFoundException() {
    }

    public EntityNotFoundException(String message) {
        super(message);
    }
}

