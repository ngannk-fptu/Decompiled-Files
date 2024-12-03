/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.util.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.xmlgraphics.util.io.ASCII85Constants;
import org.apache.xmlgraphics.util.io.Finalizable;

public class ASCII85OutputStream
extends FilterOutputStream
implements ASCII85Constants,
Finalizable {
    private static final boolean DEBUG = false;
    private int pos;
    private long buffer;
    private int posinline;
    private int bw;

    public ASCII85OutputStream(OutputStream out) {
        super(out);
    }

    @Override
    public void write(int b) throws IOException {
        this.buffer = this.pos == 0 ? (this.buffer += (long)(b << 24) & 0xFF000000L) : (this.pos == 1 ? (this.buffer += (long)(b << 16) & 0xFF0000L) : (this.pos == 2 ? (this.buffer += (long)(b << 8) & 0xFF00L) : (this.buffer += (long)b & 0xFFL)));
        ++this.pos;
        if (this.pos > 3) {
            this.checkedWrite(this.convertWord(this.buffer));
            this.buffer = 0L;
            this.pos = 0;
        }
    }

    private void checkedWrite(byte[] buf) throws IOException {
        this.checkedWrite(buf, buf.length, false);
    }

    private void checkedWrite(byte[] buf, boolean nosplit) throws IOException {
        this.checkedWrite(buf, buf.length, nosplit);
    }

    private void checkedWrite(byte[] buf, int len) throws IOException {
        this.checkedWrite(buf, len, false);
    }

    private void checkedWrite(byte[] buf, int len, boolean nosplit) throws IOException {
        if (this.posinline + len > 80) {
            int firstpart;
            int n = firstpart = nosplit ? 0 : len - (this.posinline + len - 80);
            if (firstpart > 0) {
                this.out.write(buf, 0, firstpart);
            }
            this.out.write(10);
            ++this.bw;
            int rest = len - firstpart;
            if (rest > 0) {
                this.out.write(buf, firstpart, rest);
            }
            this.posinline = rest;
        } else {
            this.out.write(buf, 0, len);
            this.posinline += len;
        }
        this.bw += len;
    }

    private byte[] convertWord(long word) {
        if ((word &= 0xFFFFFFFFFFFFFFFFL) == 0L) {
            return ZERO_ARRAY;
        }
        if (word < 0L) {
            word = -word;
        }
        byte c1 = (byte)(word / POW85[0] & 0xFFL);
        byte c2 = (byte)((word - (long)c1 * POW85[0]) / POW85[1] & 0xFFL);
        byte c3 = (byte)((word - (long)c1 * POW85[0] - (long)c2 * POW85[1]) / POW85[2] & 0xFFL);
        byte c4 = (byte)((word - (long)c1 * POW85[0] - (long)c2 * POW85[1] - (long)c3 * POW85[2]) / POW85[3] & 0xFFL);
        byte c5 = (byte)(word - (long)c1 * POW85[0] - (long)c2 * POW85[1] - (long)c3 * POW85[2] - (long)c4 * POW85[3] & 0xFFL);
        byte[] ret = new byte[]{(byte)(c1 + 33), (byte)(c2 + 33), (byte)(c3 + 33), (byte)(c4 + 33), (byte)(c5 + 33)};
        return ret;
    }

    @Override
    public void finalizeStream() throws IOException {
        if (this.pos > 0) {
            byte[] conv;
            int rest = this.pos;
            if (this.buffer != 0L) {
                conv = this.convertWord(this.buffer);
            } else {
                conv = new byte[5];
                for (int j = 0; j < 5; ++j) {
                    conv[j] = 33;
                }
            }
            this.checkedWrite(conv, rest + 1);
        }
        this.checkedWrite(EOD, true);
        this.flush();
        if (this.out instanceof Finalizable) {
            ((Finalizable)((Object)this.out)).finalizeStream();
        }
    }

    @Override
    public void close() throws IOException {
        this.finalizeStream();
        super.close();
    }
}

