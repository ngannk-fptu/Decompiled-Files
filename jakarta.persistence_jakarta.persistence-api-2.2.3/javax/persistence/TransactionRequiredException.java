/*
 * Decompiled with CFR 0.152.
 */
package javax.persistence;

import javax.persistence.PersistenceException;

public class TransactionRequiredException
extends PersistenceException {
    public TransactionRequiredException() {
    }

    public TransactionRequiredException(String message) {
        super(message);
    }
}

