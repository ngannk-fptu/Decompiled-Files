/*
 * Decompiled with CFR 0.152.
 */
package javax.persistence;

import javax.persistence.PersistenceException;

public class NonUniqueResultException
extends PersistenceException {
    public NonUniqueResultException() {
    }

    public NonUniqueResultException(String message) {
        super(message);
    }
}

