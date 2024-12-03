/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search;

import java.io.IOException;
import java.util.Arrays;
import org.apache.lucene.index.DocsAndPositionsEnum;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Weight;
import org.apache.lucene.search.similarities.Similarity;

final class ExactPhraseScorer
extends Scorer {
    private final int endMinus1;
    private static final int CHUNK = 4096;
    private int gen;
    private final int[] counts = new int[4096];
    private final int[] gens = new int[4096];
    boolean noDocs;
    private final long cost;
    private final ChunkState[] chunkStates;
    private int docID = -1;
    private int freq;
    private final Similarity.SimScorer docScorer;

    ExactPhraseScorer(Weight weight, PhraseQuery.PostingsAndFreq[] postings, Similarity.SimScorer docScorer) throws IOException {
        super(weight);
        this.docScorer = docScorer;
        this.chunkStates = new ChunkState[postings.length];
        this.endMinus1 = postings.length - 1;
        this.cost = postings[0].postings.cost();
        for (int i = 0; i < postings.length; ++i) {
            boolean useAdvance = postings[i].docFreq > 5 * postings[0].docFreq;
            this.chunkStates[i] = new ChunkState(postings[i].postings, -postings[i].position, useAdvance);
            if (i <= 0 || postings[i].postings.nextDoc() != Integer.MAX_VALUE) continue;
            this.noDocs = true;
            return;
        }
    }

    @Override
    public int nextDoc() throws IOException {
        while (true) {
            int i;
            int doc;
            if ((doc = this.chunkStates[0].posEnum.nextDoc()) == Integer.MAX_VALUE) {
                this.docID = doc;
                return doc;
            }
            for (i = 1; i < this.chunkStates.length; ++i) {
                ChunkState cs = this.chunkStates[i];
                int doc2 = cs.posEnum.docID();
                if (cs.useAdvance) {
                    if (doc2 < doc) {
                        doc2 = cs.posEnum.advance(doc);
                    }
                } else {
                    int iter = 0;
                    while (doc2 < doc) {
                        if (++iter == 50) {
                            doc2 = cs.posEnum.advance(doc);
                            break;
                        }
                        doc2 = cs.posEnum.nextDoc();
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

    @Override
    public int advance(int target) throws IOException {
        int doc = this.chunkStates[0].posEnum.advance(target);
        if (doc == Integer.MAX_VALUE) {
            this.docID = Integer.MAX_VALUE;
            return doc;
        }
        do {
            int i;
            for (i = 1; i < this.chunkStates.length; ++i) {
                int doc2 = this.chunkStates[i].posEnum.docID();
                if (doc2 < doc) {
                    doc2 = this.chunkStates[i].posEnum.advance(doc);
                }
                if (doc2 > doc) break;
            }
            if (i != this.chunkStates.length) continue;
            this.docID = doc;
            this.freq = this.phraseFreq();
            if (this.freq == 0) continue;
            return this.docID;
        } while ((doc = this.chunkStates[0].posEnum.nextDoc()) != Integer.MAX_VALUE);
        this.docID = doc;
        return doc;
    }

    public String toString() {
        return "ExactPhraseScorer(" + this.weight + ")";
    }

    @Override
    public int freq() {
        return this.freq;
    }

    @Override
    public int docID() {
        return this.docID;
    }

    @Override
    public float score() {
        return this.docScorer.score(this.docID, this.freq);
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

    @Override
    public long cost() {
        return this.cost;
    }

    private static final class ChunkState {
        final DocsAndPositionsEnum posEnum;
        final int offset;
        final boolean useAdvance;
        int posUpto;
        int posLimit;
        int pos;
        int lastPos;

        public ChunkState(DocsAndPositionsEnum posEnum, int offset, boolean useAdvance) {
            this.posEnum = posEnum;
            this.offset = offset;
            this.useAdvance = useAdvance;
        }
    }
}

