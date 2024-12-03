/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.hc.client5.http.impl.nio;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.hc.client5.http.ConnectExceptionSupport;
import org.apache.hc.client5.http.DnsResolver;
import org.apache.hc.client5.http.SystemDefaultDnsResolver;
import org.apache.hc.core5.concurrent.ComplexFuture;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.net.NamedEndpoint;
import org.apache.hc.core5.reactor.ConnectionInitiator;
import org.apache.hc.core5.reactor.IOSession;
import org.apache.hc.core5.util.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class MultihomeIOSessionRequester {
    private static final Logger LOG = LoggerFactory.getLogger(MultihomeIOSessionRequester.class);
    private final DnsResolver dnsResolver;

    MultihomeIOSessionRequester(DnsResolver dnsResolver) {
        this.dnsResolver = dnsResolver != null ? dnsResolver : SystemDefaultDnsResolver.INSTANCE;
    }

    public Future<IOSession> connect(final ConnectionInitiator connectionInitiator, final NamedEndpoint remoteEndpoint, final SocketAddress remoteAddress, final SocketAddress localAddress, final Timeout connectTimeout, final Object attachment, FutureCallback<IOSession> callback) {
        InetAddress[] remoteAddresses;
        final ComplexFuture<IOSession> future = new ComplexFuture<IOSession>(callback);
        if (remoteAddress != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("{}:{} connecting {} to {} ({})", new Object[]{remoteEndpoint.getHostName(), remoteEndpoint.getPort(), localAddress, remoteAddress, connectTimeout});
            }
            Future<IOSession> sessionFuture = connectionInitiator.connect(remoteEndpoint, remoteAddress, localAddress, connectTimeout, attachment, new FutureCallback<IOSession>(){

                @Override
                public void completed(IOSession session) {
                    future.completed(session);
                }

                @Override
                public void failed(Exception cause) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("{}:{} connection to {} failed ({}); terminating operation", new Object[]{remoteEndpoint.getHostName(), remoteEndpoint.getPort(), remoteAddress, cause.getClass()});
                    }
                    if (cause instanceof IOException) {
                        InetAddress[] inetAddressArray;
                        IOException iOException = (IOException)cause;
                        if (remoteAddress instanceof InetSocketAddress) {
                            InetAddress[] inetAddressArray2 = new InetAddress[1];
                            inetAddressArray = inetAddressArray2;
                            inetAddressArray2[0] = ((InetSocketAddress)remoteAddress).getAddress();
                        } else {
                            inetAddressArray = new InetAddress[]{};
                        }
                        future.failed(ConnectExceptionSupport.enhance(iOException, remoteEndpoint, inetAddressArray));
                    } else {
                        future.failed(cause);
                    }
                }

                @Override
                public void cancelled() {
                    future.cancel();
                }
            });
            future.setDependency(sessionFuture);
            return future;
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("{} resolving remote address", (Object)remoteEndpoint.getHostName());
        }
        try {
            remoteAddresses = this.dnsResolver.resolve(remoteEndpoint.getHostName());
        }
        catch (UnknownHostException ex) {
            future.failed(ex);
            return future;
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("{} resolved to {}", (Object)remoteEndpoint.getHostName(), Arrays.asList(remoteAddresses));
        }
        Runnable runnable = new Runnable(){
            private final AtomicInteger attempt = new AtomicInteger(0);

            void executeNext() {
                int index = this.attempt.getAndIncrement();
                final InetSocketAddress remoteAddress = new InetSocketAddress(remoteAddresses[index], remoteEndpoint.getPort());
                if (LOG.isDebugEnabled()) {
                    LOG.debug("{}:{} connecting {}->{} ({})", new Object[]{remoteEndpoint.getHostName(), remoteEndpoint.getPort(), localAddress, remoteAddress, connectTimeout});
                }
                Future<IOSession> sessionFuture = connectionInitiator.connect(remoteEndpoint, remoteAddress, localAddress, connectTimeout, attachment, new FutureCallback<IOSession>(){

                    @Override
                    public void completed(IOSession session) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("{}:{} connected {}->{} as {}", new Object[]{remoteEndpoint.getHostName(), remoteEndpoint.getPort(), localAddress, remoteAddress, session.getId()});
                        }
                        future.completed(session);
                    }

                    @Override
                    public void failed(Exception cause) {
                        if (attempt.get() >= remoteAddresses.length) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("{}:{} connection to {} failed ({}); terminating operation", new Object[]{remoteEndpoint.getHostName(), remoteEndpoint.getPort(), remoteAddress, cause.getClass()});
                            }
                            if (cause instanceof IOException) {
                                future.failed(ConnectExceptionSupport.enhance((IOException)cause, remoteEndpoint, remoteAddresses));
                            } else {
                                future.failed(cause);
                            }
                        } else {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("{}:{} connection to {} failed ({}); retrying connection to the next address", new Object[]{remoteEndpoint.getHostName(), remoteEndpoint.getPort(), remoteAddress, cause.getClass()});
                            }
                            this.executeNext();
                        }
                    }

                    @Override
                    public void cancelled() {
                        future.cancel();
                    }
                });
                future.setDependency(sessionFuture);
            }

            @Override
            public void run() {
                this.executeNext();
            }
        };
        runnable.run();
        return future;
    }

    public Future<IOSession> connect(ConnectionInitiator connectionInitiator, NamedEndpoint remoteEndpoint, SocketAddress localAddress, Timeout connectTimeout, Object attachment, FutureCallback<IOSession> callback) {
        return this.connect(connectionInitiator, remoteEndpoint, null, localAddress, connectTimeout, attachment, callback);
    }
}

