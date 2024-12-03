/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.queryParser;

import com.atlassian.lucene36.queryParser.CharStream;
import java.io.IOException;
import java.io.Reader;

public final class FastCharStream
implements CharStream {
    char[] buffer = null;
    int bufferLength = 0;
    int bufferPosition = 0;
    int tokenStart = 0;
    int bufferStart = 0;
    Reader input;

    public FastCharStream(Reader r) {
        this.input = r;
    }

    public final char readChar() throws IOException {
        if (this.bufferPosition >= this.bufferLength) {
            this.refill();
        }
        return this.buffer[this.bufferPosition++];
    }

    private final void refill() throws IOException {
        int newPosition = this.bufferLength - this.tokenStart;
        if (this.tokenStart == 0) {
            if (this.buffer == null) {
                this.buffer = new char[2048];
            } else if (this.bufferLength == this.buffer.length) {
                char[] newBuffer = new char[this.buffer.length * 2];
                System.arraycopy(this.buffer, 0, newBuffer, 0, this.bufferLength);
                this.buffer = newBuffer;
            }
        } else {
            System.arraycopy(this.buffer, this.tokenStart, this.buffer, 0, newPosition);
        }
        this.bufferLength = newPosition;
        this.bufferPosition = newPosition;
        this.bufferStart += this.tokenStart;
        this.tokenStart = 0;
        int charsRead = this.input.read(this.buffer, newPosition, this.buffer.length - newPosition);
        if (charsRead == -1) {
            throw new IOException("read past eof");
        }
        this.bufferLength += charsRead;
    }

    public final char BeginToken() throws IOException {
        this.tokenStart = this.bufferPosition;
        return this.readChar();
    }

    public final void backup(int amount) {
        this.bufferPosition -= amount;
    }

    public final String GetImage() {
        return new String(this.buffer, this.tokenStart, this.bufferPosition - this.tokenStart);
    }

    public final char[] GetSuffix(int len) {
        char[] value = new char[len];
        System.arraycopy(this.buffer, this.bufferPosition - len, value, 0, len);
        return value;
    }

    public final void Done() {
        try {
            this.input.close();
        }
        catch (IOException e) {
            System.err.println("Caught: " + e + "; ignoring.");
        }
    }

    public final int getColumn() {
        return this.bufferStart + this.bufferPosition;
    }

    public final int getLine() {
        return 1;
    }

    public final int getEndColumn() {
        return this.bufferStart + this.bufferPosition;
    }

    public final int getEndLine() {
        return 1;
    }

    public final int getBeginColumn() {
        return this.bufferStart + this.tokenStart;
    }

    public final int getBeginLine() {
        return 1;
    }
}

