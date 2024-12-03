/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.buf.UriUtil
 */
package org.apache.catalina.webresources.war;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.Permission;
import org.apache.tomcat.util.buf.UriUtil;

public class WarURLConnection
extends URLConnection {
    private final URLConnection wrappedJarUrlConnection;
    private boolean connected;

    protected WarURLConnection(URL url) throws IOException {
        super(url);
        URL innerJarUrl = UriUtil.warToJar((URL)url);
        this.wrappedJarUrlConnection = innerJarUrl.openConnection();
    }

    @Override
    public void connect() throws IOException {
        if (!this.connected) {
            this.wrappedJarUrlConnection.connect();
            this.connected = true;
        }
    }

    @Override
    public InputStream getInputStream() throws IOException {
        this.connect();
        return this.wrappedJarUrlConnection.getInputStream();
    }

    @Override
    public Permission getPermission() throws IOException {
        return this.wrappedJarUrlConnection.getPermission();
    }

    @Override
    public long getLastModified() {
        return this.wrappedJarUrlConnection.getLastModified();
    }

    @Override
    public int getContentLength() {
        return this.wrappedJarUrlConnection.getContentLength();
    }

    @Override
    public long getContentLengthLong() {
        return this.wrappedJarUrlConnection.getContentLengthLong();
    }
}

