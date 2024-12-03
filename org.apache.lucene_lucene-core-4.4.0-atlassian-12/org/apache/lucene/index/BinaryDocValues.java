/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import org.apache.lucene.util.BytesRef;

public abstract class BinaryDocValues {
    public static final byte[] MISSING = new byte[0];
    public static final BinaryDocValues EMPTY = new BinaryDocValues(){

        @Override
        public void get(int docID, BytesRef result) {
            result.bytes = MISSING;
            result.offset = 0;
            result.length = 0;
        }
    };

    protected BinaryDocValues() {
    }

    public abstract void get(int var1, BytesRef var2);
}

