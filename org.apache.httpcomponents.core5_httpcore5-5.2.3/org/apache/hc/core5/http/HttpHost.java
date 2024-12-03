/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.http.URIScheme;
import org.apache.hc.core5.net.Host;
import org.apache.hc.core5.net.NamedEndpoint;
import org.apache.hc.core5.net.URIAuthority;
import org.apache.hc.core5.util.Args;
import org.apache.hc.core5.util.LangUtils;
import org.apache.hc.core5.util.TextUtils;

@Contract(threading=ThreadingBehavior.IMMUTABLE)
public final class HttpHost
implements NamedEndpoint,
Serializable {
    private static final long serialVersionUID = -7529410654042457626L;
    public static final URIScheme DEFAULT_SCHEME = URIScheme.HTTP;
    private final String schemeName;
    private final Host host;
    private final InetAddress address;

    public HttpHost(String scheme, InetAddress address, String hostname, int port) {
        Args.containsNoBlanks(hostname, "Host name");
        this.host = new Host(hostname, port);
        this.schemeName = scheme != null ? TextUtils.toLowerCase(scheme) : HttpHost.DEFAULT_SCHEME.id;
        this.address = address;
    }

    public HttpHost(String scheme, String hostname, int port) {
        this(scheme, null, hostname, port);
    }

    public HttpHost(String hostname, int port) {
        this(null, hostname, port);
    }

    public HttpHost(String scheme, String hostname) {
        this(scheme, hostname, -1);
    }

    public static HttpHost create(String s) throws URISyntaxException {
        Args.notEmpty(s, "HTTP Host");
        String text = s;
        String scheme = null;
        int schemeIdx = text.indexOf("://");
        if (schemeIdx > 0) {
            scheme = text.substring(0, schemeIdx);
            if (TextUtils.containsBlanks(scheme)) {
                throw new URISyntaxException(s, "scheme contains blanks");
            }
            text = text.substring(schemeIdx + 3);
        }
        Host host = Host.create(text);
        return new HttpHost(scheme, host);
    }

    public static HttpHost create(URI uri) {
        String scheme = uri.getScheme();
        return new HttpHost(scheme != null ? scheme : URIScheme.HTTP.getId(), uri.getHost(), uri.getPort());
    }

    public HttpHost(String hostname) {
        this(null, hostname, -1);
    }

    public HttpHost(String scheme, InetAddress address, int port) {
        this(scheme, Args.notNull(address, "Inet address"), address.getHostName(), port);
    }

    public HttpHost(InetAddress address, int port) {
        this(null, address, port);
    }

    public HttpHost(InetAddress address) {
        this(null, address, -1);
    }

    public HttpHost(String scheme, NamedEndpoint namedEndpoint) {
        this(scheme, Args.notNull(namedEndpoint, "Named endpoint").getHostName(), namedEndpoint.getPort());
    }

    @Deprecated
    public HttpHost(URIAuthority authority) {
        this(null, authority);
    }

    @Override
    public String getHostName() {
        return this.host.getHostName();
    }

    @Override
    public int getPort() {
        return this.host.getPort();
    }

    public String getSchemeName() {
        return this.schemeName;
    }

    public InetAddress getAddress() {
        return this.address;
    }

    public String toURI() {
        StringBuilder buffer = new StringBuilder();
        buffer.append(this.schemeName);
        buffer.append("://");
        buffer.append(this.host.toString());
        return buffer.toString();
    }

    public String toHostString() {
        return this.host.toString();
    }

    public String toString() {
        return this.toURI();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof HttpHost) {
            HttpHost that = (HttpHost)obj;
            return this.schemeName.equals(that.schemeName) && this.host.equals(that.host) && Objects.equals(this.address, that.address);
        }
        return false;
    }

    public int hashCode() {
        int hash = 17;
        hash = LangUtils.hashCode(hash, this.schemeName);
        hash = LangUtils.hashCode(hash, this.host);
        hash = LangUtils.hashCode(hash, this.address);
        return hash;
    }
}

