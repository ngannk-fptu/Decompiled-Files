/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.util.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import org.apache.batik.util.io.CharDecoder;

public class GenericDecoder
implements CharDecoder {
    protected Reader reader;

    public GenericDecoder(InputStream is, String enc) throws IOException {
        this.reader = new InputStreamReader(is, enc);
        this.reader = new BufferedReader(this.reader);
    }

    public GenericDecoder(Reader r) {
        this.reader = r;
        if (!(r instanceof BufferedReader)) {
            this.reader = new BufferedReader(this.reader);
        }
    }

    @Override
    public int readChar() throws IOException {
        return this.reader.read();
    }

    @Override
    public void dispose() throws IOException {
        this.reader.close();
        this.reader = null;
    }
}

