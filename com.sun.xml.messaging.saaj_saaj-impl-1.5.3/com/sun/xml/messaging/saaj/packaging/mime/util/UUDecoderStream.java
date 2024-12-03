/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.messaging.saaj.packaging.mime.util;

import com.sun.xml.messaging.saaj.packaging.mime.util.LineInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class UUDecoderStream
extends FilterInputStream {
    private String name;
    private int mode;
    private byte[] buffer;
    private int bufsize = 0;
    private int index = 0;
    private boolean gotPrefix = false;
    private boolean gotEnd = false;
    private LineInputStream lin;

    public UUDecoderStream(InputStream in) {
        super(in);
        this.lin = new LineInputStream(in);
        this.buffer = new byte[45];
    }

    @Override
    public int read() throws IOException {
        if (this.index >= this.bufsize) {
            this.readPrefix();
            if (!this.decode()) {
                return -1;
            }
            this.index = 0;
        }
        return this.buffer[this.index++] & 0xFF;
    }

    @Override
    public int read(byte[] buf, int off, int len) throws IOException {
        int i;
        for (i = 0; i < len; ++i) {
            int c = this.read();
            if (c == -1) {
                if (i != 0) break;
                i = -1;
                break;
            }
            buf[off + i] = (byte)c;
        }
        return i;
    }

    @Override
    public boolean markSupported() {
        return false;
    }

    @Override
    public int available() throws IOException {
        return this.in.available() * 3 / 4 + (this.bufsize - this.index);
    }

    public String getName() throws IOException {
        this.readPrefix();
        return this.name;
    }

    public int getMode() throws IOException {
        this.readPrefix();
        return this.mode;
    }

    private void readPrefix() throws IOException {
        String s;
        if (this.gotPrefix) {
            return;
        }
        do {
            if ((s = this.lin.readLine()) != null) continue;
            throw new IOException("UUDecoder error: No Begin");
        } while (!s.regionMatches(true, 0, "begin", 0, 5));
        try {
            this.mode = Integer.parseInt(s.substring(6, 9));
        }
        catch (NumberFormatException ex) {
            throw new IOException("UUDecoder error: " + ex.toString());
        }
        this.name = s.substring(10);
        this.gotPrefix = true;
    }

    private boolean decode() throws IOException {
        String line;
        if (this.gotEnd) {
            return false;
        }
        this.bufsize = 0;
        do {
            if ((line = this.lin.readLine()) == null) {
                throw new IOException("Missing End");
            }
            if (!line.regionMatches(true, 0, "end", 0, 3)) continue;
            this.gotEnd = true;
            return false;
        } while (line.length() == 0);
        int count = line.charAt(0);
        if (count < 32) {
            throw new IOException("Buffer format error");
        }
        if ((count = count - 32 & 0x3F) == 0) {
            line = this.lin.readLine();
            if (line == null || !line.regionMatches(true, 0, "end", 0, 3)) {
                throw new IOException("Missing End");
            }
            this.gotEnd = true;
            return false;
        }
        int need = (count * 8 + 5) / 6;
        if (line.length() < need + 1) {
            throw new IOException("Short buffer error");
        }
        int i = 1;
        while (this.bufsize < count) {
            byte a = (byte)(line.charAt(i++) - 32 & 0x3F);
            byte b = (byte)(line.charAt(i++) - 32 & 0x3F);
            this.buffer[this.bufsize++] = (byte)(a << 2 & 0xFC | b >>> 4 & 3);
            if (this.bufsize < count) {
                a = b;
                b = (byte)(line.charAt(i++) - 32 & 0x3F);
                this.buffer[this.bufsize++] = (byte)(a << 4 & 0xF0 | b >>> 2 & 0xF);
            }
            if (this.bufsize >= count) continue;
            a = b;
            b = (byte)(line.charAt(i++) - 32 & 0x3F);
            this.buffer[this.bufsize++] = (byte)(a << 6 & 0xC0 | b & 0x3F);
        }
        return true;
    }
}

