/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.client;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Objects;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class VaultEndpoint
implements Serializable {
    public static final String API_VERSION = "v1";
    private String host = "localhost";
    private int port = 8200;
    private String scheme = "https";
    private String path = "v1";

    public static VaultEndpoint create(String host, int port) {
        Assert.hasText(host, "Host must not be empty");
        VaultEndpoint vaultEndpoint = new VaultEndpoint();
        vaultEndpoint.setHost(host);
        vaultEndpoint.setPort(port);
        return vaultEndpoint;
    }

    public static VaultEndpoint from(URI uri) {
        Assert.notNull((Object)uri, "URI must not be null");
        Assert.hasText(uri.getScheme(), "Scheme must not be empty");
        Assert.hasText(uri.getHost(), "Host must not be empty");
        VaultEndpoint vaultEndpoint = new VaultEndpoint();
        vaultEndpoint.setHost(uri.getHost());
        try {
            vaultEndpoint.setPort(uri.getPort() == -1 ? uri.toURL().getDefaultPort() : uri.getPort());
        }
        catch (MalformedURLException e) {
            throw new IllegalArgumentException(String.format("Can't retrieve default port from %s", uri), e);
        }
        vaultEndpoint.setScheme(uri.getScheme());
        String path = VaultEndpoint.getPath(uri);
        if (StringUtils.hasText(path)) {
            vaultEndpoint.setPath(path);
        }
        return vaultEndpoint;
    }

    @Nullable
    private static String getPath(URI uri) {
        String path = uri.getPath();
        return path != null && path.startsWith("/") ? path.substring(1) : path;
    }

    public String getHost() {
        return this.host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return this.port;
    }

    public void setPort(int port) {
        Assert.isTrue(port >= 1 && port <= 65535, "Port must be a valid port in the range between 1 and 65535");
        this.port = port;
    }

    public String getScheme() {
        return this.scheme;
    }

    public void setScheme(String scheme) {
        Assert.isTrue("http".equals(scheme) || "https".equals(scheme), "Scheme must be http or https");
        this.scheme = scheme;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        Assert.hasText(path, "Path must not be null or empty");
        Assert.isTrue(!path.startsWith("/"), () -> String.format("Path %s must not start with a leading slash", path));
        this.path = path;
    }

    public URI createUri(String path) {
        return URI.create(this.createUriString(path));
    }

    public String createUriString(String path) {
        Assert.hasText(path, "Path must not be empty");
        return String.format("%s://%s:%s/%s/%s", this.getScheme(), this.getHost(), this.getPort(), this.getPath(), path);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VaultEndpoint)) {
            return false;
        }
        VaultEndpoint that = (VaultEndpoint)o;
        return this.port == that.port && this.host.equals(that.host) && this.scheme.equals(that.scheme) && this.path.equals(that.path);
    }

    public int hashCode() {
        return Objects.hash(this.host, this.port, this.scheme, this.path);
    }

    public String toString() {
        return String.format("%s://%s:%d", this.scheme, this.host, this.port);
    }
}

