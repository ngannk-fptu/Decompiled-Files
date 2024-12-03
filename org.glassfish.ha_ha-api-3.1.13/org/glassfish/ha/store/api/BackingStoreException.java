/*
 * Decompiled with CFR 0.152.
 */
package org.glassfish.ha.store.api;

public class BackingStoreException
extends Exception {
    public BackingStoreException() {
    }

    public BackingStoreException(String message) {
        super(message);
    }

    public BackingStoreException(String message, Throwable th) {
        super(message, th);
    }
}

