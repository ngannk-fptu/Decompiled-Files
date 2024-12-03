/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.util.io;

import java.io.IOException;
import java.io.InputStream;
import org.apache.batik.util.io.AbstractCharDecoder;

public class ASCIIDecoder
extends AbstractCharDecoder {
    public ASCIIDecoder(InputStream is) {
        super(is);
    }

    @Override
    public int readChar() throws IOException {
        byte result;
        if (this.position == this.count) {
            this.fillBuffer();
        }
        if (this.count == -1) {
            return -1;
        }
        if ((result = this.buffer[this.position++]) < 0) {
            this.charError("ASCII");
        }
        return result;
    }
}

