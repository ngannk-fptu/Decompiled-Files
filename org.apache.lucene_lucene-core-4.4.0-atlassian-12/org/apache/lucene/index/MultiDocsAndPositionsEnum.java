/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.IOException;
import java.util.Arrays;
import org.apache.lucene.index.DocsAndPositionsEnum;
import org.apache.lucene.index.MultiTermsEnum;
import org.apache.lucene.index.ReaderSlice;
import org.apache.lucene.util.BytesRef;

public final class MultiDocsAndPositionsEnum
extends DocsAndPositionsEnum {
    private final MultiTermsEnum parent;
    final DocsAndPositionsEnum[] subDocsAndPositionsEnum;
    private EnumWithSlice[] subs;
    int numSubs;
    int upto;
    DocsAndPositionsEnum current;
    int currentBase;
    int doc = -1;

    public MultiDocsAndPositionsEnum(MultiTermsEnum parent, int subReaderCount) {
        this.parent = parent;
        this.subDocsAndPositionsEnum = new DocsAndPositionsEnum[subReaderCount];
    }

    public boolean canReuse(MultiTermsEnum parent) {
        return this.parent == parent;
    }

    public MultiDocsAndPositionsEnum reset(EnumWithSlice[] subs, int numSubs) {
        this.numSubs = numSubs;
        this.subs = new EnumWithSlice[subs.length];
        for (int i = 0; i < subs.length; ++i) {
            this.subs[i] = new EnumWithSlice();
            this.subs[i].docsAndPositionsEnum = subs[i].docsAndPositionsEnum;
            this.subs[i].slice = subs[i].slice;
        }
        this.upto = -1;
        this.doc = -1;
        this.current = null;
        return this;
    }

    public int getNumSubs() {
        return this.numSubs;
    }

    public EnumWithSlice[] getSubs() {
        return this.subs;
    }

    @Override
    public int freq() throws IOException {
        assert (this.current != null);
        return this.current.freq();
    }

    @Override
    public int docID() {
        return this.doc;
    }

    @Override
    public int advance(int target) throws IOException {
        assert (target > this.doc);
        while (true) {
            if (this.current != null) {
                int doc = target < this.currentBase ? this.current.nextDoc() : this.current.advance(target - this.currentBase);
                if (doc == Integer.MAX_VALUE) {
                    this.current = null;
                    continue;
                }
                this.doc = doc + this.currentBase;
                return this.doc;
            }
            if (this.upto == this.numSubs - 1) {
                this.doc = Integer.MAX_VALUE;
                return Integer.MAX_VALUE;
            }
            ++this.upto;
            this.current = this.subs[this.upto].docsAndPositionsEnum;
            this.currentBase = this.subs[this.upto].slice.start;
        }
    }

    @Override
    public int nextDoc() throws IOException {
        while (true) {
            int doc;
            if (this.current == null) {
                if (this.upto == this.numSubs - 1) {
                    this.doc = Integer.MAX_VALUE;
                    return Integer.MAX_VALUE;
                }
                ++this.upto;
                this.current = this.subs[this.upto].docsAndPositionsEnum;
                this.currentBase = this.subs[this.upto].slice.start;
            }
            if ((doc = this.current.nextDoc()) != Integer.MAX_VALUE) {
                this.doc = this.currentBase + doc;
                return this.doc;
            }
            this.current = null;
        }
    }

    @Override
    public int nextPosition() throws IOException {
        return this.current.nextPosition();
    }

    @Override
    public int startOffset() throws IOException {
        return this.current.startOffset();
    }

    @Override
    public int endOffset() throws IOException {
        return this.current.endOffset();
    }

    @Override
    public BytesRef getPayload() throws IOException {
        return this.current.getPayload();
    }

    @Override
    public long cost() {
        long cost = 0L;
        for (int i = 0; i < this.numSubs; ++i) {
            cost += this.subs[i].docsAndPositionsEnum.cost();
        }
        return cost;
    }

    public String toString() {
        return "MultiDocsAndPositionsEnum(" + Arrays.toString(this.getSubs()) + ")";
    }

    public static final class EnumWithSlice {
        public DocsAndPositionsEnum docsAndPositionsEnum;
        public ReaderSlice slice;

        EnumWithSlice() {
        }

        public String toString() {
            return this.slice.toString() + ":" + this.docsAndPositionsEnum;
        }
    }
}

