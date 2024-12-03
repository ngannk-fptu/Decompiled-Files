/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletOutputStream
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpServletResponseWrapper
 */
package com.opensymphony.module.sitemesh.filter;

import com.opensymphony.module.sitemesh.Page;
import com.opensymphony.module.sitemesh.PageParserSelector;
import com.opensymphony.module.sitemesh.RequestConstants;
import com.opensymphony.module.sitemesh.SitemeshBuffer;
import com.opensymphony.module.sitemesh.filter.Buffer;
import com.opensymphony.module.sitemesh.filter.HttpContentType;
import com.opensymphony.module.sitemesh.filter.RoutablePrintWriter;
import com.opensymphony.module.sitemesh.filter.RoutableServletOutputStream;
import com.opensymphony.module.sitemesh.scalability.NoopScalabilitySupport;
import com.opensymphony.module.sitemesh.scalability.ScalabilitySupport;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class PageResponseWrapper
extends HttpServletResponseWrapper {
    private final RoutablePrintWriter routablePrintWriter;
    private final RoutableServletOutputStream routableServletOutputStream;
    private final PageParserSelector parserSelector;
    private Buffer buffer;
    private boolean aborted = false;
    private boolean parseablePage = false;
    private final ScalabilitySupport scalabilitySupport;
    private final HttpServletRequest request;

    public PageResponseWrapper(HttpServletResponse response, PageParserSelector parserSelector) {
        this(response, null, new NoopScalabilitySupport(), parserSelector);
    }

    public PageResponseWrapper(HttpServletResponse response, HttpServletRequest request, PageParserSelector parserSelector) {
        this(response, request, new NoopScalabilitySupport(), parserSelector);
    }

    public PageResponseWrapper(HttpServletResponse response, ScalabilitySupport scalabilitySupport, PageParserSelector parserSelector) {
        this(response, null, scalabilitySupport, parserSelector);
    }

    public PageResponseWrapper(final HttpServletResponse response, HttpServletRequest request, ScalabilitySupport scalabilitySupport, PageParserSelector parserSelector) {
        super(response);
        this.request = request;
        this.scalabilitySupport = scalabilitySupport;
        this.parserSelector = parserSelector;
        this.routablePrintWriter = new RoutablePrintWriter(new RoutablePrintWriter.DestinationFactory(){

            public PrintWriter activateDestination() throws IOException {
                return response.getWriter();
            }
        });
        this.routableServletOutputStream = new RoutableServletOutputStream(new RoutableServletOutputStream.DestinationFactory(){

            public ServletOutputStream create() throws IOException {
                return response.getOutputStream();
            }
        });
    }

    public void setContentType(String type) {
        super.setContentType(type);
        if (type != null) {
            HttpContentType httpContentType = new HttpContentType(type);
            if (this.parserSelector.shouldParsePage(httpContentType.getType())) {
                this.activateSiteMesh(httpContentType.getType(), httpContentType.getEncoding());
            } else {
                this.deactivateSiteMesh();
            }
        }
    }

    public void activateSiteMesh(String contentType, String encoding) {
        if (this.parseablePage) {
            return;
        }
        this.buffer = new Buffer(this.parserSelector.getPageParser(contentType), encoding, this.scalabilitySupport);
        this.routablePrintWriter.updateDestination(new RoutablePrintWriter.DestinationFactory(){

            public PrintWriter activateDestination() throws IOException {
                return PageResponseWrapper.this.lazyDisable() ? PageResponseWrapper.this.getResponse().getWriter() : PageResponseWrapper.this.buffer.getWriter();
            }
        });
        this.routableServletOutputStream.updateDestination(new RoutableServletOutputStream.DestinationFactory(){

            public ServletOutputStream create() throws IOException {
                return PageResponseWrapper.this.lazyDisable() ? PageResponseWrapper.this.getResponse().getOutputStream() : PageResponseWrapper.this.buffer.getOutputStream();
            }
        });
        this.parseablePage = true;
    }

    private boolean lazyDisable() {
        if (null != this.request && this.request.getAttribute(RequestConstants.DISABLE_BUFFER_AND_DECORATION) != null) {
            this.parseablePage = false;
            this.buffer = null;
            return true;
        }
        return false;
    }

    private void deactivateSiteMesh() {
        this.parseablePage = false;
        this.buffer = null;
        this.routablePrintWriter.updateDestination(new RoutablePrintWriter.DestinationFactory(){

            public PrintWriter activateDestination() throws IOException {
                return PageResponseWrapper.this.getResponse().getWriter();
            }
        });
        this.routableServletOutputStream.updateDestination(new RoutableServletOutputStream.DestinationFactory(){

            public ServletOutputStream create() throws IOException {
                return PageResponseWrapper.this.getResponse().getOutputStream();
            }
        });
    }

    public void setContentLength(int contentLength) {
        if (!this.parseablePage) {
            super.setContentLength(contentLength);
        }
    }

    public void flushBuffer() throws IOException {
        if (!this.parseablePage) {
            super.flushBuffer();
        }
    }

    public void setHeader(String name, String value) {
        if (name.toLowerCase().equals("content-type")) {
            this.setContentType(value);
        } else if (!this.parseablePage || !name.toLowerCase().equals("content-length")) {
            super.setHeader(name, value);
        }
    }

    public void addHeader(String name, String value) {
        if (name.toLowerCase().equals("content-type")) {
            this.setContentType(value);
        } else if (!this.parseablePage || !name.toLowerCase().equals("content-length")) {
            super.addHeader(name, value);
        }
    }

    public void setStatus(int sc) {
        if (sc == 304) {
            this.aborted = true;
            this.deactivateSiteMesh();
        }
        super.setStatus(sc);
    }

    public ServletOutputStream getOutputStream() {
        return this.routableServletOutputStream;
    }

    public PrintWriter getWriter() {
        return this.routablePrintWriter;
    }

    public Page getPage() throws IOException {
        if (this.aborted || !this.parseablePage) {
            return null;
        }
        return this.buffer.parse();
    }

    public void sendError(int sc) throws IOException {
        this.aborted = true;
        super.sendError(sc);
    }

    public void sendError(int sc, String msg) throws IOException {
        this.aborted = true;
        super.sendError(sc, msg);
    }

    public void sendRedirect(String location) throws IOException {
        this.aborted = true;
        super.sendRedirect(location);
    }

    public boolean isUsingStream() {
        return this.buffer != null && this.buffer.isUsingStream();
    }

    public SitemeshBuffer getContents() throws IOException {
        if (this.aborted || !this.parseablePage) {
            return null;
        }
        return this.buffer.getContents();
    }

    public Buffer getBuffer() {
        return this.buffer;
    }

    public boolean isAborted() {
        return this.aborted;
    }

    public boolean isParseablePage() {
        return this.parseablePage;
    }
}

