/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

public abstract class NumericDocValues {
    public static final NumericDocValues EMPTY = new NumericDocValues(){

        @Override
        public long get(int docID) {
            return 0L;
        }
    };

    protected NumericDocValues() {
    }

    public abstract long get(int var1);
}

