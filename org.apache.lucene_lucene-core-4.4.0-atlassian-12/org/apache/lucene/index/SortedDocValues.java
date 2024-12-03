/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import org.apache.lucene.index.BinaryDocValues;
import org.apache.lucene.index.SortedDocValuesTermsEnum;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.util.BytesRef;

public abstract class SortedDocValues
extends BinaryDocValues {
    public static final SortedDocValues EMPTY = new SortedDocValues(){

        @Override
        public int getOrd(int docID) {
            return 0;
        }

        @Override
        public void lookupOrd(int ord, BytesRef result) {
            result.bytes = MISSING;
            result.offset = 0;
            result.length = 0;
        }

        @Override
        public int getValueCount() {
            return 1;
        }
    };

    protected SortedDocValues() {
    }

    public abstract int getOrd(int var1);

    public abstract void lookupOrd(int var1, BytesRef var2);

    public abstract int getValueCount();

    @Override
    public void get(int docID, BytesRef result) {
        int ord = this.getOrd(docID);
        if (ord == -1) {
            result.bytes = MISSING;
            result.length = 0;
            result.offset = 0;
        } else {
            this.lookupOrd(ord, result);
        }
    }

    public int lookupTerm(BytesRef key) {
        BytesRef spare = new BytesRef();
        int low = 0;
        int high = this.getValueCount() - 1;
        while (low <= high) {
            int mid = low + high >>> 1;
            this.lookupOrd(mid, spare);
            int cmp = spare.compareTo(key);
            if (cmp < 0) {
                low = mid + 1;
                continue;
            }
            if (cmp > 0) {
                high = mid - 1;
                continue;
            }
            return mid;
        }
        return -(low + 1);
    }

    public TermsEnum termsEnum() {
        return new SortedDocValuesTermsEnum(this);
    }
}

