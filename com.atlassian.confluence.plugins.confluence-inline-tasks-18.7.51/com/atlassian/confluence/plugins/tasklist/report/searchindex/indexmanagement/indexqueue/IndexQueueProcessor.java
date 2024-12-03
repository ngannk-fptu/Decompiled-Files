/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.tasklist.report.searchindex.indexmanagement.indexqueue;

public interface IndexQueueProcessor {
    public int flushQueue();

    public void requestFullReindexOnCurrentNode();
}

