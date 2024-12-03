/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.http.HttpScheme
 *  org.eclipse.jetty.io.ClientConnectionFactory
 *  org.eclipse.jetty.util.BlockingArrayQueue
 *  org.eclipse.jetty.util.HostPort
 *  org.eclipse.jetty.util.ssl.SslContextFactory$Client
 */
package org.eclipse.jetty.client;

import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.eclipse.jetty.client.Origin;
import org.eclipse.jetty.http.HttpScheme;
import org.eclipse.jetty.io.ClientConnectionFactory;
import org.eclipse.jetty.util.BlockingArrayQueue;
import org.eclipse.jetty.util.HostPort;
import org.eclipse.jetty.util.ssl.SslContextFactory;

public class ProxyConfiguration {
    private final List<Proxy> proxies = new BlockingArrayQueue();

    @Deprecated
    public List<Proxy> getProxies() {
        return this.proxies;
    }

    public void addProxy(Proxy proxy) {
        this.proxies.add(Objects.requireNonNull(proxy));
    }

    public boolean removeProxy(Proxy proxy) {
        return this.proxies.remove(proxy);
    }

    public Proxy match(Origin origin) {
        for (Proxy proxy : this.proxies) {
            if (!proxy.matches(origin)) continue;
            return proxy;
        }
        return null;
    }

    public static abstract class Proxy {
        private final Set<String> included = new HashSet<String>();
        private final Set<String> excluded = new HashSet<String>();
        private final Origin origin;
        private final SslContextFactory.Client sslContextFactory;

        protected Proxy(Origin.Address address, boolean secure, SslContextFactory.Client sslContextFactory, Origin.Protocol protocol) {
            this(new Origin(secure ? HttpScheme.HTTPS.asString() : HttpScheme.HTTP.asString(), address, null, protocol), sslContextFactory);
        }

        protected Proxy(Origin origin, SslContextFactory.Client sslContextFactory) {
            this.origin = origin;
            this.sslContextFactory = sslContextFactory;
        }

        public Origin getOrigin() {
            return this.origin;
        }

        public Origin.Address getAddress() {
            return this.origin.getAddress();
        }

        public boolean isSecure() {
            return HttpScheme.HTTPS.is(this.origin.getScheme());
        }

        public SslContextFactory.Client getSslContextFactory() {
            return this.sslContextFactory;
        }

        public Origin.Protocol getProtocol() {
            return this.origin.getProtocol();
        }

        public Set<String> getIncludedAddresses() {
            return this.included;
        }

        public Set<String> getExcludedAddresses() {
            return this.excluded;
        }

        public URI getURI() {
            return null;
        }

        public boolean matches(Origin origin) {
            if (this.getAddress().equals(origin.getAddress())) {
                return false;
            }
            boolean result = this.included.isEmpty();
            Origin.Address address = origin.getAddress();
            for (String included : this.included) {
                if (!this.matches(address, included)) continue;
                result = true;
                break;
            }
            for (String excluded : this.excluded) {
                if (!this.matches(address, excluded)) continue;
                result = false;
                break;
            }
            return result;
        }

        private boolean matches(Origin.Address address, String pattern) {
            HostPort hostPort = new HostPort(pattern);
            String host = hostPort.getHost();
            int port = hostPort.getPort();
            return host.equals(address.getHost()) && (port <= 0 || port == address.getPort());
        }

        public abstract ClientConnectionFactory newClientConnectionFactory(ClientConnectionFactory var1);

        public String toString() {
            return this.origin.toString();
        }
    }
}

