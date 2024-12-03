/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.FieldInfos;
import com.atlassian.lucene36.index.IndexFileNames;
import com.atlassian.lucene36.index.TermFreqVector;
import com.atlassian.lucene36.index.TermPositionVector;
import com.atlassian.lucene36.index.TermVectorOffsetInfo;
import com.atlassian.lucene36.index.TermVectorsReader;
import com.atlassian.lucene36.store.Directory;
import com.atlassian.lucene36.store.IndexOutput;
import com.atlassian.lucene36.util.IOUtils;
import com.atlassian.lucene36.util.StringHelper;
import com.atlassian.lucene36.util.UnicodeUtil;
import java.io.IOException;

final class TermVectorsWriter {
    private IndexOutput tvx;
    private IndexOutput tvd;
    private IndexOutput tvf;
    private FieldInfos fieldInfos;
    final UnicodeUtil.UTF8Result[] utf8Results;
    final String segment;
    final Directory directory;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public TermVectorsWriter(Directory directory, String segment, FieldInfos fieldInfos) throws IOException {
        block3: {
            this.tvx = null;
            this.tvd = null;
            this.tvf = null;
            this.utf8Results = new UnicodeUtil.UTF8Result[]{new UnicodeUtil.UTF8Result(), new UnicodeUtil.UTF8Result()};
            this.segment = segment;
            this.directory = directory;
            boolean success = false;
            try {
                this.tvx = directory.createOutput(IndexFileNames.segmentFileName(segment, "tvx"));
                this.tvx.writeInt(4);
                this.tvd = directory.createOutput(IndexFileNames.segmentFileName(segment, "tvd"));
                this.tvd.writeInt(4);
                this.tvf = directory.createOutput(IndexFileNames.segmentFileName(segment, "tvf"));
                this.tvf.writeInt(4);
                success = true;
                Object var6_5 = null;
                if (success) break block3;
            }
            catch (Throwable throwable) {
                Object var6_6 = null;
                if (!success) {
                    IOUtils.closeWhileHandlingException(this.tvx, this.tvd, this.tvf);
                }
                throw throwable;
            }
            IOUtils.closeWhileHandlingException(this.tvx, this.tvd, this.tvf);
        }
        this.fieldInfos = fieldInfos;
    }

    public final void addAllDocVectors(TermFreqVector[] vectors) throws IOException {
        this.tvx.writeLong(this.tvd.getFilePointer());
        this.tvx.writeLong(this.tvf.getFilePointer());
        if (vectors != null) {
            int numFields = vectors.length;
            this.tvd.writeVInt(numFields);
            long[] fieldPointers = new long[numFields];
            for (int i = 0; i < numFields; ++i) {
                int bits;
                boolean storeOffsets;
                boolean storePositions;
                TermPositionVector tpVector;
                fieldPointers[i] = this.tvf.getFilePointer();
                int fieldNumber = this.fieldInfos.fieldNumber(vectors[i].getField());
                this.tvd.writeVInt(fieldNumber);
                int numTerms = vectors[i].size();
                this.tvf.writeVInt(numTerms);
                if (vectors[i] instanceof TermPositionVector) {
                    tpVector = (TermPositionVector)vectors[i];
                    storePositions = tpVector.size() > 0 && tpVector.getTermPositions(0) != null;
                    storeOffsets = tpVector.size() > 0 && tpVector.getOffsets(0) != null;
                    bits = (byte)((storePositions ? 1 : 0) + (storeOffsets ? 2 : 0));
                } else {
                    tpVector = null;
                    bits = 0;
                    storePositions = false;
                    storeOffsets = false;
                }
                this.tvf.writeVInt(bits);
                String[] terms = vectors[i].getTerms();
                int[] freqs = vectors[i].getTermFrequencies();
                int utf8Upto = 0;
                this.utf8Results[1].length = 0;
                for (int j = 0; j < numTerms; ++j) {
                    int k;
                    UnicodeUtil.UTF16toUTF8(terms[j], 0, terms[j].length(), this.utf8Results[utf8Upto]);
                    int start = StringHelper.bytesDifference(this.utf8Results[1 - utf8Upto].result, this.utf8Results[1 - utf8Upto].length, this.utf8Results[utf8Upto].result, this.utf8Results[utf8Upto].length);
                    int length = this.utf8Results[utf8Upto].length - start;
                    this.tvf.writeVInt(start);
                    this.tvf.writeVInt(length);
                    this.tvf.writeBytes(this.utf8Results[utf8Upto].result, start, length);
                    utf8Upto = 1 - utf8Upto;
                    int termFreq = freqs[j];
                    this.tvf.writeVInt(termFreq);
                    if (storePositions) {
                        int[] positions = tpVector.getTermPositions(j);
                        if (positions == null) {
                            throw new IllegalStateException("Trying to write positions that are null!");
                        }
                        assert (positions.length == termFreq);
                        int lastPosition = 0;
                        for (k = 0; k < positions.length; ++k) {
                            int position = positions[k];
                            this.tvf.writeVInt(position - lastPosition);
                            lastPosition = position;
                        }
                    }
                    if (!storeOffsets) continue;
                    TermVectorOffsetInfo[] offsets = tpVector.getOffsets(j);
                    if (offsets == null) {
                        throw new IllegalStateException("Trying to write offsets that are null!");
                    }
                    assert (offsets.length == termFreq);
                    int lastEndOffset = 0;
                    for (k = 0; k < offsets.length; ++k) {
                        int startOffset = offsets[k].getStartOffset();
                        int endOffset = offsets[k].getEndOffset();
                        this.tvf.writeVInt(startOffset - lastEndOffset);
                        this.tvf.writeVInt(endOffset - startOffset);
                        lastEndOffset = endOffset;
                    }
                }
            }
            if (numFields > 1) {
                long lastFieldPointer = fieldPointers[0];
                for (int i = 1; i < numFields; ++i) {
                    long fieldPointer = fieldPointers[i];
                    this.tvd.writeVLong(fieldPointer - lastFieldPointer);
                    lastFieldPointer = fieldPointer;
                }
            }
        } else {
            this.tvd.writeVInt(0);
        }
    }

    final void addRawDocuments(TermVectorsReader reader, int[] tvdLengths, int[] tvfLengths, int numDocs) throws IOException {
        long tvdPosition = this.tvd.getFilePointer();
        long tvfPosition = this.tvf.getFilePointer();
        long tvdStart = tvdPosition;
        long tvfStart = tvfPosition;
        for (int i = 0; i < numDocs; ++i) {
            this.tvx.writeLong(tvdPosition);
            tvdPosition += (long)tvdLengths[i];
            this.tvx.writeLong(tvfPosition);
            tvfPosition += (long)tvfLengths[i];
        }
        this.tvd.copyBytes(reader.getTvdStream(), tvdPosition - tvdStart);
        this.tvf.copyBytes(reader.getTvfStream(), tvfPosition - tvfStart);
        assert (this.tvd.getFilePointer() == tvdPosition);
        assert (this.tvf.getFilePointer() == tvfPosition);
    }

    void finish(int numDocs) throws IOException {
        if (4L + (long)numDocs * 16L != this.tvx.getFilePointer()) {
            String idxName = IndexFileNames.segmentFileName(this.segment, "tvx");
            throw new RuntimeException("tvx size mismatch: " + numDocs + " docs vs " + this.tvx.getFilePointer() + " length in bytes of " + idxName + " file exists?=" + this.directory.fileExists(idxName));
        }
    }

    final void close() throws IOException {
        IOUtils.close(this.tvx, this.tvd, this.tvf);
    }
}

