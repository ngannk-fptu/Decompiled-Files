/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.monitor.impl;

public interface IndexOperationStats {
    public static final IndexOperationStats EMPTY = new IndexOperationStats(){

        @Override
        public long getEntryCountDelta() {
            return 0L;
        }

        @Override
        public long getMemoryCostDelta() {
            return 0L;
        }

        @Override
        public void onEntryAdded(Object replacedValue, Object addedValue) {
        }

        @Override
        public void onEntryRemoved(Object removedValue) {
        }
    };

    public long getEntryCountDelta();

    public long getMemoryCostDelta();

    public void onEntryAdded(Object var1, Object var2);

    public void onEntryRemoved(Object var1);
}

