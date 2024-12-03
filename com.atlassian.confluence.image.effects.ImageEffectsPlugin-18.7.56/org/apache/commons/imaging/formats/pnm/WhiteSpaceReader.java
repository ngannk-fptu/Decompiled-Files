/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.pnm;

import java.io.IOException;
import java.io.InputStream;

class WhiteSpaceReader {
    private final InputStream is;

    WhiteSpaceReader(InputStream is) {
        this.is = is;
    }

    private char read() throws IOException {
        int result = this.is.read();
        if (result < 0) {
            throw new IOException("PNM: Unexpected EOF");
        }
        return (char)result;
    }

    public char nextChar() throws IOException {
        char c = this.read();
        if (c == '#') {
            while (c != '\n' && c != '\r') {
                c = this.read();
            }
        }
        return c;
    }

    public String readtoWhiteSpace() throws IOException {
        char c = this.nextChar();
        while (Character.isWhitespace(c)) {
            c = this.nextChar();
        }
        StringBuilder buffer = new StringBuilder();
        while (!Character.isWhitespace(c)) {
            buffer.append(c);
            c = this.nextChar();
        }
        return buffer.toString();
    }

    public String readLine() throws IOException {
        StringBuilder buffer = new StringBuilder();
        char c = this.read();
        while (c != '\n' && c != '\r') {
            buffer.append(c);
            c = this.read();
        }
        return buffer.length() > 0 ? buffer.toString() : null;
    }
}

