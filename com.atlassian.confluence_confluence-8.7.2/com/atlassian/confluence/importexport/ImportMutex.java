/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.importexport;

import com.google.common.annotations.VisibleForTesting;
import java.util.concurrent.atomic.AtomicReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class ImportMutex {
    private static final Logger log = LoggerFactory.getLogger(ImportMutex.class);
    public static final ImportMutex INSTANCE = new ImportMutex();
    private final AtomicReference lock = new AtomicReference();

    @VisibleForTesting
    ImportMutex() {
    }

    public void lockMutex(Object client) {
        if (!this.lock.compareAndSet(null, client)) {
            throw new IllegalStateException("Import mutex already held by " + this.lock.get());
        }
        log.debug("Locked by {}", client);
    }

    public void unlockMutex(Object client) {
        if (!this.lock.compareAndSet(client, null)) {
            throw new IllegalStateException("Import mutex held by " + this.lock.get());
        }
        log.debug("Unlocked by {}", client);
    }

    public boolean isLocked() {
        return this.lock.get() != null;
    }
}

