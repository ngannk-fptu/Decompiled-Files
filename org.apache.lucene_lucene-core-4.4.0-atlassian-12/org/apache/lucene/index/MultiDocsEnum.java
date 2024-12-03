/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.IOException;
import java.util.Arrays;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.MultiTermsEnum;
import org.apache.lucene.index.ReaderSlice;

public final class MultiDocsEnum
extends DocsEnum {
    private final MultiTermsEnum parent;
    final DocsEnum[] subDocsEnum;
    private EnumWithSlice[] subs;
    int numSubs;
    int upto;
    DocsEnum current;
    int currentBase;
    int doc = -1;

    public MultiDocsEnum(MultiTermsEnum parent, int subReaderCount) {
        this.parent = parent;
        this.subDocsEnum = new DocsEnum[subReaderCount];
    }

    MultiDocsEnum reset(EnumWithSlice[] subs, int numSubs) {
        this.numSubs = numSubs;
        this.subs = new EnumWithSlice[subs.length];
        for (int i = 0; i < subs.length; ++i) {
            this.subs[i] = new EnumWithSlice();
            this.subs[i].docsEnum = subs[i].docsEnum;
            this.subs[i].slice = subs[i].slice;
        }
        this.upto = -1;
        this.doc = -1;
        this.current = null;
        return this;
    }

    public boolean canReuse(MultiTermsEnum parent) {
        return this.parent == parent;
    }

    public int getNumSubs() {
        return this.numSubs;
    }

    public EnumWithSlice[] getSubs() {
        return this.subs;
    }

    @Override
    public int freq() throws IOException {
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
            this.current = this.subs[this.upto].docsEnum;
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
                this.current = this.subs[this.upto].docsEnum;
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
    public long cost() {
        long cost = 0L;
        for (int i = 0; i < this.numSubs; ++i) {
            cost += this.subs[i].docsEnum.cost();
        }
        return cost;
    }

    public String toString() {
        return "MultiDocsEnum(" + Arrays.toString(this.getSubs()) + ")";
    }

    public static final class EnumWithSlice {
        public DocsEnum docsEnum;
        public ReaderSlice slice;

        EnumWithSlice() {
        }

        public String toString() {
            return this.slice.toString() + ":" + this.docsEnum;
        }
    }
}

