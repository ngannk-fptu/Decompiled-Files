/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletResponse
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.io;

import java.io.OutputStream;
import javax.servlet.http.HttpServletResponse;
import org.apache.jackrabbit.webdav.io.OutputContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OutputContextImpl
implements OutputContext {
    private static Logger log = LoggerFactory.getLogger(OutputContextImpl.class);
    private final HttpServletResponse response;
    private final OutputStream out;

    public OutputContextImpl(HttpServletResponse response, OutputStream out) {
        if (response == null) {
            throw new IllegalArgumentException("Response must not be null.");
        }
        this.response = response;
        this.out = out;
    }

    @Override
    public boolean hasStream() {
        return this.out != null;
    }

    @Override
    public OutputStream getOutputStream() {
        return this.out;
    }

    @Override
    public void setContentLanguage(String contentLanguage) {
        if (contentLanguage != null) {
            this.response.setHeader("Content-Language", contentLanguage);
        }
    }

    @Override
    public void setContentLength(long contentLength) {
        if (contentLength >= 0L) {
            this.response.setContentLengthLong(contentLength);
        }
    }

    @Override
    public void setContentType(String contentType) {
        if (contentType != null) {
            this.response.setContentType(contentType);
        }
    }

    @Override
    public void setModificationTime(long modificationTime) {
        if (modificationTime >= 0L) {
            this.response.addDateHeader("Last-Modified", modificationTime);
        }
    }

    @Override
    public void setETag(String etag) {
        if (etag != null) {
            this.response.setHeader("ETag", etag);
        }
    }

    @Override
    public void setProperty(String propertyName, String propertyValue) {
        if (propertyName != null && propertyValue != null) {
            this.response.setHeader(propertyName, propertyValue);
        }
    }
}

