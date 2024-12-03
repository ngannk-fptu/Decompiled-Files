/*
 * Decompiled with CFR 0.152.
 */
package groovy.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;

public class LineColumnReader
extends BufferedReader {
    private long line = 1L;
    private long column = 1L;
    private long lineMark = 1L;
    private long columnMark = 1L;
    private boolean newLineWasRead = false;

    public LineColumnReader(Reader reader) {
        super(reader);
    }

    @Override
    public void mark(int readAheadLimit) throws IOException {
        this.lineMark = this.line;
        this.columnMark = this.column;
        super.mark(readAheadLimit);
    }

    @Override
    public void reset() throws IOException {
        this.line = this.lineMark;
        this.column = this.columnMark;
        super.reset();
    }

    @Override
    public int read() throws IOException {
        int charRead;
        if (this.newLineWasRead) {
            ++this.line;
            this.column = 1L;
            this.newLineWasRead = false;
        }
        if ((charRead = super.read()) > -1) {
            char c = (char)charRead;
            if (c == '\r' || c == '\n') {
                this.newLineWasRead = true;
                if (c == '\r') {
                    this.mark(1);
                    c = (char)super.read();
                    if (c != '\n') {
                        this.reset();
                    }
                }
            } else {
                ++this.column;
            }
        }
        return charRead;
    }

    @Override
    public int read(char[] chars, int startOffset, int length) throws IOException {
        for (int i = startOffset; i <= startOffset + length; ++i) {
            int readInt = this.read();
            if (readInt == -1) {
                return i - startOffset;
            }
            chars[i] = (char)readInt;
        }
        return length;
    }

    @Override
    public String readLine() throws IOException {
        StringBuilder result = new StringBuilder();
        while (true) {
            int intRead;
            if ((intRead = this.read()) == -1) {
                return result.length() == 0 ? null : result.toString();
            }
            char c = (char)intRead;
            if (c == '\n' || c == '\r') break;
            result.append(c);
        }
        return result.toString();
    }

    @Override
    public long skip(long toSkip) throws IOException {
        for (long i = 0L; i < toSkip; ++i) {
            int intRead = this.read();
            if (intRead != -1) continue;
            return i;
        }
        return toSkip;
    }

    @Override
    public int read(char[] chars) throws IOException {
        return this.read(chars, 0, chars.length - 1);
    }

    @Override
    public int read(CharBuffer buffer) {
        throw new UnsupportedOperationException("read(CharBuffer) not yet implemented");
    }

    @Override
    public void close() throws IOException {
        super.close();
    }

    public long getColumn() {
        return this.column;
    }

    public void setColumn(long column) {
        this.column = column;
    }

    public long getColumnMark() {
        return this.columnMark;
    }

    public void setColumnMark(long columnMark) {
        this.columnMark = columnMark;
    }

    public long getLine() {
        return this.line;
    }

    public void setLine(long line) {
        this.line = line;
    }

    public long getLineMark() {
        return this.lineMark;
    }

    public void setLineMark(long lineMark) {
        this.lineMark = lineMark;
    }
}

