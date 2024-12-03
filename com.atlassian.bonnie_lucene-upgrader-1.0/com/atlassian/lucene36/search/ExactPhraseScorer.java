/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import com.atlassian.lucene36.index.TermPositions;
import com.atlassian.lucene36.search.PhraseQuery;
import com.atlassian.lucene36.search.Scorer;
import com.atlassian.lucene36.search.Similarity;
import com.atlassian.lucene36.search.Weight;
import java.io.IOException;
import java.util.Arrays;

final class ExactPhraseScorer
extends Scorer {
    private final byte[] norms;
    private final float value;
    private static final int SCORE_CACHE_SIZE = 32;
    private final float[] scoreCache = new float[32];
    private final int endMinus1;
    private static final int CHUNK = 4096;
    private int gen;
    private final int[] counts = new int[4096];
    private final int[] gens = new int[4096];
    boolean noDocs;
    private final ChunkState[] chunkStates;
    private int docID = -1;
    private int freq;

    ExactPhraseScorer(Weight weight, PhraseQuery.PostingsAndFreq[] postings, Similarity similarity, byte[] norms) throws IOException {
        super(similarity, weight);
        int i;
        this.norms = norms;
        this.value = weight.getValue();
        this.chunkStates = new ChunkState[postings.length];
        this.endMinus1 = postings.length - 1;
        for (i = 0; i < postings.length; ++i) {
            boolean useAdvance = postings[i].docFreq > 5 * postings[0].docFreq;
            this.chunkStates[i] = new ChunkState(postings[i].postings, -postings[i].position, useAdvance);
            if (i <= 0 || postings[i].postings.next()) continue;
            this.noDocs = true;
            return;
        }
        for (i = 0; i < 32; ++i) {
            this.scoreCache[i] = this.getSimilarity().tf((float)i) * this.value;
        }
    }

    public int nextDoc() throws IOException {
        while (true) {
            int i;
            if (!this.chunkStates[0].posEnum.next()) {
                this.docID = Integer.MAX_VALUE;
                return this.docID;
            }
            int doc = this.chunkStates[0].posEnum.doc();
            for (i = 1; i < this.chunkStates.length; ++i) {
                ChunkState cs = this.chunkStates[i];
                int doc2 = cs.posEnum.doc();
                if (cs.useAdvance) {
                    if (doc2 < doc) {
                        if (!cs.posEnum.skipTo(doc)) {
                            this.docID = Integer.MAX_VALUE;
                            return this.docID;
                        }
                        doc2 = cs.posEnum.doc();
                    }
                } else {
                    int iter = 0;
                    while (doc2 < doc) {
                        if (++iter == 50) {
                            if (!cs.posEnum.skipTo(doc)) {
                                this.docID = Integer.MAX_VALUE;
                                return this.docID;
                            }
                            doc2 = cs.posEnum.doc();
                            break;
                        }
                        if (cs.posEnum.next()) {
                            doc2 = cs.posEnum.doc();
                            continue;
                        }
                        this.docID = Integer.MAX_VALUE;
                        return this.docID;
                    }
                }
                if (doc2 > doc) break;
            }
            if (i != this.chunkStates.length) continue;
            this.docID = doc;
            this.freq = this.phraseFreq();
            if (this.freq != 0) break;
        }
        return this.docID;
    }

    public int advance(int target) throws IOException {
        if (!this.chunkStates[0].posEnum.skipTo(target)) {
            this.docID = Integer.MAX_VALUE;
            return this.docID;
        }
        int doc = this.chunkStates[0].posEnum.doc();
        while (true) {
            int i;
            for (i = 1; i < this.chunkStates.length; ++i) {
                int doc2 = this.chunkStates[i].posEnum.doc();
                if (doc2 < doc) {
                    if (!this.chunkStates[i].posEnum.skipTo(doc)) {
                        this.docID = Integer.MAX_VALUE;
                        return this.docID;
                    }
                    doc2 = this.chunkStates[i].posEnum.doc();
                }
                if (doc2 > doc) break;
            }
            if (i == this.chunkStates.length) {
                this.docID = doc;
                this.freq = this.phraseFreq();
                if (this.freq != 0) {
                    return this.docID;
                }
            }
            if (!this.chunkStates[0].posEnum.next()) {
                this.docID = Integer.MAX_VALUE;
                return this.docID;
            }
            doc = this.chunkStates[0].posEnum.doc();
        }
    }

    public String toString() {
        return "ExactPhraseScorer(" + this.weight + ")";
    }

    public float freq() {
        return this.freq;
    }

    public int docID() {
        return this.docID;
    }

    public float score() throws IOException {
        float raw = this.freq < 32 ? this.scoreCache[this.freq] : this.getSimilarity().tf((float)this.freq) * this.value;
        return this.norms == null ? raw : raw * this.getSimilarity().decodeNormValue(this.norms[this.docID]);
    }

    private int phraseFreq() throws IOException {
        this.freq = 0;
        for (int i = 0; i < this.chunkStates.length; ++i) {
            ChunkState cs = this.chunkStates[i];
            cs.posLimit = cs.posEnum.freq();
            cs.pos = cs.offset + cs.posEnum.nextPosition();
            cs.posUpto = 1;
            cs.lastPos = -1;
        }
        int chunkStart = 0;
        int chunkEnd = 4096;
        boolean end = false;
        while (!end) {
            ++this.gen;
            if (this.gen == 0) {
                Arrays.fill(this.gens, 0);
                ++this.gen;
            }
            ChunkState cs = this.chunkStates[0];
            while (cs.pos < chunkEnd) {
                if (cs.pos > cs.lastPos) {
                    cs.lastPos = cs.pos;
                    int posIndex = cs.pos - chunkStart;
                    this.counts[posIndex] = 1;
                    assert (this.gens[posIndex] != this.gen);
                    this.gens[posIndex] = this.gen;
                }
                if (cs.posUpto == cs.posLimit) {
                    end = true;
                    break;
                }
                ++cs.posUpto;
                cs.pos = cs.offset + cs.posEnum.nextPosition();
            }
            boolean any = true;
            for (int t = 1; t < this.endMinus1; ++t) {
                ChunkState cs2 = this.chunkStates[t];
                any = false;
                while (cs2.pos < chunkEnd) {
                    if (cs2.pos > cs2.lastPos) {
                        cs2.lastPos = cs2.pos;
                        int posIndex = cs2.pos - chunkStart;
                        if (posIndex >= 0 && this.gens[posIndex] == this.gen && this.counts[posIndex] == t) {
                            int n = posIndex;
                            this.counts[n] = this.counts[n] + 1;
                            any = true;
                        }
                    }
                    if (cs2.posUpto == cs2.posLimit) {
                        end = true;
                        break;
                    }
                    ++cs2.posUpto;
                    cs2.pos = cs2.offset + cs2.posEnum.nextPosition();
                }
                if (!any) break;
            }
            if (!any) {
                chunkStart += 4096;
                chunkEnd += 4096;
                continue;
            }
            ChunkState cs3 = this.chunkStates[this.endMinus1];
            while (cs3.pos < chunkEnd) {
                if (cs3.pos > cs3.lastPos) {
                    cs3.lastPos = cs3.pos;
                    int posIndex = cs3.pos - chunkStart;
                    if (posIndex >= 0 && this.gens[posIndex] == this.gen && this.counts[posIndex] == this.endMinus1) {
                        ++this.freq;
                    }
                }
                if (cs3.posUpto == cs3.posLimit) {
                    end = true;
                    break;
                }
                ++cs3.posUpto;
                cs3.pos = cs3.offset + cs3.posEnum.nextPosition();
            }
            chunkStart += 4096;
            chunkEnd += 4096;
        }
        return this.freq;
    }

    private static final class ChunkState {
        final TermPositions posEnum;
        final int offset;
        final boolean useAdvance;
        int posUpto;
        int posLimit;
        int pos;
        int lastPos;

        public ChunkState(TermPositions posEnum, int offset, boolean useAdvance) {
            this.posEnum = posEnum;
            this.offset = offset;
            this.useAdvance = useAdvance;
        }
    }
}

