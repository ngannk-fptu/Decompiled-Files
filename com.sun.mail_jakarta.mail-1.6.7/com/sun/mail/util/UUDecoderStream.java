/*
 * Decompiled with CFR 0.152.
 */
package com.sun.mail.util;

import com.sun.mail.util.DecodingException;
import com.sun.mail.util.LineInputStream;
import com.sun.mail.util.PropUtil;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class UUDecoderStream
extends FilterInputStream {
    private String name;
    private int mode;
    private byte[] buffer = new byte[45];
    private int bufsize = 0;
    private int index = 0;
    private boolean gotPrefix = false;
    private boolean gotEnd = false;
    private LineInputStream lin;
    private boolean ignoreErrors;
    private boolean ignoreMissingBeginEnd;
    private String readAhead;

    public UUDecoderStream(InputStream in) {
        super(in);
        this.lin = new LineInputStream(in);
        this.ignoreErrors = PropUtil.getBooleanSystemProperty("mail.mime.uudecode.ignoreerrors", false);
        this.ignoreMissingBeginEnd = PropUtil.getBooleanSystemProperty("mail.mime.uudecode.ignoremissingbeginend", false);
    }

    public UUDecoderStream(InputStream in, boolean ignoreErrors, boolean ignoreMissingBeginEnd) {
        super(in);
        this.lin = new LineInputStream(in);
        this.ignoreErrors = ignoreErrors;
        this.ignoreMissingBeginEnd = ignoreMissingBeginEnd;
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
        block10: {
            String line;
            if (this.gotPrefix) {
                return;
            }
            this.mode = 438;
            this.name = "encoder.buf";
            while (true) {
                if ((line = this.lin.readLine()) == null) {
                    if (!this.ignoreMissingBeginEnd) {
                        throw new DecodingException("UUDecoder: Missing begin");
                    }
                    this.gotPrefix = true;
                    this.gotEnd = true;
                    break block10;
                }
                if (line.regionMatches(false, 0, "begin", 0, 5)) {
                    block11: {
                        try {
                            this.mode = Integer.parseInt(line.substring(6, 9));
                        }
                        catch (NumberFormatException ex) {
                            if (this.ignoreErrors) break block11;
                            throw new DecodingException("UUDecoder: Error in mode: " + ex.toString());
                        }
                    }
                    if (line.length() > 10) {
                        this.name = line.substring(10);
                    } else if (!this.ignoreErrors) {
                        throw new DecodingException("UUDecoder: Missing name: " + line);
                    }
                    this.gotPrefix = true;
                    break block10;
                }
                if (!this.ignoreMissingBeginEnd || line.length() == 0) continue;
                int count = line.charAt(0);
                int need = ((count = count - 32 & 0x3F) * 8 + 5) / 6;
                if (need == 0 || line.length() >= need + 1) break;
            }
            this.readAhead = line;
            this.gotPrefix = true;
        }
    }

    private boolean decode() throws IOException {
        String line;
        int count;
        block12: {
            if (this.gotEnd) {
                return false;
            }
            this.bufsize = 0;
            count = 0;
            while (true) {
                if (this.readAhead != null) {
                    line = this.readAhead;
                    this.readAhead = null;
                } else {
                    line = this.lin.readLine();
                }
                if (line == null) {
                    if (!this.ignoreMissingBeginEnd) {
                        throw new DecodingException("UUDecoder: Missing end at EOF");
                    }
                    this.gotEnd = true;
                    return false;
                }
                if (line.equals("end")) {
                    this.gotEnd = true;
                    return false;
                }
                if (line.length() == 0) continue;
                count = line.charAt(0);
                if (count < 32) {
                    if (this.ignoreErrors) continue;
                    throw new DecodingException("UUDecoder: Buffer format error");
                }
                if ((count = count - 32 & 0x3F) == 0) {
                    line = this.lin.readLine();
                    if (!(line != null && line.equals("end") || this.ignoreMissingBeginEnd)) {
                        throw new DecodingException("UUDecoder: Missing End after count 0 line");
                    }
                    this.gotEnd = true;
                    return false;
                }
                int need = (count * 8 + 5) / 6;
                if (line.length() >= need + 1) break block12;
                if (!this.ignoreErrors) break;
            }
            throw new DecodingException("UUDecoder: Short buffer error");
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

