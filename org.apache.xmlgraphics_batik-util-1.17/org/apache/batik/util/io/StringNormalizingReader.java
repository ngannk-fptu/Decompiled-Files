/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.util.io;

import java.io.IOException;
import org.apache.batik.util.io.NormalizingReader;

public class StringNormalizingReader
extends NormalizingReader {
    protected String string;
    protected int length;
    protected int next;
    protected int line = 1;
    protected int column;

    public StringNormalizingReader(String s) {
        this.string = s;
        this.length = s.length();
    }

    @Override
    public int read() throws IOException {
        int result;
        int n = result = this.length == this.next ? -1 : (int)this.string.charAt(this.next++);
        if (result <= 13) {
            switch (result) {
                case 13: {
                    int c;
                    this.column = 0;
                    ++this.line;
                    int n2 = c = this.length == this.next ? -1 : (int)this.string.charAt(this.next);
                    if (c == 10) {
                        ++this.next;
                    }
                    return 10;
                }
                case 10: {
                    this.column = 0;
                    ++this.line;
                }
            }
        }
        return result;
    }

    @Override
    public int getLine() {
        return this.line;
    }

    @Override
    public int getColumn() {
        return this.column;
    }

    @Override
    public void close() throws IOException {
        this.string = null;
    }
}

