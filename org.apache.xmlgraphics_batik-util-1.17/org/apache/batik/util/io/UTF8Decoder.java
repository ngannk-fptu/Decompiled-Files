/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.util.io;

import java.io.IOException;
import java.io.InputStream;
import org.apache.batik.util.io.AbstractCharDecoder;

public class UTF8Decoder
extends AbstractCharDecoder {
    protected static final byte[] UTF8_BYTES = new byte[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 4, 0, 0, 0, 0, 0, 0, 0, 0};
    protected int nextChar = -1;

    public UTF8Decoder(InputStream is) {
        super(is);
    }

    @Override
    public int readChar() throws IOException {
        if (this.nextChar != -1) {
            int result = this.nextChar;
            this.nextChar = -1;
            return result;
        }
        if (this.position == this.count) {
            this.fillBuffer();
        }
        if (this.count == -1) {
            return -1;
        }
        int b1 = this.buffer[this.position++] & 0xFF;
        switch (UTF8_BYTES[b1]) {
            default: {
                this.charError("UTF-8");
            }
            case 1: {
                return b1;
            }
            case 2: {
                if (this.position == this.count) {
                    this.fillBuffer();
                }
                if (this.count == -1) {
                    this.endOfStreamError("UTF-8");
                }
                return (b1 & 0x1F) << 6 | this.buffer[this.position++] & 0x3F;
            }
            case 3: {
                if (this.position == this.count) {
                    this.fillBuffer();
                }
                if (this.count == -1) {
                    this.endOfStreamError("UTF-8");
                }
                byte b2 = this.buffer[this.position++];
                if (this.position == this.count) {
                    this.fillBuffer();
                }
                if (this.count == -1) {
                    this.endOfStreamError("UTF-8");
                }
                byte b3 = this.buffer[this.position++];
                if ((b2 & 0xC0) != 128 || (b3 & 0xC0) != 128) {
                    this.charError("UTF-8");
                }
                return (b1 & 0x1F) << 12 | (b2 & 0x3F) << 6 | b3 & 0x1F;
            }
            case 4: 
        }
        if (this.position == this.count) {
            this.fillBuffer();
        }
        if (this.count == -1) {
            this.endOfStreamError("UTF-8");
        }
        byte b2 = this.buffer[this.position++];
        if (this.position == this.count) {
            this.fillBuffer();
        }
        if (this.count == -1) {
            this.endOfStreamError("UTF-8");
        }
        byte b3 = this.buffer[this.position++];
        if (this.position == this.count) {
            this.fillBuffer();
        }
        if (this.count == -1) {
            this.endOfStreamError("UTF-8");
        }
        byte b4 = this.buffer[this.position++];
        if ((b2 & 0xC0) != 128 || (b3 & 0xC0) != 128 || (b4 & 0xC0) != 128) {
            this.charError("UTF-8");
        }
        int c = (b1 & 0x1F) << 18 | (b2 & 0x3F) << 12 | (b3 & 0x1F) << 6 | b4 & 0x1F;
        this.nextChar = (c - 65536) % 1024 + 56320;
        return (c - 65536) / 1024 + 55296;
    }
}

