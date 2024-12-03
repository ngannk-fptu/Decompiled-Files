/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.SegmentTermVector;
import com.atlassian.lucene36.index.TermPositionVector;
import com.atlassian.lucene36.index.TermVectorOffsetInfo;

class SegmentTermPositionVector
extends SegmentTermVector
implements TermPositionVector {
    protected int[][] positions;
    protected TermVectorOffsetInfo[][] offsets;
    public static final int[] EMPTY_TERM_POS = new int[0];

    public SegmentTermPositionVector(String field, String[] terms, int[] termFreqs, int[][] positions, TermVectorOffsetInfo[][] offsets) {
        super(field, terms, termFreqs);
        this.offsets = offsets;
        this.positions = positions;
    }

    public TermVectorOffsetInfo[] getOffsets(int index) {
        TermVectorOffsetInfo[] result = TermVectorOffsetInfo.EMPTY_OFFSET_INFO;
        if (this.offsets == null) {
            return null;
        }
        if (index >= 0 && index < this.offsets.length) {
            result = this.offsets[index];
        }
        return result;
    }

    public int[] getTermPositions(int index) {
        int[] result = EMPTY_TERM_POS;
        if (this.positions == null) {
            return null;
        }
        if (index >= 0 && index < this.positions.length) {
            result = this.positions[index];
        }
        return result;
    }
}

