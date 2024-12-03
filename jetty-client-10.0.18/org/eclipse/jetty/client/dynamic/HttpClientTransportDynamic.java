/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.alpn.client.ALPNClientConnection
 *  org.eclipse.jetty.alpn.client.ALPNClientConnectionFactory
 *  org.eclipse.jetty.http.HttpHeader
 *  org.eclipse.jetty.http.HttpVersion
 *  org.eclipse.jetty.io.ClientConnectionFactory
 *  org.eclipse.jetty.io.ClientConnectionFactory$Info
 *  org.eclipse.jetty.io.ClientConnector
 *  org.eclipse.jetty.io.Connection
 *  org.eclipse.jetty.io.EndPoint
 *  org.eclipse.jetty.util.Callback
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.client.dynamic;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.jetty.alpn.client.ALPNClientConnection;
import org.eclipse.jetty.alpn.client.ALPNClientConnectionFactory;
import org.eclipse.jetty.client.AbstractConnectorHttpClientTransport;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.HttpDestination;
import org.eclipse.jetty.client.HttpRequest;
import org.eclipse.jetty.client.MultiplexConnectionPool;
import org.eclipse.jetty.client.MultiplexHttpDestination;
import org.eclipse.jetty.client.Origin;
import org.eclipse.jetty.client.http.HttpClientConnectionFactory;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.io.ClientConnectionFactory;
import org.eclipse.jetty.io.ClientConnector;
import org.eclipse.jetty.io.Connection;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClientTransportDynamic
extends AbstractConnectorHttpClientTransport {
    private static final Logger LOG = LoggerFactory.getLogger(HttpClientTransportDynamic.class);
    private final List<ClientConnectionFactory.Info> factoryInfos;
    private final List<String> protocols;

    public HttpClientTransportDynamic() {
        this(HttpClientConnectionFactory.HTTP11);
    }

    public HttpClientTransportDynamic(ClientConnectionFactory.Info ... factoryInfos) {
        this(HttpClientTransportDynamic.findClientConnector(factoryInfos), factoryInfos);
    }

    public HttpClientTransportDynamic(ClientConnector connector, ClientConnectionFactory.Info ... factoryInfos) {
        super(connector);
        if (factoryInfos.length == 0) {
            factoryInfos = new ClientConnectionFactory.Info[]{HttpClientConnectionFactory.HTTP11};
        }
        this.factoryInfos = Arrays.asList(factoryInfos);
        this.protocols = Arrays.stream(factoryInfos).flatMap(info -> Stream.concat(info.getProtocols(false).stream(), info.getProtocols(true).stream())).distinct().map(p -> p.toLowerCase(Locale.ENGLISH)).collect(Collectors.toList());
        Arrays.stream(factoryInfos).forEach(arg_0 -> ((HttpClientTransportDynamic)this).addBean(arg_0));
        this.setConnectionPoolFactory(destination -> new MultiplexConnectionPool(destination, destination.getHttpClient().getMaxConnectionsPerDestination(), (Callback)destination, 1));
    }

    private static ClientConnector findClientConnector(ClientConnectionFactory.Info[] infos) {
        return Arrays.stream(infos).flatMap(info -> info.getContainedBeans(ClientConnector.class).stream()).findFirst().orElseGet(ClientConnector::new);
    }

    @Override
    public Origin newOrigin(HttpRequest request) {
        boolean secure = HttpClient.isSchemeSecure(request.getScheme());
        String http1 = "http/1.1";
        String http2 = secure ? "h2" : "h2c";
        List<Object> protocols = List.of();
        if (request.isVersionExplicit()) {
            String desired;
            HttpVersion version = request.getVersion();
            String string = desired = version == HttpVersion.HTTP_2 ? http2 : http1;
            if (this.protocols.contains(desired)) {
                protocols = List.of(desired);
            }
        } else if (secure) {
            List<String> http = List.of("http/1.1", "h2c", "h2");
            protocols = this.protocols.stream().filter(http::contains).collect(Collectors.toCollection(ArrayList::new));
            if (request.getHeaders().contains(HttpHeader.UPGRADE, "h2c")) {
                protocols.remove("h2");
            }
        } else {
            protocols = List.of(this.protocols.get(0));
        }
        Origin.Protocol protocol = null;
        if (!protocols.isEmpty()) {
            protocol = new Origin.Protocol(protocols, secure && protocols.contains(http2));
        }
        return this.getHttpClient().createOrigin(request, protocol);
    }

    @Override
    public HttpDestination newHttpDestination(Origin origin) {
        SocketAddress address = origin.getAddress().getSocketAddress();
        return new MultiplexHttpDestination(this.getHttpClient(), origin, this.getClientConnector().isIntrinsicallySecure(address));
    }

    public Connection newConnection(EndPoint endPoint, Map<String, Object> context) throws IOException {
        Object factory;
        HttpDestination destination = (HttpDestination)context.get("org.eclipse.jetty.client.destination");
        Origin.Protocol protocol = destination.getOrigin().getProtocol();
        if (protocol == null) {
            factory = this.factoryInfos.get(0).getClientConnectionFactory();
        } else {
            SocketAddress address = destination.getOrigin().getAddress().getSocketAddress();
            boolean intrinsicallySecure = this.getClientConnector().isIntrinsicallySecure(address);
            factory = !intrinsicallySecure && destination.isSecure() && protocol.isNegotiate() ? new ALPNClientConnectionFactory(this.getClientConnector().getExecutor(), this::newNegotiatedConnection, protocol.getProtocols()) : this.findClientConnectionFactoryInfo(protocol.getProtocols(), destination.isSecure()).orElseThrow(() -> new IOException("Cannot find " + ClientConnectionFactory.class.getSimpleName() + " for " + protocol)).getClientConnectionFactory();
        }
        return factory.newConnection(endPoint, context);
    }

    public void upgrade(EndPoint endPoint, Map<String, Object> context) {
        HttpDestination destination = (HttpDestination)context.get("org.eclipse.jetty.client.destination");
        Origin.Protocol protocol = destination.getOrigin().getProtocol();
        ClientConnectionFactory.Info info = this.findClientConnectionFactoryInfo(protocol.getProtocols(), destination.isSecure()).orElseThrow(() -> new IllegalStateException("Cannot find " + ClientConnectionFactory.class.getSimpleName() + " to upgrade to " + protocol));
        info.upgrade(endPoint, context);
    }

    protected Connection newNegotiatedConnection(EndPoint endPoint, Map<String, Object> context) throws IOException {
        try {
            ClientConnectionFactory.Info factoryInfo;
            ALPNClientConnection alpnConnection = (ALPNClientConnection)endPoint.getConnection();
            String protocol = alpnConnection.getProtocol();
            if (protocol != null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("ALPN negotiated {} among {}", (Object)protocol, (Object)alpnConnection.getProtocols());
                }
                List<String> protocols = List.of(protocol);
                factoryInfo = this.findClientConnectionFactoryInfo(protocols, true).orElseThrow(() -> new IOException("Cannot find " + ClientConnectionFactory.class.getSimpleName() + " for negotiated protocol " + protocol));
            } else {
                factoryInfo = this.factoryInfos.get(0);
                if (LOG.isDebugEnabled()) {
                    LOG.debug("No ALPN protocol, using {}", (Object)factoryInfo);
                }
            }
            return factoryInfo.getClientConnectionFactory().newConnection(endPoint, context);
        }
        catch (Throwable failure) {
            this.connectFailed(context, failure);
            throw failure;
        }
    }

    private Optional<ClientConnectionFactory.Info> findClientConnectionFactoryInfo(List<String> protocols, boolean secure) {
        return this.factoryInfos.stream().filter(info -> info.matches(protocols, secure)).findFirst();
    }
}

