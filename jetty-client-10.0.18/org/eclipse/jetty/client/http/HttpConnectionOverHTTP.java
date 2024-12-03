/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.http.HttpHeader
 *  org.eclipse.jetty.http.HttpVersion
 *  org.eclipse.jetty.io.AbstractConnection
 *  org.eclipse.jetty.io.Connection$UpgradeFrom
 *  org.eclipse.jetty.io.EndPoint
 *  org.eclipse.jetty.util.Attachable
 *  org.eclipse.jetty.util.Promise
 *  org.eclipse.jetty.util.thread.Sweeper$Sweepable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.client.http;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;
import org.eclipse.jetty.client.HttpChannel;
import org.eclipse.jetty.client.HttpConnection;
import org.eclipse.jetty.client.HttpConversation;
import org.eclipse.jetty.client.HttpDestination;
import org.eclipse.jetty.client.HttpExchange;
import org.eclipse.jetty.client.HttpProxy;
import org.eclipse.jetty.client.HttpRequest;
import org.eclipse.jetty.client.HttpUpgrader;
import org.eclipse.jetty.client.IConnection;
import org.eclipse.jetty.client.SendFailure;
import org.eclipse.jetty.client.api.Connection;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.client.http.HttpChannelOverHTTP;
import org.eclipse.jetty.client.http.HttpReceiverOverHTTP;
import org.eclipse.jetty.client.http.ProtocolHttpUpgrader;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.io.AbstractConnection;
import org.eclipse.jetty.io.Connection;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.util.Attachable;
import org.eclipse.jetty.util.Promise;
import org.eclipse.jetty.util.thread.Sweeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpConnectionOverHTTP
extends AbstractConnection
implements IConnection,
Connection.UpgradeFrom,
Sweeper.Sweepable,
Attachable {
    private static final Logger LOG = LoggerFactory.getLogger(HttpConnectionOverHTTP.class);
    private final AtomicBoolean closed = new AtomicBoolean();
    private final AtomicInteger sweeps = new AtomicInteger();
    private final Promise<Connection> promise;
    private final Delegate delegate;
    private final HttpChannelOverHTTP channel;
    private final LongAdder bytesIn = new LongAdder();
    private final LongAdder bytesOut = new LongAdder();
    private long idleTimeout;

    public HttpConnectionOverHTTP(EndPoint endPoint, Map<String, Object> context) {
        this(endPoint, HttpConnectionOverHTTP.destinationFrom(context), HttpConnectionOverHTTP.promiseFrom(context));
    }

    private static HttpDestination destinationFrom(Map<String, Object> context) {
        return (HttpDestination)context.get("org.eclipse.jetty.client.destination");
    }

    private static Promise<Connection> promiseFrom(Map<String, Object> context) {
        return (Promise)context.get("org.eclipse.jetty.client.connection.promise");
    }

    public HttpConnectionOverHTTP(EndPoint endPoint, HttpDestination destination, Promise<Connection> promise) {
        super(endPoint, destination.getHttpClient().getExecutor());
        this.promise = promise;
        this.delegate = new Delegate(destination);
        this.channel = this.newHttpChannel();
    }

    protected HttpChannelOverHTTP newHttpChannel() {
        return new HttpChannelOverHTTP(this);
    }

    public HttpChannelOverHTTP getHttpChannel() {
        return this.channel;
    }

    public HttpDestination getHttpDestination() {
        return this.delegate.getHttpDestination();
    }

    public long getBytesIn() {
        return this.bytesIn.longValue();
    }

    protected void addBytesIn(long bytesIn) {
        this.bytesIn.add(bytesIn);
    }

    public long getBytesOut() {
        return this.bytesOut.longValue();
    }

    protected void addBytesOut(long bytesOut) {
        this.bytesOut.add(bytesOut);
    }

    public long getMessagesIn() {
        return this.getHttpChannel().getMessagesIn();
    }

    public long getMessagesOut() {
        return this.getHttpChannel().getMessagesOut();
    }

    @Override
    public void send(Request request, Response.CompleteListener listener) {
        this.delegate.send(request, listener);
    }

    @Override
    public SendFailure send(HttpExchange exchange) {
        return this.delegate.send(exchange);
    }

    public void onOpen() {
        super.onOpen();
        this.fillInterested();
        this.promise.succeeded((Object)this);
    }

    @Override
    public boolean isClosed() {
        return this.closed.get();
    }

    public void setAttachment(Object obj) {
        this.delegate.setAttachment(obj);
    }

    public Object getAttachment() {
        return this.delegate.getAttachment();
    }

    public boolean onIdleExpired() {
        long idleTimeout = this.getEndPoint().getIdleTimeout();
        boolean close = this.onIdleTimeout(idleTimeout);
        if (close) {
            this.close(new TimeoutException("Idle timeout " + idleTimeout + " ms"));
        }
        return false;
    }

    protected boolean onIdleTimeout(long idleTimeout) {
        TimeoutException failure = new TimeoutException("Idle timeout " + idleTimeout + " ms");
        return this.delegate.onIdleTimeout(idleTimeout, failure);
    }

    public void onFillable() {
        this.channel.receive();
    }

    public ByteBuffer onUpgradeFrom() {
        HttpReceiverOverHTTP receiver = this.channel.getHttpReceiver();
        return receiver.onUpgradeFrom();
    }

    void onResponseHeaders(HttpExchange exchange) {
        HttpRequest request = exchange.getRequest();
        if (request instanceof HttpProxy.TunnelRequest) {
            this.getEndPoint().setIdleTimeout(this.idleTimeout);
        }
    }

    public void release() {
        this.getEndPoint().setIdleTimeout(this.idleTimeout);
        this.getHttpDestination().release(this);
    }

    public void remove() {
        this.getHttpDestination().remove(this);
    }

    @Override
    public void close() {
        this.close(new AsynchronousCloseException());
    }

    protected void close(Throwable failure) {
        if (this.closed.compareAndSet(false, true)) {
            this.getHttpDestination().remove(this);
            this.abort(failure);
            this.channel.destroy();
            this.getEndPoint().shutdownOutput();
            if (LOG.isDebugEnabled()) {
                LOG.debug("Shutdown {}", (Object)this);
            }
            this.getEndPoint().close();
            if (LOG.isDebugEnabled()) {
                LOG.debug("Closed {}", (Object)this);
            }
            this.delegate.destroy();
        }
    }

    protected boolean abort(Throwable failure) {
        HttpExchange exchange = this.channel.getHttpExchange();
        return exchange != null && exchange.getRequest().abort(failure);
    }

    public boolean sweep() {
        if (!this.closed.get()) {
            return false;
        }
        return this.sweeps.incrementAndGet() > 3;
    }

    public String toConnectionString() {
        return String.format("%s@%x(l:%s <-> r:%s,closed=%b)=>%s", this.getClass().getSimpleName(), this.hashCode(), this.getEndPoint().getLocalSocketAddress(), this.getEndPoint().getRemoteSocketAddress(), this.closed.get(), this.channel);
    }

    private class Delegate
    extends HttpConnection {
        private Delegate(HttpDestination destination) {
            super(destination);
        }

        @Override
        protected Iterator<HttpChannel> getHttpChannels() {
            return Collections.singleton(HttpConnectionOverHTTP.this.channel).iterator();
        }

        @Override
        public SendFailure send(HttpExchange exchange) {
            HttpRequest request = exchange.getRequest();
            this.normalizeRequest(request);
            EndPoint endPoint = HttpConnectionOverHTTP.this.getEndPoint();
            HttpConnectionOverHTTP.this.idleTimeout = endPoint.getIdleTimeout();
            long requestIdleTimeout = request.getIdleTimeout();
            if (requestIdleTimeout >= 0L) {
                endPoint.setIdleTimeout(requestIdleTimeout);
            }
            return this.send(HttpConnectionOverHTTP.this.channel, exchange);
        }

        @Override
        protected void normalizeRequest(HttpRequest request) {
            HttpConversation conversation;
            HttpUpgrader upgrader;
            super.normalizeRequest(request);
            if (request instanceof HttpProxy.TunnelRequest) {
                request.idleTimeout(2L * this.getHttpClient().getConnectTimeout(), TimeUnit.MILLISECONDS);
            }
            if ((upgrader = (HttpUpgrader)(conversation = request.getConversation()).getAttribute(HttpUpgrader.class.getName())) == null) {
                if (request instanceof HttpUpgrader.Factory) {
                    upgrader = ((HttpUpgrader.Factory)((Object)request)).newHttpUpgrader(HttpVersion.HTTP_1_1);
                    conversation.setAttribute(HttpUpgrader.class.getName(), upgrader);
                    upgrader.prepare(request);
                } else {
                    String protocol = request.getHeaders().get(HttpHeader.UPGRADE);
                    if (protocol != null) {
                        upgrader = new ProtocolHttpUpgrader(this.getHttpDestination(), protocol);
                        conversation.setAttribute(HttpUpgrader.class.getName(), upgrader);
                        upgrader.prepare(request);
                    }
                }
            }
        }

        @Override
        public void close() {
            HttpConnectionOverHTTP.this.close();
        }

        @Override
        public boolean isClosed() {
            return HttpConnectionOverHTTP.this.isClosed();
        }

        @Override
        public String toString() {
            return HttpConnectionOverHTTP.this.toString();
        }
    }
}

