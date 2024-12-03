/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.DocumentsWriter;
import com.atlassian.lucene36.util.ArrayUtil;
import com.atlassian.lucene36.util.RamUsageEstimator;

final class CharBlockPool {
    public char[][] buffers = new char[10][];
    int numBuffer;
    int bufferUpto = -1;
    public int charUpto = 16384;
    public char[] buffer;
    public int charOffset = -16384;
    private final DocumentsWriter docWriter;

    public CharBlockPool(DocumentsWriter docWriter) {
        this.docWriter = docWriter;
    }

    public void reset() {
        this.docWriter.recycleCharBlocks(this.buffers, 1 + this.bufferUpto);
        this.bufferUpto = -1;
        this.charUpto = 16384;
        this.charOffset = -16384;
    }

    public void nextBuffer() {
        if (1 + this.bufferUpto == this.buffers.length) {
            char[][] newBuffers = new char[ArrayUtil.oversize(this.buffers.length + 1, RamUsageEstimator.NUM_BYTES_OBJECT_REF)][];
            System.arraycopy(this.buffers, 0, newBuffers, 0, this.buffers.length);
            this.buffers = newBuffers;
        }
        char[] cArray = this.docWriter.getCharBlock();
        this.buffers[1 + this.bufferUpto] = cArray;
        this.buffer = cArray;
        ++this.bufferUpto;
        this.charUpto = 0;
        this.charOffset += 16384;
    }
}

