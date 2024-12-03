/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.util.ArrayUtil
 *  org.apache.lucene.util.BytesRef
 *  org.apache.lucene.util.InPlaceMergeSorter
 *  org.apache.lucene.util.RamUsageEstimator
 */
package org.apache.lucene.search.postingshighlight;

import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.InPlaceMergeSorter;
import org.apache.lucene.util.RamUsageEstimator;

public final class Passage {
    int startOffset = -1;
    int endOffset = -1;
    float score = 0.0f;
    int[] matchStarts = new int[8];
    int[] matchEnds = new int[8];
    BytesRef[] matchTerms = new BytesRef[8];
    int numMatches = 0;

    void addMatch(int startOffset, int endOffset, BytesRef term) {
        assert (startOffset >= this.startOffset && startOffset <= this.endOffset);
        if (this.numMatches == this.matchStarts.length) {
            int newLength = ArrayUtil.oversize((int)(this.numMatches + 1), (int)RamUsageEstimator.NUM_BYTES_OBJECT_REF);
            int[] newMatchStarts = new int[newLength];
            int[] newMatchEnds = new int[newLength];
            BytesRef[] newMatchTerms = new BytesRef[newLength];
            System.arraycopy(this.matchStarts, 0, newMatchStarts, 0, this.numMatches);
            System.arraycopy(this.matchEnds, 0, newMatchEnds, 0, this.numMatches);
            System.arraycopy(this.matchTerms, 0, newMatchTerms, 0, this.numMatches);
            this.matchStarts = newMatchStarts;
            this.matchEnds = newMatchEnds;
            this.matchTerms = newMatchTerms;
        }
        assert (this.matchStarts.length == this.matchEnds.length && this.matchEnds.length == this.matchTerms.length);
        this.matchStarts[this.numMatches] = startOffset;
        this.matchEnds[this.numMatches] = endOffset;
        this.matchTerms[this.numMatches] = term;
        ++this.numMatches;
    }

    void sort() {
        final int[] starts = this.matchStarts;
        final int[] ends = this.matchEnds;
        final BytesRef[] terms = this.matchTerms;
        new InPlaceMergeSorter(){

            protected void swap(int i, int j) {
                int temp = starts[i];
                starts[i] = starts[j];
                starts[j] = temp;
                temp = ends[i];
                ends[i] = ends[j];
                ends[j] = temp;
                BytesRef tempTerm = terms[i];
                terms[i] = terms[j];
                terms[j] = tempTerm;
            }

            protected int compare(int i, int j) {
                return Long.signum((long)starts[i] - (long)starts[j]);
            }
        }.sort(0, this.numMatches);
    }

    void reset() {
        this.endOffset = -1;
        this.startOffset = -1;
        this.score = 0.0f;
        this.numMatches = 0;
    }

    public int getStartOffset() {
        return this.startOffset;
    }

    public int getEndOffset() {
        return this.endOffset;
    }

    public float getScore() {
        return this.score;
    }

    public int getNumMatches() {
        return this.numMatches;
    }

    public int[] getMatchStarts() {
        return this.matchStarts;
    }

    public int[] getMatchEnds() {
        return this.matchEnds;
    }

    public BytesRef[] getMatchTerms() {
        return this.matchTerms;
    }
}

