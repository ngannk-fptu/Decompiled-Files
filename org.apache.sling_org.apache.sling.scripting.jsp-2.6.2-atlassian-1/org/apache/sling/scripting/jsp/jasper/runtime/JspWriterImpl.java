/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletResponse
 *  javax.servlet.jsp.JspWriter
 */
package org.apache.sling.scripting.jsp.jasper.runtime;

import java.io.IOException;
import java.io.Writer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.servlet.ServletResponse;
import javax.servlet.jsp.JspWriter;
import org.apache.sling.scripting.jsp.jasper.compiler.Localizer;
import org.apache.sling.scripting.jsp.jasper.security.SecurityUtil;

public class JspWriterImpl
extends JspWriter {
    private Writer out;
    private ServletResponse response;
    private char[] cb;
    private int nextChar;
    private boolean flushed = false;
    private boolean closed = false;
    static String lineSeparator = System.getProperty("line.separator");

    public JspWriterImpl() {
        super(8192, true);
    }

    public JspWriterImpl(ServletResponse response) {
        this(response, 8192, true);
    }

    public JspWriterImpl(ServletResponse response, int sz, boolean autoFlush) {
        super(sz, autoFlush);
        if (sz < 0) {
            throw new IllegalArgumentException("Buffer size <= 0");
        }
        this.response = response;
        this.cb = sz == 0 ? null : new char[sz];
        this.nextChar = 0;
    }

    void init(ServletResponse response, int sz, boolean autoFlush) {
        this.response = response;
        if (sz > 0 && (this.cb == null || sz > this.cb.length)) {
            this.cb = new char[sz];
        }
        this.nextChar = 0;
        this.autoFlush = autoFlush;
        this.bufferSize = sz;
    }

    void recycle() {
        this.flushed = false;
        this.closed = false;
        this.out = null;
        this.nextChar = 0;
        this.response = null;
    }

    protected final void flushBuffer() throws IOException {
        if (this.bufferSize == 0) {
            return;
        }
        this.flushed = true;
        this.ensureOpen();
        if (this.nextChar == 0) {
            return;
        }
        this.initOut();
        this.out.write(this.cb, 0, this.nextChar);
        this.nextChar = 0;
    }

    private void initOut() throws IOException {
        if (this.out == null) {
            this.out = this.response.getWriter();
        }
    }

    private String getLocalizeMessage(final String message) {
        if (SecurityUtil.isPackageProtectionEnabled()) {
            return (String)AccessController.doPrivileged(new PrivilegedAction(){

                public Object run() {
                    return Localizer.getMessage(message);
                }
            });
        }
        return Localizer.getMessage(message);
    }

    public final void clear() throws IOException {
        if (this.bufferSize == 0 && this.out != null) {
            throw new IllegalStateException(this.getLocalizeMessage("jsp.error.ise_on_clear"));
        }
        if (this.flushed) {
            throw new IOException(this.getLocalizeMessage("jsp.error.attempt_to_clear_flushed_buffer"));
        }
        this.ensureOpen();
        this.nextChar = 0;
    }

    public void clearBuffer() throws IOException {
        if (this.bufferSize == 0) {
            throw new IllegalStateException(this.getLocalizeMessage("jsp.error.ise_on_clear"));
        }
        this.ensureOpen();
        this.nextChar = 0;
    }

    private final void bufferOverflow() throws IOException {
        throw new IOException(this.getLocalizeMessage("jsp.error.overflow"));
    }

    public void flush() throws IOException {
        this.flushBuffer();
        if (this.out != null) {
            this.out.flush();
        }
    }

    public void close() throws IOException {
        if (this.response == null || this.closed) {
            return;
        }
        this.flush();
        if (this.out != null) {
            this.out.close();
        }
        this.out = null;
        this.closed = true;
    }

    public int getRemaining() {
        return this.bufferSize - this.nextChar;
    }

    private void ensureOpen() throws IOException {
        if (this.response == null || this.closed) {
            throw new IOException("Stream closed");
        }
    }

    public void write(int c) throws IOException {
        this.ensureOpen();
        if (this.bufferSize == 0) {
            this.initOut();
            this.out.write(c);
        } else {
            if (this.nextChar >= this.bufferSize) {
                if (this.autoFlush) {
                    this.flushBuffer();
                } else {
                    this.bufferOverflow();
                }
            }
            this.cb[this.nextChar++] = (char)c;
        }
    }

    private int min(int a, int b) {
        if (a < b) {
            return a;
        }
        return b;
    }

    public void write(char[] cbuf, int off, int len) throws IOException {
        this.ensureOpen();
        if (this.bufferSize == 0) {
            this.initOut();
            this.out.write(cbuf, off, len);
            return;
        }
        if (off < 0 || off > cbuf.length || len < 0 || off + len > cbuf.length || off + len < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (len == 0) {
            return;
        }
        if (len >= this.bufferSize) {
            if (this.autoFlush) {
                this.flushBuffer();
            } else {
                this.bufferOverflow();
            }
            this.initOut();
            this.out.write(cbuf, off, len);
            return;
        }
        int b = off;
        int t = off + len;
        while (b < t) {
            int d = this.min(this.bufferSize - this.nextChar, t - b);
            System.arraycopy(cbuf, b, this.cb, this.nextChar, d);
            b += d;
            this.nextChar += d;
            if (this.nextChar < this.bufferSize) continue;
            if (this.autoFlush) {
                this.flushBuffer();
                continue;
            }
            this.bufferOverflow();
        }
    }

    public void write(char[] buf) throws IOException {
        this.write(buf, 0, buf.length);
    }

    public void write(String s, int off, int len) throws IOException {
        this.ensureOpen();
        if (this.bufferSize == 0) {
            this.initOut();
            this.out.write(s, off, len);
            return;
        }
        int b = off;
        int t = off + len;
        while (b < t) {
            int d = this.min(this.bufferSize - this.nextChar, t - b);
            s.getChars(b, b + d, this.cb, this.nextChar);
            b += d;
            this.nextChar += d;
            if (this.nextChar < this.bufferSize) continue;
            if (this.autoFlush) {
                this.flushBuffer();
                continue;
            }
            this.bufferOverflow();
        }
    }

    public void write(String s) throws IOException {
        if (s == null) {
            this.write(s, 0, 0);
        } else {
            this.write(s, 0, s.length());
        }
    }

    public void newLine() throws IOException {
        this.write(lineSeparator);
    }

    public void print(boolean b) throws IOException {
        this.write(b ? "true" : "false");
    }

    public void print(char c) throws IOException {
        this.write(String.valueOf(c));
    }

    public void print(int i) throws IOException {
        this.write(String.valueOf(i));
    }

    public void print(long l) throws IOException {
        this.write(String.valueOf(l));
    }

    public void print(float f) throws IOException {
        this.write(String.valueOf(f));
    }

    public void print(double d) throws IOException {
        this.write(String.valueOf(d));
    }

    public void print(char[] s) throws IOException {
        this.write(s);
    }

    public void print(String s) throws IOException {
        if (s == null) {
            s = "null";
        }
        this.write(s);
    }

    public void print(Object obj) throws IOException {
        this.write(String.valueOf(obj));
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
}

