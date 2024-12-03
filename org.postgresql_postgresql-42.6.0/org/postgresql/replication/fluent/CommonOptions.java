/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package org.postgresql.replication.fluent;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.postgresql.replication.LogSequenceNumber;

public interface CommonOptions {
    public @Nullable String getSlotName();

    public LogSequenceNumber getStartLSNPosition();

    public int getStatusInterval();
}

