/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.reactor;

import org.apache.hc.core5.annotation.Internal;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.net.NamedEndpoint;
import org.apache.hc.core5.net.Ports;
import org.apache.hc.core5.util.Args;

@Internal
public final class EndpointParameters
implements NamedEndpoint {
    private final String scheme;
    private final String hostName;
    private final int port;
    private final Object attachment;

    public EndpointParameters(String scheme, String hostName, int port, Object attachment) {
        this.scheme = Args.notBlank(scheme, "Protocol scheme");
        this.hostName = Args.notBlank(hostName, "Endpoint name");
        this.port = Ports.checkWithDefault(port);
        this.attachment = attachment;
    }

    public EndpointParameters(HttpHost host, Object attachment) {
        Args.notNull(host, "HTTP host");
        this.scheme = host.getSchemeName();
        this.hostName = host.getHostName();
        this.port = host.getPort();
        this.attachment = attachment;
    }

    public String getScheme() {
        return this.scheme;
    }

    @Override
    public String getHostName() {
        return this.hostName;
    }

    @Override
    public int getPort() {
        return this.port;
    }

    public Object getAttachment() {
        return this.attachment;
    }

    public String toString() {
        return "EndpointParameters{scheme='" + this.scheme + '\'' + ", name='" + this.hostName + '\'' + ", port=" + this.port + ", attachment=" + this.attachment + '}';
    }
}

