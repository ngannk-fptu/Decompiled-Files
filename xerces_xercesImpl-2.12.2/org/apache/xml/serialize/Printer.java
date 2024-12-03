/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.serialize;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import org.apache.xml.serialize.OutputFormat;

public class Printer {
    protected final OutputFormat _format;
    protected Writer _writer;
    protected StringWriter _dtdWriter;
    protected Writer _docWriter;
    protected IOException _exception;
    private static final int BufferSize = 4096;
    private final char[] _buffer = new char[4096];
    private int _pos = 0;

    public Printer(Writer writer, OutputFormat outputFormat) {
        this._writer = writer;
        this._format = outputFormat;
        this._exception = null;
        this._dtdWriter = null;
        this._docWriter = null;
        this._pos = 0;
    }

    public IOException getException() {
        return this._exception;
    }

    public void enterDTD() throws IOException {
        if (this._dtdWriter == null) {
            this.flushLine(false);
            this._dtdWriter = new StringWriter();
            this._docWriter = this._writer;
            this._writer = this._dtdWriter;
        }
    }

    public String leaveDTD() throws IOException {
        if (this._writer == this._dtdWriter) {
            this.flushLine(false);
            this._writer = this._docWriter;
            return this._dtdWriter.toString();
        }
        return null;
    }

    public void printText(String string) throws IOException {
        try {
            int n = string.length();
            for (int i = 0; i < n; ++i) {
                if (this._pos == 4096) {
                    this._writer.write(this._buffer);
                    this._pos = 0;
                }
                this._buffer[this._pos] = string.charAt(i);
                ++this._pos;
            }
        }
        catch (IOException iOException) {
            if (this._exception == null) {
                this._exception = iOException;
            }
            throw iOException;
        }
    }

    public void printText(StringBuffer stringBuffer) throws IOException {
        try {
            int n = stringBuffer.length();
            for (int i = 0; i < n; ++i) {
                if (this._pos == 4096) {
                    this._writer.write(this._buffer);
                    this._pos = 0;
                }
                this._buffer[this._pos] = stringBuffer.charAt(i);
                ++this._pos;
            }
        }
        catch (IOException iOException) {
            if (this._exception == null) {
                this._exception = iOException;
            }
            throw iOException;
        }
    }

    public void printText(char[] cArray, int n, int n2) throws IOException {
        try {
            while (n2-- > 0) {
                if (this._pos == 4096) {
                    this._writer.write(this._buffer);
                    this._pos = 0;
                }
                this._buffer[this._pos] = cArray[n];
                ++n;
                ++this._pos;
            }
        }
        catch (IOException iOException) {
            if (this._exception == null) {
                this._exception = iOException;
            }
            throw iOException;
        }
    }

    public void printText(char c) throws IOException {
        try {
            if (this._pos == 4096) {
                this._writer.write(this._buffer);
                this._pos = 0;
            }
            this._buffer[this._pos] = c;
            ++this._pos;
        }
        catch (IOException iOException) {
            if (this._exception == null) {
                this._exception = iOException;
            }
            throw iOException;
        }
    }

    public void printSpace() throws IOException {
        try {
            if (this._pos == 4096) {
                this._writer.write(this._buffer);
                this._pos = 0;
            }
            this._buffer[this._pos] = 32;
            ++this._pos;
        }
        catch (IOException iOException) {
            if (this._exception == null) {
                this._exception = iOException;
            }
            throw iOException;
        }
    }

    public void breakLine() throws IOException {
        try {
            if (this._pos == 4096) {
                this._writer.write(this._buffer);
                this._pos = 0;
            }
            this._buffer[this._pos] = 10;
            ++this._pos;
        }
        catch (IOException iOException) {
            if (this._exception == null) {
                this._exception = iOException;
            }
            throw iOException;
        }
    }

    public void breakLine(boolean bl) throws IOException {
        this.breakLine();
    }

    public void flushLine(boolean bl) throws IOException {
        block2: {
            try {
                this._writer.write(this._buffer, 0, this._pos);
            }
            catch (IOException iOException) {
                if (this._exception != null) break block2;
                this._exception = iOException;
            }
        }
        this._pos = 0;
    }

    public void flush() throws IOException {
        try {
            this._writer.write(this._buffer, 0, this._pos);
            this._writer.flush();
        }
        catch (IOException iOException) {
            if (this._exception == null) {
                this._exception = iOException;
            }
            throw iOException;
        }
        this._pos = 0;
    }

    public void indent() {
    }

    public void unindent() {
    }

    public int getNextIndent() {
        return 0;
    }

    public void setNextIndent(int n) {
    }

    public void setThisIndent(int n) {
    }
}

