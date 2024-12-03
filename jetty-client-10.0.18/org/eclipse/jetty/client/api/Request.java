/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.http.HttpFields
 *  org.eclipse.jetty.http.HttpFields$Mutable
 *  org.eclipse.jetty.http.HttpHeader
 *  org.eclipse.jetty.http.HttpMethod
 *  org.eclipse.jetty.http.HttpVersion
 *  org.eclipse.jetty.util.Callback
 *  org.eclipse.jetty.util.Fields
 */
package org.eclipse.jetty.client.api;

import java.io.IOException;
import java.net.HttpCookie;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.EventListener;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import org.eclipse.jetty.client.api.ContentProvider;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.Fields;

public interface Request {
    public String getScheme();

    public Request scheme(String var1);

    public String getHost();

    default public Request host(String host) {
        return this;
    }

    public int getPort();

    default public Request port(int port) {
        return this;
    }

    public String getMethod();

    public Request method(HttpMethod var1);

    public Request method(String var1);

    public String getPath();

    public Request path(String var1);

    public String getQuery();

    public URI getURI();

    public HttpVersion getVersion();

    public Request version(HttpVersion var1);

    public Fields getParams();

    public Request param(String var1, String var2);

    public HttpFields getHeaders();

    public Request headers(Consumer<HttpFields.Mutable> var1);

    @Deprecated
    public Request header(String var1, String var2);

    @Deprecated
    public Request header(HttpHeader var1, String var2);

    public List<HttpCookie> getCookies();

    public Request cookie(HttpCookie var1);

    public Request tag(Object var1);

    public Object getTag();

    public Request attribute(String var1, Object var2);

    public Map<String, Object> getAttributes();

    @Deprecated
    public ContentProvider getContent();

    @Deprecated
    public Request content(ContentProvider var1);

    @Deprecated
    public Request content(ContentProvider var1, String var2);

    public Content getBody();

    public Request body(Content var1);

    public Request file(Path var1) throws IOException;

    public Request file(Path var1, String var2) throws IOException;

    public String getAgent();

    public Request agent(String var1);

    public Request accept(String ... var1);

    public long getIdleTimeout();

    public Request idleTimeout(long var1, TimeUnit var3);

    public long getTimeout();

    public Request timeout(long var1, TimeUnit var3);

    public boolean isFollowRedirects();

    public Request followRedirects(boolean var1);

    public <T extends RequestListener> List<T> getRequestListeners(Class<T> var1);

    public Request listener(Listener var1);

    public Request onRequestQueued(QueuedListener var1);

    public Request onRequestBegin(BeginListener var1);

    public Request onRequestHeaders(HeadersListener var1);

    public Request onRequestCommit(CommitListener var1);

    public Request onRequestContent(ContentListener var1);

    public Request onRequestSuccess(SuccessListener var1);

    public Request onRequestFailure(FailureListener var1);

    public Request onResponseBegin(Response.BeginListener var1);

    public Request onResponseHeader(Response.HeaderListener var1);

    public Request onResponseHeaders(Response.HeadersListener var1);

    public Request onResponseContent(Response.ContentListener var1);

    public Request onResponseContentAsync(Response.AsyncContentListener var1);

    public Request onResponseContentDemanded(Response.DemandedContentListener var1);

    public Request onResponseSuccess(Response.SuccessListener var1);

    public Request onResponseFailure(Response.FailureListener var1);

    public Request onComplete(Response.CompleteListener var1);

    public ContentResponse send() throws InterruptedException, TimeoutException, ExecutionException;

    public void send(Response.CompleteListener var1);

    public boolean abort(Throwable var1);

    public Throwable getAbortCause();

    public static interface Content {
        default public String getContentType() {
            return "application/octet-stream";
        }

        default public long getLength() {
            return -1L;
        }

        default public boolean isReproducible() {
            return false;
        }

        public Subscription subscribe(Consumer var1, boolean var2);

        default public void fail(Throwable failure) {
        }

        public static interface Subscription {
            public void demand();

            default public void fail(Throwable failure) {
            }
        }

        public static interface Consumer {
            public void onContent(ByteBuffer var1, boolean var2, Callback var3);

            default public void onFailure(Throwable failure) {
            }
        }
    }

    public static interface Listener
    extends QueuedListener,
    BeginListener,
    HeadersListener,
    CommitListener,
    ContentListener,
    SuccessListener,
    FailureListener {
        @Override
        default public void onQueued(Request request) {
        }

        @Override
        default public void onBegin(Request request) {
        }

        @Override
        default public void onHeaders(Request request) {
        }

        @Override
        default public void onCommit(Request request) {
        }

        @Override
        default public void onContent(Request request, ByteBuffer content) {
        }

        @Override
        default public void onSuccess(Request request) {
        }

        @Override
        default public void onFailure(Request request, Throwable failure) {
        }

        public static class Adapter
        implements Listener {
        }
    }

    public static interface FailureListener
    extends RequestListener {
        public void onFailure(Request var1, Throwable var2);
    }

    public static interface SuccessListener
    extends RequestListener {
        public void onSuccess(Request var1);
    }

    public static interface ContentListener
    extends RequestListener {
        public void onContent(Request var1, ByteBuffer var2);
    }

    public static interface CommitListener
    extends RequestListener {
        public void onCommit(Request var1);
    }

    public static interface HeadersListener
    extends RequestListener {
        public void onHeaders(Request var1);
    }

    public static interface BeginListener
    extends RequestListener {
        public void onBegin(Request var1);
    }

    public static interface QueuedListener
    extends RequestListener {
        public void onQueued(Request var1);
    }

    public static interface RequestListener
    extends EventListener {
    }
}

