/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.Nullable
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.api;

import com.sun.istack.Nullable;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import javax.xml.ws.WebServiceException;

public final class EndpointAddress {
    @Nullable
    private URL url;
    private final URI uri;
    private final String stringForm;
    private volatile boolean dontUseProxyMethod;
    private Proxy proxy;

    public EndpointAddress(URI uri) {
        this.uri = uri;
        this.stringForm = uri.toString();
        try {
            this.initURL();
            this.proxy = this.chooseProxy();
        }
        catch (MalformedURLException malformedURLException) {
            // empty catch block
        }
    }

    public EndpointAddress(String url) throws URISyntaxException {
        this.uri = new URI(url);
        this.stringForm = url;
        try {
            this.initURL();
            this.proxy = this.chooseProxy();
        }
        catch (MalformedURLException malformedURLException) {
            // empty catch block
        }
    }

    private void initURL() throws MalformedURLException {
        String scheme = this.uri.getScheme();
        if (scheme == null) {
            this.url = new URL(this.uri.toString());
            return;
        }
        this.url = "http".equals(scheme = scheme.toLowerCase()) || "https".equals(scheme) ? new URL(this.uri.toASCIIString()) : this.uri.toURL();
    }

    public static EndpointAddress create(String url) {
        try {
            return new EndpointAddress(url);
        }
        catch (URISyntaxException e) {
            throw new WebServiceException("Illegal endpoint address: " + url, (Throwable)e);
        }
    }

    private Proxy chooseProxy() {
        ProxySelector sel = AccessController.doPrivileged(new PrivilegedAction<ProxySelector>(){

            @Override
            public ProxySelector run() {
                return ProxySelector.getDefault();
            }
        });
        if (sel == null) {
            return Proxy.NO_PROXY;
        }
        if (!sel.getClass().getName().equals("sun.net.spi.DefaultProxySelector")) {
            return null;
        }
        Iterator<Proxy> it = sel.select(this.uri).iterator();
        if (it.hasNext()) {
            return it.next();
        }
        return Proxy.NO_PROXY;
    }

    public URL getURL() {
        return this.url;
    }

    public URI getURI() {
        return this.uri;
    }

    public URLConnection openConnection() throws IOException {
        if (this.url == null) {
            throw new WebServiceException("URI=" + this.uri + " doesn't have the corresponding URL");
        }
        if (this.proxy != null && !this.dontUseProxyMethod) {
            try {
                return this.url.openConnection(this.proxy);
            }
            catch (UnsupportedOperationException e) {
                this.dontUseProxyMethod = true;
            }
        }
        return this.url.openConnection();
    }

    public String toString() {
        return this.stringForm;
    }
}

