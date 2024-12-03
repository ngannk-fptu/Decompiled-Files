/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.httpclient;

import java.net.InetAddress;
import org.apache.commons.httpclient.HttpConnection;
import org.apache.commons.httpclient.HttpHost;
import org.apache.commons.httpclient.ProxyHost;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.params.HostParams;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.util.LangUtils;

public class HostConfiguration
implements Cloneable {
    public static final HostConfiguration ANY_HOST_CONFIGURATION = new HostConfiguration();
    private HttpHost host = null;
    private ProxyHost proxyHost = null;
    private InetAddress localAddress = null;
    private HostParams params = new HostParams();

    public HostConfiguration() {
    }

    public HostConfiguration(HostConfiguration hostConfiguration) {
        this.init(hostConfiguration);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void init(HostConfiguration hostConfiguration) {
        HostConfiguration hostConfiguration2 = hostConfiguration;
        synchronized (hostConfiguration2) {
            try {
                this.host = hostConfiguration.host != null ? (HttpHost)hostConfiguration.host.clone() : null;
                this.proxyHost = hostConfiguration.proxyHost != null ? (ProxyHost)hostConfiguration.proxyHost.clone() : null;
                this.localAddress = hostConfiguration.getLocalAddress();
                this.params = (HostParams)hostConfiguration.getParams().clone();
            }
            catch (CloneNotSupportedException e) {
                throw new IllegalArgumentException("Host configuration could not be cloned");
            }
        }
    }

    public Object clone() {
        HostConfiguration copy;
        try {
            copy = (HostConfiguration)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new IllegalArgumentException("Host configuration could not be cloned");
        }
        copy.init(this);
        return copy;
    }

    public synchronized String toString() {
        boolean appendComma = false;
        StringBuffer b = new StringBuffer(50);
        b.append("HostConfiguration[");
        if (this.host != null) {
            appendComma = true;
            b.append("host=").append(this.host);
        }
        if (this.proxyHost != null) {
            if (appendComma) {
                b.append(", ");
            } else {
                appendComma = true;
            }
            b.append("proxyHost=").append(this.proxyHost);
        }
        if (this.localAddress != null) {
            if (appendComma) {
                b.append(", ");
            } else {
                appendComma = true;
            }
            b.append("localAddress=").append(this.localAddress);
            if (appendComma) {
                b.append(", ");
            } else {
                appendComma = true;
            }
            b.append("params=").append(this.params);
        }
        b.append("]");
        return b.toString();
    }

    public synchronized boolean hostEquals(HttpConnection connection) {
        if (connection == null) {
            throw new IllegalArgumentException("Connection may not be null");
        }
        if (this.host != null) {
            if (!this.host.getHostName().equalsIgnoreCase(connection.getHost())) {
                return false;
            }
            if (this.host.getPort() != connection.getPort()) {
                return false;
            }
            if (!this.host.getProtocol().equals(connection.getProtocol())) {
                return false;
            }
            return !(this.localAddress != null ? !this.localAddress.equals(connection.getLocalAddress()) : connection.getLocalAddress() != null);
        }
        return false;
    }

    public synchronized boolean proxyEquals(HttpConnection connection) {
        if (connection == null) {
            throw new IllegalArgumentException("Connection may not be null");
        }
        if (this.proxyHost != null) {
            return this.proxyHost.getHostName().equalsIgnoreCase(connection.getProxyHost()) && this.proxyHost.getPort() == connection.getProxyPort();
        }
        return connection.getProxyHost() == null;
    }

    public synchronized boolean isHostSet() {
        return this.host != null;
    }

    public synchronized void setHost(HttpHost host) {
        this.host = host;
    }

    public synchronized void setHost(String host, int port, String protocol) {
        this.host = new HttpHost(host, port, Protocol.getProtocol(protocol));
    }

    public synchronized void setHost(String host, String virtualHost, int port, Protocol protocol) {
        this.setHost(host, port, protocol);
        this.params.setVirtualHost(virtualHost);
    }

    public synchronized void setHost(String host, int port, Protocol protocol) {
        if (host == null) {
            throw new IllegalArgumentException("host must not be null");
        }
        if (protocol == null) {
            throw new IllegalArgumentException("protocol must not be null");
        }
        this.host = new HttpHost(host, port, protocol);
    }

    public synchronized void setHost(String host, int port) {
        this.setHost(host, port, Protocol.getProtocol("http"));
    }

    public synchronized void setHost(String host) {
        Protocol defaultProtocol = Protocol.getProtocol("http");
        this.setHost(host, defaultProtocol.getDefaultPort(), defaultProtocol);
    }

    public synchronized void setHost(URI uri) {
        try {
            this.setHost(uri.getHost(), uri.getPort(), uri.getScheme());
        }
        catch (URIException e) {
            throw new IllegalArgumentException(e.toString());
        }
    }

    public synchronized String getHostURL() {
        if (this.host == null) {
            throw new IllegalStateException("Host must be set to create a host URL");
        }
        return this.host.toURI();
    }

    public synchronized String getHost() {
        if (this.host != null) {
            return this.host.getHostName();
        }
        return null;
    }

    public synchronized String getVirtualHost() {
        return this.params.getVirtualHost();
    }

    public synchronized int getPort() {
        if (this.host != null) {
            return this.host.getPort();
        }
        return -1;
    }

    public synchronized Protocol getProtocol() {
        if (this.host != null) {
            return this.host.getProtocol();
        }
        return null;
    }

    public synchronized boolean isProxySet() {
        return this.proxyHost != null;
    }

    public synchronized void setProxyHost(ProxyHost proxyHost) {
        this.proxyHost = proxyHost;
    }

    public synchronized void setProxy(String proxyHost, int proxyPort) {
        this.proxyHost = new ProxyHost(proxyHost, proxyPort);
    }

    public synchronized String getProxyHost() {
        if (this.proxyHost != null) {
            return this.proxyHost.getHostName();
        }
        return null;
    }

    public synchronized int getProxyPort() {
        if (this.proxyHost != null) {
            return this.proxyHost.getPort();
        }
        return -1;
    }

    public synchronized void setLocalAddress(InetAddress localAddress) {
        this.localAddress = localAddress;
    }

    public synchronized InetAddress getLocalAddress() {
        return this.localAddress;
    }

    public HostParams getParams() {
        return this.params;
    }

    public void setParams(HostParams params) {
        if (params == null) {
            throw new IllegalArgumentException("Parameters may not be null");
        }
        this.params = params;
    }

    public synchronized boolean equals(Object o) {
        if (o instanceof HostConfiguration) {
            if (o == this) {
                return true;
            }
            HostConfiguration that = (HostConfiguration)o;
            return LangUtils.equals(this.host, that.host) && LangUtils.equals(this.proxyHost, that.proxyHost) && LangUtils.equals(this.localAddress, that.localAddress);
        }
        return false;
    }

    public synchronized int hashCode() {
        int hash = 17;
        hash = LangUtils.hashCode(hash, this.host);
        hash = LangUtils.hashCode(hash, this.proxyHost);
        hash = LangUtils.hashCode(hash, this.localAddress);
        return hash;
    }
}

