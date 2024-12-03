/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import org.apache.lucene.index.ReaderSlice;
import org.apache.lucene.index.ReaderUtil;
import org.apache.lucene.util.Bits;

final class MultiBits
implements Bits {
    private final Bits[] subs;
    private final int[] starts;
    private final boolean defaultValue;

    public MultiBits(Bits[] subs, int[] starts, boolean defaultValue) {
        assert (starts.length == 1 + subs.length);
        this.subs = subs;
        this.starts = starts;
        this.defaultValue = defaultValue;
    }

    private boolean checkLength(int reader, int doc) {
        int length = this.starts[1 + reader] - this.starts[reader];
        assert (doc - this.starts[reader] < length) : "doc=" + doc + " reader=" + reader + " starts[reader]=" + this.starts[reader] + " length=" + length;
        return true;
    }

    @Override
    public boolean get(int doc) {
        int reader = ReaderUtil.subIndex(doc, this.starts);
        assert (reader != -1);
        Bits bits = this.subs[reader];
        if (bits == null) {
            return this.defaultValue;
        }
        assert (this.checkLength(reader, doc));
        return bits.get(doc - this.starts[reader]);
    }

    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append(this.subs.length + " subs: ");
        for (int i = 0; i < this.subs.length; ++i) {
            if (i != 0) {
                b.append("; ");
            }
            if (this.subs[i] == null) {
                b.append("s=" + this.starts[i] + " l=null");
                continue;
            }
            b.append("s=" + this.starts[i] + " l=" + this.subs[i].length() + " b=" + this.subs[i]);
        }
        b.append(" end=" + this.starts[this.subs.length]);
        return b.toString();
    }

    public SubResult getMatchingSub(ReaderSlice slice) {
        int reader = ReaderUtil.subIndex(slice.start, this.starts);
        assert (reader != -1);
        assert (reader < this.subs.length) : "slice=" + slice + " starts[-1]=" + this.starts[this.starts.length - 1];
        SubResult subResult = new SubResult();
        if (this.starts[reader] == slice.start && this.starts[1 + reader] == slice.start + slice.length) {
            subResult.matches = true;
            subResult.result = this.subs[reader];
        } else {
            subResult.matches = false;
        }
        return subResult;
    }

    @Override
    public int length() {
        return this.starts[this.starts.length - 1];
    }

    public static final class SubResult {
        public boolean matches;
        public Bits result;
    }
}

