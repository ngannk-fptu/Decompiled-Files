/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletOutputStream
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpServletResponseWrapper
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.gzipfilter;

import com.atlassian.gzipfilter.GzipResponseWrapper;
import com.atlassian.gzipfilter.RoutablePrintWriter;
import com.atlassian.gzipfilter.RoutableServletOutputStream;
import com.atlassian.gzipfilter.selector.GzipCompatibilitySelector;
import com.atlassian.gzipfilter.util.HttpContentType;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SelectingResponseWrapper
extends HttpServletResponseWrapper {
    private static final Logger log = LoggerFactory.getLogger(SelectingResponseWrapper.class);
    private final RoutablePrintWriter routablePrintWriter;
    private final RoutableServletOutputStream routableServletOutputStream;
    private final GzipCompatibilitySelector compatibilitySelector;
    private final GzipResponseWrapper wrappedResponse;
    private boolean gzippablePage = false;
    private boolean headersCommitted = false;

    public SelectingResponseWrapper(HttpServletResponse unWrappedResponse, GzipCompatibilitySelector compatibilitySelector, String defaultEncoding) {
        super(unWrappedResponse);
        this.wrappedResponse = new GzipResponseWrapper(unWrappedResponse, defaultEncoding);
        this.compatibilitySelector = compatibilitySelector;
        Runnable gzipHeadersCommitter = new Runnable(){

            @Override
            public void run() {
                SelectingResponseWrapper.this.commitGzipHeaders();
            }
        };
        this.routablePrintWriter = new RoutablePrintWriter(new RoutablePrintWriterDestinationFactory((ServletResponse)unWrappedResponse), gzipHeadersCommitter);
        this.routableServletOutputStream = new RoutableServletOutputStream(new RoutableServletOutputStreamDestinationFactory((ServletResponse)unWrappedResponse), gzipHeadersCommitter);
    }

    public void setContentType(String type) {
        super.setContentType(type);
        if (type != null) {
            HttpContentType httpContentType = new HttpContentType(type);
            if (this.compatibilitySelector.shouldGzip(httpContentType.getType())) {
                this.activateGzip(httpContentType.getEncoding());
            } else {
                this.deactivateGzip();
            }
        }
    }

    public void sendRedirect(String location) throws IOException {
        if (!this.wrappedResponse.isCommitted() && this.gzippablePage) {
            this.deactivateGzip();
        }
        super.sendRedirect(location);
    }

    public void setStatus(int statusCode, String sm) {
        super.setStatus(statusCode, sm);
        if (!this.shouldGzip(statusCode)) {
            this.deactivateGzip();
        }
    }

    public void setStatus(int statusCode) {
        super.setStatus(statusCode);
        if (!this.shouldGzip(statusCode)) {
            this.deactivateGzip();
        }
    }

    public void sendError(int sc, String msg) throws IOException {
        if (this.gzippablePage) {
            this.deactivateGzip();
        }
        super.sendError(sc, msg);
    }

    public void sendError(int sc) throws IOException {
        if (this.gzippablePage) {
            this.deactivateGzip();
        }
        super.sendError(sc);
    }

    private boolean shouldGzip(int statusCode) {
        return statusCode != 204 && statusCode != 304;
    }

    private void commitGzipHeaders() {
        if (this.headersCommitted) {
            return;
        }
        if (!this.gzippablePage) {
            log.trace("Not a gzippable page");
            return;
        }
        if (this.wrappedResponse.isCommitted()) {
            log.debug("Response is committed, can't set gzip headers");
            return;
        }
        log.debug("Setting gzip headers");
        this.wrappedResponse.setHeader("Content-Encoding", "gzip");
        this.wrappedResponse.setHeader("Vary", "User-Agent");
        this.headersCommitted = true;
    }

    private void activateGzip(String encoding) {
        if (this.gzippablePage) {
            return;
        }
        if (this.wrappedResponse.isCommitted()) {
            log.debug("Response is committed, gzip can not be activated");
            return;
        }
        if (this.headersCommitted) {
            log.debug("Headers are committed, gzip can not be activated");
            return;
        }
        if (this.wrappedResponse.containsHeader("Content-Length")) {
            log.debug("Gzip compression can not be activated when the Content-Length header has already been set on the response, and therefore uncompressed content will be sent instead");
            return;
        }
        if (encoding != null) {
            this.wrappedResponse.setEncoding(encoding);
        }
        this.routablePrintWriter.updateDestination(new RoutablePrintWriterDestinationFactory((ServletResponse)this.wrappedResponse));
        this.routableServletOutputStream.updateDestination(new RoutableServletOutputStreamDestinationFactory((ServletResponse)this.wrappedResponse));
        this.gzippablePage = true;
        log.debug("gzip activated");
    }

    private void deactivateGzip() {
        this.gzippablePage = false;
        this.routablePrintWriter.updateDestination(new RoutablePrintWriterDestinationFactory(this.getResponse()));
        this.routableServletOutputStream.updateDestination(new RoutableServletOutputStreamDestinationFactory(this.getResponse()));
        log.debug("gzip deactivated");
    }

    public void setContentLength(int contentLength) {
        if (!this.gzippablePage) {
            super.setContentLength(contentLength);
        }
    }

    public void flushBuffer() throws IOException {
        if (!this.gzippablePage) {
            log.debug("Flushing buffer");
            super.flushBuffer();
        }
    }

    public void setHeader(String name, String value) {
        if (name.toLowerCase().equals("content-type")) {
            this.setContentType(value);
        } else if (!this.gzippablePage || !name.toLowerCase().equals("content-length")) {
            super.setHeader(name, value);
        }
    }

    public void addHeader(String name, String value) {
        if (name.toLowerCase().equals("content-type")) {
            this.setContentType(value);
        } else if (!this.gzippablePage || !name.toLowerCase().equals("content-length")) {
            super.addHeader(name, value);
        }
    }

    public ServletOutputStream getOutputStream() {
        return this.routableServletOutputStream;
    }

    public PrintWriter getWriter() {
        return this.routablePrintWriter;
    }

    public void finishResponse() {
        if (this.gzippablePage) {
            this.commitGzipHeaders();
            this.wrappedResponse.finishResponse();
        }
    }

    private static class RoutableServletOutputStreamDestinationFactory
    implements RoutableServletOutputStream.DestinationFactory {
        private final ServletResponse servletResponse;

        public RoutableServletOutputStreamDestinationFactory(ServletResponse servletResponse) {
            this.servletResponse = servletResponse;
        }

        @Override
        public ServletOutputStream create() throws IOException {
            return this.servletResponse.getOutputStream();
        }
    }

    private static class RoutablePrintWriterDestinationFactory
    implements RoutablePrintWriter.DestinationFactory {
        private final ServletResponse servletResponse;

        public RoutablePrintWriterDestinationFactory(ServletResponse servletResponse) {
            this.servletResponse = servletResponse;
        }

        @Override
        public PrintWriter activateDestination() throws IOException {
            return this.servletResponse.getWriter();
        }
    }
}

