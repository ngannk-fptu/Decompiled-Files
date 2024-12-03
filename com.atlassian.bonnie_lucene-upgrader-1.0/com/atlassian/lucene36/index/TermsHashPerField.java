/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.analysis.tokenattributes.CharTermAttribute;
import com.atlassian.lucene36.document.Fieldable;
import com.atlassian.lucene36.index.ByteBlockPool;
import com.atlassian.lucene36.index.ByteSliceReader;
import com.atlassian.lucene36.index.CharBlockPool;
import com.atlassian.lucene36.index.DocInverterPerField;
import com.atlassian.lucene36.index.DocumentsWriter;
import com.atlassian.lucene36.index.FieldInfo;
import com.atlassian.lucene36.index.FieldInvertState;
import com.atlassian.lucene36.index.IntBlockPool;
import com.atlassian.lucene36.index.InvertedDocConsumerPerField;
import com.atlassian.lucene36.index.ParallelPostingsArray;
import com.atlassian.lucene36.index.TermsHashConsumerPerField;
import com.atlassian.lucene36.index.TermsHashPerThread;
import com.atlassian.lucene36.util.SorterTemplate;
import java.io.IOException;
import java.util.Arrays;

final class TermsHashPerField
extends InvertedDocConsumerPerField {
    final TermsHashConsumerPerField consumer;
    final TermsHashPerField nextPerField;
    final TermsHashPerThread perThread;
    final DocumentsWriter.DocState docState;
    final FieldInvertState fieldState;
    CharTermAttribute termAtt;
    final CharBlockPool charPool;
    final IntBlockPool intPool;
    final ByteBlockPool bytePool;
    final int streamCount;
    final int numPostingInt;
    final FieldInfo fieldInfo;
    boolean postingsCompacted;
    int numPostings;
    private int postingsHashSize = 4;
    private int postingsHashHalfSize = this.postingsHashSize / 2;
    private int postingsHashMask = this.postingsHashSize - 1;
    private int[] postingsHash;
    ParallelPostingsArray postingsArray;
    private boolean doCall;
    private boolean doNextCall;
    int[] intUptos;
    int intUptoStart;

    public TermsHashPerField(DocInverterPerField docInverterPerField, TermsHashPerThread perThread, TermsHashPerThread nextPerThread, FieldInfo fieldInfo) {
        this.perThread = perThread;
        this.intPool = perThread.intPool;
        this.charPool = perThread.charPool;
        this.bytePool = perThread.bytePool;
        this.docState = perThread.docState;
        this.postingsHash = new int[this.postingsHashSize];
        Arrays.fill(this.postingsHash, -1);
        this.bytesUsed(this.postingsHashSize * 4);
        this.fieldState = docInverterPerField.fieldState;
        this.consumer = perThread.consumer.addField(this, fieldInfo);
        this.initPostingsArray();
        this.streamCount = this.consumer.getStreamCount();
        this.numPostingInt = 2 * this.streamCount;
        this.fieldInfo = fieldInfo;
        this.nextPerField = nextPerThread != null ? (TermsHashPerField)nextPerThread.addField(docInverterPerField, fieldInfo) : null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void close() {
        if (this.perThread.termsHash.trackAllocations) {
            try {
                if (this.postingsHash != null) {
                    this.bytesUsed(-this.postingsHash.length * 4);
                    this.postingsHash = null;
                }
                if (this.postingsArray != null) {
                    this.bytesUsed(-this.postingsArray.bytesPerPosting() * this.postingsArray.size);
                    this.postingsArray = null;
                }
                Object var2_1 = null;
                if (this.nextPerField != null) {
                    this.nextPerField.close();
                }
            }
            catch (Throwable throwable) {
                Object var2_2 = null;
                if (this.nextPerField != null) {
                    this.nextPerField.close();
                }
                throw throwable;
            }
        }
    }

    private void initPostingsArray() {
        this.postingsArray = this.consumer.createPostingsArray(2);
        this.bytesUsed(this.postingsArray.size * this.postingsArray.bytesPerPosting());
    }

    private void bytesUsed(long size) {
        if (this.perThread.termsHash.trackAllocations) {
            this.perThread.termsHash.docWriter.bytesUsed(size);
        }
    }

    void shrinkHash(int targetSize) {
        assert (this.postingsCompacted || this.numPostings == 0);
        int newSize = 4;
        if (4 != this.postingsHash.length) {
            long previousSize = this.postingsHash.length;
            this.postingsHash = new int[4];
            this.bytesUsed((4L - previousSize) * 4L);
            Arrays.fill(this.postingsHash, -1);
            this.postingsHashSize = 4;
            this.postingsHashHalfSize = 2;
            this.postingsHashMask = 3;
        }
        if (this.postingsArray != null) {
            this.bytesUsed(-this.postingsArray.bytesPerPosting() * this.postingsArray.size);
            this.postingsArray = null;
        }
    }

    public void reset() {
        if (!this.postingsCompacted) {
            this.compactPostings();
        }
        assert (this.numPostings <= this.postingsHash.length);
        if (this.numPostings > 0) {
            Arrays.fill(this.postingsHash, 0, this.numPostings, -1);
            this.numPostings = 0;
        }
        this.postingsCompacted = false;
        if (this.nextPerField != null) {
            this.nextPerField.reset();
        }
    }

    public synchronized void abort() {
        this.reset();
        if (this.nextPerField != null) {
            this.nextPerField.abort();
        }
    }

    private final void growParallelPostingsArray() {
        int oldSize = this.postingsArray.size;
        this.postingsArray = this.postingsArray.grow();
        this.bytesUsed(this.postingsArray.bytesPerPosting() * (this.postingsArray.size - oldSize));
    }

    public void initReader(ByteSliceReader reader, int termID, int stream) {
        assert (stream < this.streamCount);
        int intStart = this.postingsArray.intStarts[termID];
        int[] ints = this.intPool.buffers[intStart >> 13];
        int upto = intStart & 0x1FFF;
        reader.init(this.bytePool, this.postingsArray.byteStarts[termID] + stream * ByteBlockPool.FIRST_LEVEL_SIZE, ints[upto + stream]);
    }

    private void compactPostings() {
        int upto = 0;
        for (int i = 0; i < this.postingsHashSize; ++i) {
            if (this.postingsHash[i] == -1) continue;
            if (upto < i) {
                this.postingsHash[upto] = this.postingsHash[i];
                this.postingsHash[i] = -1;
            }
            ++upto;
        }
        assert (upto == this.numPostings) : "upto=" + upto + " numPostings=" + this.numPostings;
        this.postingsCompacted = true;
    }

    public int[] sortPostings() {
        this.compactPostings();
        final int[] postingsHash = this.postingsHash;
        new SorterTemplate(){
            private int pivotTerm;
            private int pivotBufPos;
            private char[] pivotBuf;

            protected void swap(int i, int j) {
                int o = postingsHash[i];
                postingsHash[i] = postingsHash[j];
                postingsHash[j] = o;
            }

            protected int compare(int i, int j) {
                int term1 = postingsHash[i];
                int term2 = postingsHash[j];
                if (term1 == term2) {
                    return 0;
                }
                int textStart1 = TermsHashPerField.this.postingsArray.textStarts[term1];
                int textStart2 = TermsHashPerField.this.postingsArray.textStarts[term2];
                char[] text1 = TermsHashPerField.this.charPool.buffers[textStart1 >> 14];
                int pos1 = textStart1 & 0x3FFF;
                char[] text2 = TermsHashPerField.this.charPool.buffers[textStart2 >> 14];
                int pos2 = textStart2 & 0x3FFF;
                return this.comparePostings(text1, pos1, text2, pos2);
            }

            protected void setPivot(int i) {
                this.pivotTerm = postingsHash[i];
                int textStart = TermsHashPerField.this.postingsArray.textStarts[this.pivotTerm];
                this.pivotBuf = TermsHashPerField.this.charPool.buffers[textStart >> 14];
                this.pivotBufPos = textStart & 0x3FFF;
            }

            protected int comparePivot(int j) {
                int term = postingsHash[j];
                if (this.pivotTerm == term) {
                    return 0;
                }
                int textStart = TermsHashPerField.this.postingsArray.textStarts[term];
                char[] text = TermsHashPerField.this.charPool.buffers[textStart >> 14];
                int pos = textStart & 0x3FFF;
                return this.comparePostings(this.pivotBuf, this.pivotBufPos, text, pos);
            }

            private int comparePostings(char[] text1, int pos1, char[] text2, int pos2) {
                char c1;
                assert (text1 != text2 || pos1 != pos2);
                while (true) {
                    char c2;
                    if ((c1 = text1[pos1++]) == (c2 = text2[pos2++])) continue;
                    if ('\uffff' == c2) {
                        return 1;
                    }
                    if ('\uffff' == c1) {
                        return -1;
                    }
                    return c1 - c2;
                    assert (c1 != '\uffff');
                }
            }
        }.quickSort(0, this.numPostings - 1);
        return postingsHash;
    }

    private boolean postingEquals(int termID, char[] tokenText, int tokenTextLen) {
        int textStart = this.postingsArray.textStarts[termID];
        char[] text = this.perThread.charPool.buffers[textStart >> 14];
        assert (text != null);
        int pos = textStart & 0x3FFF;
        for (int tokenPos = 0; tokenPos < tokenTextLen; ++tokenPos) {
            if (tokenText[tokenPos] != text[pos]) {
                return false;
            }
            ++pos;
        }
        return '\uffff' == text[pos];
    }

    void start(Fieldable f) {
        this.termAtt = this.fieldState.attributeSource.addAttribute(CharTermAttribute.class);
        this.consumer.start(f);
        if (this.nextPerField != null) {
            this.nextPerField.start(f);
        }
    }

    boolean start(Fieldable[] fields, int count) throws IOException {
        this.doCall = this.consumer.start(fields, count);
        if (this.postingsArray == null) {
            this.initPostingsArray();
        }
        if (this.nextPerField != null) {
            this.doNextCall = this.nextPerField.start(fields, count);
        }
        return this.doCall || this.doNextCall;
    }

    public void add(int textStart) throws IOException {
        int code = textStart;
        int hashPos = code & this.postingsHashMask;
        assert (!this.postingsCompacted);
        int termID = this.postingsHash[hashPos];
        if (termID != -1 && this.postingsArray.textStarts[termID] != textStart) {
            int inc = (code >> 8) + code | 1;
            while ((termID = this.postingsHash[hashPos = (code += inc) & this.postingsHashMask]) != -1 && this.postingsArray.textStarts[termID] != textStart) {
            }
        }
        if (termID == -1) {
            if ((termID = this.numPostings++) >= this.postingsArray.size) {
                this.growParallelPostingsArray();
            }
            assert (termID >= 0);
            this.postingsArray.textStarts[termID] = textStart;
            assert (this.postingsHash[hashPos] == -1);
            this.postingsHash[hashPos] = termID;
            if (this.numPostings == this.postingsHashHalfSize) {
                this.rehashPostings(2 * this.postingsHashSize);
            }
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
            int intStart = this.postingsArray.intStarts[termID];
            this.intUptos = this.intPool.buffers[intStart >> 13];
            this.intUptoStart = intStart & 0x1FFF;
            this.consumer.addTerm(termID);
        }
    }

    void add() throws IOException {
        int tokenTextLen;
        assert (!this.postingsCompacted);
        char[] tokenText = this.termAtt.buffer();
        int downto = tokenTextLen = this.termAtt.length();
        int code = 0;
        while (downto > 0) {
            int ch;
            if ((ch = tokenText[--downto]) >= 56320 && ch <= 57343) {
                if (0 == downto) {
                    tokenText[downto] = 65533;
                    ch = 65533;
                } else {
                    char ch2 = tokenText[downto - 1];
                    if (ch2 >= '\ud800' && ch2 <= '\udbff') {
                        code = (code * 31 + ch) * 31 + ch2;
                        --downto;
                        continue;
                    }
                    tokenText[downto] = 65533;
                    ch = 65533;
                }
            } else if (ch >= 55296 && (ch <= 56319 || ch == 65535)) {
                tokenText[downto] = 65533;
                ch = 65533;
            }
            code = code * 31 + ch;
        }
        int hashPos = code & this.postingsHashMask;
        int termID = this.postingsHash[hashPos];
        if (termID != -1 && !this.postingEquals(termID, tokenText, tokenTextLen)) {
            int inc = (code >> 8) + code | 1;
            while ((termID = this.postingsHash[hashPos = (code += inc) & this.postingsHashMask]) != -1 && !this.postingEquals(termID, tokenText, tokenTextLen)) {
            }
        }
        if (termID == -1) {
            int textLen1 = 1 + tokenTextLen;
            if (textLen1 + this.charPool.charUpto > 16384) {
                if (textLen1 > 16384) {
                    if (this.docState.maxTermPrefix == null) {
                        this.docState.maxTermPrefix = new String(tokenText, 0, 30);
                    }
                    this.consumer.skippingLongTerm();
                    return;
                }
                this.charPool.nextBuffer();
            }
            if ((termID = this.numPostings++) >= this.postingsArray.size) {
                this.growParallelPostingsArray();
            }
            assert (termID != -1);
            char[] text = this.charPool.buffer;
            int textUpto = this.charPool.charUpto;
            this.postingsArray.textStarts[termID] = textUpto + this.charPool.charOffset;
            this.charPool.charUpto += textLen1;
            System.arraycopy(tokenText, 0, text, textUpto, tokenTextLen);
            text[textUpto + tokenTextLen] = 65535;
            assert (this.postingsHash[hashPos] == -1);
            this.postingsHash[hashPos] = termID;
            if (this.numPostings == this.postingsHashHalfSize) {
                this.rehashPostings(2 * this.postingsHashSize);
                this.bytesUsed(2 * this.numPostings * 4);
            }
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

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    void finish() throws IOException {
        try {
            this.consumer.finish();
            Object var2_1 = null;
            if (this.nextPerField == null) return;
        }
        catch (Throwable throwable) {
            Object var2_2 = null;
            if (this.nextPerField == null) throw throwable;
            this.nextPerField.finish();
            throw throwable;
        }
        this.nextPerField.finish();
    }

    void rehashPostings(int newSize) {
        int newMask = newSize - 1;
        int[] newHash = new int[newSize];
        Arrays.fill(newHash, -1);
        for (int i = 0; i < this.postingsHashSize; ++i) {
            int code;
            int termID = this.postingsHash[i];
            if (termID == -1) continue;
            if (this.perThread.primary) {
                int textStart = this.postingsArray.textStarts[termID];
                int start = textStart & 0x3FFF;
                char[] text = this.charPool.buffers[textStart >> 14];
                int pos = start;
                while (text[pos] != '\uffff') {
                    ++pos;
                }
                code = 0;
                while (pos > start) {
                    code = code * 31 + text[--pos];
                }
            } else {
                code = this.postingsArray.textStarts[termID];
            }
            int hashPos = code & newMask;
            assert (hashPos >= 0);
            if (newHash[hashPos] != -1) {
                int inc = (code >> 8) + code | 1;
                while (newHash[hashPos = (code += inc) & newMask] != -1) {
                }
            }
            newHash[hashPos] = termID;
        }
        this.postingsHashMask = newMask;
        this.postingsHash = newHash;
        this.postingsHashSize = newSize;
        this.postingsHashHalfSize = newSize >> 1;
    }
}

