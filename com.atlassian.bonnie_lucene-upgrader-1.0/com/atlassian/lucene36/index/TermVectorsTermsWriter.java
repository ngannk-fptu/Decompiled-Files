/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.DocumentsWriter;
import com.atlassian.lucene36.index.IndexFileNames;
import com.atlassian.lucene36.index.SegmentWriteState;
import com.atlassian.lucene36.index.TermVectorsTermsWriterPerField;
import com.atlassian.lucene36.index.TermVectorsTermsWriterPerThread;
import com.atlassian.lucene36.index.TermsHashConsumer;
import com.atlassian.lucene36.index.TermsHashConsumerPerField;
import com.atlassian.lucene36.index.TermsHashConsumerPerThread;
import com.atlassian.lucene36.index.TermsHashPerThread;
import com.atlassian.lucene36.store.IndexOutput;
import com.atlassian.lucene36.store.RAMOutputStream;
import com.atlassian.lucene36.util.ArrayUtil;
import com.atlassian.lucene36.util.IOUtils;
import com.atlassian.lucene36.util.RamUsageEstimator;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class TermVectorsTermsWriter
extends TermsHashConsumer {
    final DocumentsWriter docWriter;
    PerDoc[] docFreeList = new PerDoc[1];
    int freeCount;
    IndexOutput tvx;
    IndexOutput tvd;
    IndexOutput tvf;
    int lastDocID;
    boolean hasVectors;
    int allocCount;

    public TermVectorsTermsWriter(DocumentsWriter docWriter) {
        this.docWriter = docWriter;
    }

    @Override
    public TermsHashConsumerPerThread addThread(TermsHashPerThread termsHashPerThread) {
        return new TermVectorsTermsWriterPerThread(termsHashPerThread, this);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    synchronized void flush(Map<TermsHashConsumerPerThread, Collection<TermsHashConsumerPerField>> threadsAndFields, SegmentWriteState state) throws IOException {
        if (this.tvx != null) {
            assert (state.segmentName != null);
            this.fill(state.numDocs);
            try {
                if (4L + (long)state.numDocs * 16L != this.tvx.getFilePointer()) {
                    String idxName = IndexFileNames.segmentFileName(state.segmentName, "tvx");
                    throw new RuntimeException("tvx size mismatch: " + state.numDocs + " docs vs " + this.tvx.getFilePointer() + " length in bytes of " + idxName + " file exists?=" + state.directory.fileExists(idxName));
                }
                Object var5_5 = null;
            }
            catch (Throwable throwable) {
                Object var5_6 = null;
                IOUtils.close(this.tvx, this.tvf, this.tvd);
                this.tvf = null;
                this.tvd = null;
                this.tvx = null;
                throw throwable;
            }
            IOUtils.close(this.tvx, this.tvf, this.tvd);
            this.tvf = null;
            this.tvd = null;
            this.tvx = null;
            this.lastDocID = 0;
            state.hasVectors = this.hasVectors;
            this.hasVectors = false;
        }
        for (Map.Entry<TermsHashConsumerPerThread, Collection<TermsHashConsumerPerField>> entry : threadsAndFields.entrySet()) {
            for (TermsHashConsumerPerField field : entry.getValue()) {
                TermVectorsTermsWriterPerField perField = (TermVectorsTermsWriterPerField)field;
                perField.termsHashPerField.reset();
                perField.shrinkHash();
            }
            TermVectorsTermsWriterPerThread perThread = (TermVectorsTermsWriterPerThread)entry.getKey();
            perThread.termsHashPerThread.reset(true);
        }
    }

    synchronized PerDoc getPerDoc() {
        if (this.freeCount == 0) {
            ++this.allocCount;
            if (this.allocCount > this.docFreeList.length) {
                assert (this.allocCount == 1 + this.docFreeList.length);
                this.docFreeList = new PerDoc[ArrayUtil.oversize(this.allocCount, RamUsageEstimator.NUM_BYTES_OBJECT_REF)];
            }
            return new PerDoc();
        }
        return this.docFreeList[--this.freeCount];
    }

    void fill(int docID) throws IOException {
        if (this.lastDocID < docID) {
            long tvfPosition = this.tvf.getFilePointer();
            while (this.lastDocID < docID) {
                this.tvx.writeLong(this.tvd.getFilePointer());
                this.tvd.writeVInt(0);
                this.tvx.writeLong(tvfPosition);
                ++this.lastDocID;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    synchronized void initTermVectorsWriter() throws IOException {
        if (this.tvx == null) {
            block4: {
                boolean success = false;
                try {
                    this.hasVectors = true;
                    this.tvx = this.docWriter.directory.createOutput(IndexFileNames.segmentFileName(this.docWriter.getSegment(), "tvx"));
                    this.tvd = this.docWriter.directory.createOutput(IndexFileNames.segmentFileName(this.docWriter.getSegment(), "tvd"));
                    this.tvf = this.docWriter.directory.createOutput(IndexFileNames.segmentFileName(this.docWriter.getSegment(), "tvf"));
                    this.tvx.writeInt(4);
                    this.tvd.writeInt(4);
                    this.tvf.writeInt(4);
                    success = true;
                    Object var3_2 = null;
                    if (success) break block4;
                }
                catch (Throwable throwable) {
                    Object var3_3 = null;
                    if (!success) {
                        IOUtils.closeWhileHandlingException(this.tvx, this.tvd, this.tvf);
                    }
                    throw throwable;
                }
                IOUtils.closeWhileHandlingException(this.tvx, this.tvd, this.tvf);
            }
            this.lastDocID = 0;
        }
    }

    synchronized void finishDocument(PerDoc perDoc) throws IOException {
        assert (this.docWriter.writer.testPoint("TermVectorsTermsWriter.finishDocument start"));
        this.initTermVectorsWriter();
        this.fill(perDoc.docID);
        this.tvx.writeLong(this.tvd.getFilePointer());
        this.tvx.writeLong(this.tvf.getFilePointer());
        this.tvd.writeVInt(perDoc.numVectorFields);
        if (perDoc.numVectorFields > 0) {
            for (int i = 0; i < perDoc.numVectorFields; ++i) {
                this.tvd.writeVInt(perDoc.fieldNumbers[i]);
            }
            assert (0L == perDoc.fieldPointers[0]);
            long lastPos = perDoc.fieldPointers[0];
            for (int i = 1; i < perDoc.numVectorFields; ++i) {
                long pos = perDoc.fieldPointers[i];
                this.tvd.writeVLong(pos - lastPos);
                lastPos = pos;
            }
            perDoc.perDocTvf.writeTo(this.tvf);
            perDoc.numVectorFields = 0;
        }
        assert (this.lastDocID == perDoc.docID) : "lastDocID=" + this.lastDocID + " perDoc.docID=" + perDoc.docID;
        ++this.lastDocID;
        perDoc.reset();
        this.free(perDoc);
        assert (this.docWriter.writer.testPoint("TermVectorsTermsWriter.finishDocument end"));
    }

    @Override
    public void abort() {
        this.hasVectors = false;
        try {
            IOUtils.closeWhileHandlingException(this.tvx, this.tvd, this.tvf);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            this.docWriter.directory.deleteFile(IndexFileNames.segmentFileName(this.docWriter.getSegment(), "tvx"));
        }
        catch (IOException ignored) {
            // empty catch block
        }
        try {
            this.docWriter.directory.deleteFile(IndexFileNames.segmentFileName(this.docWriter.getSegment(), "tvd"));
        }
        catch (IOException ignored) {
            // empty catch block
        }
        try {
            this.docWriter.directory.deleteFile(IndexFileNames.segmentFileName(this.docWriter.getSegment(), "tvf"));
        }
        catch (IOException iOException) {
            // empty catch block
        }
        this.tvf = null;
        this.tvd = null;
        this.tvx = null;
        this.lastDocID = 0;
    }

    synchronized void free(PerDoc doc) {
        assert (this.freeCount < this.docFreeList.length);
        this.docFreeList[this.freeCount++] = doc;
    }

    class PerDoc
    extends DocumentsWriter.DocWriter {
        final DocumentsWriter.PerDocBuffer buffer;
        RAMOutputStream perDocTvf;
        int numVectorFields;
        int[] fieldNumbers;
        long[] fieldPointers;

        PerDoc() {
            this.buffer = TermVectorsTermsWriter.this.docWriter.newPerDocBuffer();
            this.perDocTvf = new RAMOutputStream(this.buffer);
            this.fieldNumbers = new int[1];
            this.fieldPointers = new long[1];
        }

        void reset() {
            this.perDocTvf.reset();
            this.buffer.recycle();
            this.numVectorFields = 0;
        }

        void abort() {
            this.reset();
            TermVectorsTermsWriter.this.free(this);
        }

        void addField(int fieldNumber) {
            if (this.numVectorFields == this.fieldNumbers.length) {
                this.fieldNumbers = ArrayUtil.grow(this.fieldNumbers);
            }
            if (this.numVectorFields == this.fieldPointers.length) {
                this.fieldPointers = ArrayUtil.grow(this.fieldPointers);
            }
            this.fieldNumbers[this.numVectorFields] = fieldNumber;
            this.fieldPointers[this.numVectorFields] = this.perDocTvf.getFilePointer();
            ++this.numVectorFields;
        }

        public long sizeInBytes() {
            return this.buffer.getSizeInBytes();
        }

        public void finish() throws IOException {
            TermVectorsTermsWriter.this.finishDocument(this);
        }
    }
}

