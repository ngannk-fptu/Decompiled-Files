/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http2.nio.pool;

import java.net.InetSocketAddress;
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
        this.connectionInitiator = Args.notNull(connectionInitiator, "Connection initiator");
        this.addressResolver = addressResolver != null ? addressResolver : DefaultAddressResolver.INSTANCE;
        this.tlsStrategy = tlsStrategy;
    }

    public TimeValue getValidateAfterInactivity() {
        return this.validateAfterInactivity;
    }

    public void setValidateAfterInactivity(TimeValue timeValue) {
        this.validateAfterInactivity = timeValue;
    }

    @Override
    protected void closeSession(IOSession ioSession, CloseMode closeMode) {
        if (closeMode == CloseMode.GRACEFUL) {
            ioSession.enqueue(ShutdownCommand.GRACEFUL, Command.Priority.NORMAL);
        } else {
            ioSession.close(closeMode);
        }
    }

    @Override
    protected Future<IOSession> connectSession(final HttpHost namedEndpoint, final Timeout connectTimeout, final FutureCallback<IOSession> callback) {
        InetSocketAddress remoteAddress = this.addressResolver.resolve(namedEndpoint);
        return this.connectionInitiator.connect(namedEndpoint, remoteAddress, null, connectTimeout, null, (FutureCallback<IOSession>)new CallbackContribution<IOSession>(callback){

            @Override
            public void completed(IOSession ioSession) {
                if (H2ConnPool.this.tlsStrategy != null && URIScheme.HTTPS.same(namedEndpoint.getSchemeName()) && ioSession instanceof TransportSecurityLayer) {
                    H2ConnPool.this.tlsStrategy.upgrade((TransportSecurityLayer)((Object)ioSession), namedEndpoint, ioSession.getLocalAddress(), ioSession.getRemoteAddress(), null, connectTimeout);
                    ioSession.setSocketTimeout(connectTimeout);
                }
                callback.completed(ioSession);
            }
        });
    }

    @Override
    protected void validateSession(final IOSession ioSession, final Callback<Boolean> callback) {
        if (ioSession.isOpen()) {
            long lastAccessTime;
            long deadline;
            TimeValue timeValue = this.validateAfterInactivity;
            if (TimeValue.isNonNegative(timeValue) && (deadline = (lastAccessTime = Math.min(ioSession.getLastReadTime(), ioSession.getLastWriteTime())) + timeValue.toMilliseconds()) <= System.currentTimeMillis()) {
                final Timeout socketTimeoutMillis = ioSession.getSocketTimeout();
                ioSession.enqueue(new PingCommand(new BasicPingHandler(new Callback<Boolean>(){

                    @Override
                    public void execute(Boolean result) {
                        ioSession.setSocketTimeout(socketTimeoutMillis);
                        callback.execute(result);
                    }
                })), Command.Priority.NORMAL);
                return;
            }
            callback.execute(true);
        } else {
            callback.execute(false);
        }
    }
}

