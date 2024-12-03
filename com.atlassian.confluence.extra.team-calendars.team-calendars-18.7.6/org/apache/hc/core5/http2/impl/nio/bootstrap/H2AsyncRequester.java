/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http2.impl.nio.bootstrap;

import java.util.concurrent.Future;
import org.apache.hc.core5.annotation.Internal;
import org.apache.hc.core5.concurrent.CallbackContribution;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.function.Callback;
import org.apache.hc.core5.function.Decorator;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.impl.bootstrap.HttpAsyncRequester;
import org.apache.hc.core5.http.nio.AsyncClientEndpoint;
import org.apache.hc.core5.http.nio.ssl.TlsStrategy;
import org.apache.hc.core5.http2.HttpVersionPolicy;
import org.apache.hc.core5.http2.ssl.ApplicationProtocol;
import org.apache.hc.core5.net.NamedEndpoint;
import org.apache.hc.core5.pool.ManagedConnPool;
import org.apache.hc.core5.reactor.IOEventHandlerFactory;
import org.apache.hc.core5.reactor.IOReactorConfig;
import org.apache.hc.core5.reactor.IOSession;
import org.apache.hc.core5.reactor.IOSessionListener;
import org.apache.hc.core5.reactor.ProtocolIOSession;
import org.apache.hc.core5.reactor.ssl.TlsDetails;
import org.apache.hc.core5.util.Timeout;

public class H2AsyncRequester
extends HttpAsyncRequester {
    private final HttpVersionPolicy versionPolicy;

    @Internal
    public H2AsyncRequester(HttpVersionPolicy versionPolicy, IOReactorConfig ioReactorConfig, IOEventHandlerFactory eventHandlerFactory, Decorator<IOSession> ioSessionDecorator, Callback<Exception> exceptionCallback, IOSessionListener sessionListener, ManagedConnPool<HttpHost, IOSession> connPool) {
        super(ioReactorConfig, eventHandlerFactory, ioSessionDecorator, exceptionCallback, sessionListener, connPool);
        this.versionPolicy = versionPolicy != null ? versionPolicy : HttpVersionPolicy.NEGOTIATE;
    }

    @Internal
    public H2AsyncRequester(HttpVersionPolicy versionPolicy, IOReactorConfig ioReactorConfig, IOEventHandlerFactory eventHandlerFactory, Decorator<IOSession> ioSessionDecorator, Callback<Exception> exceptionCallback, IOSessionListener sessionListener, ManagedConnPool<HttpHost, IOSession> connPool, TlsStrategy tlsStrategy, Timeout handshakeTimeout) {
        super(ioReactorConfig, eventHandlerFactory, ioSessionDecorator, exceptionCallback, sessionListener, connPool, tlsStrategy, handshakeTimeout);
        this.versionPolicy = versionPolicy != null ? versionPolicy : HttpVersionPolicy.NEGOTIATE;
    }

    @Override
    protected Future<AsyncClientEndpoint> doConnect(HttpHost host, Timeout timeout, Object attachment, FutureCallback<AsyncClientEndpoint> callback) {
        return super.doConnect(host, timeout, attachment != null ? attachment : this.versionPolicy, callback);
    }

    @Override
    protected void doTlsUpgrade(ProtocolIOSession ioSession, NamedEndpoint endpoint, final FutureCallback<ProtocolIOSession> callback) {
        super.doTlsUpgrade(ioSession, endpoint, (FutureCallback<ProtocolIOSession>)new CallbackContribution<ProtocolIOSession>(callback){

            @Override
            public void completed(ProtocolIOSession protocolSession) {
                boolean switchProtocol;
                switch (H2AsyncRequester.this.versionPolicy) {
                    case FORCE_HTTP_2: {
                        switchProtocol = true;
                        break;
                    }
                    case NEGOTIATE: {
                        TlsDetails tlsDetails = protocolSession.getTlsDetails();
                        String appProtocol = tlsDetails != null ? tlsDetails.getApplicationProtocol() : null;
                        switchProtocol = ApplicationProtocol.HTTP_2.id.equals(appProtocol);
                        break;
                    }
                    default: {
                        switchProtocol = false;
                    }
                }
                if (switchProtocol) {
                    protocolSession.switchProtocol(ApplicationProtocol.HTTP_2.id, callback);
                } else if (callback != null) {
                    callback.completed(protocolSession);
                }
            }
        });
    }
}

