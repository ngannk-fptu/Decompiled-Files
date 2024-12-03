/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.TermFreqVector;
import com.atlassian.lucene36.index.TermVectorOffsetInfo;

public interface TermPositionVector
extends TermFreqVector {
    public int[] getTermPositions(int var1);

    public TermVectorOffsetInfo[] getOffsets(int var1);
}

