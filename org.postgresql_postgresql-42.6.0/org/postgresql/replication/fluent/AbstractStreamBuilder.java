/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package org.postgresql.replication.fluent;

import java.util.concurrent.TimeUnit;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.postgresql.replication.LogSequenceNumber;
import org.postgresql.replication.fluent.ChainedCommonStreamBuilder;

public abstract class AbstractStreamBuilder<T extends ChainedCommonStreamBuilder<T>>
implements ChainedCommonStreamBuilder<T> {
    private static final int DEFAULT_STATUS_INTERVAL = (int)TimeUnit.SECONDS.toMillis(10L);
    protected int statusIntervalMs = DEFAULT_STATUS_INTERVAL;
    protected LogSequenceNumber startPosition = LogSequenceNumber.INVALID_LSN;
    protected @Nullable String slotName;

    protected abstract T self();

    @Override
    public T withStatusInterval(int time, TimeUnit format) {
        this.statusIntervalMs = (int)TimeUnit.MILLISECONDS.convert(time, format);
        return this.self();
    }

    @Override
    public T withStartPosition(LogSequenceNumber lsn) {
        this.startPosition = lsn;
        return this.self();
    }

    @Override
    public T withSlotName(String slotName) {
        this.slotName = slotName;
        return this.self();
    }
}

