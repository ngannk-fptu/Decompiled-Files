/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.client5.http.entity.mime;

public interface ContentDescriptor {
    public String getMimeType();

    public String getMediaType();

    public String getSubType();

    public String getCharset();

    public long getContentLength();
}

