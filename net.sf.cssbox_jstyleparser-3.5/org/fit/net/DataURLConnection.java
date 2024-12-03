/*
 * Decompiled with CFR 0.152.
 */
package org.fit.net;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

class DataURLConnection
extends URLConnection {
    private String mime;
    private String charset;
    private byte[] data;

    public DataURLConnection(URL url, String mime, String charset, byte[] data) {
        super(url);
        this.mime = new String(mime);
        this.charset = new String(charset);
        this.data = data;
    }

    protected DataURLConnection(URL url) {
        super(url);
    }

    @Override
    public void connect() throws IOException {
    }

    @Override
    public String getContentEncoding() {
        return this.charset;
    }

    @Override
    public int getContentLength() {
        return this.data.length;
    }

    @Override
    public String getContentType() {
        return this.mime;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(this.data);
    }
}

