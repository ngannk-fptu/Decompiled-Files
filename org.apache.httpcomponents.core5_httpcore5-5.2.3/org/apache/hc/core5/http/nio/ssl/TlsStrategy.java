/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http.nio.ssl;

import java.net.SocketAddress;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.URIScheme;
import org.apache.hc.core5.net.NamedEndpoint;
import org.apache.hc.core5.reactor.ssl.TransportSecurityLayer;
import org.apache.hc.core5.util.Timeout;

public interface TlsStrategy {
    @Deprecated
    public boolean upgrade(TransportSecurityLayer var1, HttpHost var2, SocketAddress var3, SocketAddress var4, Object var5, Timeout var6);

    default public void upgrade(TransportSecurityLayer sessionLayer, NamedEndpoint endpoint, Object attachment, Timeout handshakeTimeout, FutureCallback<TransportSecurityLayer> callback) {
        this.upgrade(sessionLayer, new HttpHost(URIScheme.HTTPS.id, endpoint.getHostName(), endpoint.getPort()), null, null, attachment, handshakeTimeout);
        if (callback != null) {
            callback.completed(sessionLayer);
        }
    }
}

