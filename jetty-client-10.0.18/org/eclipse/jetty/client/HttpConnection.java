/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.http.HttpField
 *  org.eclipse.jetty.http.HttpField$LongValueHttpField
 *  org.eclipse.jetty.http.HttpFields
 *  org.eclipse.jetty.http.HttpHeader
 *  org.eclipse.jetty.io.CyclicTimeouts
 *  org.eclipse.jetty.util.Attachable
 *  org.eclipse.jetty.util.HttpCookieStore
 *  org.eclipse.jetty.util.HttpCookieStore$Empty
 *  org.eclipse.jetty.util.NanoTime
 *  org.eclipse.jetty.util.thread.AutoLock
 *  org.eclipse.jetty.util.thread.Scheduler
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.client;

import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeoutException;
import org.eclipse.jetty.client.HttpChannel;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.HttpDestination;
import org.eclipse.jetty.client.HttpExchange;
import org.eclipse.jetty.client.HttpProxy;
import org.eclipse.jetty.client.HttpRequest;
import org.eclipse.jetty.client.HttpRequestException;
import org.eclipse.jetty.client.IConnection;
import org.eclipse.jetty.client.ProxyConfiguration;
import org.eclipse.jetty.client.SendFailure;
import org.eclipse.jetty.client.api.Authentication;
import org.eclipse.jetty.client.api.AuthenticationStore;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.client.util.BytesRequestContent;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.io.CyclicTimeouts;
import org.eclipse.jetty.util.Attachable;
import org.eclipse.jetty.util.HttpCookieStore;
import org.eclipse.jetty.util.NanoTime;
import org.eclipse.jetty.util.thread.AutoLock;
import org.eclipse.jetty.util.thread.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class HttpConnection
implements IConnection,
Attachable {
    private static final Logger LOG = LoggerFactory.getLogger(HttpConnection.class);
    private final AutoLock lock = new AutoLock();
    private final HttpDestination destination;
    private final RequestTimeouts requestTimeouts;
    private Object attachment;
    private int idleTimeoutGuard;
    private long idleTimeoutNanoTime;

    protected HttpConnection(HttpDestination destination) {
        this.destination = destination;
        this.requestTimeouts = new RequestTimeouts(destination.getHttpClient().getScheduler());
        this.idleTimeoutNanoTime = NanoTime.now();
    }

    public HttpClient getHttpClient() {
        return this.destination.getHttpClient();
    }

    public HttpDestination getHttpDestination() {
        return this.destination;
    }

    protected abstract Iterator<HttpChannel> getHttpChannels();

    @Override
    public void send(Request request, Response.CompleteListener listener) {
        HttpExchange exchange;
        SendFailure result;
        HttpRequest httpRequest = (HttpRequest)request;
        ArrayList<Response.ResponseListener> listeners = new ArrayList<Response.ResponseListener>(httpRequest.getResponseListeners());
        httpRequest.sent();
        if (listener != null) {
            listeners.add(listener);
        }
        if ((result = this.send(exchange = new HttpExchange(this.getHttpDestination(), httpRequest, listeners))) != null) {
            httpRequest.abort(result.failure);
        }
    }

    protected SendFailure send(HttpChannel channel, HttpExchange exchange) {
        boolean send;
        try (AutoLock l = this.lock.lock();){
            boolean bl = send = this.idleTimeoutGuard >= 0;
            if (send) {
                ++this.idleTimeoutGuard;
            }
        }
        if (send) {
            SendFailure result;
            HttpRequest request = exchange.getRequest();
            if (channel.associate(exchange)) {
                request.sent();
                this.requestTimeouts.schedule(channel);
                channel.send();
                result = null;
            } else {
                channel.release();
                result = new SendFailure(new HttpRequestException("Could not associate request to connection", request), false);
            }
            try (AutoLock l = this.lock.lock();){
                --this.idleTimeoutGuard;
                this.idleTimeoutNanoTime = NanoTime.now();
            }
            return result;
        }
        return new SendFailure(new TimeoutException(), true);
    }

    protected void normalizeRequest(HttpRequest request) {
        URI uri;
        Request.Content content;
        URI uri2;
        boolean normalized = request.normalized();
        if (LOG.isDebugEnabled()) {
            LOG.debug("Normalizing {} {}", (Object)(!normalized ? 1 : 0), (Object)request);
        }
        if (normalized) {
            return;
        }
        String path = request.getPath();
        if (path.trim().length() == 0) {
            path = "/";
            request.path(path);
        }
        boolean http1 = request.getVersion().getVersion() <= 11;
        boolean applyProxyAuthentication = false;
        ProxyConfiguration.Proxy proxy = this.destination.getProxy();
        if (proxy instanceof HttpProxy) {
            boolean tunnelled = ((HttpProxy)proxy).requiresTunnel(this.destination.getOrigin());
            if (http1 && !tunnelled && (uri2 = request.getURI()) != null) {
                request.path(uri2.toString());
            }
            applyProxyAuthentication = !tunnelled;
        }
        HttpFields headers = request.getHeaders();
        if (http1 && !headers.contains(HttpHeader.HOST.asString())) {
            uri2 = request.getURI();
            if (uri2 != null) {
                request.addHeader(new HttpField(HttpHeader.HOST, uri2.getAuthority()));
            } else {
                request.addHeader(this.getHttpDestination().getHostField());
            }
        }
        if ((content = request.getBody()) == null) {
            request.body(new BytesRequestContent(new byte[0][]));
        } else {
            long contentLength;
            if (!headers.contains(HttpHeader.CONTENT_TYPE)) {
                String contentType = content.getContentType();
                if (contentType == null) {
                    contentType = this.getHttpClient().getDefaultRequestContentType();
                }
                if (contentType != null) {
                    HttpField field = new HttpField(HttpHeader.CONTENT_TYPE, contentType);
                    request.addHeader(field);
                }
            }
            if ((contentLength = content.getLength()) >= 0L && !headers.contains(HttpHeader.CONTENT_LENGTH)) {
                request.addHeader((HttpField)new HttpField.LongValueHttpField(HttpHeader.CONTENT_LENGTH, contentLength));
            }
        }
        StringBuilder cookies = this.convertCookies(request.getCookies(), null);
        CookieStore cookieStore = this.getHttpClient().getCookieStore();
        if (cookieStore != null && cookieStore.getClass() != HttpCookieStore.Empty.class && (uri = request.getURI()) != null) {
            cookies = this.convertCookies(HttpCookieStore.matchPath((URI)uri, cookieStore.get(uri)), cookies);
        }
        if (cookies != null) {
            HttpField cookieField = new HttpField(HttpHeader.COOKIE, cookies.toString());
            request.addHeader(cookieField);
        }
        if (applyProxyAuthentication) {
            this.applyProxyAuthentication(request, proxy);
        }
        this.applyRequestAuthentication(request);
    }

    private StringBuilder convertCookies(List<HttpCookie> cookies, StringBuilder builder) {
        for (HttpCookie cookie : cookies) {
            if (builder == null) {
                builder = new StringBuilder();
            }
            if (builder.length() > 0) {
                builder.append("; ");
            }
            builder.append(cookie.getName()).append("=").append(cookie.getValue());
        }
        return builder;
    }

    private void applyRequestAuthentication(Request request) {
        Authentication.Result result;
        URI uri;
        AuthenticationStore authenticationStore = this.getHttpClient().getAuthenticationStore();
        if (authenticationStore.hasAuthenticationResults() && (uri = request.getURI()) != null && (result = authenticationStore.findAuthenticationResult(uri)) != null) {
            result.apply(request);
        }
    }

    private void applyProxyAuthentication(Request request, ProxyConfiguration.Proxy proxy) {
        Authentication.Result result;
        if (proxy != null && (result = this.getHttpClient().getAuthenticationStore().findAuthenticationResult(proxy.getURI())) != null) {
            result.apply(request);
        }
    }

    public boolean onIdleTimeout(long idleTimeout, Throwable failure) {
        try (AutoLock l = this.lock.lock();){
            if (this.idleTimeoutGuard == 0) {
                boolean idle;
                long elapsed = NanoTime.millisSince((long)this.idleTimeoutNanoTime);
                boolean bl = idle = elapsed > idleTimeout / 2L;
                if (idle) {
                    this.idleTimeoutGuard = -1;
                }
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Idle timeout {}/{}ms - {}", new Object[]{elapsed, idleTimeout, this});
                }
                boolean bl2 = idle;
                return bl2;
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("Idle timeout skipped - {}", (Object)this);
            }
            boolean bl = false;
            return bl;
        }
    }

    public void setAttachment(Object obj) {
        this.attachment = obj;
    }

    public Object getAttachment() {
        return this.attachment;
    }

    public void destroy() {
        this.requestTimeouts.destroy();
    }

    public String toString() {
        return String.format("%s@%h", this.getClass().getSimpleName(), this);
    }

    private class RequestTimeouts
    extends CyclicTimeouts<HttpChannel> {
        private RequestTimeouts(Scheduler scheduler) {
            super(scheduler);
        }

        protected Iterator<HttpChannel> iterator() {
            return HttpConnection.this.getHttpChannels();
        }

        protected boolean onExpired(HttpChannel channel) {
            HttpExchange exchange = channel.getHttpExchange();
            if (exchange != null) {
                HttpRequest request = exchange.getRequest();
                request.abort(new TimeoutException("Total timeout " + request.getConversation().getTimeout() + " ms elapsed"));
            }
            return false;
        }
    }
}

