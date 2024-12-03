/*
 * Decompiled with CFR 0.152.
 */
package net.sourceforge.jtds.ssl;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

class TdsTlsOutputStream
extends FilterOutputStream {
    private final List bufferedRecords = new ArrayList();
    private int totalSize;

    TdsTlsOutputStream(OutputStream out) {
        super(out);
    }

    private void deferRecord(byte[] record, int len) {
        byte[] tmp = new byte[len];
        System.arraycopy(record, 0, tmp, 0, len);
        this.bufferedRecords.add(tmp);
        this.totalSize += len;
    }

    private void flushBufferedRecords() throws IOException {
        byte[] tmp = new byte[this.totalSize];
        int off = 0;
        for (int i = 0; i < this.bufferedRecords.size(); ++i) {
            byte[] x = (byte[])this.bufferedRecords.get(i);
            System.arraycopy(x, 0, tmp, off, x.length);
            off += x.length;
        }
        this.putTdsPacket(tmp, off);
        this.bufferedRecords.clear();
        this.totalSize = 0;
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        if (len < 5 || off > 0) {
            this.out.write(b, off, len);
            return;
        }
        int contentType = b[0] & 0xFF;
        int length = (b[3] & 0xFF) << 8 | b[4] & 0xFF;
        if (contentType < 20 || contentType > 23 || length != len - 5) {
            this.putTdsPacket(b, len);
            return;
        }
        switch (contentType) {
            case 23: {
                this.out.write(b, off, len);
                break;
            }
            case 20: {
                this.deferRecord(b, len);
                break;
            }
            case 21: {
                break;
            }
            case 22: {
                if (len >= 9) {
                    byte hsType = b[5];
                    int hsLen = (b[6] & 0xFF) << 16 | (b[7] & 0xFF) << 8 | b[8] & 0xFF;
                    if (hsLen == len - 9 && hsType == 1) {
                        this.putTdsPacket(b, len);
                        break;
                    }
                    this.deferRecord(b, len);
                    if (hsLen == len - 9 && hsType == 16) break;
                    this.flushBufferedRecords();
                    break;
                }
            }
            default: {
                this.out.write(b, off, len);
            }
        }
    }

    void putTdsPacket(byte[] b, int len) throws IOException {
        byte[] tdsHdr = new byte[8];
        tdsHdr[0] = 18;
        tdsHdr[1] = 1;
        tdsHdr[2] = (byte)(len + 8 >> 8);
        tdsHdr[3] = (byte)(len + 8);
        this.out.write(tdsHdr, 0, tdsHdr.length);
        this.out.write(b, 0, len);
    }

    @Override
    public void flush() throws IOException {
        super.flush();
    }
}

