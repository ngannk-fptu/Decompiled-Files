/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.portlet.ActionRequest
 */
package org.apache.commons.fileupload.portlet;

import java.io.IOException;
import java.io.InputStream;
import javax.portlet.ActionRequest;
import org.apache.commons.fileupload.UploadContext;

public class PortletRequestContext
implements UploadContext {
    private final ActionRequest request;

    public PortletRequestContext(ActionRequest request) {
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
    @Deprecated
    public int getContentLength() {
        return this.request.getContentLength();
    }

    @Override
    public long contentLength() {
        long size;
        try {
            size = Long.parseLong(this.request.getProperty("Content-length"));
        }
        catch (NumberFormatException e) {
            size = this.request.getContentLength();
        }
        return size;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return this.request.getPortletInputStream();
    }

    public String toString() {
        return String.format("ContentLength=%s, ContentType=%s", this.contentLength(), this.getContentType());
    }
}

