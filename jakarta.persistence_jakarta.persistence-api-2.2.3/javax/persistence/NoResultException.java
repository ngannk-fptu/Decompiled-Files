/*
 * Decompiled with CFR 0.152.
 */
package javax.persistence;

import javax.persistence.PersistenceException;

public class NoResultException
extends PersistenceException {
    public NoResultException() {
    }

    public NoResultException(String message) {
        super(message);
    }
}

