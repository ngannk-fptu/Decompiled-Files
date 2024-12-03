/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.CharFilter
 */
package org.apache.lucene.analysis.fa;

import java.io.IOException;
import java.io.Reader;
import org.apache.lucene.analysis.CharFilter;

public class PersianCharFilter
extends CharFilter {
    public PersianCharFilter(Reader in) {
        super(in);
    }

    public int read(char[] cbuf, int off, int len) throws IOException {
        int charsRead = this.input.read(cbuf, off, len);
        if (charsRead > 0) {
            int end = off + charsRead;
            while (off < end) {
                if (cbuf[off] == '\u200c') {
                    cbuf[off] = 32;
                }
                ++off;
            }
        }
        return charsRead;
    }

    public int read() throws IOException {
        int ch = this.input.read();
        if (ch == 8204) {
            return 32;
        }
        return ch;
    }

    protected int correct(int currentOff) {
        return currentOff;
    }
}

