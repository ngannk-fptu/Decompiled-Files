/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.executionservice.impl;

import com.hazelcast.spi.impl.executionservice.impl.CompletableFutureEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class CompletableFutureTask
implements Runnable {
    private final List<CompletableFutureEntry> entries = new ArrayList<CompletableFutureEntry>();
    private final Lock entriesLock = new ReentrantLock();

    CompletableFutureTask() {
    }

    <V> void registerCompletableFutureEntry(CompletableFutureEntry<V> entry) {
        this.entriesLock.lock();
        try {
            this.entries.add(entry);
        }
        finally {
            this.entriesLock.unlock();
        }
    }

    @Override
    public void run() {
        List<CompletableFutureEntry> removableEntries = this.removableEntries();
        this.removeEntries(removableEntries);
    }

    private void removeEntries(List<CompletableFutureEntry> removableEntries) {
        if (removableEntries.isEmpty()) {
            return;
        }
        this.entriesLock.lock();
        try {
            this.entries.removeAll(removableEntries);
        }
        finally {
            this.entriesLock.unlock();
        }
    }

    private List<CompletableFutureEntry> removableEntries() {
        CompletableFutureEntry[] entries = this.copyEntries();
        ArrayList<CompletableFutureEntry> removableEntries = Collections.EMPTY_LIST;
        for (CompletableFutureEntry entry : entries) {
            if (!entry.processState()) continue;
            if (removableEntries.isEmpty()) {
                removableEntries = new ArrayList<CompletableFutureEntry>(entries.length / 2);
            }
            removableEntries.add(entry);
        }
        return removableEntries;
    }

    private CompletableFutureEntry[] copyEntries() {
        CompletableFutureEntry[] copy;
        if (this.entries.isEmpty()) {
            return new CompletableFutureEntry[0];
        }
        this.entriesLock.lock();
        try {
            copy = new CompletableFutureEntry[this.entries.size()];
            copy = this.entries.toArray(copy);
        }
        finally {
            this.entriesLock.unlock();
        }
        return copy;
    }

    public String toString() {
        return "CompletableFutureTask{}";
    }
}

