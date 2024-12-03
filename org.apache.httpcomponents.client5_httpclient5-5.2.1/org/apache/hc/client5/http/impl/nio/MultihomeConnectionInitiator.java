/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.annotation.Contract
 *  org.apache.hc.core5.annotation.ThreadingBehavior
 *  org.apache.hc.core5.concurrent.FutureCallback
 *  org.apache.hc.core5.net.NamedEndpoint
 *  org.apache.hc.core5.reactor.ConnectionInitiator
 *  org.apache.hc.core5.reactor.IOSession
 *  org.apache.hc.core5.util.Args
 *  org.apache.hc.core5.util.Timeout
 */
package org.apache.hc.client5.http.impl.nio;

import java.net.SocketAddress;
import java.util.concurrent.Future;
import org.apache.hc.client5.http.DnsResolver;
import org.apache.hc.client5.http.impl.nio.MultihomeIOSessionRequester;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.net.NamedEndpoint;
import org.apache.hc.core5.reactor.ConnectionInitiator;
import org.apache.hc.core5.reactor.IOSession;
import org.apache.hc.core5.util.Args;
import org.apache.hc.core5.util.Timeout;

@Contract(threading=ThreadingBehavior.SAFE_CONDITIONAL)
public final class MultihomeConnectionInitiator
implements ConnectionInitiator {
    private final ConnectionInitiator connectionInitiator;
    private final MultihomeIOSessionRequester sessionRequester;

    public MultihomeConnectionInitiator(ConnectionInitiator connectionInitiator, DnsResolver dnsResolver) {
        this.connectionInitiator = (ConnectionInitiator)Args.notNull((Object)connectionInitiator, (String)"Connection initiator");
        this.sessionRequester = new MultihomeIOSessionRequester(dnsResolver);
    }

    public Future<IOSession> connect(NamedEndpoint remoteEndpoint, SocketAddress remoteAddress, SocketAddress localAddress, Timeout connectTimeout, Object attachment, FutureCallback<IOSession> callback) {
        Args.notNull((Object)remoteEndpoint, (String)"Remote endpoint");
        return this.sessionRequester.connect(this.connectionInitiator, remoteEndpoint, remoteAddress, localAddress, connectTimeout, attachment, callback);
    }

    public Future<IOSession> connect(NamedEndpoint remoteEndpoint, SocketAddress localAddress, Timeout connectTimeout, Object attachment, FutureCallback<IOSession> callback) {
        Args.notNull((Object)remoteEndpoint, (String)"Remote endpoint");
        return this.sessionRequester.connect(this.connectionInitiator, remoteEndpoint, localAddress, connectTimeout, attachment, callback);
    }
}

