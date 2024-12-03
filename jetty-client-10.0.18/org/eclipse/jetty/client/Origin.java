/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.util.HostPort
 *  org.eclipse.jetty.util.URIUtil
 */
package org.eclipse.jetty.client;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;
import java.util.Objects;
import org.eclipse.jetty.util.HostPort;
import org.eclipse.jetty.util.URIUtil;

public class Origin {
    private final String scheme;
    private final Address address;
    private final Object tag;
    private final Protocol protocol;

    public Origin(String scheme, String host, int port) {
        this(scheme, host, port, null);
    }

    public Origin(String scheme, String host, int port, Object tag) {
        this(scheme, new Address(host, port), tag);
    }

    public Origin(String scheme, String host, int port, Object tag, Protocol protocol) {
        this(scheme, new Address(host, port), tag, protocol);
    }

    public Origin(String scheme, Address address) {
        this(scheme, address, null);
    }

    public Origin(String scheme, Address address, Object tag) {
        this(scheme, address, tag, null);
    }

    public Origin(String scheme, Address address, Object tag, Protocol protocol) {
        this.scheme = Objects.requireNonNull(scheme);
        this.address = address;
        this.tag = tag;
        this.protocol = protocol;
    }

    public String getScheme() {
        return this.scheme;
    }

    public Address getAddress() {
        return this.address;
    }

    public Object getTag() {
        return this.tag;
    }

    public Protocol getProtocol() {
        return this.protocol;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        Origin that = (Origin)obj;
        return this.scheme.equals(that.scheme) && this.address.equals(that.address) && Objects.equals(this.tag, that.tag) && Objects.equals(this.protocol, that.protocol);
    }

    public int hashCode() {
        return Objects.hash(this.scheme, this.address, this.tag, this.protocol);
    }

    public String asString() {
        StringBuilder result = new StringBuilder();
        URIUtil.appendSchemeHostPort((StringBuilder)result, (String)this.scheme, (String)this.address.host, (int)this.address.port);
        return result.toString();
    }

    public String toString() {
        return String.format("%s@%x[%s,tag=%s,protocol=%s]", this.getClass().getSimpleName(), this.hashCode(), this.asString(), this.getTag(), this.getProtocol());
    }

    public static class Address {
        private final String host;
        private final int port;
        private final SocketAddress address;

        public Address(String host, int port) {
            this.host = HostPort.normalizeHost((String)Objects.requireNonNull(host));
            this.port = port;
            this.address = InetSocketAddress.createUnresolved(this.getHost(), this.getPort());
        }

        public String getHost() {
            return this.host;
        }

        public int getPort() {
            return this.port;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || this.getClass() != obj.getClass()) {
                return false;
            }
            Address that = (Address)obj;
            return this.host.equals(that.host) && this.port == that.port;
        }

        public int hashCode() {
            return Objects.hash(this.host, this.port);
        }

        public String asString() {
            return String.format("%s:%d", this.host, this.port);
        }

        public SocketAddress getSocketAddress() {
            return this.address;
        }

        public String toString() {
            return this.asString();
        }
    }

    public static class Protocol {
        private final List<String> protocols;
        private final boolean negotiate;

        public Protocol(List<String> protocols, boolean negotiate) {
            this.protocols = protocols;
            this.negotiate = negotiate;
        }

        public List<String> getProtocols() {
            return this.protocols;
        }

        public boolean isNegotiate() {
            return this.negotiate;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || this.getClass() != obj.getClass()) {
                return false;
            }
            Protocol that = (Protocol)obj;
            return this.protocols.equals(that.protocols) && this.negotiate == that.negotiate;
        }

        public int hashCode() {
            return Objects.hash(this.protocols, this.negotiate);
        }

        public String asString() {
            return String.format("proto=%s,nego=%b", this.protocols, this.negotiate);
        }

        public String toString() {
            return String.format("%s@%x[%s]", this.getClass().getSimpleName(), this.hashCode(), this.asString());
        }
    }
}

