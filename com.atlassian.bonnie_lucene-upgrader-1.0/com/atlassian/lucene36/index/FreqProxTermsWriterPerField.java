/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.analysis.tokenattributes.PayloadAttribute;
import com.atlassian.lucene36.document.Fieldable;
import com.atlassian.lucene36.index.DocumentsWriter;
import com.atlassian.lucene36.index.FieldInfo;
import com.atlassian.lucene36.index.FieldInvertState;
import com.atlassian.lucene36.index.FreqProxTermsWriterPerThread;
import com.atlassian.lucene36.index.ParallelPostingsArray;
import com.atlassian.lucene36.index.Payload;
import com.atlassian.lucene36.index.TermsHashConsumerPerField;
import com.atlassian.lucene36.index.TermsHashPerField;
import java.io.IOException;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class FreqProxTermsWriterPerField
extends TermsHashConsumerPerField
implements Comparable<FreqProxTermsWriterPerField> {
    final FreqProxTermsWriterPerThread perThread;
    final TermsHashPerField termsHashPerField;
    final FieldInfo fieldInfo;
    final DocumentsWriter.DocState docState;
    final FieldInvertState fieldState;
    FieldInfo.IndexOptions indexOptions;
    PayloadAttribute payloadAttribute;
    boolean hasPayloads;

    public FreqProxTermsWriterPerField(TermsHashPerField termsHashPerField, FreqProxTermsWriterPerThread perThread, FieldInfo fieldInfo) {
        this.termsHashPerField = termsHashPerField;
        this.perThread = perThread;
        this.fieldInfo = fieldInfo;
        this.docState = termsHashPerField.docState;
        this.fieldState = termsHashPerField.fieldState;
        this.indexOptions = fieldInfo.indexOptions;
    }

    @Override
    int getStreamCount() {
        if (this.indexOptions != FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS) {
            return 1;
        }
        return 2;
    }

    @Override
    void finish() {
    }

    @Override
    void skippingLongTerm() throws IOException {
    }

    @Override
    public int compareTo(FreqProxTermsWriterPerField other) {
        return this.fieldInfo.name.compareTo(other.fieldInfo.name);
    }

    void reset() {
        this.indexOptions = this.fieldInfo.indexOptions;
        this.payloadAttribute = null;
    }

    @Override
    boolean start(Fieldable[] fields, int count) {
        for (int i = 0; i < count; ++i) {
            if (!fields[i].isIndexed()) continue;
            return true;
        }
        return false;
    }

    @Override
    void start(Fieldable f) {
        this.payloadAttribute = this.fieldState.attributeSource.hasAttribute(PayloadAttribute.class) ? this.fieldState.attributeSource.getAttribute(PayloadAttribute.class) : null;
    }

    void writeProx(int termID, int proxCode) {
        Payload payload = this.payloadAttribute == null ? null : this.payloadAttribute.getPayload();
        if (payload != null && payload.length > 0) {
            this.termsHashPerField.writeVInt(1, proxCode << 1 | 1);
            this.termsHashPerField.writeVInt(1, payload.length);
            this.termsHashPerField.writeBytes(1, payload.data, payload.offset, payload.length);
            this.hasPayloads = true;
        } else {
            this.termsHashPerField.writeVInt(1, proxCode << 1);
        }
        FreqProxPostingsArray postings = (FreqProxPostingsArray)this.termsHashPerField.postingsArray;
        postings.lastPositions[termID] = this.fieldState.position;
    }

    @Override
    void newTerm(int termID) {
        assert (this.docState.testPoint("FreqProxTermsWriterPerField.newTerm start"));
        FreqProxPostingsArray postings = (FreqProxPostingsArray)this.termsHashPerField.postingsArray;
        postings.lastDocIDs[termID] = this.docState.docID;
        if (this.indexOptions == FieldInfo.IndexOptions.DOCS_ONLY) {
            postings.lastDocCodes[termID] = this.docState.docID;
        } else {
            postings.lastDocCodes[termID] = this.docState.docID << 1;
            postings.docFreqs[termID] = 1;
            if (this.indexOptions == FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS) {
                this.writeProx(termID, this.fieldState.position);
            }
        }
        this.fieldState.maxTermFrequency = Math.max(1, this.fieldState.maxTermFrequency);
        ++this.fieldState.uniqueTermCount;
    }

    @Override
    void addTerm(int termID) {
        assert (this.docState.testPoint("FreqProxTermsWriterPerField.addTerm start"));
        FreqProxPostingsArray postings = (FreqProxPostingsArray)this.termsHashPerField.postingsArray;
        assert (this.indexOptions == FieldInfo.IndexOptions.DOCS_ONLY || postings.docFreqs[termID] > 0);
        if (this.indexOptions == FieldInfo.IndexOptions.DOCS_ONLY) {
            if (this.docState.docID != postings.lastDocIDs[termID]) {
                assert (this.docState.docID > postings.lastDocIDs[termID]);
                this.termsHashPerField.writeVInt(0, postings.lastDocCodes[termID]);
                postings.lastDocCodes[termID] = this.docState.docID - postings.lastDocIDs[termID];
                postings.lastDocIDs[termID] = this.docState.docID;
                ++this.fieldState.uniqueTermCount;
            }
        } else if (this.docState.docID != postings.lastDocIDs[termID]) {
            assert (this.docState.docID > postings.lastDocIDs[termID]);
            if (1 == postings.docFreqs[termID]) {
                this.termsHashPerField.writeVInt(0, postings.lastDocCodes[termID] | 1);
            } else {
                this.termsHashPerField.writeVInt(0, postings.lastDocCodes[termID]);
                this.termsHashPerField.writeVInt(0, postings.docFreqs[termID]);
            }
            postings.docFreqs[termID] = 1;
            this.fieldState.maxTermFrequency = Math.max(1, this.fieldState.maxTermFrequency);
            postings.lastDocCodes[termID] = this.docState.docID - postings.lastDocIDs[termID] << 1;
            postings.lastDocIDs[termID] = this.docState.docID;
            if (this.indexOptions == FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS) {
                this.writeProx(termID, this.fieldState.position);
            }
            ++this.fieldState.uniqueTermCount;
        } else {
            int n = termID;
            int n2 = postings.docFreqs[n] + 1;
            postings.docFreqs[n] = n2;
            this.fieldState.maxTermFrequency = Math.max(this.fieldState.maxTermFrequency, n2);
            if (this.indexOptions == FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS) {
                this.writeProx(termID, this.fieldState.position - postings.lastPositions[termID]);
            }
        }
    }

    @Override
    ParallelPostingsArray createPostingsArray(int size) {
        return new FreqProxPostingsArray(size);
    }

    public void abort() {
    }

    static final class FreqProxPostingsArray
    extends ParallelPostingsArray {
        int[] docFreqs;
        int[] lastDocIDs;
        int[] lastDocCodes;
        int[] lastPositions;

        public FreqProxPostingsArray(int size) {
            super(size);
            this.docFreqs = new int[size];
            this.lastDocIDs = new int[size];
            this.lastDocCodes = new int[size];
            this.lastPositions = new int[size];
        }

        ParallelPostingsArray newInstance(int size) {
            return new FreqProxPostingsArray(size);
        }

        void copyTo(ParallelPostingsArray toArray, int numToCopy) {
            assert (toArray instanceof FreqProxPostingsArray);
            FreqProxPostingsArray to = (FreqProxPostingsArray)toArray;
            super.copyTo(toArray, numToCopy);
            System.arraycopy(this.docFreqs, 0, to.docFreqs, 0, numToCopy);
            System.arraycopy(this.lastDocIDs, 0, to.lastDocIDs, 0, numToCopy);
            System.arraycopy(this.lastDocCodes, 0, to.lastDocCodes, 0, numToCopy);
            System.arraycopy(this.lastPositions, 0, to.lastPositions, 0, numToCopy);
        }

        int bytesPerPosting() {
            return 28;
        }
    }
}

