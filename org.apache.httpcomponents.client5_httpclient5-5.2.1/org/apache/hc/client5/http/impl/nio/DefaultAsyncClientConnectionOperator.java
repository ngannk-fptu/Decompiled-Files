/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.concurrent.BasicFuture
 *  org.apache.hc.core5.concurrent.CallbackContribution
 *  org.apache.hc.core5.concurrent.ComplexFuture
 *  org.apache.hc.core5.concurrent.FutureCallback
 *  org.apache.hc.core5.concurrent.FutureContribution
 *  org.apache.hc.core5.http.HttpHost
 *  org.apache.hc.core5.http.URIScheme
 *  org.apache.hc.core5.http.config.Lookup
 *  org.apache.hc.core5.http.nio.ssl.TlsStrategy
 *  org.apache.hc.core5.http.protocol.HttpContext
 *  org.apache.hc.core5.net.NamedEndpoint
 *  org.apache.hc.core5.reactor.ConnectionInitiator
 *  org.apache.hc.core5.reactor.IOSession
 *  org.apache.hc.core5.reactor.ssl.TransportSecurityLayer
 *  org.apache.hc.core5.util.Args
 *  org.apache.hc.core5.util.Timeout
 */
package org.apache.hc.client5.http.impl.nio;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.Future;
import org.apache.hc.client5.http.DnsResolver;
import org.apache.hc.client5.http.SchemePortResolver;
import org.apache.hc.client5.http.config.TlsConfig;
import org.apache.hc.client5.http.impl.DefaultSchemePortResolver;
import org.apache.hc.client5.http.impl.nio.DefaultManagedAsyncClientConnection;
import org.apache.hc.client5.http.impl.nio.MultihomeIOSessionRequester;
import org.apache.hc.client5.http.nio.AsyncClientConnectionOperator;
import org.apache.hc.client5.http.nio.ManagedAsyncClientConnection;
import org.apache.hc.client5.http.routing.RoutingSupport;
import org.apache.hc.core5.concurrent.BasicFuture;
import org.apache.hc.core5.concurrent.CallbackContribution;
import org.apache.hc.core5.concurrent.ComplexFuture;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.concurrent.FutureContribution;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.URIScheme;
import org.apache.hc.core5.http.config.Lookup;
import org.apache.hc.core5.http.nio.ssl.TlsStrategy;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.net.NamedEndpoint;
import org.apache.hc.core5.reactor.ConnectionInitiator;
import org.apache.hc.core5.reactor.IOSession;
import org.apache.hc.core5.reactor.ssl.TransportSecurityLayer;
import org.apache.hc.core5.util.Args;
import org.apache.hc.core5.util.Timeout;

final class DefaultAsyncClientConnectionOperator
implements AsyncClientConnectionOperator {
    private final SchemePortResolver schemePortResolver;
    private final MultihomeIOSessionRequester sessionRequester;
    private final Lookup<TlsStrategy> tlsStrategyLookup;

    DefaultAsyncClientConnectionOperator(Lookup<TlsStrategy> tlsStrategyLookup, SchemePortResolver schemePortResolver, DnsResolver dnsResolver) {
        this.tlsStrategyLookup = (Lookup)Args.notNull(tlsStrategyLookup, (String)"TLS strategy lookup");
        this.schemePortResolver = schemePortResolver != null ? schemePortResolver : DefaultSchemePortResolver.INSTANCE;
        this.sessionRequester = new MultihomeIOSessionRequester(dnsResolver);
    }

    @Override
    public Future<ManagedAsyncClientConnection> connect(ConnectionInitiator connectionInitiator, HttpHost host, SocketAddress localAddress, Timeout connectTimeout, Object attachment, FutureCallback<ManagedAsyncClientConnection> callback) {
        return this.connect(connectionInitiator, host, localAddress, connectTimeout, attachment, null, callback);
    }

    @Override
    public Future<ManagedAsyncClientConnection> connect(ConnectionInitiator connectionInitiator, final HttpHost host, SocketAddress localAddress, final Timeout connectTimeout, final Object attachment, HttpContext context, FutureCallback<ManagedAsyncClientConnection> callback) {
        Args.notNull((Object)connectionInitiator, (String)"Connection initiator");
        Args.notNull((Object)host, (String)"Host");
        final ComplexFuture future = new ComplexFuture(callback);
        HttpHost remoteEndpoint = RoutingSupport.normalize(host, this.schemePortResolver);
        InetAddress remoteAddress = host.getAddress();
        final TlsStrategy tlsStrategy = this.tlsStrategyLookup != null ? (TlsStrategy)this.tlsStrategyLookup.lookup(host.getSchemeName()) : null;
        final TlsConfig tlsConfig = attachment instanceof TlsConfig ? (TlsConfig)attachment : TlsConfig.DEFAULT;
        Future<IOSession> sessionFuture = this.sessionRequester.connect(connectionInitiator, (NamedEndpoint)remoteEndpoint, remoteAddress != null ? new InetSocketAddress(remoteAddress, remoteEndpoint.getPort()) : null, localAddress, connectTimeout, tlsConfig.getHttpVersionPolicy(), new FutureCallback<IOSession>(){

            public void completed(IOSession session) {
                final DefaultManagedAsyncClientConnection connection = new DefaultManagedAsyncClientConnection(session);
                if (tlsStrategy != null && URIScheme.HTTPS.same(host.getSchemeName())) {
                    try {
                        final Timeout socketTimeout = connection.getSocketTimeout();
                        Timeout handshakeTimeout = tlsConfig.getHandshakeTimeout();
                        tlsStrategy.upgrade((TransportSecurityLayer)connection, (NamedEndpoint)host, attachment, handshakeTimeout != null ? handshakeTimeout : connectTimeout, (FutureCallback)new FutureContribution<TransportSecurityLayer>((BasicFuture)future){

                            public void completed(TransportSecurityLayer transportSecurityLayer) {
                                connection.setSocketTimeout(socketTimeout);
                                future.completed((Object)connection);
                            }
                        });
                    }
                    catch (Exception ex) {
                        future.failed(ex);
                    }
                } else {
                    future.completed((Object)connection);
                }
            }

            public void failed(Exception ex) {
                future.failed(ex);
            }

            public void cancelled() {
                future.cancel();
            }
        });
        future.setDependency(sessionFuture);
        return future;
    }

    @Override
    public void upgrade(ManagedAsyncClientConnection connection, HttpHost host, Object attachment) {
        this.upgrade(connection, host, attachment, null, null);
    }

    @Override
    public void upgrade(ManagedAsyncClientConnection connection, HttpHost host, Object attachment, HttpContext context) {
        this.upgrade(connection, host, attachment, context, null);
    }

    @Override
    public void upgrade(final ManagedAsyncClientConnection connection, HttpHost host, Object attachment, HttpContext context, final FutureCallback<ManagedAsyncClientConnection> callback) {
        TlsStrategy tlsStrategy;
        TlsStrategy tlsStrategy2 = tlsStrategy = this.tlsStrategyLookup != null ? (TlsStrategy)this.tlsStrategyLookup.lookup(host.getSchemeName()) : null;
        if (tlsStrategy != null) {
            tlsStrategy.upgrade((TransportSecurityLayer)connection, (NamedEndpoint)host, attachment, null, (FutureCallback)new CallbackContribution<TransportSecurityLayer>(callback){

                public void completed(TransportSecurityLayer transportSecurityLayer) {
                    if (callback != null) {
                        callback.completed((Object)connection);
                    }
                }
            });
        }
    }
}

