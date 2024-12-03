/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.attachments.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class BAAInputStream
extends InputStream {
    ArrayList data = new ArrayList();
    static final int BUFFER_SIZE = 4096;
    int i;
    int size;
    int currIndex;
    int totalIndex;
    int mark = 0;
    byte[] currBuffer = null;
    byte[] read_byte = new byte[1];

    public BAAInputStream(ArrayList data, int size) {
        this.data = data;
        this.size = size;
        this.i = 0;
        this.currIndex = 0;
        this.totalIndex = 0;
        this.currBuffer = (byte[])data.get(0);
    }

    public int read() throws IOException {
        int read = this.read(this.read_byte);
        if (read < 0) {
            return -1;
        }
        return this.read_byte[0] & 0xFF;
    }

    public int available() throws IOException {
        return this.size - this.totalIndex;
    }

    public synchronized void mark(int readlimit) {
        this.mark = this.totalIndex;
    }

    public boolean markSupported() {
        return true;
    }

    public int read(byte[] b, int off, int len) throws IOException {
        int total = 0;
        if (this.totalIndex >= this.size) {
            return -1;
        }
        while (total < len && this.totalIndex < this.size) {
            int copy = Math.min(len - total, 4096 - this.currIndex);
            copy = Math.min(copy, this.size - this.totalIndex);
            System.arraycopy(this.currBuffer, this.currIndex, b, off, copy);
            total += copy;
            this.currIndex += copy;
            this.totalIndex += copy;
            off += copy;
            if (this.currIndex < 4096) continue;
            if (this.i + 1 < this.data.size()) {
                this.currBuffer = (byte[])this.data.get(this.i + 1);
                ++this.i;
                this.currIndex = 0;
                continue;
            }
            this.currBuffer = null;
            this.currIndex = 4096;
        }
        return total;
    }

    public int read(byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }

    public synchronized void reset() throws IOException {
        this.i = this.mark / 4096;
        this.currIndex = this.mark - this.i * 4096;
        this.currBuffer = (byte[])this.data.get(this.i);
        this.totalIndex = this.mark;
    }

    public void writeTo(OutputStream os) throws IOException {
        if (this.data != null) {
            int numBuffers = this.data.size();
            for (int j = 0; j < numBuffers - 1; ++j) {
                os.write((byte[])this.data.get(j), 0, 4096);
            }
            if (numBuffers > 0) {
                int writeLimit = this.size - (numBuffers - 1) * 4096;
                os.write((byte[])this.data.get(numBuffers - 1), 0, writeLimit);
            }
        }
    }
}

