/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.classloader.url;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class BytesUrlConnection
extends URLConnection {
    private final byte[] content;

    public BytesUrlConnection(URL url, byte[] content) {
        super(url);
        this.content = content;
    }

    @Override
    public void connect() {
    }

    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(this.content);
    }
}

