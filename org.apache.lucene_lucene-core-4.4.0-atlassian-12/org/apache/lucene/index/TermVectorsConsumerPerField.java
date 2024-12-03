/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.IOException;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PayloadAttribute;
import org.apache.lucene.codecs.TermVectorsWriter;
import org.apache.lucene.index.ByteSliceReader;
import org.apache.lucene.index.DocumentsWriterPerThread;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.FieldInvertState;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.ParallelPostingsArray;
import org.apache.lucene.index.TermVectorsConsumer;
import org.apache.lucene.index.TermsHashConsumerPerField;
import org.apache.lucene.index.TermsHashPerField;
import org.apache.lucene.util.ByteBlockPool;
import org.apache.lucene.util.BytesRef;

final class TermVectorsConsumerPerField
extends TermsHashConsumerPerField {
    final TermsHashPerField termsHashPerField;
    final TermVectorsConsumer termsWriter;
    final FieldInfo fieldInfo;
    final DocumentsWriterPerThread.DocState docState;
    final FieldInvertState fieldState;
    boolean doVectors;
    boolean doVectorPositions;
    boolean doVectorOffsets;
    boolean doVectorPayloads;
    int maxNumPostings;
    OffsetAttribute offsetAttribute;
    PayloadAttribute payloadAttribute;
    boolean hasPayloads;

    public TermVectorsConsumerPerField(TermsHashPerField termsHashPerField, TermVectorsConsumer termsWriter, FieldInfo fieldInfo) {
        this.termsHashPerField = termsHashPerField;
        this.termsWriter = termsWriter;
        this.fieldInfo = fieldInfo;
        this.docState = termsHashPerField.docState;
        this.fieldState = termsHashPerField.fieldState;
    }

    @Override
    int getStreamCount() {
        return 2;
    }

    @Override
    boolean start(IndexableField[] fields, int count) {
        this.doVectors = false;
        this.doVectorPositions = false;
        this.doVectorOffsets = false;
        this.doVectorPayloads = false;
        this.hasPayloads = false;
        for (int i = 0; i < count; ++i) {
            IndexableField field = fields[i];
            if (field.fieldType().indexed()) {
                if (field.fieldType().storeTermVectors()) {
                    this.doVectors = true;
                    this.doVectorPositions |= field.fieldType().storeTermVectorPositions();
                    this.doVectorOffsets |= field.fieldType().storeTermVectorOffsets();
                    if (this.doVectorPositions) {
                        this.doVectorPayloads |= field.fieldType().storeTermVectorPayloads();
                        continue;
                    }
                    if (!field.fieldType().storeTermVectorPayloads()) continue;
                    throw new IllegalArgumentException("cannot index term vector payloads for field: " + field + " without term vector positions");
                }
                if (field.fieldType().storeTermVectorOffsets()) {
                    throw new IllegalArgumentException("cannot index term vector offsets when term vectors are not indexed (field=\"" + field.name());
                }
                if (field.fieldType().storeTermVectorPositions()) {
                    throw new IllegalArgumentException("cannot index term vector positions when term vectors are not indexed (field=\"" + field.name());
                }
                if (!field.fieldType().storeTermVectorPayloads()) continue;
                throw new IllegalArgumentException("cannot index term vector payloads when term vectors are not indexed (field=\"" + field.name());
            }
            if (field.fieldType().storeTermVectors()) {
                throw new IllegalArgumentException("cannot index term vectors when field is not indexed (field=\"" + field.name());
            }
            if (field.fieldType().storeTermVectorOffsets()) {
                throw new IllegalArgumentException("cannot index term vector offsets when field is not indexed (field=\"" + field.name());
            }
            if (field.fieldType().storeTermVectorPositions()) {
                throw new IllegalArgumentException("cannot index term vector positions when field is not indexed (field=\"" + field.name());
            }
            if (!field.fieldType().storeTermVectorPayloads()) continue;
            throw new IllegalArgumentException("cannot index term vector payloads when field is not indexed (field=\"" + field.name());
        }
        if (this.doVectors) {
            this.termsWriter.hasVectors = true;
            if (this.termsHashPerField.bytesHash.size() != 0) {
                this.termsHashPerField.reset();
            }
        }
        return this.doVectors;
    }

    public void abort() {
    }

    @Override
    void finish() {
        if (!this.doVectors || this.termsHashPerField.bytesHash.size() == 0) {
            return;
        }
        this.termsWriter.addFieldToFlush(this);
    }

    void finishDocument() throws IOException {
        assert (this.docState.testPoint("TermVectorsTermsWriterPerField.finish start"));
        int numPostings = this.termsHashPerField.bytesHash.size();
        BytesRef flushTerm = this.termsWriter.flushTerm;
        assert (numPostings >= 0);
        if (numPostings > this.maxNumPostings) {
            this.maxNumPostings = numPostings;
        }
        assert (this.termsWriter.vectorFieldsInOrder(this.fieldInfo));
        TermVectorsPostingsArray postings = (TermVectorsPostingsArray)this.termsHashPerField.postingsArray;
        TermVectorsWriter tv = this.termsWriter.writer;
        int[] termIDs = this.termsHashPerField.sortPostings(tv.getComparator());
        tv.startField(this.fieldInfo, numPostings, this.doVectorPositions, this.doVectorOffsets, this.hasPayloads);
        ByteSliceReader posReader = this.doVectorPositions ? this.termsWriter.vectorSliceReaderPos : null;
        ByteSliceReader offReader = this.doVectorOffsets ? this.termsWriter.vectorSliceReaderOff : null;
        ByteBlockPool termBytePool = this.termsHashPerField.termBytePool;
        for (int j = 0; j < numPostings; ++j) {
            int termID = termIDs[j];
            int freq = postings.freqs[termID];
            termBytePool.setBytesRef(flushTerm, postings.textStarts[termID]);
            tv.startTerm(flushTerm, freq);
            if (this.doVectorPositions || this.doVectorOffsets) {
                if (posReader != null) {
                    this.termsHashPerField.initReader(posReader, termID, 0);
                }
                if (offReader != null) {
                    this.termsHashPerField.initReader(offReader, termID, 1);
                }
                tv.addProx(freq, posReader, offReader);
            }
            tv.finishTerm();
        }
        tv.finishField();
        this.termsHashPerField.reset();
        this.fieldInfo.setStoreTermVectors();
    }

    void shrinkHash() {
        this.termsHashPerField.shrinkHash(this.maxNumPostings);
        this.maxNumPostings = 0;
    }

    @Override
    void start(IndexableField f) {
        this.offsetAttribute = this.doVectorOffsets ? this.fieldState.attributeSource.addAttribute(OffsetAttribute.class) : null;
        this.payloadAttribute = this.doVectorPayloads && this.fieldState.attributeSource.hasAttribute(PayloadAttribute.class) ? this.fieldState.attributeSource.getAttribute(PayloadAttribute.class) : null;
    }

    void writeProx(TermVectorsPostingsArray postings, int termID) {
        if (this.doVectorOffsets) {
            int startOffset = this.fieldState.offset + this.offsetAttribute.startOffset();
            int endOffset = this.fieldState.offset + this.offsetAttribute.endOffset();
            this.termsHashPerField.writeVInt(1, startOffset - postings.lastOffsets[termID]);
            this.termsHashPerField.writeVInt(1, endOffset - startOffset);
            postings.lastOffsets[termID] = endOffset;
        }
        if (this.doVectorPositions) {
            BytesRef payload = this.payloadAttribute == null ? null : this.payloadAttribute.getPayload();
            int pos = this.fieldState.position - postings.lastPositions[termID];
            if (payload != null && payload.length > 0) {
                this.termsHashPerField.writeVInt(0, pos << 1 | 1);
                this.termsHashPerField.writeVInt(0, payload.length);
                this.termsHashPerField.writeBytes(0, payload.bytes, payload.offset, payload.length);
                this.hasPayloads = true;
            } else {
                this.termsHashPerField.writeVInt(0, pos << 1);
            }
            postings.lastPositions[termID] = this.fieldState.position;
        }
    }

    @Override
    void newTerm(int termID) {
        assert (this.docState.testPoint("TermVectorsTermsWriterPerField.newTerm start"));
        TermVectorsPostingsArray postings = (TermVectorsPostingsArray)this.termsHashPerField.postingsArray;
        postings.freqs[termID] = 1;
        postings.lastOffsets[termID] = 0;
        postings.lastPositions[termID] = 0;
        this.writeProx(postings, termID);
    }

    @Override
    void addTerm(int termID) {
        assert (this.docState.testPoint("TermVectorsTermsWriterPerField.addTerm start"));
        TermVectorsPostingsArray postings = (TermVectorsPostingsArray)this.termsHashPerField.postingsArray;
        int n = termID;
        postings.freqs[n] = postings.freqs[n] + 1;
        this.writeProx(postings, termID);
    }

    @Override
    void skippingLongTerm() {
    }

    @Override
    ParallelPostingsArray createPostingsArray(int size) {
        return new TermVectorsPostingsArray(size);
    }

    static final class TermVectorsPostingsArray
    extends ParallelPostingsArray {
        int[] freqs;
        int[] lastOffsets;
        int[] lastPositions;

        public TermVectorsPostingsArray(int size) {
            super(size);
            this.freqs = new int[size];
            this.lastOffsets = new int[size];
            this.lastPositions = new int[size];
        }

        @Override
        ParallelPostingsArray newInstance(int size) {
            return new TermVectorsPostingsArray(size);
        }

        @Override
        void copyTo(ParallelPostingsArray toArray, int numToCopy) {
            assert (toArray instanceof TermVectorsPostingsArray);
            TermVectorsPostingsArray to = (TermVectorsPostingsArray)toArray;
            super.copyTo(toArray, numToCopy);
            System.arraycopy(this.freqs, 0, to.freqs, 0, this.size);
            System.arraycopy(this.lastOffsets, 0, to.lastOffsets, 0, this.size);
            System.arraycopy(this.lastPositions, 0, to.lastPositions, 0, this.size);
        }

        @Override
        int bytesPerPosting() {
            return super.bytesPerPosting() + 12;
        }
    }
}

