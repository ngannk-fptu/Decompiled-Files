/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.HttpHost
 *  org.apache.http.annotation.Contract
 *  org.apache.http.annotation.ThreadingBehavior
 *  org.apache.http.util.Args
 *  org.apache.http.util.LangUtils
 */
package org.apache.http.conn.routing;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.http.HttpHost;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.conn.routing.RouteInfo;
import org.apache.http.util.Args;
import org.apache.http.util.LangUtils;

@Contract(threading=ThreadingBehavior.IMMUTABLE)
public final class HttpRoute
implements RouteInfo,
Cloneable {
    private final HttpHost targetHost;
    private final InetAddress localAddress;
    private final List<HttpHost> proxyChain;
    private final RouteInfo.TunnelType tunnelled;
    private final RouteInfo.LayerType layered;
    private final boolean secure;

    private HttpRoute(HttpHost target, InetAddress local, List<HttpHost> proxies, boolean secure, RouteInfo.TunnelType tunnelled, RouteInfo.LayerType layered) {
        Args.notNull((Object)target, (String)"Target host");
        this.targetHost = HttpRoute.normalize(target);
        this.localAddress = local;
        this.proxyChain = proxies != null && !proxies.isEmpty() ? new ArrayList<HttpHost>(proxies) : null;
        if (tunnelled == RouteInfo.TunnelType.TUNNELLED) {
            Args.check((this.proxyChain != null ? 1 : 0) != 0, (String)"Proxy required if tunnelled");
        }
        this.secure = secure;
        this.tunnelled = tunnelled != null ? tunnelled : RouteInfo.TunnelType.PLAIN;
        this.layered = layered != null ? layered : RouteInfo.LayerType.PLAIN;
    }

    private static int getDefaultPort(String schemeName) {
        if ("http".equalsIgnoreCase(schemeName)) {
            return 80;
        }
        if ("https".equalsIgnoreCase(schemeName)) {
            return 443;
        }
        return -1;
    }

    private static HttpHost normalize(HttpHost target) {
        if (target.getPort() >= 0) {
            return target;
        }
        InetAddress address = target.getAddress();
        String schemeName = target.getSchemeName();
        return address != null ? new HttpHost(address, HttpRoute.getDefaultPort(schemeName), schemeName) : new HttpHost(target.getHostName(), HttpRoute.getDefaultPort(schemeName), schemeName);
    }

    public HttpRoute(HttpHost target, InetAddress local, HttpHost[] proxies, boolean secure, RouteInfo.TunnelType tunnelled, RouteInfo.LayerType layered) {
        this(target, local, proxies != null ? Arrays.asList(proxies) : null, secure, tunnelled, layered);
    }

    public HttpRoute(HttpHost target, InetAddress local, HttpHost proxy, boolean secure, RouteInfo.TunnelType tunnelled, RouteInfo.LayerType layered) {
        this(target, local, proxy != null ? Collections.singletonList(proxy) : null, secure, tunnelled, layered);
    }

    public HttpRoute(HttpHost target, InetAddress local, boolean secure) {
        this(target, local, Collections.emptyList(), secure, RouteInfo.TunnelType.PLAIN, RouteInfo.LayerType.PLAIN);
    }

    public HttpRoute(HttpHost target) {
        this(target, null, Collections.emptyList(), false, RouteInfo.TunnelType.PLAIN, RouteInfo.LayerType.PLAIN);
    }

    public HttpRoute(HttpHost target, InetAddress local, HttpHost proxy, boolean secure) {
        this(target, local, Collections.singletonList(Args.notNull((Object)proxy, (String)"Proxy host")), secure, secure ? RouteInfo.TunnelType.TUNNELLED : RouteInfo.TunnelType.PLAIN, secure ? RouteInfo.LayerType.LAYERED : RouteInfo.LayerType.PLAIN);
    }

    public HttpRoute(HttpHost target, HttpHost proxy) {
        this(target, null, proxy, false);
    }

    @Override
    public HttpHost getTargetHost() {
        return this.targetHost;
    }

    @Override
    public InetAddress getLocalAddress() {
        return this.localAddress;
    }

    public InetSocketAddress getLocalSocketAddress() {
        return this.localAddress != null ? new InetSocketAddress(this.localAddress, 0) : null;
    }

    @Override
    public int getHopCount() {
        return this.proxyChain != null ? this.proxyChain.size() + 1 : 1;
    }

    @Override
    public HttpHost getHopTarget(int hop) {
        Args.notNegative((int)hop, (String)"Hop index");
        int hopcount = this.getHopCount();
        Args.check((hop < hopcount ? 1 : 0) != 0, (String)"Hop index exceeds tracked route length");
        return hop < hopcount - 1 ? this.proxyChain.get(hop) : this.targetHost;
    }

    @Override
    public HttpHost getProxyHost() {
        return this.proxyChain != null && !this.proxyChain.isEmpty() ? this.proxyChain.get(0) : null;
    }

    @Override
    public RouteInfo.TunnelType getTunnelType() {
        return this.tunnelled;
    }

    @Override
    public boolean isTunnelled() {
        return this.tunnelled == RouteInfo.TunnelType.TUNNELLED;
    }

    @Override
    public RouteInfo.LayerType getLayerType() {
        return this.layered;
    }

    @Override
    public boolean isLayered() {
        return this.layered == RouteInfo.LayerType.LAYERED;
    }

    @Override
    public boolean isSecure() {
        return this.secure;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof HttpRoute) {
            HttpRoute that = (HttpRoute)obj;
            return this.secure == that.secure && this.tunnelled == that.tunnelled && this.layered == that.layered && LangUtils.equals((Object)this.targetHost, (Object)that.targetHost) && LangUtils.equals((Object)this.localAddress, (Object)that.localAddress) && LangUtils.equals(this.proxyChain, that.proxyChain);
        }
        return false;
    }

    public int hashCode() {
        int hash = 17;
        hash = LangUtils.hashCode((int)hash, (Object)this.targetHost);
        hash = LangUtils.hashCode((int)hash, (Object)this.localAddress);
        if (this.proxyChain != null) {
            for (HttpHost element : this.proxyChain) {
                hash = LangUtils.hashCode((int)hash, (Object)element);
            }
        }
        hash = LangUtils.hashCode((int)hash, (boolean)this.secure);
        hash = LangUtils.hashCode((int)hash, (Object)((Object)this.tunnelled));
        hash = LangUtils.hashCode((int)hash, (Object)((Object)this.layered));
        return hash;
    }

    public String toString() {
        StringBuilder cab = new StringBuilder(50 + this.getHopCount() * 30);
        if (this.localAddress != null) {
            cab.append(this.localAddress);
            cab.append("->");
        }
        cab.append('{');
        if (this.tunnelled == RouteInfo.TunnelType.TUNNELLED) {
            cab.append('t');
        }
        if (this.layered == RouteInfo.LayerType.LAYERED) {
            cab.append('l');
        }
        if (this.secure) {
            cab.append('s');
        }
        cab.append("}->");
        if (this.proxyChain != null) {
            for (HttpHost aProxyChain : this.proxyChain) {
                cab.append(aProxyChain);
                cab.append("->");
            }
        }
        cab.append(this.targetHost);
        return cab.toString();
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}

