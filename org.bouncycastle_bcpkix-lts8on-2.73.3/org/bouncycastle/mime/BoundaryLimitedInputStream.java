/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.util.Strings
 */
package org.bouncycastle.mime;

import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.util.Strings;

public class BoundaryLimitedInputStream
extends InputStream {
    private final InputStream src;
    private final byte[] boundary;
    private final byte[] buf;
    private int bufOff = 0;
    private int index = 0;
    private boolean ended = false;
    private int lastI;

    public BoundaryLimitedInputStream(InputStream src, String startBoundary) {
        this.src = src;
        this.boundary = Strings.toByteArray((String)startBoundary);
        this.buf = new byte[startBoundary.length() + 3];
        this.bufOff = 0;
    }

    @Override
    public int read() throws IOException {
        int i;
        if (this.ended) {
            return -1;
        }
        if (this.index < this.bufOff) {
            i = this.buf[this.index++] & 0xFF;
            if (this.index < this.bufOff) {
                return i;
            }
            this.bufOff = 0;
            this.index = 0;
        } else {
            i = this.src.read();
        }
        this.lastI = i;
        if (i < 0) {
            return -1;
        }
        if (i == 13 || i == 10) {
            int ch;
            this.index = 0;
            if (i == 13) {
                ch = this.src.read();
                if (ch == 10) {
                    this.buf[this.bufOff++] = 10;
                    ch = this.src.read();
                }
            } else {
                ch = this.src.read();
            }
            if (ch == 45) {
                this.buf[this.bufOff++] = 45;
                ch = this.src.read();
            }
            if (ch == 45) {
                int c;
                this.buf[this.bufOff++] = 45;
                int base = this.bufOff;
                while (this.bufOff - base != this.boundary.length && (c = this.src.read()) >= 0) {
                    this.buf[this.bufOff] = (byte)c;
                    if (this.buf[this.bufOff] != this.boundary[this.bufOff - base]) {
                        ++this.bufOff;
                        break;
                    }
                    ++this.bufOff;
                }
                if (this.bufOff - base == this.boundary.length) {
                    this.ended = true;
                    return -1;
                }
            } else if (ch >= 0) {
                this.buf[this.bufOff++] = (byte)ch;
            }
        }
        return i;
    }
}

