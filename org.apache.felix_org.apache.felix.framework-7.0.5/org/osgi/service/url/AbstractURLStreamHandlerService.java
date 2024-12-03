/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.annotation.versioning.ConsumerType
 */
package org.osgi.service.url;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import org.osgi.annotation.versioning.ConsumerType;
import org.osgi.service.url.URLStreamHandlerService;
import org.osgi.service.url.URLStreamHandlerSetter;

@ConsumerType
public abstract class AbstractURLStreamHandlerService
extends URLStreamHandler
implements URLStreamHandlerService {
    protected volatile URLStreamHandlerSetter realHandler;

    @Override
    public abstract URLConnection openConnection(URL var1) throws IOException;

    @Override
    public void parseURL(URLStreamHandlerSetter realHandler, URL u, String spec, int start, int limit) {
        this.realHandler = realHandler;
        this.parseURL(u, spec, start, limit);
    }

    @Override
    public String toExternalForm(URL u) {
        return super.toExternalForm(u);
    }

    @Override
    public boolean equals(URL u1, URL u2) {
        return super.equals(u1, u2);
    }

    @Override
    public int getDefaultPort() {
        return super.getDefaultPort();
    }

    @Override
    public InetAddress getHostAddress(URL u) {
        return super.getHostAddress(u);
    }

    @Override
    public int hashCode(URL u) {
        return super.hashCode(u);
    }

    @Override
    public boolean hostsEqual(URL u1, URL u2) {
        return super.hostsEqual(u1, u2);
    }

    @Override
    public boolean sameFile(URL u1, URL u2) {
        return super.sameFile(u1, u2);
    }

    @Override
    protected void setURL(URL u, String proto, String host, int port, String file, String ref) {
        this.realHandler.setURL(u, proto, host, port, file, ref);
    }

    @Override
    protected void setURL(URL u, String proto, String host, int port, String auth, String user, String path, String query, String ref) {
        this.realHandler.setURL(u, proto, host, port, auth, user, path, query, ref);
    }
}

