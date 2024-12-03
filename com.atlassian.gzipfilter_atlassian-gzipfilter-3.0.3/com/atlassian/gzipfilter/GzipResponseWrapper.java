/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletOutputStream
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpServletResponseWrapper
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.gzipfilter;

import com.atlassian.gzipfilter.GzipResponseStream;
import com.atlassian.gzipfilter.util.IOUtils;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GzipResponseWrapper
extends HttpServletResponseWrapper {
    private static final Logger log = LoggerFactory.getLogger(GzipResponseWrapper.class);
    protected HttpServletResponse origResponse = null;
    protected ServletOutputStream stream = null;
    protected PrintWriter writer = null;
    private String encoding;

    public GzipResponseWrapper(HttpServletResponse response, String encoding) {
        super(response);
        this.encoding = encoding;
        this.origResponse = response;
    }

    protected ServletOutputStream createOutputStream() throws IOException {
        return new GzipResponseStream(this.origResponse);
    }

    public void finishResponse() {
        if (this.stream == null) {
            try {
                this.stream = this.createOutputStream();
                this.stream.flush();
            }
            catch (IOException e) {
                log.warn("Was unable to create GzipResponseStream. Invalid gzip stream was sent in response body!", (Throwable)e);
            }
        }
        IOUtils.closeQuietly(this.writer);
        IOUtils.closeQuietly((OutputStream)this.stream);
    }

    public void flushBuffer() throws IOException {
        if (this.stream != null) {
            this.stream.flush();
        }
    }

    public ServletOutputStream getOutputStream() throws IOException {
        if (this.writer != null) {
            throw new IllegalStateException("getWriter() has already been called!");
        }
        if (this.stream == null) {
            this.stream = this.createOutputStream();
        }
        return this.stream;
    }

    public PrintWriter getWriter() throws IOException {
        if (this.writer != null) {
            return this.writer;
        }
        if (this.stream != null) {
            throw new IllegalStateException("getOutputStream() has already been called!");
        }
        this.stream = this.createOutputStream();
        this.writer = new PrintWriter(new OutputStreamWriter((OutputStream)this.stream, this.encoding));
        return this.writer;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }
}

