/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http.impl.bootstrap;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.Future;
import org.apache.hc.core5.annotation.Internal;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.function.Callback;
import org.apache.hc.core5.function.Decorator;
import org.apache.hc.core5.http.URIScheme;
import org.apache.hc.core5.http.impl.bootstrap.AsyncServer;
import org.apache.hc.core5.http.nio.command.ShutdownCommand;
import org.apache.hc.core5.reactor.EndpointParameters;
import org.apache.hc.core5.reactor.IOEventHandlerFactory;
import org.apache.hc.core5.reactor.IOReactorConfig;
import org.apache.hc.core5.reactor.IOSession;
import org.apache.hc.core5.reactor.IOSessionListener;
import org.apache.hc.core5.reactor.ListenerEndpoint;

public class HttpAsyncServer
extends AsyncServer {
    private final String canonicalName;

    @Internal
    public HttpAsyncServer(IOEventHandlerFactory eventHandlerFactory, IOReactorConfig ioReactorConfig, Decorator<IOSession> ioSessionDecorator, Callback<Exception> exceptionCallback, IOSessionListener sessionListener, String canonicalName) {
        super(eventHandlerFactory, ioReactorConfig, ioSessionDecorator, exceptionCallback, sessionListener, ShutdownCommand.GRACEFUL_NORMAL_CALLBACK);
        this.canonicalName = canonicalName;
    }

    @Internal
    public HttpAsyncServer(IOEventHandlerFactory eventHandlerFactory, IOReactorConfig ioReactorConfig, Decorator<IOSession> ioSessionDecorator, Callback<Exception> exceptionCallback, IOSessionListener sessionListener) {
        this(eventHandlerFactory, ioReactorConfig, ioSessionDecorator, exceptionCallback, sessionListener, (String)null);
    }

    public Future<ListenerEndpoint> listen(SocketAddress address, URIScheme scheme, Object attachment, FutureCallback<ListenerEndpoint> callback) {
        InetSocketAddress inetSocketAddress = (InetSocketAddress)address;
        EndpointParameters parameters = new EndpointParameters(scheme.id, this.canonicalName != null ? this.canonicalName : "localhost", inetSocketAddress.getPort(), attachment);
        return super.listen(address, parameters, callback);
    }

    public Future<ListenerEndpoint> listen(SocketAddress address, URIScheme scheme, FutureCallback<ListenerEndpoint> callback) {
        return this.listen(address, scheme, null, callback);
    }

    public Future<ListenerEndpoint> listen(SocketAddress address, URIScheme scheme) {
        return this.listen(address, scheme, null, null);
    }

    @Override
    @Deprecated
    public Future<ListenerEndpoint> listen(SocketAddress address, FutureCallback<ListenerEndpoint> callback) {
        return super.listen(address, callback);
    }

    @Override
    @Deprecated
    public Future<ListenerEndpoint> listen(SocketAddress address) {
        return super.listen(address);
    }
}

