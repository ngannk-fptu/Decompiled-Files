/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletOutputStream
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpServletResponseWrapper
 *  org.apache.tomcat.util.http.FastHttpDateFormat
 */
package org.apache.catalina.ssi;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Locale;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import org.apache.tomcat.util.http.FastHttpDateFormat;

public class ResponseIncludeWrapper
extends HttpServletResponseWrapper {
    private static final String LAST_MODIFIED = "last-modified";
    protected long lastModified = -1L;
    protected final ServletOutputStream captureServletOutputStream;
    protected ServletOutputStream servletOutputStream;
    protected PrintWriter printWriter;

    public ResponseIncludeWrapper(HttpServletResponse response, ServletOutputStream captureServletOutputStream) {
        super(response);
        this.captureServletOutputStream = captureServletOutputStream;
    }

    public void flushOutputStreamOrWriter() throws IOException {
        if (this.servletOutputStream != null) {
            this.servletOutputStream.flush();
        }
        if (this.printWriter != null) {
            this.printWriter.flush();
        }
    }

    public PrintWriter getWriter() throws IOException {
        if (this.servletOutputStream == null) {
            if (this.printWriter == null) {
                this.setCharacterEncoding(this.getCharacterEncoding());
                this.printWriter = new PrintWriter(new OutputStreamWriter((OutputStream)this.captureServletOutputStream, this.getCharacterEncoding()));
            }
            return this.printWriter;
        }
        throw new IllegalStateException();
    }

    public ServletOutputStream getOutputStream() throws IOException {
        if (this.printWriter == null) {
            if (this.servletOutputStream == null) {
                this.servletOutputStream = this.captureServletOutputStream;
            }
            return this.servletOutputStream;
        }
        throw new IllegalStateException();
    }

    public long getLastModified() {
        return this.lastModified;
    }

    public void addDateHeader(String name, long value) {
        super.addDateHeader(name, value);
        String lname = name.toLowerCase(Locale.ENGLISH);
        if (lname.equals(LAST_MODIFIED)) {
            this.lastModified = value;
        }
    }

    public void addHeader(String name, String value) {
        long lastModified;
        super.addHeader(name, value);
        String lname = name.toLowerCase(Locale.ENGLISH);
        if (lname.equals(LAST_MODIFIED) && (lastModified = FastHttpDateFormat.parseDate((String)value)) != -1L) {
            this.lastModified = lastModified;
        }
    }

    public void setDateHeader(String name, long value) {
        super.setDateHeader(name, value);
        String lname = name.toLowerCase(Locale.ENGLISH);
        if (lname.equals(LAST_MODIFIED)) {
            this.lastModified = value;
        }
    }

    public void setHeader(String name, String value) {
        long lastModified;
        super.setHeader(name, value);
        String lname = name.toLowerCase(Locale.ENGLISH);
        if (lname.equals(LAST_MODIFIED) && (lastModified = FastHttpDateFormat.parseDate((String)value)) != -1L) {
            this.lastModified = lastModified;
        }
    }
}

