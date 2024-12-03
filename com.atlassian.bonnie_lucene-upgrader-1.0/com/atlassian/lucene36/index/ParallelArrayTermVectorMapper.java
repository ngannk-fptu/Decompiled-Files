/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.SegmentTermPositionVector;
import com.atlassian.lucene36.index.SegmentTermVector;
import com.atlassian.lucene36.index.TermFreqVector;
import com.atlassian.lucene36.index.TermVectorMapper;
import com.atlassian.lucene36.index.TermVectorOffsetInfo;

class ParallelArrayTermVectorMapper
extends TermVectorMapper {
    private String[] terms;
    private int[] termFreqs;
    private int[][] positions;
    private TermVectorOffsetInfo[][] offsets;
    private int currentPosition;
    private boolean storingOffsets;
    private boolean storingPositions;
    private String field;

    ParallelArrayTermVectorMapper() {
    }

    public void setExpectations(String field, int numTerms, boolean storeOffsets, boolean storePositions) {
        this.field = field;
        this.terms = new String[numTerms];
        this.termFreqs = new int[numTerms];
        this.storingOffsets = storeOffsets;
        this.storingPositions = storePositions;
        if (storePositions) {
            this.positions = new int[numTerms][];
        }
        if (storeOffsets) {
            this.offsets = new TermVectorOffsetInfo[numTerms][];
        }
    }

    public void map(String term, int frequency, TermVectorOffsetInfo[] offsets, int[] positions) {
        this.terms[this.currentPosition] = term;
        this.termFreqs[this.currentPosition] = frequency;
        if (this.storingOffsets) {
            this.offsets[this.currentPosition] = offsets;
        }
        if (this.storingPositions) {
            this.positions[this.currentPosition] = positions;
        }
        ++this.currentPosition;
    }

    public TermFreqVector materializeVector() {
        SegmentTermVector tv = null;
        if (this.field != null && this.terms != null) {
            tv = this.storingPositions || this.storingOffsets ? new SegmentTermPositionVector(this.field, this.terms, this.termFreqs, this.positions, this.offsets) : new SegmentTermVector(this.field, this.terms, this.termFreqs);
        }
        return tv;
    }
}

