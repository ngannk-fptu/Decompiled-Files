/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.attachments.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import org.apache.axiom.ext.io.ReadFromSupport;
import org.apache.axiom.ext.io.StreamCopyException;

public class BAAOutputStream
extends OutputStream
implements ReadFromSupport {
    ArrayList data = new ArrayList();
    static final int BUFFER_SIZE = 4096;
    int index = 0;
    byte[] currBuffer = null;
    byte[] writeByte = new byte[1];

    public BAAOutputStream() {
        this.addBuffer();
    }

    private void addBuffer() {
        this.currBuffer = new byte[4096];
        this.data.add(this.currBuffer);
        this.index = 0;
    }

    public void write(byte[] b, int off, int len) throws IOException {
        int total = 0;
        while (total < len) {
            int copy = Math.min(len - total, 4096 - this.index);
            System.arraycopy(b, off, this.currBuffer, this.index, copy);
            total += copy;
            this.index += copy;
            off += copy;
            if (this.index < 4096) continue;
            this.addBuffer();
        }
    }

    public void write(byte[] b) throws IOException {
        this.write(b, 0, b.length);
    }

    public void write(int b) throws IOException {
        this.writeByte[0] = (byte)b;
        this.write(this.writeByte, 0, 1);
    }

    public ArrayList buffers() {
        return this.data;
    }

    public int length() {
        return 4096 * (this.data.size() - 1) + this.index;
    }

    public long receive(InputStream is, long maxRead) throws IOException {
        return this.readFrom(is, maxRead);
    }

    public long readFrom(InputStream is, long maxRead) throws StreamCopyException {
        if (maxRead == -1L) {
            maxRead = Long.MAX_VALUE;
        }
        long bytesReceived = 0L;
        boolean done = false;
        while (!done) {
            int bytesRead;
            int len = (int)Math.min((long)(4096 - this.index), maxRead - bytesReceived);
            try {
                bytesRead = is.read(this.currBuffer, this.index, len);
            }
            catch (IOException ex) {
                throw new StreamCopyException(1, ex);
            }
            if (bytesRead >= 0) {
                bytesReceived += (long)bytesRead;
                this.index += bytesRead;
                if (this.index >= 4096) {
                    this.addBuffer();
                }
                if (bytesReceived < maxRead) continue;
                done = true;
                continue;
            }
            done = true;
        }
        return bytesReceived;
    }
}

