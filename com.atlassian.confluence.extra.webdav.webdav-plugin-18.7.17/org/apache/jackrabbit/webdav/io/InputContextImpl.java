/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.io;

import java.io.InputStream;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import org.apache.jackrabbit.webdav.io.InputContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InputContextImpl
implements InputContext {
    private static Logger log = LoggerFactory.getLogger(InputContextImpl.class);
    private final HttpServletRequest request;
    private final InputStream in;

    public InputContextImpl(HttpServletRequest request, InputStream in) {
        if (request == null) {
            throw new IllegalArgumentException("DavResource and Request must not be null.");
        }
        this.request = request;
        this.in = in;
    }

    @Override
    public boolean hasStream() {
        return this.in != null;
    }

    @Override
    public InputStream getInputStream() {
        return this.in;
    }

    @Override
    public long getModificationTime() {
        return new Date().getTime();
    }

    @Override
    public String getContentLanguage() {
        return this.request.getHeader("Content-Language");
    }

    @Override
    public long getContentLength() {
        String length = this.request.getHeader("Content-Length");
        if (length == null) {
            return -1L;
        }
        try {
            return Long.parseLong(length);
        }
        catch (NumberFormatException ex) {
            log.error("broken Content-Length header: " + length);
            return -1L;
        }
    }

    @Override
    public String getContentType() {
        return this.request.getHeader("Content-Type");
    }

    @Override
    public String getProperty(String propertyName) {
        return this.request.getHeader(propertyName);
    }
}

