/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.io.ClientConnectionFactory
 *  org.eclipse.jetty.io.ClientConnector
 *  org.eclipse.jetty.io.Connection
 *  org.eclipse.jetty.io.EndPoint
 *  org.eclipse.jetty.util.Callback
 *  org.eclipse.jetty.util.ProcessorUtils
 *  org.eclipse.jetty.util.annotation.ManagedAttribute
 *  org.eclipse.jetty.util.annotation.ManagedObject
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.client.http;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.List;
import java.util.Map;
import org.eclipse.jetty.client.AbstractConnectorHttpClientTransport;
import org.eclipse.jetty.client.DuplexConnectionPool;
import org.eclipse.jetty.client.DuplexHttpDestination;
import org.eclipse.jetty.client.HttpDestination;
import org.eclipse.jetty.client.HttpRequest;
import org.eclipse.jetty.client.Origin;
import org.eclipse.jetty.client.http.HttpClientConnectionFactory;
import org.eclipse.jetty.io.ClientConnectionFactory;
import org.eclipse.jetty.io.ClientConnector;
import org.eclipse.jetty.io.Connection;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.ProcessorUtils;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.util.annotation.ManagedObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedObject(value="The HTTP/1.1 client transport")
public class HttpClientTransportOverHTTP
extends AbstractConnectorHttpClientTransport {
    public static final Origin.Protocol HTTP11 = new Origin.Protocol(List.of("http/1.1"), false);
    private static final Logger LOG = LoggerFactory.getLogger(HttpClientTransportOverHTTP.class);
    private final ClientConnectionFactory factory = new HttpClientConnectionFactory();
    private int headerCacheSize = 1024;
    private boolean headerCacheCaseSensitive;

    public HttpClientTransportOverHTTP() {
        this(Math.max(1, ProcessorUtils.availableProcessors() / 2));
    }

    public HttpClientTransportOverHTTP(int selectors) {
        this(new ClientConnector());
        this.getClientConnector().setSelectors(selectors);
    }

    public HttpClientTransportOverHTTP(ClientConnector connector) {
        super(connector);
        this.setConnectionPoolFactory(destination -> new DuplexConnectionPool(destination, this.getHttpClient().getMaxConnectionsPerDestination(), (Callback)destination));
    }

    @Override
    public Origin newOrigin(HttpRequest request) {
        return this.getHttpClient().createOrigin(request, HTTP11);
    }

    @Override
    public HttpDestination newHttpDestination(Origin origin) {
        SocketAddress address = origin.getAddress().getSocketAddress();
        return new DuplexHttpDestination(this.getHttpClient(), origin, this.getClientConnector().isIntrinsicallySecure(address));
    }

    public Connection newConnection(EndPoint endPoint, Map<String, Object> context) throws IOException {
        Connection connection = this.factory.newConnection(endPoint, context);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Created {}", (Object)connection);
        }
        return connection;
    }

    @ManagedAttribute(value="The maximum allowed size in bytes for an HTTP header field cache")
    public int getHeaderCacheSize() {
        return this.headerCacheSize;
    }

    public void setHeaderCacheSize(int headerCacheSize) {
        this.headerCacheSize = headerCacheSize;
    }

    @ManagedAttribute(value="Whether the header field cache is case sensitive")
    public boolean isHeaderCacheCaseSensitive() {
        return this.headerCacheCaseSensitive;
    }

    public void setHeaderCacheCaseSensitive(boolean headerCacheCaseSensitive) {
        this.headerCacheCaseSensitive = headerCacheCaseSensitive;
    }
}

