/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletOutputStream
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpServletResponseWrapper
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.opensymphony.oscache.web.filter;

import com.opensymphony.oscache.web.filter.ResponseContent;
import com.opensymphony.oscache.web.filter.SplitServletOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Locale;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CacheHttpServletResponseWrapper
extends HttpServletResponseWrapper {
    private final Log log = LogFactory.getLog(((Object)((Object)this)).getClass());
    private PrintWriter cachedWriter;
    private ResponseContent result = new ResponseContent();
    private SplitServletOutputStream cacheOut = null;
    private boolean fragment = false;
    private int status = 200;
    private long expires = 1L;
    private long lastModified = -1L;

    public CacheHttpServletResponseWrapper(HttpServletResponse response) {
        this(response, false, Long.MAX_VALUE, 1L, -1L);
    }

    public CacheHttpServletResponseWrapper(HttpServletResponse response, boolean fragment, long time, long lastModified, long expires) {
        super(response);
        this.fragment = fragment;
        this.expires = expires;
        this.lastModified = lastModified;
        if (!fragment) {
            if (lastModified == -1L) {
                long current = System.currentTimeMillis() / 1000L;
                this.result.setLastModified(current * 1000L);
                super.setDateHeader("Last-Modified", this.result.getLastModified());
            }
            if (expires == -1L) {
                this.result.setExpires(this.result.getLastModified() + time);
                super.setDateHeader("Expires", this.result.getExpires());
            }
        }
    }

    public ResponseContent getContent() {
        this.result.commit();
        return this.result;
    }

    public void setContentType(String value) {
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)("ContentType: " + value));
        }
        super.setContentType(value);
        this.result.setContentType(value);
    }

    public void setDateHeader(String name, long value) {
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)("dateheader: " + name + ": " + value));
        }
        if (this.lastModified != 0L && "Last-Modified".equalsIgnoreCase(name) && !this.fragment) {
            this.result.setLastModified(value);
        }
        if (this.expires != 0L && "Expires".equalsIgnoreCase(name) && !this.fragment) {
            this.result.setExpires(value);
        }
        super.setDateHeader(name, value);
    }

    public void addDateHeader(String name, long value) {
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)("dateheader: " + name + ": " + value));
        }
        if (this.lastModified != 0L && "Last-Modified".equalsIgnoreCase(name) && !this.fragment) {
            this.result.setLastModified(value);
        }
        if (this.expires != 0L && "Expires".equalsIgnoreCase(name) && !this.fragment) {
            this.result.setExpires(value);
        }
        super.addDateHeader(name, value);
    }

    public void setHeader(String name, String value) {
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)("header: " + name + ": " + value));
        }
        if ("Content-Type".equalsIgnoreCase(name)) {
            this.result.setContentType(value);
        }
        if ("Content-Encoding".equalsIgnoreCase(name)) {
            this.result.setContentEncoding(value);
        }
        super.setHeader(name, value);
    }

    public void addHeader(String name, String value) {
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)("header: " + name + ": " + value));
        }
        if ("Content-Type".equalsIgnoreCase(name)) {
            this.result.setContentType(value);
        }
        if ("Content-Encoding".equalsIgnoreCase(name)) {
            this.result.setContentEncoding(value);
        }
        super.addHeader(name, value);
    }

    public void setIntHeader(String name, int value) {
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)("intheader: " + name + ": " + value));
        }
        super.setIntHeader(name, value);
    }

    public void setStatus(int status) {
        super.setStatus(status);
        this.status = status;
    }

    public void sendError(int status, String string) throws IOException {
        super.sendError(status, string);
        this.status = status;
    }

    public void sendError(int status) throws IOException {
        super.sendError(status);
        this.status = status;
    }

    public void setStatus(int status, String string) {
        super.setStatus(status, string);
        this.status = status;
    }

    public void sendRedirect(String location) throws IOException {
        this.status = 302;
        super.sendRedirect(location);
    }

    public int getStatus() {
        return this.status;
    }

    public void setLocale(Locale value) {
        super.setLocale(value);
        this.result.setLocale(value);
    }

    public ServletOutputStream getOutputStream() throws IOException {
        if (this.cacheOut == null) {
            this.cacheOut = new SplitServletOutputStream(this.result.getOutputStream(), (OutputStream)super.getOutputStream());
        }
        return this.cacheOut;
    }

    public PrintWriter getWriter() throws IOException {
        if (this.cachedWriter == null) {
            String encoding = this.getCharacterEncoding();
            this.cachedWriter = encoding != null ? new PrintWriter(new OutputStreamWriter((OutputStream)this.getOutputStream(), encoding)) : new PrintWriter(new OutputStreamWriter((OutputStream)this.getOutputStream()));
        }
        return this.cachedWriter;
    }

    public void flushBuffer() throws IOException {
        super.flushBuffer();
        if (this.cacheOut != null) {
            this.cacheOut.flush();
        }
        if (this.cachedWriter != null) {
            this.cachedWriter.flush();
        }
    }
}

