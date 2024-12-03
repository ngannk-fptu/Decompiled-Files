/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axiom.om.util;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.commons.logging.Log;

public class LogOutputStream
extends OutputStream {
    private byte[] temp = new byte[1];
    private boolean isDebugEnabled = false;
    private long count = 0L;
    private Log log;
    private int BUFFER_LEN = 4096;
    private byte[] buffer = new byte[this.BUFFER_LEN];
    private int bufferIndex = 0;
    private int limit;

    public LogOutputStream(Log log, int limit) {
        this.isDebugEnabled = log.isDebugEnabled();
        this.log = log;
        this.limit = limit;
    }

    public long getLength() {
        return this.count;
    }

    public void close() throws IOException {
        if (this.bufferIndex > 0) {
            this.log.debug((Object)new String(this.buffer, 0, this.bufferIndex));
            this.bufferIndex = 0;
        }
        this.buffer = null;
        this.temp = null;
        this.log = null;
    }

    public void flush() throws IOException {
    }

    public void write(byte[] b, int off, int len) throws IOException {
        if (this.count >= (long)this.limit) {
            this.count += (long)len;
            return;
        }
        if (this.count + (long)len >= (long)this.limit) {
            this.count += (long)len;
            len = (int)((long)len - ((long)this.limit - this.count));
        } else {
            this.count += (long)len;
        }
        if (this.isDebugEnabled) {
            if (len + this.bufferIndex < this.BUFFER_LEN) {
                System.arraycopy(b, off, this.buffer, this.bufferIndex, len);
                this.bufferIndex += len;
            } else {
                if (this.bufferIndex > 0) {
                    this.log.debug((Object)new String(this.buffer, 0, this.bufferIndex));
                    this.bufferIndex = 0;
                }
                if (len + this.bufferIndex < this.BUFFER_LEN) {
                    System.arraycopy(b, off, this.buffer, this.bufferIndex, len);
                    this.bufferIndex += len;
                } else {
                    this.log.debug((Object)new String(b, off, len));
                }
            }
        }
    }

    public void write(byte[] b) throws IOException {
        this.write(b, 0, b.length);
    }

    public void write(int b) throws IOException {
        this.temp[0] = (byte)b;
        this.write(this.temp, 0, 1);
    }
}

