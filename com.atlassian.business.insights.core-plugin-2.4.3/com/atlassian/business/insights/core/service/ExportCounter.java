/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.business.insights.core.service;

import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nonnull;

public class ExportCounter {
    @Nonnull
    private final AtomicInteger rowCounter = new AtomicInteger();
    @Nonnull
    private final AtomicInteger exportedEntitiesCounter = new AtomicInteger();

    @Nonnull
    public AtomicInteger getRowCounter() {
        return this.rowCounter;
    }

    @Nonnull
    public AtomicInteger getExportedEntitiesCounter() {
        return this.exportedEntitiesCounter;
    }

    public void incrementRowCounter(int rowCount) {
        this.rowCounter.addAndGet(rowCount);
    }

    public void incrementExportedEntitiesCounter() {
        this.exportedEntitiesCounter.getAndIncrement();
    }

    public String toString() {
        return String.format("Processed %d entity(ies) and wrote %d row(s) in total", this.exportedEntitiesCounter.get(), this.rowCounter.get());
    }
}

