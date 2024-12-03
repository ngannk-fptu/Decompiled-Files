/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.util.io;

import java.io.IOException;
import java.io.InputStream;
import org.apache.batik.util.io.AbstractCharDecoder;

public class UTF16Decoder
extends AbstractCharDecoder {
    protected boolean bigEndian;

    public UTF16Decoder(InputStream is) throws IOException {
        super(is);
        int b2;
        int b1 = is.read();
        if (b1 == -1) {
            this.endOfStreamError("UTF-16");
        }
        if ((b2 = is.read()) == -1) {
            this.endOfStreamError("UTF-16");
        }
        int m = (b1 & 0xFF) << 8 | b2 & 0xFF;
        switch (m) {
            case 65279: {
                this.bigEndian = true;
                break;
            }
            case 65534: {
                break;
            }
            default: {
                this.charError("UTF-16");
            }
        }
    }

    public UTF16Decoder(InputStream is, boolean be) {
        super(is);
        this.bigEndian = be;
    }

    @Override
    public int readChar() throws IOException {
        int c;
        if (this.position == this.count) {
            this.fillBuffer();
        }
        if (this.count == -1) {
            return -1;
        }
        byte b1 = this.buffer[this.position++];
        if (this.position == this.count) {
            this.fillBuffer();
        }
        if (this.count == -1) {
            this.endOfStreamError("UTF-16");
        }
        byte b2 = this.buffer[this.position++];
        int n = c = this.bigEndian ? (b1 & 0xFF) << 8 | b2 & 0xFF : (b2 & 0xFF) << 8 | b1 & 0xFF;
        if (c == 65534) {
            this.charError("UTF-16");
        }
        return c;
    }
}

