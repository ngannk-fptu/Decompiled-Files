/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.search;

public class ExecutionHints {
    public static final int DEFAULT_RESULT_BATCH_SIZE = -1;
    private int batchSize = -1;

    public ExecutionHints setResultBatchSize(int size) {
        this.batchSize = size;
        return this;
    }

    public int getResultBatchSize() {
        return this.batchSize;
    }
}

