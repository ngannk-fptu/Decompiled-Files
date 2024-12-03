/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 */
package com.atlassian.oauth2.client.storage;

import com.atlassian.activeobjects.external.ActiveObjects;

public abstract class AbstractStore {
    protected final ActiveObjects activeObjects;

    protected AbstractStore(ActiveObjects activeObjects) {
        this.activeObjects = activeObjects;
    }

    protected <T, E extends Exception> T executeInTransaction(SupplierWithException<T, E> lambda, Class<E> exceptionClass) throws E {
        try {
            return (T)this.activeObjects.executeInTransaction(() -> {
                try {
                    return lambda.get();
                }
                catch (RuntimeException e) {
                    throw e;
                }
                catch (Exception e) {
                    if (e.getClass().equals(exceptionClass)) {
                        throw new ExceptionHolder(e);
                    }
                    throw new RuntimeException(e);
                }
            });
        }
        catch (ExceptionHolder e) {
            throw e.heldException;
        }
    }

    private static class ExceptionHolder
    extends RuntimeException {
        private final Exception heldException;

        private ExceptionHolder(Exception heldException) {
            this.heldException = heldException;
        }
    }

    protected static interface SupplierWithException<T, E extends Exception> {
        public T get() throws E;
    }
}

