/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import org.apache.lucene.index.SortedSetDocValuesTermsEnum;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.util.BytesRef;

public abstract class SortedSetDocValues {
    public static final long NO_MORE_ORDS = -1L;
    public static final SortedSetDocValues EMPTY = new SortedSetDocValues(){

        @Override
        public long nextOrd() {
            return -1L;
        }

        @Override
        public void setDocument(int docID) {
        }

        @Override
        public void lookupOrd(long ord, BytesRef result) {
            throw new IndexOutOfBoundsException();
        }

        @Override
        public long getValueCount() {
            return 0L;
        }
    };

    protected SortedSetDocValues() {
    }

    public abstract long nextOrd();

    public abstract void setDocument(int var1);

    public abstract void lookupOrd(long var1, BytesRef var3);

    public abstract long getValueCount();

    public long lookupTerm(BytesRef key) {
        BytesRef spare = new BytesRef();
        long low = 0L;
        long high = this.getValueCount() - 1L;
        while (low <= high) {
            long mid = low + high >>> 1;
            this.lookupOrd(mid, spare);
            int cmp = spare.compareTo(key);
            if (cmp < 0) {
                low = mid + 1L;
                continue;
            }
            if (cmp > 0) {
                high = mid - 1L;
                continue;
            }
            return mid;
        }
        return -(low + 1L);
    }

    public TermsEnum termsEnum() {
        return new SortedSetDocValuesTermsEnum(this);
    }
}

