/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.jsp.JspWriter
 *  javax.servlet.jsp.tagext.BodyContent
 */
package org.apache.jasper.runtime;

import java.io.CharArrayReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyContent;
import org.apache.jasper.compiler.Localizer;

public class BodyContentImpl
extends BodyContent {
    private static final boolean LIMIT_BUFFER;
    private static final int TAG_BUFFER_SIZE;
    private char[] cb = new char[TAG_BUFFER_SIZE];
    private int nextChar;
    private boolean closed;
    private Writer writer;

    public BodyContentImpl(JspWriter enclosingWriter) {
        super(enclosingWriter);
        this.bufferSize = this.cb.length;
        this.nextChar = 0;
        this.closed = false;
    }

    public void write(int c) throws IOException {
        if (this.writer != null) {
            this.writer.write(c);
        } else {
            this.ensureOpen();
            if (this.nextChar >= this.bufferSize) {
                this.reAllocBuff(1);
            }
            this.cb[this.nextChar++] = (char)c;
        }
    }

    public void write(char[] cbuf, int off, int len) throws IOException {
        if (this.writer != null) {
            this.writer.write(cbuf, off, len);
        } else {
            this.ensureOpen();
            if (off < 0 || off > cbuf.length || len < 0 || off + len > cbuf.length || off + len < 0) {
                throw new IndexOutOfBoundsException();
            }
            if (len == 0) {
                return;
            }
            if (len >= this.bufferSize - this.nextChar) {
                this.reAllocBuff(len);
            }
            System.arraycopy(cbuf, off, this.cb, this.nextChar, len);
            this.nextChar += len;
        }
    }

    public void write(char[] buf) throws IOException {
        if (this.writer != null) {
            this.writer.write(buf);
        } else {
            this.write(buf, 0, buf.length);
        }
    }

    public void write(String s, int off, int len) throws IOException {
        if (this.writer != null) {
            this.writer.write(s, off, len);
        } else {
            this.ensureOpen();
            if (len >= this.bufferSize - this.nextChar) {
                this.reAllocBuff(len);
            }
            s.getChars(off, off + len, this.cb, this.nextChar);
            this.nextChar += len;
        }
    }

    public void write(String s) throws IOException {
        if (this.writer != null) {
            this.writer.write(s);
        } else {
            this.write(s, 0, s.length());
        }
    }

    public void newLine() throws IOException {
        if (this.writer != null) {
            this.writer.write(System.lineSeparator());
        } else {
            this.write(System.lineSeparator());
        }
    }

    public void print(boolean b) throws IOException {
        if (this.writer != null) {
            this.writer.write(b ? "true" : "false");
        } else {
            this.write(b ? "true" : "false");
        }
    }

    public void print(char c) throws IOException {
        if (this.writer != null) {
            this.writer.write(String.valueOf(c));
        } else {
            this.write(String.valueOf(c));
        }
    }

    public void print(int i) throws IOException {
        if (this.writer != null) {
            this.writer.write(String.valueOf(i));
        } else {
            this.write(String.valueOf(i));
        }
    }

    public void print(long l) throws IOException {
        if (this.writer != null) {
            this.writer.write(String.valueOf(l));
        } else {
            this.write(String.valueOf(l));
        }
    }

    public void print(float f) throws IOException {
        if (this.writer != null) {
            this.writer.write(String.valueOf(f));
        } else {
            this.write(String.valueOf(f));
        }
    }

    public void print(double d) throws IOException {
        if (this.writer != null) {
            this.writer.write(String.valueOf(d));
        } else {
            this.write(String.valueOf(d));
        }
    }

    public void print(char[] s) throws IOException {
        if (this.writer != null) {
            this.writer.write(s);
        } else {
            this.write(s);
        }
    }

    public void print(String s) throws IOException {
        if (s == null) {
            s = "null";
        }
        if (this.writer != null) {
            this.writer.write(s);
        } else {
            this.write(s);
        }
    }

    public void print(Object obj) throws IOException {
        if (this.writer != null) {
            this.writer.write(String.valueOf(obj));
        } else {
            this.write(String.valueOf(obj));
        }
    }

    public void println() throws IOException {
        this.newLine();
    }

    public void println(boolean x) throws IOException {
        this.print(x);
        this.println();
    }

    public void println(char x) throws IOException {
        this.print(x);
        this.println();
    }

    public void println(int x) throws IOException {
        this.print(x);
        this.println();
    }

    public void println(long x) throws IOException {
        this.print(x);
        this.println();
    }

    public void println(float x) throws IOException {
        this.print(x);
        this.println();
    }

    public void println(double x) throws IOException {
        this.print(x);
        this.println();
    }

    public void println(char[] x) throws IOException {
        this.print(x);
        this.println();
    }

    public void println(String x) throws IOException {
        this.print(x);
        this.println();
    }

    public void println(Object x) throws IOException {
        this.print(x);
        this.println();
    }

    public void clear() throws IOException {
        if (this.writer != null) {
            throw new IOException();
        }
        this.nextChar = 0;
        if (LIMIT_BUFFER && this.cb.length > TAG_BUFFER_SIZE) {
            this.cb = new char[TAG_BUFFER_SIZE];
            this.bufferSize = this.cb.length;
        }
    }

    public void clearBuffer() throws IOException {
        if (this.writer == null) {
            this.clear();
        }
    }

    public void close() throws IOException {
        if (this.writer != null) {
            this.writer.close();
        } else {
            this.closed = true;
        }
    }

    public int getBufferSize() {
        return this.writer == null ? this.bufferSize : 0;
    }

    public int getRemaining() {
        return this.writer == null ? this.bufferSize - this.nextChar : 0;
    }

    public Reader getReader() {
        return this.writer == null ? new CharArrayReader(this.cb, 0, this.nextChar) : null;
    }

    public String getString() {
        return this.writer == null ? new String(this.cb, 0, this.nextChar) : null;
    }

    public void writeOut(Writer out) throws IOException {
        if (this.writer == null) {
            out.write(this.cb, 0, this.nextChar);
        }
    }

    void setWriter(Writer writer) {
        this.writer = writer;
        this.closed = false;
        if (writer == null) {
            this.clearBody();
        }
    }

    protected void recycle() {
        this.writer = null;
        try {
            this.clear();
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    private void ensureOpen() throws IOException {
        if (this.closed) {
            throw new IOException(Localizer.getMessage("jsp.error.stream.closed"));
        }
    }

    private void reAllocBuff(int len) {
        if (this.bufferSize + len <= this.cb.length) {
            this.bufferSize = this.cb.length;
            return;
        }
        if (len < this.cb.length) {
            len = this.cb.length;
        }
        char[] tmp = new char[this.cb.length + len];
        System.arraycopy(this.cb, 0, tmp, 0, this.cb.length);
        this.cb = tmp;
        this.bufferSize = this.cb.length;
    }

    static {
        if (System.getSecurityManager() == null) {
            LIMIT_BUFFER = Boolean.parseBoolean(System.getProperty("org.apache.jasper.runtime.BodyContentImpl.LIMIT_BUFFER", "false"));
            TAG_BUFFER_SIZE = Integer.getInteger("org.apache.jasper.runtime.BodyContentImpl.BUFFER_SIZE", 512);
        } else {
            LIMIT_BUFFER = AccessController.doPrivileged(new PrivilegedAction<Boolean>(){

                @Override
                public Boolean run() {
                    return Boolean.valueOf(System.getProperty("org.apache.jasper.runtime.BodyContentImpl.LIMIT_BUFFER", "false"));
                }
            });
            TAG_BUFFER_SIZE = AccessController.doPrivileged(new PrivilegedAction<Integer>(){

                @Override
                public Integer run() {
                    return Integer.getInteger("org.apache.jasper.runtime.BodyContentImpl.BUFFER_SIZE", 512);
                }
            });
        }
    }
}

