/*
 * Decompiled with CFR 0.152.
 */
package groovy.json.internal;

import groovy.json.internal.ArrayUtils;
import groovy.json.internal.CharScanner;
import groovy.json.internal.CharacterSource;
import groovy.json.internal.Chr;
import groovy.json.internal.Exceptions;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

public class ReaderCharacterSource
implements CharacterSource {
    private static final int MAX_TOKEN_SIZE = 5;
    private final Reader reader;
    private int readAheadSize;
    private int ch = -2;
    private boolean foundEscape;
    private char[] readBuf;
    private int index;
    private int length;
    boolean more = true;
    private boolean done = false;
    private static final char[] EMPTY_CHARS = new char[0];

    public ReaderCharacterSource(Reader reader, int readAheadSize) {
        this.reader = reader;
        this.readBuf = new char[readAheadSize + 5];
        this.readAheadSize = readAheadSize;
    }

    public ReaderCharacterSource(Reader reader) {
        this.reader = reader;
        this.readAheadSize = 10000;
        this.readBuf = new char[this.readAheadSize + 5];
    }

    public ReaderCharacterSource(String string) {
        this(new StringReader(string));
    }

    private void readForToken() {
        try {
            this.length += this.reader.read(this.readBuf, this.readBuf.length - 5, 5);
        }
        catch (IOException e) {
            Exceptions.handle(e);
        }
    }

    private void ensureBuffer() {
        try {
            if (this.index >= this.length && !this.done) {
                this.readNextBuffer();
            } else {
                this.more = !this.done || this.index < this.length;
            }
        }
        catch (Exception ex) {
            String str = CharScanner.errorDetails("ensureBuffer issue", this.readBuf, this.index, this.ch);
            Exceptions.handle(str, (Throwable)ex);
        }
    }

    private void readNextBuffer() throws IOException {
        this.length = this.reader.read(this.readBuf, 0, this.readAheadSize);
        this.index = 0;
        if (this.length == -1) {
            this.ch = -1;
            this.length = 0;
            this.more = false;
            this.done = true;
        } else {
            this.more = true;
        }
    }

    @Override
    public final int nextChar() {
        this.ensureBuffer();
        this.ch = this.readBuf[this.index++];
        return this.ch;
    }

    @Override
    public final int currentChar() {
        this.ensureBuffer();
        return this.readBuf[this.index];
    }

    @Override
    public final boolean hasChar() {
        this.ensureBuffer();
        return this.more;
    }

    @Override
    public final boolean consumeIfMatch(char[] match) {
        try {
            char[] _chars = this.readBuf;
            int i = 0;
            int idx = this.index;
            boolean ok = true;
            if (idx + match.length > this.length) {
                this.readForToken();
            }
            while (i < match.length && (ok &= match[i] == _chars[idx])) {
                ++i;
                ++idx;
            }
            if (ok) {
                this.index = idx;
                return true;
            }
            return false;
        }
        catch (Exception ex) {
            String str = CharScanner.errorDetails("consumeIfMatch issue", this.readBuf, this.index, this.ch);
            return Exceptions.handle(Boolean.TYPE, str, ex);
        }
    }

    @Override
    public final int location() {
        return this.index;
    }

    @Override
    public final int safeNextChar() {
        try {
            this.ensureBuffer();
            return this.index + 1 < this.readBuf.length ? this.readBuf[this.index++] : -1;
        }
        catch (Exception ex) {
            String str = CharScanner.errorDetails("safeNextChar issue", this.readBuf, this.index, this.ch);
            return Exceptions.handle(Integer.TYPE, str, ex);
        }
    }

    @Override
    public char[] findNextChar(int match, int esc) {
        try {
            this.ensureBuffer();
            this.foundEscape = false;
            if (this.readBuf[this.index] == '\"') {
                ++this.index;
                return EMPTY_CHARS;
            }
            int start = this.index;
            char[] results = null;
            boolean foundEnd = false;
            boolean wasEscaped = false;
            while (!foundEnd) {
                while (this.index < this.length) {
                    this.ch = this.readBuf[this.index];
                    if (wasEscaped) {
                        wasEscaped = false;
                    } else {
                        if (this.ch == match) {
                            foundEnd = true;
                            break;
                        }
                        if (this.ch == esc) {
                            this.foundEscape = true;
                            wasEscaped = true;
                        }
                    }
                    ++this.index;
                }
                results = results != null ? Chr.add(results, ArrayUtils.copyRange(this.readBuf, start, this.index)) : ArrayUtils.copyRange(this.readBuf, start, this.index);
                this.ensureBuffer();
                if (this.index == 0) {
                    start = 0;
                }
                if (!this.done) continue;
            }
            if (this.done) {
                return Exceptions.die(char[].class, "Unable to find close char " + (char)match + ": " + new String(results));
            }
            ++this.index;
            return results;
        }
        catch (Exception ex) {
            String str = CharScanner.errorDetails("findNextChar issue", this.readBuf, this.index, this.ch);
            return Exceptions.handle(char[].class, str, ex);
        }
    }

    @Override
    public boolean hadEscape() {
        return this.foundEscape;
    }

    @Override
    public void skipWhiteSpace() {
        try {
            this.index = CharScanner.skipWhiteSpace(this.readBuf, this.index, this.length);
            if (this.index >= this.length && this.more) {
                this.ensureBuffer();
                this.skipWhiteSpace();
            }
        }
        catch (Exception ex) {
            String str = CharScanner.errorDetails("skipWhiteSpace issue", this.readBuf, this.index, this.ch);
            Exceptions.handle(str, (Throwable)ex);
        }
    }

    @Override
    public char[] readNumber() {
        try {
            this.ensureBuffer();
            char[] results = CharScanner.readNumber(this.readBuf, this.index, this.length);
            this.index += results.length;
            if (this.index >= this.length && this.more) {
                this.ensureBuffer();
                if (this.length != 0) {
                    char[] results2 = this.readNumber();
                    return Chr.add(results, results2);
                }
                return results;
            }
            return results;
        }
        catch (Exception ex) {
            String str = CharScanner.errorDetails("readNumber issue", this.readBuf, this.index, this.ch);
            return Exceptions.handle(char[].class, str, ex);
        }
    }

    @Override
    public String errorDetails(String message) {
        return CharScanner.errorDetails(message, this.readBuf, this.index, this.ch);
    }
}

