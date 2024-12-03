/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.serialize;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.Printer;

public class IndentPrinter
extends Printer {
    private StringBuffer _line = new StringBuffer(80);
    private StringBuffer _text = new StringBuffer(20);
    private int _spaces = 0;
    private int _thisIndent = 0;
    private int _nextIndent = 0;

    public IndentPrinter(Writer writer, OutputFormat outputFormat) {
        super(writer, outputFormat);
    }

    @Override
    public void enterDTD() {
        if (this._dtdWriter == null) {
            this._line.append(this._text);
            this._text = new StringBuffer(20);
            this.flushLine(false);
            this._dtdWriter = new StringWriter();
            this._docWriter = this._writer;
            this._writer = this._dtdWriter;
        }
    }

    @Override
    public String leaveDTD() {
        if (this._writer == this._dtdWriter) {
            this._line.append(this._text);
            this._text = new StringBuffer(20);
            this.flushLine(false);
            this._writer = this._docWriter;
            return this._dtdWriter.toString();
        }
        return null;
    }

    @Override
    public void printText(String string) {
        this._text.append(string);
    }

    @Override
    public void printText(StringBuffer stringBuffer) {
        this._text.append(stringBuffer.toString());
    }

    @Override
    public void printText(char c) {
        this._text.append(c);
    }

    @Override
    public void printText(char[] cArray, int n, int n2) {
        this._text.append(cArray, n, n2);
    }

    @Override
    public void printSpace() {
        if (this._text.length() > 0) {
            block5: {
                if (this._format.getLineWidth() > 0 && this._thisIndent + this._line.length() + this._spaces + this._text.length() > this._format.getLineWidth()) {
                    this.flushLine(false);
                    try {
                        this._writer.write(this._format.getLineSeparator());
                    }
                    catch (IOException iOException) {
                        if (this._exception != null) break block5;
                        this._exception = iOException;
                    }
                }
            }
            while (this._spaces > 0) {
                this._line.append(' ');
                --this._spaces;
            }
            this._line.append(this._text);
            this._text = new StringBuffer(20);
        }
        ++this._spaces;
    }

    @Override
    public void breakLine() {
        this.breakLine(false);
    }

    @Override
    public void breakLine(boolean bl) {
        block4: {
            if (this._text.length() > 0) {
                while (this._spaces > 0) {
                    this._line.append(' ');
                    --this._spaces;
                }
                this._line.append(this._text);
                this._text = new StringBuffer(20);
            }
            this.flushLine(bl);
            try {
                this._writer.write(this._format.getLineSeparator());
            }
            catch (IOException iOException) {
                if (this._exception != null) break block4;
                this._exception = iOException;
            }
        }
    }

    @Override
    public void flushLine(boolean bl) {
        block6: {
            if (this._line.length() > 0) {
                try {
                    if (this._format.getIndenting() && !bl) {
                        int n = this._thisIndent;
                        if (2 * n > this._format.getLineWidth() && this._format.getLineWidth() > 0) {
                            n = this._format.getLineWidth() / 2;
                        }
                        while (n > 0) {
                            this._writer.write(32);
                            --n;
                        }
                    }
                    this._thisIndent = this._nextIndent;
                    this._spaces = 0;
                    this._writer.write(this._line.toString());
                    this._line = new StringBuffer(40);
                }
                catch (IOException iOException) {
                    if (this._exception != null) break block6;
                    this._exception = iOException;
                }
            }
        }
    }

    @Override
    public void flush() {
        block3: {
            if (this._line.length() > 0 || this._text.length() > 0) {
                this.breakLine();
            }
            try {
                this._writer.flush();
            }
            catch (IOException iOException) {
                if (this._exception != null) break block3;
                this._exception = iOException;
            }
        }
    }

    @Override
    public void indent() {
        this._nextIndent += this._format.getIndent();
    }

    @Override
    public void unindent() {
        this._nextIndent -= this._format.getIndent();
        if (this._nextIndent < 0) {
            this._nextIndent = 0;
        }
        if (this._line.length() + this._spaces + this._text.length() == 0) {
            this._thisIndent = this._nextIndent;
        }
    }

    @Override
    public int getNextIndent() {
        return this._nextIndent;
    }

    @Override
    public void setNextIndent(int n) {
        this._nextIndent = n;
    }

    @Override
    public void setThisIndent(int n) {
        this._thisIndent = n;
    }
}

