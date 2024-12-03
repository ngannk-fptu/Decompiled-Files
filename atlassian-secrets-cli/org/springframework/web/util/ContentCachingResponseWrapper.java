/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletOutputStream
 *  javax.servlet.WriteListener
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpServletResponseWrapper
 */
package org.springframework.web.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import org.springframework.lang.Nullable;
import org.springframework.util.FastByteArrayOutputStream;

public class ContentCachingResponseWrapper
extends HttpServletResponseWrapper {
    private final FastByteArrayOutputStream content = new FastByteArrayOutputStream(1024);
    @Nullable
    private ServletOutputStream outputStream;
    @Nullable
    private PrintWriter writer;
    private int statusCode = 200;
    @Nullable
    private Integer contentLength;

    public ContentCachingResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    public void setStatus(int sc) {
        super.setStatus(sc);
        this.statusCode = sc;
    }

    public void setStatus(int sc, String sm) {
        super.setStatus(sc, sm);
        this.statusCode = sc;
    }

    public void sendError(int sc) throws IOException {
        this.copyBodyToResponse(false);
        try {
            super.sendError(sc);
        }
        catch (IllegalStateException ex) {
            super.setStatus(sc);
        }
        this.statusCode = sc;
    }

    public void sendError(int sc, String msg) throws IOException {
        this.copyBodyToResponse(false);
        try {
            super.sendError(sc, msg);
        }
        catch (IllegalStateException ex) {
            super.setStatus(sc, msg);
        }
        this.statusCode = sc;
    }

    public void sendRedirect(String location) throws IOException {
        this.copyBodyToResponse(false);
        super.sendRedirect(location);
    }

    public ServletOutputStream getOutputStream() throws IOException {
        if (this.outputStream == null) {
            this.outputStream = new ResponseServletOutputStream(this.getResponse().getOutputStream());
        }
        return this.outputStream;
    }

    public PrintWriter getWriter() throws IOException {
        if (this.writer == null) {
            String characterEncoding = this.getCharacterEncoding();
            this.writer = characterEncoding != null ? new ResponsePrintWriter(characterEncoding) : new ResponsePrintWriter("ISO-8859-1");
        }
        return this.writer;
    }

    public void flushBuffer() throws IOException {
    }

    public void setContentLength(int len) {
        if (len > this.content.size()) {
            this.content.resize(len);
        }
        this.contentLength = len;
    }

    public void setContentLengthLong(long len) {
        if (len > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Content-Length exceeds ContentCachingResponseWrapper's maximum (2147483647): " + len);
        }
        int lenInt = (int)len;
        if (lenInt > this.content.size()) {
            this.content.resize(lenInt);
        }
        this.contentLength = lenInt;
    }

    public void setBufferSize(int size) {
        if (size > this.content.size()) {
            this.content.resize(size);
        }
    }

    public void resetBuffer() {
        this.content.reset();
    }

    public void reset() {
        super.reset();
        this.content.reset();
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public byte[] getContentAsByteArray() {
        return this.content.toByteArray();
    }

    public InputStream getContentInputStream() {
        return this.content.getInputStream();
    }

    public int getContentSize() {
        return this.content.size();
    }

    public void copyBodyToResponse() throws IOException {
        this.copyBodyToResponse(true);
    }

    protected void copyBodyToResponse(boolean complete) throws IOException {
        if (this.content.size() > 0) {
            HttpServletResponse rawResponse = (HttpServletResponse)this.getResponse();
            if ((complete || this.contentLength != null) && !rawResponse.isCommitted()) {
                rawResponse.setContentLength(complete ? this.content.size() : this.contentLength.intValue());
                this.contentLength = null;
            }
            this.content.writeTo((OutputStream)rawResponse.getOutputStream());
            this.content.reset();
            if (complete) {
                super.flushBuffer();
            }
        }
    }

    private class ResponsePrintWriter
    extends PrintWriter {
        public ResponsePrintWriter(String characterEncoding) throws UnsupportedEncodingException {
            super(new OutputStreamWriter((OutputStream)ContentCachingResponseWrapper.this.content, characterEncoding));
        }

        @Override
        public void write(char[] buf, int off, int len) {
            super.write(buf, off, len);
            super.flush();
        }

        @Override
        public void write(String s, int off, int len) {
            super.write(s, off, len);
            super.flush();
        }

        @Override
        public void write(int c) {
            super.write(c);
            super.flush();
        }
    }

    private class ResponseServletOutputStream
    extends ServletOutputStream {
        private final ServletOutputStream os;

        public ResponseServletOutputStream(ServletOutputStream os) {
            this.os = os;
        }

        public void write(int b) throws IOException {
            ContentCachingResponseWrapper.this.content.write(b);
        }

        public void write(byte[] b, int off, int len) throws IOException {
            ContentCachingResponseWrapper.this.content.write(b, off, len);
        }

        public boolean isReady() {
            return this.os.isReady();
        }

        public void setWriteListener(WriteListener writeListener) {
            this.os.setWriteListener(writeListener);
        }
    }
}

