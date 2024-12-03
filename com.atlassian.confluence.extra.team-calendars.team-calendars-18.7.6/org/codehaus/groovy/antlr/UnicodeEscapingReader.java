/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.antlr;

import groovyjarjarantlr.CharScanner;
import groovyjarjarantlr.Token;
import groovyjarjarantlr.TokenStreamException;
import java.io.IOException;
import java.io.Reader;
import org.codehaus.groovy.antlr.SourceBuffer;

public class UnicodeEscapingReader
extends Reader {
    private final Reader reader;
    private CharScanner lexer;
    private boolean hasNextChar = false;
    private int nextChar;
    private final SourceBuffer sourceBuffer;
    private int previousLine;
    private int numUnicodeEscapesFound = 0;
    private int numUnicodeEscapesFoundOnCurrentLine = 0;

    public UnicodeEscapingReader(Reader reader, SourceBuffer sourceBuffer) {
        this.reader = reader;
        this.sourceBuffer = sourceBuffer;
        this.lexer = new DummyLexer();
    }

    public void setLexer(CharScanner lexer) {
        this.lexer = lexer;
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        int count;
        int c = 0;
        for (count = 0; count < len && (c = this.read()) != -1; ++count) {
            cbuf[off + count] = (char)c;
        }
        return count == 0 && c == -1 ? -1 : count;
    }

    @Override
    public int read() throws IOException {
        int c;
        if (this.hasNextChar) {
            this.hasNextChar = false;
            this.write(this.nextChar);
            return this.nextChar;
        }
        if (this.previousLine != this.lexer.getLine()) {
            this.numUnicodeEscapesFoundOnCurrentLine = 0;
            this.previousLine = this.lexer.getLine();
        }
        if ((c = this.reader.read()) != 92) {
            this.write(c);
            return c;
        }
        c = this.reader.read();
        if (c != 117) {
            this.hasNextChar = true;
            this.nextChar = c;
            this.write(92);
            return 92;
        }
        int numberOfUChars = 0;
        do {
            ++numberOfUChars;
        } while ((c = this.reader.read()) == 117);
        this.checkHexDigit(c);
        StringBuilder charNum = new StringBuilder();
        charNum.append((char)c);
        for (int i = 0; i < 3; ++i) {
            c = this.reader.read();
            this.checkHexDigit(c);
            charNum.append((char)c);
        }
        int rv = Integer.parseInt(charNum.toString(), 16);
        this.write(rv);
        this.numUnicodeEscapesFound += 4 + numberOfUChars;
        this.numUnicodeEscapesFoundOnCurrentLine += 4 + numberOfUChars;
        return rv;
    }

    private void write(int c) {
        if (this.sourceBuffer != null) {
            this.sourceBuffer.write(c);
        }
    }

    private void checkHexDigit(int c) throws IOException {
        if (c >= 48 && c <= 57) {
            return;
        }
        if (c >= 97 && c <= 102) {
            return;
        }
        if (c >= 65 && c <= 70) {
            return;
        }
        this.hasNextChar = true;
        this.nextChar = c;
        throw new IOException("Did not find four digit hex character code. line: " + this.lexer.getLine() + " col:" + this.lexer.getColumn());
    }

    public int getUnescapedUnicodeColumnCount() {
        return this.numUnicodeEscapesFoundOnCurrentLine;
    }

    public int getUnescapedUnicodeOffsetCount() {
        return this.numUnicodeEscapesFound;
    }

    @Override
    public void close() throws IOException {
        this.reader.close();
    }

    private static class DummyLexer
    extends CharScanner {
        private final Token t = new Token();

        private DummyLexer() {
        }

        @Override
        public Token nextToken() throws TokenStreamException {
            return this.t;
        }

        @Override
        public int getColumn() {
            return 0;
        }

        @Override
        public int getLine() {
            return 0;
        }
    }
}

