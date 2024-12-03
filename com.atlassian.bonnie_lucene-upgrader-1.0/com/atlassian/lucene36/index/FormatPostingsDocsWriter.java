/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.CorruptIndexException;
import com.atlassian.lucene36.index.DefaultSkipListWriter;
import com.atlassian.lucene36.index.FieldInfo;
import com.atlassian.lucene36.index.FormatPostingsDocsConsumer;
import com.atlassian.lucene36.index.FormatPostingsPositionsConsumer;
import com.atlassian.lucene36.index.FormatPostingsPositionsWriter;
import com.atlassian.lucene36.index.FormatPostingsTermsWriter;
import com.atlassian.lucene36.index.IndexFileNames;
import com.atlassian.lucene36.index.SegmentWriteState;
import com.atlassian.lucene36.index.TermInfo;
import com.atlassian.lucene36.store.IndexOutput;
import com.atlassian.lucene36.util.IOUtils;
import com.atlassian.lucene36.util.UnicodeUtil;
import java.io.Closeable;
import java.io.IOException;

final class FormatPostingsDocsWriter
extends FormatPostingsDocsConsumer
implements Closeable {
    final IndexOutput out;
    final FormatPostingsTermsWriter parent;
    final FormatPostingsPositionsWriter posWriter;
    final DefaultSkipListWriter skipListWriter;
    final int skipInterval;
    final int totalNumDocs;
    boolean omitTermFreqAndPositions;
    boolean storePayloads;
    long freqStart;
    FieldInfo fieldInfo;
    int lastDocID;
    int df;
    private final TermInfo termInfo = new TermInfo();
    final UnicodeUtil.UTF8Result utf8 = new UnicodeUtil.UTF8Result();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    FormatPostingsDocsWriter(SegmentWriteState state, FormatPostingsTermsWriter parent) throws IOException {
        this.parent = parent;
        this.out = parent.parent.dir.createOutput(IndexFileNames.segmentFileName(parent.parent.segment, "frq"));
        boolean success = false;
        try {
            this.totalNumDocs = parent.parent.totalNumDocs;
            this.skipInterval = parent.parent.termsOut.skipInterval;
            this.skipListWriter = parent.parent.skipListWriter;
            this.skipListWriter.setFreqOutput(this.out);
            this.posWriter = new FormatPostingsPositionsWriter(state, this);
            return;
        }
        catch (Throwable throwable) {
            Object var5_5 = null;
            if (success) throw throwable;
            IOUtils.closeWhileHandlingException(this.out);
            throw throwable;
        }
    }

    void setField(FieldInfo fieldInfo) {
        this.fieldInfo = fieldInfo;
        this.omitTermFreqAndPositions = fieldInfo.indexOptions == FieldInfo.IndexOptions.DOCS_ONLY;
        this.storePayloads = fieldInfo.storePayloads;
        this.posWriter.setField(fieldInfo);
    }

    FormatPostingsPositionsConsumer addDoc(int docID, int termDocFreq) throws IOException {
        int delta = docID - this.lastDocID;
        if (docID < 0 || this.df > 0 && delta <= 0) {
            throw new CorruptIndexException("docs out of order (" + docID + " <= " + this.lastDocID + " ) (out: " + this.out + ")");
        }
        if (++this.df % this.skipInterval == 0) {
            this.skipListWriter.setSkipData(this.lastDocID, this.storePayloads, this.posWriter.lastPayloadLength);
            this.skipListWriter.bufferSkip(this.df);
        }
        assert (docID < this.totalNumDocs) : "docID=" + docID + " totalNumDocs=" + this.totalNumDocs;
        this.lastDocID = docID;
        if (this.omitTermFreqAndPositions) {
            this.out.writeVInt(delta);
        } else if (1 == termDocFreq) {
            this.out.writeVInt(delta << 1 | 1);
        } else {
            this.out.writeVInt(delta << 1);
            this.out.writeVInt(termDocFreq);
        }
        return this.posWriter;
    }

    void finish() throws IOException {
        long skipPointer = this.skipListWriter.writeSkip(this.out);
        this.termInfo.set(this.df, this.parent.freqStart, this.parent.proxStart, (int)(skipPointer - this.parent.freqStart));
        UnicodeUtil.UTF16toUTF8(this.parent.currentTerm, this.parent.currentTermStart, this.utf8);
        if (this.df > 0) {
            this.parent.termsOut.add(this.fieldInfo.number, this.utf8.result, this.utf8.length, this.termInfo);
        }
        this.lastDocID = 0;
        this.df = 0;
    }

    public void close() throws IOException {
        IOUtils.close(this.out, this.posWriter);
    }
}

