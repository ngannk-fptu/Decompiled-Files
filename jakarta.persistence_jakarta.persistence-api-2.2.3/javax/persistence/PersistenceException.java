/*
 * Decompiled with CFR 0.152.
 */
package javax.persistence;

public class PersistenceException
extends RuntimeException {
    public PersistenceException() {
    }

    public PersistenceException(String message) {
        super(message);
    }

    public PersistenceException(String message, Throwable cause) {
        super(message, cause);
    }

    public PersistenceException(Throwable cause) {
        super(cause);
    }
}

