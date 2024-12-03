/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.httpclient.api.factory;

import javax.annotation.Nonnull;

public class Host {
    private final String host;
    private final int port;

    public Host(@Nonnull String host, int port) {
        if (host == null || host.trim().length() == 0) {
            throw new IllegalArgumentException("Host must not be null or empty");
        }
        if (port <= 0 || port > 65535) {
            throw new IllegalArgumentException("Port must be greater than 0 and less than 65535");
        }
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return this.host;
    }

    public int getPort() {
        return this.port;
    }

    public String toString() {
        return "Host{host='" + this.host + '\'' + ", port=" + this.port + '}';
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Host host1 = (Host)o;
        if (this.port != host1.port) {
            return false;
        }
        return this.host.equals(host1.host);
    }

    public int hashCode() {
        int result = this.host.hashCode();
        result = 31 * result + this.port;
        return result;
    }
}

