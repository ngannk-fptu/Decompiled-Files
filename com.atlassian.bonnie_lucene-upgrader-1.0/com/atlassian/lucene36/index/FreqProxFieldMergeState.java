/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.ByteSliceReader;
import com.atlassian.lucene36.index.CharBlockPool;
import com.atlassian.lucene36.index.FieldInfo;
import com.atlassian.lucene36.index.FreqProxTermsWriterPerField;
import java.io.IOException;

final class FreqProxFieldMergeState {
    final FreqProxTermsWriterPerField field;
    final int numPostings;
    final CharBlockPool charPool;
    final int[] termIDs;
    final FreqProxTermsWriterPerField.FreqProxPostingsArray postings;
    int currentTermID;
    char[] text;
    int textOffset;
    private int postingUpto = -1;
    final ByteSliceReader freq = new ByteSliceReader();
    final ByteSliceReader prox = new ByteSliceReader();
    int docID;
    int termFreq;

    public FreqProxFieldMergeState(FreqProxTermsWriterPerField field) {
        this.field = field;
        this.charPool = field.perThread.termsHashPerThread.charPool;
        this.numPostings = field.termsHashPerField.numPostings;
        this.termIDs = field.termsHashPerField.sortPostings();
        this.postings = (FreqProxTermsWriterPerField.FreqProxPostingsArray)field.termsHashPerField.postingsArray;
    }

    boolean nextTerm() throws IOException {
        ++this.postingUpto;
        if (this.postingUpto == this.numPostings) {
            return false;
        }
        this.currentTermID = this.termIDs[this.postingUpto];
        this.docID = 0;
        int textStart = this.postings.textStarts[this.currentTermID];
        this.text = this.charPool.buffers[textStart >> 14];
        this.textOffset = textStart & 0x3FFF;
        this.field.termsHashPerField.initReader(this.freq, this.currentTermID, 0);
        if (this.field.fieldInfo.indexOptions == FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS) {
            this.field.termsHashPerField.initReader(this.prox, this.currentTermID, 1);
        }
        boolean result = this.nextDoc();
        assert (result);
        return true;
    }

    public String termText() {
        int upto = this.textOffset;
        while (this.text[upto] != '\uffff') {
            ++upto;
        }
        return new String(this.text, this.textOffset, upto - this.textOffset);
    }

    public boolean nextDoc() throws IOException {
        if (this.freq.eof()) {
            if (this.postings.lastDocCodes[this.currentTermID] != -1) {
                this.docID = this.postings.lastDocIDs[this.currentTermID];
                if (this.field.indexOptions != FieldInfo.IndexOptions.DOCS_ONLY) {
                    this.termFreq = this.postings.docFreqs[this.currentTermID];
                }
                this.postings.lastDocCodes[this.currentTermID] = -1;
                return true;
            }
            return false;
        }
        int code = this.freq.readVInt();
        if (this.field.indexOptions == FieldInfo.IndexOptions.DOCS_ONLY) {
            this.docID += code;
        } else {
            this.docID += code >>> 1;
            this.termFreq = (code & 1) != 0 ? 1 : this.freq.readVInt();
        }
        assert (this.docID != this.postings.lastDocIDs[this.currentTermID]);
        return true;
    }
}

