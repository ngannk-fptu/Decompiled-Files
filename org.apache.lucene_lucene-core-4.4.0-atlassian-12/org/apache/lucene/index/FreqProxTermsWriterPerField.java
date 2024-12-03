/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.IOException;
import java.util.Comparator;
import java.util.Map;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PayloadAttribute;
import org.apache.lucene.codecs.FieldsConsumer;
import org.apache.lucene.codecs.PostingsConsumer;
import org.apache.lucene.codecs.TermStats;
import org.apache.lucene.codecs.TermsConsumer;
import org.apache.lucene.index.ByteSliceReader;
import org.apache.lucene.index.DocumentsWriterPerThread;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.FieldInvertState;
import org.apache.lucene.index.FreqProxTermsWriter;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.ParallelPostingsArray;
import org.apache.lucene.index.SegmentWriteState;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermsHashConsumerPerField;
import org.apache.lucene.index.TermsHashPerField;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.FixedBitSet;

final class FreqProxTermsWriterPerField
extends TermsHashConsumerPerField
implements Comparable<FreqProxTermsWriterPerField> {
    final FreqProxTermsWriter parent;
    final TermsHashPerField termsHashPerField;
    final FieldInfo fieldInfo;
    final DocumentsWriterPerThread.DocState docState;
    final FieldInvertState fieldState;
    private boolean hasFreq;
    private boolean hasProx;
    private boolean hasOffsets;
    PayloadAttribute payloadAttribute;
    OffsetAttribute offsetAttribute;
    boolean hasPayloads;
    BytesRef payload;

    public FreqProxTermsWriterPerField(TermsHashPerField termsHashPerField, FreqProxTermsWriter parent, FieldInfo fieldInfo) {
        this.termsHashPerField = termsHashPerField;
        this.parent = parent;
        this.fieldInfo = fieldInfo;
        this.docState = termsHashPerField.docState;
        this.fieldState = termsHashPerField.fieldState;
        this.setIndexOptions(fieldInfo.getIndexOptions());
    }

    @Override
    int getStreamCount() {
        if (!this.hasProx) {
            return 1;
        }
        return 2;
    }

    @Override
    void finish() {
        if (this.hasPayloads) {
            this.fieldInfo.setStorePayloads();
        }
    }

    @Override
    void skippingLongTerm() {
    }

    @Override
    public int compareTo(FreqProxTermsWriterPerField other) {
        return this.fieldInfo.name.compareTo(other.fieldInfo.name);
    }

    void reset() {
        this.setIndexOptions(this.fieldInfo.getIndexOptions());
        this.payloadAttribute = null;
    }

    private void setIndexOptions(FieldInfo.IndexOptions indexOptions) {
        if (indexOptions == null) {
            this.hasOffsets = true;
            this.hasProx = true;
            this.hasFreq = true;
        } else {
            this.hasFreq = indexOptions.compareTo(FieldInfo.IndexOptions.DOCS_AND_FREQS) >= 0;
            this.hasProx = indexOptions.compareTo(FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS) >= 0;
            this.hasOffsets = indexOptions.compareTo(FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS) >= 0;
        }
    }

    @Override
    boolean start(IndexableField[] fields, int count) {
        for (int i = 0; i < count; ++i) {
            if (!fields[i].fieldType().indexed()) continue;
            return true;
        }
        return false;
    }

    @Override
    void start(IndexableField f) {
        this.payloadAttribute = this.fieldState.attributeSource.hasAttribute(PayloadAttribute.class) ? this.fieldState.attributeSource.getAttribute(PayloadAttribute.class) : null;
        this.offsetAttribute = this.hasOffsets ? this.fieldState.attributeSource.addAttribute(OffsetAttribute.class) : null;
    }

    void writeProx(int termID, int proxCode) {
        assert (this.hasProx);
        BytesRef payload = this.payloadAttribute == null ? null : this.payloadAttribute.getPayload();
        if (payload != null && payload.length > 0) {
            this.termsHashPerField.writeVInt(1, proxCode << 1 | 1);
            this.termsHashPerField.writeVInt(1, payload.length);
            this.termsHashPerField.writeBytes(1, payload.bytes, payload.offset, payload.length);
            this.hasPayloads = true;
        } else {
            this.termsHashPerField.writeVInt(1, proxCode << 1);
        }
        FreqProxPostingsArray postings = (FreqProxPostingsArray)this.termsHashPerField.postingsArray;
        postings.lastPositions[termID] = this.fieldState.position;
    }

    void writeOffsets(int termID, int offsetAccum) {
        assert (this.hasOffsets);
        int startOffset = offsetAccum + this.offsetAttribute.startOffset();
        int endOffset = offsetAccum + this.offsetAttribute.endOffset();
        FreqProxPostingsArray postings = (FreqProxPostingsArray)this.termsHashPerField.postingsArray;
        assert (startOffset - postings.lastOffsets[termID] >= 0);
        this.termsHashPerField.writeVInt(1, startOffset - postings.lastOffsets[termID]);
        this.termsHashPerField.writeVInt(1, endOffset - startOffset);
        postings.lastOffsets[termID] = startOffset;
    }

    @Override
    void newTerm(int termID) {
        assert (this.docState.testPoint("FreqProxTermsWriterPerField.newTerm start"));
        FreqProxPostingsArray postings = (FreqProxPostingsArray)this.termsHashPerField.postingsArray;
        postings.lastDocIDs[termID] = this.docState.docID;
        if (!this.hasFreq) {
            postings.lastDocCodes[termID] = this.docState.docID;
        } else {
            postings.lastDocCodes[termID] = this.docState.docID << 1;
            postings.termFreqs[termID] = 1;
            if (this.hasProx) {
                this.writeProx(termID, this.fieldState.position);
                if (this.hasOffsets) {
                    this.writeOffsets(termID, this.fieldState.offset);
                }
            } else assert (!this.hasOffsets);
        }
        this.fieldState.maxTermFrequency = Math.max(1, this.fieldState.maxTermFrequency);
        ++this.fieldState.uniqueTermCount;
    }

    @Override
    void addTerm(int termID) {
        assert (this.docState.testPoint("FreqProxTermsWriterPerField.addTerm start"));
        FreqProxPostingsArray postings = (FreqProxPostingsArray)this.termsHashPerField.postingsArray;
        assert (!this.hasFreq || postings.termFreqs[termID] > 0);
        if (!this.hasFreq) {
            assert (postings.termFreqs == null);
            if (this.docState.docID != postings.lastDocIDs[termID]) {
                assert (this.docState.docID > postings.lastDocIDs[termID]);
                this.termsHashPerField.writeVInt(0, postings.lastDocCodes[termID]);
                postings.lastDocCodes[termID] = this.docState.docID - postings.lastDocIDs[termID];
                postings.lastDocIDs[termID] = this.docState.docID;
                ++this.fieldState.uniqueTermCount;
            }
        } else if (this.docState.docID != postings.lastDocIDs[termID]) {
            assert (this.docState.docID > postings.lastDocIDs[termID]) : "id: " + this.docState.docID + " postings ID: " + postings.lastDocIDs[termID] + " termID: " + termID;
            if (1 == postings.termFreqs[termID]) {
                this.termsHashPerField.writeVInt(0, postings.lastDocCodes[termID] | 1);
            } else {
                this.termsHashPerField.writeVInt(0, postings.lastDocCodes[termID]);
                this.termsHashPerField.writeVInt(0, postings.termFreqs[termID]);
            }
            postings.termFreqs[termID] = 1;
            this.fieldState.maxTermFrequency = Math.max(1, this.fieldState.maxTermFrequency);
            postings.lastDocCodes[termID] = this.docState.docID - postings.lastDocIDs[termID] << 1;
            postings.lastDocIDs[termID] = this.docState.docID;
            if (this.hasProx) {
                this.writeProx(termID, this.fieldState.position);
                if (this.hasOffsets) {
                    postings.lastOffsets[termID] = 0;
                    this.writeOffsets(termID, this.fieldState.offset);
                }
            } else assert (!this.hasOffsets);
            ++this.fieldState.uniqueTermCount;
        } else {
            int n = termID;
            int n2 = postings.termFreqs[n] + 1;
            postings.termFreqs[n] = n2;
            this.fieldState.maxTermFrequency = Math.max(this.fieldState.maxTermFrequency, n2);
            if (this.hasProx) {
                this.writeProx(termID, this.fieldState.position - postings.lastPositions[termID]);
            }
            if (this.hasOffsets) {
                this.writeOffsets(termID, this.fieldState.offset);
            }
        }
    }

    @Override
    ParallelPostingsArray createPostingsArray(int size) {
        return new FreqProxPostingsArray(size, this.hasFreq, this.hasProx, this.hasOffsets);
    }

    public void abort() {
    }

    void flush(String fieldName, FieldsConsumer consumer, SegmentWriteState state) throws IOException {
        if (!this.fieldInfo.isIndexed()) {
            return;
        }
        TermsConsumer termsConsumer = consumer.addField(this.fieldInfo);
        Comparator<BytesRef> termComp = termsConsumer.getComparator();
        FieldInfo.IndexOptions currentFieldIndexOptions = this.fieldInfo.getIndexOptions();
        assert (currentFieldIndexOptions != null);
        boolean writeTermFreq = currentFieldIndexOptions.compareTo(FieldInfo.IndexOptions.DOCS_AND_FREQS) >= 0;
        boolean writePositions = currentFieldIndexOptions.compareTo(FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS) >= 0;
        boolean writeOffsets = currentFieldIndexOptions.compareTo(FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS) >= 0;
        boolean readTermFreq = this.hasFreq;
        boolean readPositions = this.hasProx;
        boolean readOffsets = this.hasOffsets;
        assert (!writeTermFreq || readTermFreq);
        assert (!writePositions || readPositions);
        assert (!writeOffsets || readOffsets);
        assert (!writeOffsets || writePositions);
        Map<Term, Integer> segDeletes = state.segDeletes != null && state.segDeletes.terms.size() > 0 ? state.segDeletes.terms : null;
        int[] termIDs = this.termsHashPerField.sortPostings(termComp);
        int numTerms = this.termsHashPerField.bytesHash.size();
        BytesRef text = new BytesRef();
        FreqProxPostingsArray postings = (FreqProxPostingsArray)this.termsHashPerField.postingsArray;
        ByteSliceReader freq = new ByteSliceReader();
        ByteSliceReader prox = new ByteSliceReader();
        FixedBitSet visitedDocs = new FixedBitSet(state.segmentInfo.getDocCount());
        long sumTotalTermFreq = 0L;
        long sumDocFreq = 0L;
        Term protoTerm = new Term(fieldName);
        for (int i = 0; i < numTerms; ++i) {
            int delDocLimit;
            int termID = termIDs[i];
            int textStart = postings.textStarts[termID];
            this.termsHashPerField.bytePool.setBytesRef(text, textStart);
            this.termsHashPerField.initReader(freq, termID, 0);
            if (readPositions || readOffsets) {
                this.termsHashPerField.initReader(prox, termID, 1);
            }
            PostingsConsumer postingsConsumer = termsConsumer.startTerm(text);
            if (segDeletes != null) {
                protoTerm.bytes = text;
                Integer docIDUpto = segDeletes.get(protoTerm);
                delDocLimit = docIDUpto != null ? docIDUpto : 0;
            } else {
                delDocLimit = 0;
            }
            int docFreq = 0;
            long totalTermFreq = 0L;
            int docID = 0;
            while (true) {
                int termFreq;
                if (freq.eof()) {
                    if (postings.lastDocCodes[termID] == -1) break;
                    docID = postings.lastDocIDs[termID];
                    termFreq = readTermFreq ? postings.termFreqs[termID] : -1;
                    postings.lastDocCodes[termID] = -1;
                } else {
                    int code = freq.readVInt();
                    if (!readTermFreq) {
                        docID += code;
                        termFreq = -1;
                    } else {
                        docID += code >>> 1;
                        termFreq = (code & 1) != 0 ? 1 : freq.readVInt();
                    }
                    assert (docID != postings.lastDocIDs[termID]);
                }
                ++docFreq;
                assert (docID < state.segmentInfo.getDocCount()) : "doc=" + docID + " maxDoc=" + state.segmentInfo.getDocCount();
                visitedDocs.set(docID);
                postingsConsumer.startDoc(docID, writeTermFreq ? termFreq : -1);
                if (docID < delDocLimit) {
                    if (state.liveDocs == null) {
                        state.liveDocs = this.docState.docWriter.codec.liveDocsFormat().newLiveDocs(state.segmentInfo.getDocCount());
                    }
                    if (state.liveDocs.get(docID)) {
                        ++state.delCountOnFlush;
                        state.liveDocs.clear(docID);
                    }
                }
                totalTermFreq += (long)termFreq;
                if (readPositions || readOffsets) {
                    int position = 0;
                    int offset = 0;
                    for (int j = 0; j < termFreq; ++j) {
                        BytesRef thisPayload;
                        if (!readPositions) continue;
                        int code = prox.readVInt();
                        position += code >>> 1;
                        if ((code & 1) != 0) {
                            int payloadLength = prox.readVInt();
                            if (this.payload == null) {
                                this.payload = new BytesRef();
                                this.payload.bytes = new byte[payloadLength];
                            } else if (this.payload.bytes.length < payloadLength) {
                                this.payload.grow(payloadLength);
                            }
                            prox.readBytes(this.payload.bytes, 0, payloadLength);
                            this.payload.length = payloadLength;
                            thisPayload = this.payload;
                        } else {
                            thisPayload = null;
                        }
                        if (readOffsets) {
                            int startOffset = offset + prox.readVInt();
                            int endOffset = startOffset + prox.readVInt();
                            if (writePositions) {
                                if (writeOffsets) {
                                    assert (startOffset >= 0 && endOffset >= startOffset) : "startOffset=" + startOffset + ",endOffset=" + endOffset + ",offset=" + offset;
                                    postingsConsumer.addPosition(position, thisPayload, startOffset, endOffset);
                                } else {
                                    postingsConsumer.addPosition(position, thisPayload, -1, -1);
                                }
                            }
                            offset = startOffset;
                            continue;
                        }
                        if (!writePositions) continue;
                        postingsConsumer.addPosition(position, thisPayload, -1, -1);
                    }
                }
                postingsConsumer.finishDoc();
            }
            termsConsumer.finishTerm(text, new TermStats(docFreq, writeTermFreq ? totalTermFreq : -1L));
            sumTotalTermFreq += totalTermFreq;
            sumDocFreq += (long)docFreq;
        }
        termsConsumer.finish(writeTermFreq ? sumTotalTermFreq : -1L, sumDocFreq, visitedDocs.cardinality());
    }

    static final class FreqProxPostingsArray
    extends ParallelPostingsArray {
        int[] termFreqs;
        int[] lastDocIDs;
        int[] lastDocCodes;
        int[] lastPositions;
        int[] lastOffsets;

        public FreqProxPostingsArray(int size, boolean writeFreqs, boolean writeProx, boolean writeOffsets) {
            super(size);
            if (writeFreqs) {
                this.termFreqs = new int[size];
            }
            this.lastDocIDs = new int[size];
            this.lastDocCodes = new int[size];
            if (writeProx) {
                this.lastPositions = new int[size];
                if (writeOffsets) {
                    this.lastOffsets = new int[size];
                }
            } else assert (!writeOffsets);
        }

        @Override
        ParallelPostingsArray newInstance(int size) {
            return new FreqProxPostingsArray(size, this.termFreqs != null, this.lastPositions != null, this.lastOffsets != null);
        }

        @Override
        void copyTo(ParallelPostingsArray toArray, int numToCopy) {
            assert (toArray instanceof FreqProxPostingsArray);
            FreqProxPostingsArray to = (FreqProxPostingsArray)toArray;
            super.copyTo(toArray, numToCopy);
            System.arraycopy(this.lastDocIDs, 0, to.lastDocIDs, 0, numToCopy);
            System.arraycopy(this.lastDocCodes, 0, to.lastDocCodes, 0, numToCopy);
            if (this.lastPositions != null) {
                assert (to.lastPositions != null);
                System.arraycopy(this.lastPositions, 0, to.lastPositions, 0, numToCopy);
            }
            if (this.lastOffsets != null) {
                assert (to.lastOffsets != null);
                System.arraycopy(this.lastOffsets, 0, to.lastOffsets, 0, numToCopy);
            }
            if (this.termFreqs != null) {
                assert (to.termFreqs != null);
                System.arraycopy(this.termFreqs, 0, to.termFreqs, 0, numToCopy);
            }
        }

        @Override
        int bytesPerPosting() {
            int bytes = 20;
            if (this.lastPositions != null) {
                bytes += 4;
            }
            if (this.lastOffsets != null) {
                bytes += 4;
            }
            if (this.termFreqs != null) {
                bytes += 4;
            }
            return bytes;
        }
    }
}

