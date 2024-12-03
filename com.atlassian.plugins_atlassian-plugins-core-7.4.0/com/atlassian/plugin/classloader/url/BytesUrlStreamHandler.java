/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.classloader.url;

import com.atlassian.plugin.classloader.url.BytesUrlConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

public class BytesUrlStreamHandler
extends URLStreamHandler {
    private final byte[] content;

    public BytesUrlStreamHandler(byte[] content) {
        this.content = content;
    }

    @Override
    public URLConnection openConnection(URL url) {
        return new BytesUrlConnection(url, this.content);
    }
}

