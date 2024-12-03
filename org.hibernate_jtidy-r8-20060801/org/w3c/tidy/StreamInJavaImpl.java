/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.tidy;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import org.w3c.tidy.Lexer;
import org.w3c.tidy.StreamIn;

public class StreamInJavaImpl
implements StreamIn {
    private static final int CHARBUF_SIZE = 5;
    private int[] charbuf = new int[5];
    private int bufpos;
    private Reader reader;
    private boolean endOfStream;
    private boolean pushed;
    private int curcol;
    private int lastcol;
    private int curline;
    private int tabsize;
    private int tabs;

    protected StreamInJavaImpl(InputStream stream, String encoding, int tabsize) throws UnsupportedEncodingException {
        this.reader = new InputStreamReader(stream, encoding);
        this.pushed = false;
        this.tabsize = tabsize;
        this.curline = 1;
        this.curcol = 1;
        this.endOfStream = false;
    }

    protected StreamInJavaImpl(Reader reader, int tabsize) {
        this.reader = reader;
        this.pushed = false;
        this.tabsize = tabsize;
        this.curline = 1;
        this.curcol = 1;
        this.endOfStream = false;
    }

    public int readCharFromStream() {
        int c;
        try {
            c = this.reader.read();
            if (c < 0) {
                this.endOfStream = true;
            }
        }
        catch (IOException e) {
            this.endOfStream = true;
            return -1;
        }
        return c;
    }

    public int readChar() {
        if (this.pushed) {
            int c = this.charbuf[--this.bufpos];
            if (this.bufpos == 0) {
                this.pushed = false;
            }
            if (c == 10) {
                this.curcol = 1;
                ++this.curline;
                return c;
            }
            ++this.curcol;
            return c;
        }
        this.lastcol = this.curcol++;
        if (this.tabs > 0) {
            --this.tabs;
            return 32;
        }
        int c = this.readCharFromStream();
        if (c < 0) {
            this.endOfStream = true;
            return -1;
        }
        if (c == 10) {
            this.curcol = 1;
            ++this.curline;
            return c;
        }
        if (c == 13) {
            c = this.readCharFromStream();
            if (c != 10) {
                if (c != -1) {
                    this.ungetChar(c);
                }
                c = 10;
            }
            this.curcol = 1;
            ++this.curline;
            return c;
        }
        if (c == 9) {
            this.tabs = this.tabsize - (this.curcol - 1) % this.tabsize - 1;
            ++this.curcol;
            c = 32;
            return c;
        }
        ++this.curcol;
        return c;
    }

    public void ungetChar(int c) {
        this.pushed = true;
        if (this.bufpos >= 5) {
            System.arraycopy(this.charbuf, 0, this.charbuf, 1, 4);
            --this.bufpos;
        }
        this.charbuf[this.bufpos++] = c;
        if (c == 10) {
            --this.curline;
        }
        this.curcol = this.lastcol;
    }

    public boolean isEndOfStream() {
        return this.endOfStream;
    }

    public int getCurcol() {
        return this.curcol;
    }

    public int getCurline() {
        return this.curline;
    }

    public void setLexer(Lexer lexer) {
    }
}

