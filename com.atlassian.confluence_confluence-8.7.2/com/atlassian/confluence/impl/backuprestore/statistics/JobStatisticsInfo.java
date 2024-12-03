/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.backuprestore.statistics;

public interface JobStatisticsInfo {
    public long getPersistedObjectsCount();

    public long getSkippedObjectsCount();

    public long getProcessedObjectsCounter();

    public long getTotalNumberOfObjects();

    public long getStartTime();
}

