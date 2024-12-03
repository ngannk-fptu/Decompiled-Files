/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.setup;

import java.util.concurrent.atomic.AtomicBoolean;

public class SetupLocks {
    private final AtomicBoolean currentlyInstallingDatabase = new AtomicBoolean(false);
    private final AtomicBoolean currentlyPopulatingData = new AtomicBoolean(false);

    private AtomicBoolean enumToAtomicBoolean(Lock lock) {
        switch (lock) {
            case CURRENTLY_INSTALLING_DATABASE: {
                return this.currentlyInstallingDatabase;
            }
            case CURRENTLY_POPULATING_DATA: {
                return this.currentlyPopulatingData;
            }
        }
        throw new UnsupportedOperationException("Lock " + lock + " not found.");
    }

    public boolean compareAndSet(Lock lock, boolean expect, boolean update) {
        return this.enumToAtomicBoolean(lock).compareAndSet(expect, update);
    }

    public void set(Lock lock, boolean newValue) {
        this.enumToAtomicBoolean(lock).set(newValue);
    }

    public static enum Lock {
        CURRENTLY_INSTALLING_DATABASE,
        CURRENTLY_POPULATING_DATA;

    }
}

