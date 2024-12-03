/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.monitor.impl;

import com.hazelcast.monitor.impl.PerIndexStats;

public interface IndexesStats {
    public static final IndexesStats EMPTY = new IndexesStats(){

        @Override
        public long getQueryCount() {
            return 0L;
        }

        @Override
        public void incrementQueryCount() {
        }

        @Override
        public long getIndexedQueryCount() {
            return 0L;
        }

        @Override
        public void incrementIndexedQueryCount() {
        }

        @Override
        public PerIndexStats createPerIndexStats(boolean ordered, boolean queryableEntriesAreCached) {
            return PerIndexStats.EMPTY;
        }
    };

    public long getQueryCount();

    public void incrementQueryCount();

    public long getIndexedQueryCount();

    public void incrementIndexedQueryCount();

    public PerIndexStats createPerIndexStats(boolean var1, boolean var2);
}

