/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.HttpHost
 *  org.apache.http.util.Args
 *  org.apache.http.util.Asserts
 *  org.apache.http.util.LangUtils
 */
package org.apache.http.conn.routing;

import java.net.InetAddress;
import org.apache.http.HttpHost;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.routing.RouteInfo;
import org.apache.http.util.Args;
import org.apache.http.util.Asserts;
import org.apache.http.util.LangUtils;

public final class RouteTracker
implements RouteInfo,
Cloneable {
    private final HttpHost targetHost;
    private final InetAddress localAddress;
    private boolean connected;
    private HttpHost[] proxyChain;
    private RouteInfo.TunnelType tunnelled;
    private RouteInfo.LayerType layered;
    private boolean secure;

    public RouteTracker(HttpHost target, InetAddress local) {
        Args.notNull((Object)target, (String)"Target host");
        this.targetHost = target;
        this.localAddress = local;
        this.tunnelled = RouteInfo.TunnelType.PLAIN;
        this.layered = RouteInfo.LayerType.PLAIN;
    }

    public void reset() {
        this.connected = false;
        this.proxyChain = null;
        this.tunnelled = RouteInfo.TunnelType.PLAIN;
        this.layered = RouteInfo.LayerType.PLAIN;
        this.secure = false;
    }

    public RouteTracker(HttpRoute route) {
        this(route.getTargetHost(), route.getLocalAddress());
    }

    public void connectTarget(boolean secure) {
        Asserts.check((!this.connected ? 1 : 0) != 0, (String)"Already connected");
        this.connected = true;
        this.secure = secure;
    }

    public void connectProxy(HttpHost proxy, boolean secure) {
        Args.notNull((Object)proxy, (String)"Proxy host");
        Asserts.check((!this.connected ? 1 : 0) != 0, (String)"Already connected");
        this.connected = true;
        this.proxyChain = new HttpHost[]{proxy};
        this.secure = secure;
    }

    public void tunnelTarget(boolean secure) {
        Asserts.check((boolean)this.connected, (String)"No tunnel unless connected");
        Asserts.notNull((Object)this.proxyChain, (String)"No tunnel without proxy");
        this.tunnelled = RouteInfo.TunnelType.TUNNELLED;
        this.secure = secure;
    }

    public void tunnelProxy(HttpHost proxy, boolean secure) {
        Args.notNull((Object)proxy, (String)"Proxy host");
        Asserts.check((boolean)this.connected, (String)"No tunnel unless connected");
        Asserts.notNull((Object)this.proxyChain, (String)"No tunnel without proxy");
        HttpHost[] proxies = new HttpHost[this.proxyChain.length + 1];
        System.arraycopy(this.proxyChain, 0, proxies, 0, this.proxyChain.length);
        proxies[proxies.length - 1] = proxy;
        this.proxyChain = proxies;
        this.secure = secure;
    }

    public void layerProtocol(boolean secure) {
        Asserts.check((boolean)this.connected, (String)"No layered protocol unless connected");
        this.layered = RouteInfo.LayerType.LAYERED;
        this.secure = secure;
    }

    @Override
    public HttpHost getTargetHost() {
        return this.targetHost;
    }

    @Override
    public InetAddress getLocalAddress() {
        return this.localAddress;
    }

    @Override
    public int getHopCount() {
        int hops = 0;
        if (this.connected) {
            hops = this.proxyChain == null ? 1 : this.proxyChain.length + 1;
        }
        return hops;
    }

    @Override
    public HttpHost getHopTarget(int hop) {
        Args.notNegative((int)hop, (String)"Hop index");
        int hopcount = this.getHopCount();
        Args.check((hop < hopcount ? 1 : 0) != 0, (String)"Hop index exceeds tracked route length");
        HttpHost result = null;
        result = hop < hopcount - 1 ? this.proxyChain[hop] : this.targetHost;
        return result;
    }

    @Override
    public HttpHost getProxyHost() {
        return this.proxyChain == null ? null : this.proxyChain[0];
    }

    public boolean isConnected() {
        return this.connected;
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

    public HttpRoute toRoute() {
        return !this.connected ? null : new HttpRoute(this.targetHost, this.localAddress, this.proxyChain, this.secure, this.tunnelled, this.layered);
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof RouteTracker)) {
            return false;
        }
        RouteTracker that = (RouteTracker)o;
        return this.connected == that.connected && this.secure == that.secure && this.tunnelled == that.tunnelled && this.layered == that.layered && LangUtils.equals((Object)this.targetHost, (Object)that.targetHost) && LangUtils.equals((Object)this.localAddress, (Object)that.localAddress) && LangUtils.equals((Object[])this.proxyChain, (Object[])that.proxyChain);
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
        hash = LangUtils.hashCode((int)hash, (boolean)this.connected);
        hash = LangUtils.hashCode((int)hash, (boolean)this.secure);
        hash = LangUtils.hashCode((int)hash, (Object)((Object)this.tunnelled));
        hash = LangUtils.hashCode((int)hash, (Object)((Object)this.layered));
        return hash;
    }

    public String toString() {
        StringBuilder cab = new StringBuilder(50 + this.getHopCount() * 30);
        cab.append("RouteTracker[");
        if (this.localAddress != null) {
            cab.append(this.localAddress);
            cab.append("->");
        }
        cab.append('{');
        if (this.connected) {
            cab.append('c');
        }
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
            for (HttpHost element : this.proxyChain) {
                cab.append(element);
                cab.append("->");
            }
        }
        cab.append(this.targetHost);
        cab.append(']');
        return cab.toString();
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}

