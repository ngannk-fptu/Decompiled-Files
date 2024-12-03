/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.IOException;
import java.util.Comparator;
import org.apache.lucene.analysis.tokenattributes.TermToBytesRefAttribute;
import org.apache.lucene.index.ByteSliceReader;
import org.apache.lucene.index.DocInverterPerField;
import org.apache.lucene.index.DocumentsWriterPerThread;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.FieldInvertState;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.InvertedDocConsumerPerField;
import org.apache.lucene.index.ParallelPostingsArray;
import org.apache.lucene.index.TermsHash;
import org.apache.lucene.index.TermsHashConsumerPerField;
import org.apache.lucene.util.ByteBlockPool;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.BytesRefHash;
import org.apache.lucene.util.Counter;
import org.apache.lucene.util.IntBlockPool;

final class TermsHashPerField
extends InvertedDocConsumerPerField {
    private static final int HASH_INIT_SIZE = 4;
    final TermsHashConsumerPerField consumer;
    final TermsHash termsHash;
    final TermsHashPerField nextPerField;
    final DocumentsWriterPerThread.DocState docState;
    final FieldInvertState fieldState;
    TermToBytesRefAttribute termAtt;
    BytesRef termBytesRef;
    final IntBlockPool intPool;
    final ByteBlockPool bytePool;
    final ByteBlockPool termBytePool;
    final int streamCount;
    final int numPostingInt;
    final FieldInfo fieldInfo;
    final BytesRefHash bytesHash;
    ParallelPostingsArray postingsArray;
    private final Counter bytesUsed;
    private boolean doCall;
    private boolean doNextCall;
    int[] intUptos;
    int intUptoStart;

    public TermsHashPerField(DocInverterPerField docInverterPerField, TermsHash termsHash, TermsHash nextTermsHash, FieldInfo fieldInfo) {
        this.intPool = termsHash.intPool;
        this.bytePool = termsHash.bytePool;
        this.termBytePool = termsHash.termBytePool;
        this.docState = termsHash.docState;
        this.termsHash = termsHash;
        this.bytesUsed = termsHash.bytesUsed;
        this.fieldState = docInverterPerField.fieldState;
        this.consumer = termsHash.consumer.addField(this, fieldInfo);
        PostingsBytesStartArray byteStarts = new PostingsBytesStartArray(this, this.bytesUsed);
        this.bytesHash = new BytesRefHash(this.termBytePool, 4, byteStarts);
        this.streamCount = this.consumer.getStreamCount();
        this.numPostingInt = 2 * this.streamCount;
        this.fieldInfo = fieldInfo;
        this.nextPerField = nextTermsHash != null ? (TermsHashPerField)nextTermsHash.addField(docInverterPerField, fieldInfo) : null;
    }

    void shrinkHash(int targetSize) {
        this.bytesHash.clear(false);
    }

    public void reset() {
        this.bytesHash.clear(false);
        if (this.nextPerField != null) {
            this.nextPerField.reset();
        }
    }

    @Override
    public void abort() {
        this.reset();
        if (this.nextPerField != null) {
            this.nextPerField.abort();
        }
    }

    public void initReader(ByteSliceReader reader, int termID, int stream) {
        assert (stream < this.streamCount);
        int intStart = this.postingsArray.intStarts[termID];
        int[] ints = this.intPool.buffers[intStart >> 13];
        int upto = intStart & 0x1FFF;
        reader.init(this.bytePool, this.postingsArray.byteStarts[termID] + stream * ByteBlockPool.FIRST_LEVEL_SIZE, ints[upto + stream]);
    }

    public int[] sortPostings(Comparator<BytesRef> termComp) {
        return this.bytesHash.sort(termComp);
    }

    @Override
    void start(IndexableField f) {
        this.termAtt = this.fieldState.attributeSource.getAttribute(TermToBytesRefAttribute.class);
        this.termBytesRef = this.termAtt.getBytesRef();
        this.consumer.start(f);
        if (this.nextPerField != null) {
            this.nextPerField.start(f);
        }
    }

    @Override
    boolean start(IndexableField[] fields, int count) throws IOException {
        this.doCall = this.consumer.start(fields, count);
        this.bytesHash.reinit();
        if (this.nextPerField != null) {
            this.doNextCall = this.nextPerField.start(fields, count);
        }
        return this.doCall || this.doNextCall;
    }

    public void add(int textStart) throws IOException {
        int termID = this.bytesHash.addByPoolOffset(textStart);
        if (termID >= 0) {
            if (this.numPostingInt + this.intPool.intUpto > 8192) {
                this.intPool.nextBuffer();
            }
            if (32768 - this.bytePool.byteUpto < this.numPostingInt * ByteBlockPool.FIRST_LEVEL_SIZE) {
                this.bytePool.nextBuffer();
            }
            this.intUptos = this.intPool.buffer;
            this.intUptoStart = this.intPool.intUpto;
            this.intPool.intUpto += this.streamCount;
            this.postingsArray.intStarts[termID] = this.intUptoStart + this.intPool.intOffset;
            for (int i = 0; i < this.streamCount; ++i) {
                int upto = this.bytePool.newSlice(ByteBlockPool.FIRST_LEVEL_SIZE);
                this.intUptos[this.intUptoStart + i] = upto + this.bytePool.byteOffset;
            }
            this.postingsArray.byteStarts[termID] = this.intUptos[this.intUptoStart];
            this.consumer.newTerm(termID);
        } else {
            termID = -termID - 1;
            int intStart = this.postingsArray.intStarts[termID];
            this.intUptos = this.intPool.buffers[intStart >> 13];
            this.intUptoStart = intStart & 0x1FFF;
            this.consumer.addTerm(termID);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    void add() throws IOException {
        int termID;
        try {
            termID = this.bytesHash.add(this.termBytesRef, this.termAtt.fillBytesRef());
        }
        catch (BytesRefHash.MaxBytesLengthExceededException e) {
            if (this.docState.maxTermPrefix == null) {
                int saved = this.termBytesRef.length;
                try {
                    this.termBytesRef.length = Math.min(30, 32766);
                    this.docState.maxTermPrefix = this.termBytesRef.toString();
                }
                finally {
                    this.termBytesRef.length = saved;
                }
            }
            this.consumer.skippingLongTerm();
            return;
        }
        if (termID >= 0) {
            this.bytesHash.byteStart(termID);
            if (this.numPostingInt + this.intPool.intUpto > 8192) {
                this.intPool.nextBuffer();
            }
            if (32768 - this.bytePool.byteUpto < this.numPostingInt * ByteBlockPool.FIRST_LEVEL_SIZE) {
                this.bytePool.nextBuffer();
            }
            this.intUptos = this.intPool.buffer;
            this.intUptoStart = this.intPool.intUpto;
            this.intPool.intUpto += this.streamCount;
            this.postingsArray.intStarts[termID] = this.intUptoStart + this.intPool.intOffset;
            for (int i = 0; i < this.streamCount; ++i) {
                int upto = this.bytePool.newSlice(ByteBlockPool.FIRST_LEVEL_SIZE);
                this.intUptos[this.intUptoStart + i] = upto + this.bytePool.byteOffset;
            }
            this.postingsArray.byteStarts[termID] = this.intUptos[this.intUptoStart];
            this.consumer.newTerm(termID);
        } else {
            termID = -termID - 1;
            int intStart = this.postingsArray.intStarts[termID];
            this.intUptos = this.intPool.buffers[intStart >> 13];
            this.intUptoStart = intStart & 0x1FFF;
            this.consumer.addTerm(termID);
        }
        if (this.doNextCall) {
            this.nextPerField.add(this.postingsArray.textStarts[termID]);
        }
    }

    void writeByte(int stream, byte b) {
        int upto = this.intUptos[this.intUptoStart + stream];
        byte[] bytes = this.bytePool.buffers[upto >> 15];
        assert (bytes != null);
        int offset = upto & Short.MAX_VALUE;
        if (bytes[offset] != 0) {
            offset = this.bytePool.allocSlice(bytes, offset);
            bytes = this.bytePool.buffer;
            this.intUptos[this.intUptoStart + stream] = offset + this.bytePool.byteOffset;
        }
        bytes[offset] = b;
        int n = this.intUptoStart + stream;
        this.intUptos[n] = this.intUptos[n] + 1;
    }

    public void writeBytes(int stream, byte[] b, int offset, int len) {
        int end = offset + len;
        for (int i = offset; i < end; ++i) {
            this.writeByte(stream, b[i]);
        }
    }

    void writeVInt(int stream, int i) {
        assert (stream < this.streamCount);
        while ((i & 0xFFFFFF80) != 0) {
            this.writeByte(stream, (byte)(i & 0x7F | 0x80));
            i >>>= 7;
        }
        this.writeByte(stream, (byte)i);
    }

    @Override
    void finish() throws IOException {
        this.consumer.finish();
        if (this.nextPerField != null) {
            this.nextPerField.finish();
        }
    }

    private static final class PostingsBytesStartArray
    extends BytesRefHash.BytesStartArray {
        private final TermsHashPerField perField;
        private final Counter bytesUsed;

        private PostingsBytesStartArray(TermsHashPerField perField, Counter bytesUsed) {
            this.perField = perField;
            this.bytesUsed = bytesUsed;
        }

        @Override
        public int[] init() {
            if (this.perField.postingsArray == null) {
                this.perField.postingsArray = this.perField.consumer.createPostingsArray(2);
                this.bytesUsed.addAndGet(this.perField.postingsArray.size * this.perField.postingsArray.bytesPerPosting());
            }
            return this.perField.postingsArray.textStarts;
        }

        @Override
        public int[] grow() {
            ParallelPostingsArray postingsArray = this.perField.postingsArray;
            int oldSize = this.perField.postingsArray.size;
            postingsArray = this.perField.postingsArray = postingsArray.grow();
            this.bytesUsed.addAndGet(postingsArray.bytesPerPosting() * (postingsArray.size - oldSize));
            return postingsArray.textStarts;
        }

        @Override
        public int[] clear() {
            if (this.perField.postingsArray != null) {
                this.bytesUsed.addAndGet(-(this.perField.postingsArray.size * this.perField.postingsArray.bytesPerPosting()));
                this.perField.postingsArray = null;
            }
            return null;
        }

        @Override
        public Counter bytesUsed() {
            return this.bytesUsed;
        }
    }
}

