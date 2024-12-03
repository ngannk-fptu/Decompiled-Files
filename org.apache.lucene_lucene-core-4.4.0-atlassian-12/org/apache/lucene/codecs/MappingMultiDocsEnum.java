/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs;

import java.io.IOException;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.MergeState;
import org.apache.lucene.index.MultiDocsEnum;

public final class MappingMultiDocsEnum
extends DocsEnum {
    private MultiDocsEnum.EnumWithSlice[] subs;
    int numSubs;
    int upto;
    MergeState.DocMap currentMap;
    DocsEnum current;
    int currentBase;
    int doc = -1;
    private MergeState mergeState;

    MappingMultiDocsEnum reset(MultiDocsEnum docsEnum) {
        this.numSubs = docsEnum.getNumSubs();
        this.subs = docsEnum.getSubs();
        this.upto = -1;
        this.current = null;
        return this;
    }

    public void setMergeState(MergeState mergeState) {
        this.mergeState = mergeState;
    }

    public int getNumSubs() {
        return this.numSubs;
    }

    public MultiDocsEnum.EnumWithSlice[] getSubs() {
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
    public int advance(int target) {
        throw new UnsupportedOperationException();
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
                int reader = this.subs[this.upto].slice.readerIndex;
                this.current = this.subs[this.upto].docsEnum;
                this.currentBase = this.mergeState.docBase[reader];
                this.currentMap = this.mergeState.docMaps[reader];
                assert (this.currentMap.maxDoc() == this.subs[this.upto].slice.length) : "readerIndex=" + reader + " subs.len=" + this.subs.length + " len1=" + this.currentMap.maxDoc() + " vs " + this.subs[this.upto].slice.length;
            }
            if ((doc = this.current.nextDoc()) != Integer.MAX_VALUE) {
                if ((doc = this.currentMap.get(doc)) == -1) continue;
                this.doc = this.currentBase + doc;
                return this.doc;
            }
            this.current = null;
        }
    }

    @Override
    public long cost() {
        long cost = 0L;
        for (MultiDocsEnum.EnumWithSlice enumWithSlice : this.subs) {
            cost += enumWithSlice.docsEnum.cost();
        }
        return cost;
    }
}

