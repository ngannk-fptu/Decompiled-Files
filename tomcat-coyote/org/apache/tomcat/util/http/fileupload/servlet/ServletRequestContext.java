/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package org.apache.tomcat.util.http.fileupload.servlet;

import java.io.IOException;
import java.io.InputStream;
import javax.servlet.http.HttpServletRequest;
import org.apache.tomcat.util.http.fileupload.UploadContext;

public class ServletRequestContext
implements UploadContext {
    private final HttpServletRequest request;

    public ServletRequestContext(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public String getCharacterEncoding() {
        return this.request.getCharacterEncoding();
    }

    @Override
    public String getContentType() {
        return this.request.getContentType();
    }

    @Override
    public long contentLength() {
        long size;
        try {
            size = Long.parseLong(this.request.getHeader("Content-length"));
        }
        catch (NumberFormatException e) {
            size = this.request.getContentLength();
        }
        return size;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return this.request.getInputStream();
    }

    public String toString() {
        return String.format("ContentLength=%s, ContentType=%s", this.contentLength(), this.getContentType());
    }
}

