/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs;

import java.io.IOException;
import org.apache.lucene.index.DocsAndPositionsEnum;
import org.apache.lucene.index.MergeState;
import org.apache.lucene.index.MultiDocsAndPositionsEnum;
import org.apache.lucene.util.BytesRef;

public final class MappingMultiDocsAndPositionsEnum
extends DocsAndPositionsEnum {
    private MultiDocsAndPositionsEnum.EnumWithSlice[] subs;
    int numSubs;
    int upto;
    MergeState.DocMap currentMap;
    DocsAndPositionsEnum current;
    int currentBase;
    int doc = -1;
    private MergeState mergeState;

    MappingMultiDocsAndPositionsEnum reset(MultiDocsAndPositionsEnum postingsEnum) {
        this.numSubs = postingsEnum.getNumSubs();
        this.subs = postingsEnum.getSubs();
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

    public MultiDocsAndPositionsEnum.EnumWithSlice[] getSubs() {
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
                this.current = this.subs[this.upto].docsAndPositionsEnum;
                this.currentBase = this.mergeState.docBase[reader];
                this.currentMap = this.mergeState.docMaps[reader];
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
        for (MultiDocsAndPositionsEnum.EnumWithSlice enumWithSlice : this.subs) {
            cost += enumWithSlice.docsAndPositionsEnum.cost();
        }
        return cost;
    }
}

