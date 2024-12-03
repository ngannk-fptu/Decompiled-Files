/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl.bitmap;

public interface AscendingLongIterator {
    public static final long END = -1L;
    public static final AscendingLongIterator EMPTY = new AscendingLongIterator(){

        @Override
        public long getIndex() {
            return -1L;
        }

        @Override
        public long advance() {
            return -1L;
        }

        @Override
        public long advanceAtLeastTo(long member) {
            return -1L;
        }
    };

    public long getIndex();

    public long advance();

    public long advanceAtLeastTo(long var1);
}

