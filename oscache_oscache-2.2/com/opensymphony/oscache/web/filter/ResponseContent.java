/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletResponse
 */
package com.opensymphony.oscache.web.filter;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Locale;
import java.util.zip.GZIPInputStream;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public class ResponseContent
implements Serializable {
    private transient ByteArrayOutputStream bout = new ByteArrayOutputStream(1000);
    private Locale locale = null;
    private String contentEncoding = null;
    private String contentType = null;
    private byte[] content = null;
    private long expires = Long.MAX_VALUE;
    private long lastModified = -1L;

    public String getContentType() {
        return this.contentType;
    }

    public void setContentType(String value) {
        this.contentType = value;
    }

    public long getLastModified() {
        return this.lastModified;
    }

    public void setLastModified(long value) {
        this.lastModified = value;
    }

    public String getContentEncoding() {
        return this.contentEncoding;
    }

    public void setContentEncoding(String contentEncoding) {
        this.contentEncoding = contentEncoding;
    }

    public void setLocale(Locale value) {
        this.locale = value;
    }

    public long getExpires() {
        return this.expires;
    }

    public void setExpires(long value) {
        this.expires = value;
    }

    public OutputStream getOutputStream() {
        return this.bout;
    }

    public int getSize() {
        return this.content != null ? this.content.length : -1;
    }

    public void commit() {
        this.content = this.bout.toByteArray();
    }

    public void writeTo(ServletResponse response) throws IOException {
        this.writeTo(response, false, false);
    }

    public void writeTo(ServletResponse response, boolean fragment, boolean acceptsGZip) throws IOException {
        if (this.contentType != null) {
            response.setContentType(this.contentType);
        }
        if (fragment) {
            acceptsGZip = false;
        } else if (response instanceof HttpServletResponse) {
            HttpServletResponse httpResponse = (HttpServletResponse)response;
            if (this.lastModified != -1L) {
                httpResponse.setDateHeader("Last-Modified", this.lastModified);
            }
            if (this.expires != Long.MAX_VALUE) {
                httpResponse.setDateHeader("Expires", this.expires);
            }
        }
        if (this.locale != null) {
            response.setLocale(this.locale);
        }
        BufferedOutputStream out = new BufferedOutputStream((OutputStream)response.getOutputStream());
        if (this.isContentGZiped()) {
            if (acceptsGZip) {
                ((HttpServletResponse)response).addHeader("Content-Encoding", "gzip");
                response.setContentLength(this.content.length);
                ((OutputStream)out).write(this.content);
            } else {
                ByteArrayInputStream bais = new ByteArrayInputStream(this.content);
                GZIPInputStream zis = new GZIPInputStream(bais);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int numBytesRead = 0;
                byte[] tempBytes = new byte[4196];
                while ((numBytesRead = zis.read(tempBytes, 0, tempBytes.length)) != -1) {
                    baos.write(tempBytes, 0, numBytesRead);
                }
                byte[] result = baos.toByteArray();
                response.setContentLength(result.length);
                ((OutputStream)out).write(result);
            }
        } else {
            response.setContentLength(this.content.length);
            ((OutputStream)out).write(this.content);
        }
        ((OutputStream)out).flush();
    }

    public boolean isContentGZiped() {
        return "gzip".equals(this.contentEncoding);
    }
}

