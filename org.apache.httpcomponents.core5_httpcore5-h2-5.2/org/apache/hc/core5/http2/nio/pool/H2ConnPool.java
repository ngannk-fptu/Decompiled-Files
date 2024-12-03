/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.annotation.Contract
 *  org.apache.hc.core5.annotation.ThreadingBehavior
 *  org.apache.hc.core5.concurrent.CallbackContribution
 *  org.apache.hc.core5.concurrent.FutureCallback
 *  org.apache.hc.core5.function.Callback
 *  org.apache.hc.core5.function.Resolver
 *  org.apache.hc.core5.http.HttpHost
 *  org.apache.hc.core5.http.URIScheme
 *  org.apache.hc.core5.http.impl.DefaultAddressResolver
 *  org.apache.hc.core5.http.nio.command.ShutdownCommand
 *  org.apache.hc.core5.http.nio.ssl.TlsStrategy
 *  org.apache.hc.core5.io.CloseMode
 *  org.apache.hc.core5.net.NamedEndpoint
 *  org.apache.hc.core5.reactor.AbstractIOSessionPool
 *  org.apache.hc.core5.reactor.Command
 *  org.apache.hc.core5.reactor.Command$Priority
 *  org.apache.hc.core5.reactor.ConnectionInitiator
 *  org.apache.hc.core5.reactor.IOSession
 *  org.apache.hc.core5.reactor.ssl.TransportSecurityLayer
 *  org.apache.hc.core5.util.Args
 *  org.apache.hc.core5.util.TimeValue
 *  org.apache.hc.core5.util.Timeout
 */
package org.apache.hc.core5.http2.nio.pool;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.Future;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.concurrent.CallbackContribution;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.function.Callback;
import org.apache.hc.core5.function.Resolver;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.URIScheme;
import org.apache.hc.core5.http.impl.DefaultAddressResolver;
import org.apache.hc.core5.http.nio.command.ShutdownCommand;
import org.apache.hc.core5.http.nio.ssl.TlsStrategy;
import org.apache.hc.core5.http2.nio.command.PingCommand;
import org.apache.hc.core5.http2.nio.support.BasicPingHandler;
import org.apache.hc.core5.io.CloseMode;
import org.apache.hc.core5.net.NamedEndpoint;
import org.apache.hc.core5.reactor.AbstractIOSessionPool;
import org.apache.hc.core5.reactor.Command;
import org.apache.hc.core5.reactor.ConnectionInitiator;
import org.apache.hc.core5.reactor.IOSession;
import org.apache.hc.core5.reactor.ssl.TransportSecurityLayer;
import org.apache.hc.core5.util.Args;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;

@Contract(threading=ThreadingBehavior.SAFE)
public final class H2ConnPool
extends AbstractIOSessionPool<HttpHost> {
    private final ConnectionInitiator connectionInitiator;
    private final Resolver<HttpHost, InetSocketAddress> addressResolver;
    private final TlsStrategy tlsStrategy;
    private volatile TimeValue validateAfterInactivity = TimeValue.NEG_ONE_MILLISECOND;

    public H2ConnPool(ConnectionInitiator connectionInitiator, Resolver<HttpHost, InetSocketAddress> addressResolver, TlsStrategy tlsStrategy) {
        this.connectionInitiator = (ConnectionInitiator)Args.notNull((Object)connectionInitiator, (String)"Connection initiator");
        this.addressResolver = addressResolver != null ? addressResolver : DefaultAddressResolver.INSTANCE;
        this.tlsStrategy = tlsStrategy;
    }

    public TimeValue getValidateAfterInactivity() {
        return this.validateAfterInactivity;
    }

    public void setValidateAfterInactivity(TimeValue timeValue) {
        this.validateAfterInactivity = timeValue;
    }

    protected void closeSession(IOSession ioSession, CloseMode closeMode) {
        if (closeMode == CloseMode.GRACEFUL) {
            ioSession.enqueue((Command)ShutdownCommand.GRACEFUL, Command.Priority.NORMAL);
        } else {
            ioSession.close(closeMode);
        }
    }

    protected Future<IOSession> connectSession(final HttpHost namedEndpoint, final Timeout connectTimeout, final FutureCallback<IOSession> callback) {
        InetSocketAddress remoteAddress = (InetSocketAddress)this.addressResolver.resolve((Object)namedEndpoint);
        return this.connectionInitiator.connect((NamedEndpoint)namedEndpoint, (SocketAddress)remoteAddress, null, connectTimeout, null, (FutureCallback)new CallbackContribution<IOSession>(callback){

            public void completed(final IOSession ioSession) {
                if (H2ConnPool.this.tlsStrategy != null && URIScheme.HTTPS.same(namedEndpoint.getSchemeName()) && ioSession instanceof TransportSecurityLayer) {
                    H2ConnPool.this.tlsStrategy.upgrade((TransportSecurityLayer)ioSession, (NamedEndpoint)namedEndpoint, null, connectTimeout, (FutureCallback)new CallbackContribution<TransportSecurityLayer>(callback){

                        public void completed(TransportSecurityLayer transportSecurityLayer) {
                            callback.completed((Object)ioSession);
                        }
                    });
                    ioSession.setSocketTimeout(connectTimeout);
                } else {
                    callback.completed((Object)ioSession);
                }
            }
        });
    }

    protected void validateSession(IOSession ioSession, Callback<Boolean> callback) {
        if (ioSession.isOpen()) {
            long lastAccessTime;
            long deadline;
            TimeValue timeValue = this.validateAfterInactivity;
            if (TimeValue.isNonNegative((TimeValue)timeValue) && (deadline = (lastAccessTime = Math.min(ioSession.getLastReadTime(), ioSession.getLastWriteTime())) + timeValue.toMilliseconds()) <= System.currentTimeMillis()) {
                Timeout socketTimeoutMillis = ioSession.getSocketTimeout();
                ioSession.enqueue((Command)new PingCommand(new BasicPingHandler((Callback<Boolean>)((Callback)result -> {
                    ioSession.setSocketTimeout(socketTimeoutMillis);
                    callback.execute(result);
                }))), Command.Priority.NORMAL);
                return;
            }
            callback.execute((Object)true);
        } else {
            callback.execute((Object)false);
        }
    }
}

