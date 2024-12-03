/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.http.HttpHeader
 *  org.eclipse.jetty.http.HttpMethod
 *  org.eclipse.jetty.io.ClientConnectionFactory
 *  org.eclipse.jetty.io.Connection
 *  org.eclipse.jetty.io.EndPoint
 *  org.eclipse.jetty.util.Attachable
 *  org.eclipse.jetty.util.Promise
 *  org.eclipse.jetty.util.ssl.SslContextFactory$Client
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.HttpConversation;
import org.eclipse.jetty.client.HttpDestination;
import org.eclipse.jetty.client.HttpRequest;
import org.eclipse.jetty.client.HttpResponseException;
import org.eclipse.jetty.client.Origin;
import org.eclipse.jetty.client.ProxyConfiguration;
import org.eclipse.jetty.client.api.Connection;
import org.eclipse.jetty.client.api.Destination;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.client.api.Result;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.io.ClientConnectionFactory;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.util.Attachable;
import org.eclipse.jetty.util.Promise;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpProxy
extends ProxyConfiguration.Proxy {
    private static final Logger LOG = LoggerFactory.getLogger(HttpProxy.class);

    public HttpProxy(String host, int port) {
        this(new Origin.Address(host, port), false);
    }

    public HttpProxy(Origin.Address address, boolean secure) {
        this(address, secure, null, new Origin.Protocol(List.of("http/1.1"), false));
    }

    public HttpProxy(Origin.Address address, boolean secure, Origin.Protocol protocol) {
        this(address, secure, null, Objects.requireNonNull(protocol));
    }

    public HttpProxy(Origin.Address address, SslContextFactory.Client sslContextFactory) {
        this(address, true, sslContextFactory, new Origin.Protocol(List.of("http/1.1"), false));
    }

    public HttpProxy(Origin.Address address, SslContextFactory.Client sslContextFactory, Origin.Protocol protocol) {
        this(address, true, sslContextFactory, Objects.requireNonNull(protocol));
    }

    private HttpProxy(Origin.Address address, boolean secure, SslContextFactory.Client sslContextFactory, Origin.Protocol protocol) {
        super(address, secure, sslContextFactory, Objects.requireNonNull(protocol));
    }

    @Override
    public ClientConnectionFactory newClientConnectionFactory(ClientConnectionFactory connectionFactory) {
        return new HttpProxyClientConnectionFactory(connectionFactory);
    }

    @Override
    public URI getURI() {
        return URI.create(this.getOrigin().asString());
    }

    boolean requiresTunnel(Origin serverOrigin) {
        if (HttpClient.isSchemeSecure(serverOrigin.getScheme())) {
            return true;
        }
        Origin.Protocol serverProtocol = serverOrigin.getProtocol();
        if (serverProtocol == null) {
            return true;
        }
        List<String> serverProtocols = serverProtocol.getProtocols();
        return this.getProtocol().getProtocols().stream().noneMatch(p -> this.protocolMatches((String)p, serverProtocols));
    }

    private boolean protocolMatches(String protocol, List<String> protocols) {
        return protocols.stream().anyMatch(p -> protocol.equalsIgnoreCase((String)p) || this.isHTTP2((String)p) && this.isHTTP2(protocol));
    }

    private boolean isHTTP2(String protocol) {
        return "h2".equalsIgnoreCase(protocol) || "h2c".equalsIgnoreCase(protocol);
    }

    private class HttpProxyClientConnectionFactory
    implements ClientConnectionFactory {
        private final ClientConnectionFactory connectionFactory;

        private HttpProxyClientConnectionFactory(ClientConnectionFactory connectionFactory) {
            this.connectionFactory = connectionFactory;
        }

        public org.eclipse.jetty.io.Connection newConnection(EndPoint endPoint, Map<String, Object> context) throws IOException {
            HttpDestination destination = (HttpDestination)context.get("org.eclipse.jetty.client.destination");
            if (HttpProxy.this.requiresTunnel(destination.getOrigin())) {
                return this.newProxyConnection(endPoint, context);
            }
            return this.connectionFactory.newConnection(endPoint, context);
        }

        private org.eclipse.jetty.io.Connection newProxyConnection(EndPoint endPoint, Map<String, Object> context) throws IOException {
            HttpDestination destination = (HttpDestination)context.get("org.eclipse.jetty.client.destination");
            HttpClient client = destination.getHttpClient();
            HttpDestination proxyDestination = client.resolveDestination(HttpProxy.this.getOrigin());
            context.put("org.eclipse.jetty.client.destination", proxyDestination);
            Promise promise = (Promise)context.get("org.eclipse.jetty.client.connection.promise");
            CreateTunnelPromise tunnelPromise = new CreateTunnelPromise(this.connectionFactory, endPoint, destination, (Promise<Connection>)promise, context);
            context.put("org.eclipse.jetty.client.connection.promise", tunnelPromise);
            return this.connectionFactory.newConnection(endPoint, context);
        }
    }

    public static class TunnelRequest
    extends HttpRequest {
        private final URI proxyURI;

        private TunnelRequest(HttpClient client, URI proxyURI) {
            this(client, new HttpConversation(), proxyURI);
        }

        private TunnelRequest(HttpClient client, HttpConversation conversation, URI proxyURI) {
            super(client, conversation, proxyURI);
            this.proxyURI = proxyURI;
            this.method(HttpMethod.CONNECT);
        }

        @Override
        HttpRequest copyInstance(URI newURI) {
            return new TunnelRequest(this.getHttpClient(), this.getConversation(), newURI);
        }

        @Override
        public URI getURI() {
            return this.proxyURI;
        }
    }

    private static class TunnelPromise
    implements Promise<Connection> {
        private final Request request;
        private final Response.CompleteListener listener;
        private final Promise<Connection> promise;

        private TunnelPromise(Request request, Response.CompleteListener listener, Promise<Connection> promise) {
            this.request = request;
            this.listener = listener;
            this.promise = promise;
        }

        public void succeeded(Connection connection) {
            connection.send(this.request, this.listener);
        }

        public void failed(Throwable x) {
            this.promise.failed(x);
        }
    }

    private static class ProxyConnection
    implements Connection,
    Attachable {
        private final Destination destination;
        private final Connection connection;
        private final Promise<Connection> promise;
        private Object attachment;

        private ProxyConnection(Destination destination, Connection connection, Promise<Connection> promise) {
            this.destination = destination;
            this.connection = connection;
            this.promise = promise;
        }

        @Override
        public void send(Request request, Response.CompleteListener listener) {
            if (this.connection.isClosed()) {
                this.destination.newConnection(new TunnelPromise(request, listener, this.promise));
            } else {
                this.connection.send(request, listener);
            }
        }

        @Override
        public void close() {
            this.connection.close();
        }

        @Override
        public boolean isClosed() {
            return this.connection.isClosed();
        }

        public void setAttachment(Object obj) {
            this.attachment = obj;
        }

        public Object getAttachment() {
            return this.attachment;
        }
    }

    private static class CreateTunnelPromise
    implements Promise<Connection> {
        private final ClientConnectionFactory connectionFactory;
        private final EndPoint endPoint;
        private final HttpDestination destination;
        private final Promise<Connection> promise;
        private final Map<String, Object> context;

        private CreateTunnelPromise(ClientConnectionFactory connectionFactory, EndPoint endPoint, HttpDestination destination, Promise<Connection> promise, Map<String, Object> context) {
            this.connectionFactory = connectionFactory;
            this.endPoint = endPoint;
            this.destination = destination;
            this.promise = promise;
            this.context = context;
        }

        public void succeeded(Connection connection) {
            this.context.put("org.eclipse.jetty.client.destination", this.destination);
            this.context.put("org.eclipse.jetty.client.connection.promise", this.promise);
            this.tunnel(connection);
        }

        public void failed(Throwable x) {
            this.tunnelFailed(this.endPoint, x);
        }

        private void tunnel(Connection connection) {
            String target = this.destination.getOrigin().getAddress().asString();
            HttpClient httpClient = this.destination.getHttpClient();
            long connectTimeout = httpClient.getConnectTimeout();
            Request connect = new TunnelRequest(httpClient, this.destination.getProxy().getURI()).path(target).headers(headers -> headers.put(HttpHeader.HOST, target)).timeout(connectTimeout, TimeUnit.MILLISECONDS);
            HttpDestination proxyDestination = httpClient.resolveDestination(this.destination.getProxy().getOrigin());
            connect.attribute(Connection.class.getName(), new ProxyConnection(proxyDestination, connection, this.promise));
            connection.send(connect, new TunnelListener(connect));
        }

        private void tunnelSucceeded(EndPoint endPoint) {
            try {
                HttpDestination destination = (HttpDestination)this.context.get("org.eclipse.jetty.client.destination");
                ClientConnectionFactory factory = this.connectionFactory;
                if (destination.isSecure()) {
                    InetSocketAddress address = InetSocketAddress.createUnresolved(destination.getHost(), destination.getPort());
                    this.context.put("org.eclipse.jetty.client.connector.remoteSocketAddress", address);
                    factory = destination.newSslClientConnectionFactory(null, factory);
                }
                org.eclipse.jetty.io.Connection oldConnection = endPoint.getConnection();
                org.eclipse.jetty.io.Connection newConnection = factory.newConnection(endPoint, this.context);
                if (LOG.isDebugEnabled()) {
                    LOG.debug("HTTP tunnel established: {} over {}", (Object)oldConnection, (Object)newConnection);
                }
                endPoint.upgrade(newConnection);
            }
            catch (Throwable x) {
                this.tunnelFailed(endPoint, x);
            }
        }

        private void tunnelFailed(EndPoint endPoint, Throwable failure) {
            endPoint.close(failure);
            this.promise.failed(failure);
        }

        private class TunnelListener
        extends Response.Listener.Adapter {
            private final HttpConversation conversation;

            private TunnelListener(Request request) {
                this.conversation = ((HttpRequest)request).getConversation();
            }

            @Override
            public void onHeaders(Response response) {
                EndPoint endPoint = (EndPoint)this.conversation.getAttribute(EndPoint.class.getName());
                if (response.getStatus() == 200) {
                    CreateTunnelPromise.this.tunnelSucceeded(endPoint);
                } else {
                    HttpResponseException failure = new HttpResponseException("Unexpected " + response + " for " + response.getRequest(), response);
                    CreateTunnelPromise.this.tunnelFailed(endPoint, failure);
                }
            }

            @Override
            public void onComplete(Result result) {
                if (result.isFailed()) {
                    CreateTunnelPromise.this.tunnelFailed(CreateTunnelPromise.this.endPoint, result.getFailure());
                }
            }
        }
    }
}

