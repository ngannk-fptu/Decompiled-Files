/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.util.io;

import java.io.IOException;
import java.io.InputStream;
import org.apache.batik.util.io.AbstractCharDecoder;

public class ISO_8859_1Decoder
extends AbstractCharDecoder {
    public ISO_8859_1Decoder(InputStream is) {
        super(is);
    }

    @Override
    public int readChar() throws IOException {
        if (this.position == this.count) {
            this.fillBuffer();
        }
        if (this.count == -1) {
            return -1;
        }
        return this.buffer[this.position++] & 0xFF;
    }
}

