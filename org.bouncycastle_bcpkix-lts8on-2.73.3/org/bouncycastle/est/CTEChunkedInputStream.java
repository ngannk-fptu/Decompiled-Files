/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.est;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class CTEChunkedInputStream
extends InputStream {
    private InputStream src;
    int chunkLen = 0;

    public CTEChunkedInputStream(InputStream inputStream) {
        this.src = inputStream;
    }

    private String readEOL() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int c = 0;
        do {
            if ((c = this.src.read()) == -1) {
                if (bos.size() == 0) {
                    return null;
                }
                return bos.toString().trim();
            }
            bos.write(c & 0xFF);
        } while (c != 10);
        return bos.toString().trim();
    }

    @Override
    public int read() throws IOException {
        if (this.chunkLen == Integer.MIN_VALUE) {
            return -1;
        }
        if (this.chunkLen == 0) {
            String line = null;
            while ((line = this.readEOL()) != null && line.length() == 0) {
            }
            if (line == null) {
                return -1;
            }
            this.chunkLen = Integer.parseInt(line.trim(), 16);
            if (this.chunkLen == 0) {
                this.readEOL();
                this.chunkLen = Integer.MIN_VALUE;
                return -1;
            }
        }
        int i = this.src.read();
        --this.chunkLen;
        return i;
    }
}

