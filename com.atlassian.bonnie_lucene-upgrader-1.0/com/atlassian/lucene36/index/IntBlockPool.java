/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.DocumentsWriter;

final class IntBlockPool {
    public int[][] buffers = new int[10][];
    int bufferUpto = -1;
    public int intUpto = 8192;
    public int[] buffer;
    public int intOffset = -8192;
    private final DocumentsWriter docWriter;

    public IntBlockPool(DocumentsWriter docWriter) {
        this.docWriter = docWriter;
    }

    public void reset() {
        if (this.bufferUpto != -1) {
            if (this.bufferUpto > 0) {
                this.docWriter.recycleIntBlocks(this.buffers, 1, 1 + this.bufferUpto);
            }
            this.bufferUpto = 0;
            this.intUpto = 0;
            this.intOffset = 0;
            this.buffer = this.buffers[0];
        }
    }

    public void nextBuffer() {
        if (1 + this.bufferUpto == this.buffers.length) {
            int[][] newBuffers = new int[(int)((double)this.buffers.length * 1.5)][];
            System.arraycopy(this.buffers, 0, newBuffers, 0, this.buffers.length);
            this.buffers = newBuffers;
        }
        int[] nArray = this.docWriter.getIntBlock();
        this.buffers[1 + this.bufferUpto] = nArray;
        this.buffer = nArray;
        ++this.bufferUpto;
        this.intUpto = 0;
        this.intOffset += 8192;
    }
}

