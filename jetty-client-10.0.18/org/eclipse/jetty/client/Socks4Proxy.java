/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.io.AbstractConnection
 *  org.eclipse.jetty.io.ClientConnectionFactory
 *  org.eclipse.jetty.io.Connection
 *  org.eclipse.jetty.io.EndPoint
 *  org.eclipse.jetty.util.BufferUtil
 *  org.eclipse.jetty.util.Callback
 *  org.eclipse.jetty.util.Promise
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.jetty.client.HttpDestination;
import org.eclipse.jetty.client.Origin;
import org.eclipse.jetty.client.ProxyConfiguration;
import org.eclipse.jetty.io.AbstractConnection;
import org.eclipse.jetty.io.ClientConnectionFactory;
import org.eclipse.jetty.io.Connection;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Socks4Proxy
extends ProxyConfiguration.Proxy {
    public Socks4Proxy(String host, int port) {
        this(new Origin.Address(host, port), false);
    }

    public Socks4Proxy(Origin.Address address, boolean secure) {
        super(address, secure, null, null);
    }

    @Override
    public ClientConnectionFactory newClientConnectionFactory(ClientConnectionFactory connectionFactory) {
        return new Socks4ProxyClientConnectionFactory(connectionFactory);
    }

    public static class Socks4ProxyClientConnectionFactory
    implements ClientConnectionFactory {
        private final ClientConnectionFactory connectionFactory;

        public Socks4ProxyClientConnectionFactory(ClientConnectionFactory connectionFactory) {
            this.connectionFactory = connectionFactory;
        }

        public Connection newConnection(EndPoint endPoint, Map<String, Object> context) {
            HttpDestination destination = (HttpDestination)context.get("org.eclipse.jetty.client.destination");
            Executor executor = destination.getHttpClient().getExecutor();
            Socks4ProxyConnection connection = new Socks4ProxyConnection(endPoint, executor, this.connectionFactory, context);
            return this.customize((Connection)connection, context);
        }
    }

    private static class Socks4ProxyConnection
    extends AbstractConnection
    implements Callback {
        private static final Pattern IPv4_PATTERN = Pattern.compile("(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})");
        private static final Logger LOG = LoggerFactory.getLogger(Socks4ProxyConnection.class);
        private final Socks4Parser parser = new Socks4Parser();
        private final ClientConnectionFactory connectionFactory;
        private final Map<String, Object> context;

        public Socks4ProxyConnection(EndPoint endPoint, Executor executor, ClientConnectionFactory connectionFactory, Map<String, Object> context) {
            super(endPoint, executor);
            this.connectionFactory = connectionFactory;
            this.context = context;
        }

        public void onOpen() {
            super.onOpen();
            this.writeSocks4Connect();
        }

        private void writeSocks4Connect() {
            HttpDestination destination = (HttpDestination)this.context.get("org.eclipse.jetty.client.destination");
            String host = destination.getHost();
            short port = (short)destination.getPort();
            Matcher matcher = IPv4_PATTERN.matcher(host);
            if (matcher.matches()) {
                ByteBuffer buffer = ByteBuffer.allocate(9);
                buffer.put((byte)4).put((byte)1).putShort(port);
                for (int i = 1; i <= 4; ++i) {
                    buffer.put((byte)Integer.parseInt(matcher.group(i)));
                }
                buffer.put((byte)0);
                buffer.flip();
                this.getEndPoint().write((Callback)this, new ByteBuffer[]{buffer});
            } else {
                byte[] hostBytes = host.getBytes(StandardCharsets.UTF_8);
                ByteBuffer buffer = ByteBuffer.allocate(9 + hostBytes.length + 1);
                buffer.put((byte)4).put((byte)1).putShort(port);
                buffer.put((byte)0).put((byte)0).put((byte)0).put((byte)1).put((byte)0);
                buffer.put(hostBytes).put((byte)0);
                buffer.flip();
                this.getEndPoint().write((Callback)this, new ByteBuffer[]{buffer});
            }
        }

        public void succeeded() {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Written SOCKS4 connect request");
            }
            this.fillInterested();
        }

        public void failed(Throwable x) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("SOCKS4 failure", x);
            }
            this.getEndPoint().close(x);
            Promise promise = (Promise)this.context.get("org.eclipse.jetty.client.connection.promise");
            promise.failed(x);
        }

        public boolean onIdleExpired() {
            this.failed(new TimeoutException("Idle timeout expired"));
            return false;
        }

        public void onFillable() {
            try {
                ByteBuffer buffer;
                do {
                    buffer = BufferUtil.allocate((int)this.parser.expected());
                    int filled = this.getEndPoint().fill(buffer);
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Read SOCKS4 connect response, {} bytes", (Object)filled);
                    }
                    if (filled < 0) {
                        throw new IOException("SOCKS4 tunnel failed, connection closed");
                    }
                    if (filled != 0) continue;
                    this.fillInterested();
                    return;
                } while (!this.parser.parse(buffer));
                return;
            }
            catch (Throwable x) {
                this.failed(x);
                return;
            }
        }

        private void onSocks4Response(int responseCode) throws IOException {
            if (responseCode != 90) {
                throw new IOException("SOCKS4 tunnel failed with code " + responseCode);
            }
            this.tunnel();
        }

        private void tunnel() {
            try {
                HttpDestination destination = (HttpDestination)this.context.get("org.eclipse.jetty.client.destination");
                InetSocketAddress address = InetSocketAddress.createUnresolved(destination.getHost(), destination.getPort());
                this.context.put("org.eclipse.jetty.client.connector.remoteSocketAddress", address);
                ClientConnectionFactory connectionFactory = this.connectionFactory;
                if (destination.isSecure()) {
                    connectionFactory = destination.newSslClientConnectionFactory(null, connectionFactory);
                }
                Connection newConnection = connectionFactory.newConnection(this.getEndPoint(), this.context);
                this.getEndPoint().upgrade(newConnection);
                if (LOG.isDebugEnabled()) {
                    LOG.debug("SOCKS4 tunnel established: {} over {}", (Object)this, (Object)newConnection);
                }
            }
            catch (Throwable x) {
                this.failed(x);
            }
        }

        private class Socks4Parser {
            private static final int EXPECTED_LENGTH = 8;
            private int cursor;
            private int response;

            private Socks4Parser() {
            }

            private boolean parse(ByteBuffer buffer) throws IOException {
                while (buffer.hasRemaining()) {
                    byte current = buffer.get();
                    if (this.cursor == 1) {
                        this.response = current & 0xFF;
                    }
                    ++this.cursor;
                    if (this.cursor != 8) continue;
                    Socks4ProxyConnection.this.onSocks4Response(this.response);
                    return true;
                }
                return false;
            }

            private int expected() {
                return 8 - this.cursor;
            }
        }
    }
}

