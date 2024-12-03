/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.rgbe;

import java.io.IOException;
import java.io.InputStream;

class InfoHeaderReader {
    private final InputStream is;

    InfoHeaderReader(InputStream is) {
        this.is = is;
    }

    private char read() throws IOException {
        int result = this.is.read();
        if (result < 0) {
            throw new IOException("HDR: Unexpected EOF");
        }
        return (char)result;
    }

    public String readNextLine() throws IOException {
        char c;
        StringBuilder buffer = new StringBuilder();
        while ((c = this.read()) != '\n') {
            buffer.append(c);
        }
        return buffer.toString();
    }
}

