/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.io;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;

public final class URLConnectionOptions {
    public static final URLConnectionOptions DEFAULT = new URLConnectionOptions();
    private boolean allowUserInteraction;
    private int connectTimeoutMillis;
    private int readTimeoutMillis;
    private boolean useCaches = true;

    public URLConnectionOptions() {
    }

    public URLConnectionOptions(URLConnectionOptions urlConnectionOptions) {
        this.allowUserInteraction = urlConnectionOptions.getAllowUserInteraction();
        this.useCaches = urlConnectionOptions.getUseCaches();
        this.connectTimeoutMillis = urlConnectionOptions.getConnectTimeoutMillis();
        this.readTimeoutMillis = urlConnectionOptions.getReadTimeoutMillis();
    }

    public URLConnection apply(URLConnection urlConnection) {
        urlConnection.setUseCaches(this.useCaches);
        urlConnection.setConnectTimeout(this.connectTimeoutMillis);
        urlConnection.setReadTimeout(this.readTimeoutMillis);
        return urlConnection;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof URLConnectionOptions)) {
            return false;
        }
        URLConnectionOptions other = (URLConnectionOptions)obj;
        return this.allowUserInteraction == other.allowUserInteraction && this.connectTimeoutMillis == other.connectTimeoutMillis && this.readTimeoutMillis == other.readTimeoutMillis && this.useCaches == other.useCaches;
    }

    public boolean getAllowUserInteraction() {
        return this.allowUserInteraction;
    }

    public int getConnectTimeoutMillis() {
        return this.connectTimeoutMillis;
    }

    public int getReadTimeoutMillis() {
        return this.readTimeoutMillis;
    }

    public boolean getUseCaches() {
        return this.useCaches;
    }

    public int hashCode() {
        return Objects.hash(this.allowUserInteraction, this.connectTimeoutMillis, this.readTimeoutMillis, this.useCaches);
    }

    public URLConnection openConnection(URL url) throws IOException {
        return this.apply(url.openConnection());
    }

    public URLConnectionOptions setAllowUserInteraction(boolean allowUserInteraction) {
        this.allowUserInteraction = allowUserInteraction;
        return this;
    }

    public URLConnectionOptions setConnectTimeoutMillis(int connectTimeoutMillis) {
        this.connectTimeoutMillis = connectTimeoutMillis;
        return this;
    }

    public URLConnectionOptions setReadTimeoutMillis(int readTimeoutMillis) {
        this.readTimeoutMillis = readTimeoutMillis;
        return this;
    }

    public URLConnectionOptions setUseCaches(boolean useCaches) {
        this.useCaches = useCaches;
        return this;
    }

    public String toString() {
        return "URLConnectionOptions [allowUserInteraction=" + this.allowUserInteraction + ", connectTimeoutMillis=" + this.connectTimeoutMillis + ", readTimeoutMillis=" + this.readTimeoutMillis + ", useCaches=" + this.useCaches + "]";
    }
}

