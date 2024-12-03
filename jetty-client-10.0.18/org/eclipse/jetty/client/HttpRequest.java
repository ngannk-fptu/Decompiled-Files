/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.http.HttpField
 *  org.eclipse.jetty.http.HttpFields
 *  org.eclipse.jetty.http.HttpFields$Mutable
 *  org.eclipse.jetty.http.HttpHeader
 *  org.eclipse.jetty.http.HttpMethod
 *  org.eclipse.jetty.http.HttpVersion
 *  org.eclipse.jetty.util.Callback
 *  org.eclipse.jetty.util.Fields
 *  org.eclipse.jetty.util.Fields$Field
 *  org.eclipse.jetty.util.NanoTime
 *  org.eclipse.jetty.util.URIUtil
 */
package org.eclipse.jetty.client;

import java.io.IOException;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.LongConsumer;
import java.util.function.Supplier;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.HttpConversation;
import org.eclipse.jetty.client.HttpDestination;
import org.eclipse.jetty.client.Origin;
import org.eclipse.jetty.client.api.ContentProvider;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.client.api.Result;
import org.eclipse.jetty.client.internal.RequestContentAdapter;
import org.eclipse.jetty.client.util.FutureResponseListener;
import org.eclipse.jetty.client.util.PathRequestContent;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.Fields;
import org.eclipse.jetty.util.NanoTime;
import org.eclipse.jetty.util.URIUtil;

public class HttpRequest
implements Request {
    private static final URI NULL_URI = URI.create("null:0");
    private final HttpFields.Mutable headers = HttpFields.build();
    private final Fields params = new Fields(true);
    private final List<Response.ResponseListener> responseListeners = new ArrayList<Response.ResponseListener>();
    private final AtomicReference<Throwable> aborted = new AtomicReference();
    private final HttpClient client;
    private final HttpConversation conversation;
    private String scheme;
    private String host;
    private int port;
    private String path;
    private String query;
    private URI uri;
    private String method = HttpMethod.GET.asString();
    private HttpVersion version = HttpVersion.HTTP_1_1;
    private boolean versionExplicit;
    private long idleTimeout = -1L;
    private long timeout;
    private long timeoutNanoTime = Long.MAX_VALUE;
    private Request.Content content;
    private boolean followRedirects;
    private List<HttpCookie> cookies;
    private Map<String, Object> attributes;
    private List<Request.RequestListener> requestListeners;
    private BiFunction<Request, Request, Response.CompleteListener> pushListener;
    private Supplier<HttpFields> trailers;
    private String upgradeProtocol;
    private Object tag;
    private boolean normalized;

    protected HttpRequest(HttpClient client, HttpConversation conversation, URI uri) {
        HttpField userAgentField;
        if (uri.getHost() == null) {
            throw new IllegalArgumentException(String.format("Invalid URI host: null (authority: %s)", uri.getRawAuthority()));
        }
        this.client = client;
        this.conversation = conversation;
        this.scheme = uri.getScheme();
        this.host = uri.getHost();
        this.port = HttpClient.normalizePort(this.scheme, uri.getPort());
        this.path = uri.getRawPath();
        this.query = uri.getRawQuery();
        this.extractParams(this.query);
        this.followRedirects(client.isFollowRedirects());
        HttpField acceptEncodingField = client.getAcceptEncodingField();
        if (acceptEncodingField != null) {
            this.headers.put(acceptEncodingField);
        }
        if ((userAgentField = client.getUserAgentField()) != null) {
            this.headers.put(userAgentField);
        }
    }

    HttpRequest copy(URI newURI) {
        if (newURI == null) {
            StringBuilder builder = new StringBuilder(64);
            URIUtil.appendSchemeHostPort((StringBuilder)builder, (String)this.getScheme(), (String)this.getHost(), (int)this.getPort());
            newURI = URI.create(builder.toString());
        }
        HttpRequest newRequest = this.copyInstance(newURI);
        newRequest.method(this.getMethod()).version(this.getVersion()).body(this.getBody()).idleTimeout(this.getIdleTimeout(), TimeUnit.MILLISECONDS).timeout(this.getTimeout(), TimeUnit.MILLISECONDS).followRedirects(this.isFollowRedirects()).tag(this.getTag()).headers(h -> h.clear().add(this.getHeaders()).remove(EnumSet.of(HttpHeader.HOST, HttpHeader.EXPECT, HttpHeader.COOKIE, HttpHeader.AUTHORIZATION, HttpHeader.PROXY_AUTHORIZATION)));
        return newRequest;
    }

    HttpRequest copyInstance(URI newURI) {
        return new HttpRequest(this.getHttpClient(), this.getConversation(), newURI);
    }

    HttpClient getHttpClient() {
        return this.client;
    }

    public HttpConversation getConversation() {
        return this.conversation;
    }

    @Override
    public String getScheme() {
        return this.scheme;
    }

    @Override
    public Request scheme(String scheme) {
        this.scheme = scheme;
        this.uri = null;
        return this;
    }

    @Override
    public String getHost() {
        return this.host;
    }

    @Override
    public Request host(String host) {
        this.host = host;
        this.uri = null;
        return this;
    }

    @Override
    public int getPort() {
        return this.port;
    }

    @Override
    public Request port(int port) {
        this.port = port;
        this.uri = null;
        return this;
    }

    @Override
    public String getMethod() {
        return this.method;
    }

    @Override
    public Request method(HttpMethod method) {
        return this.method(method.asString());
    }

    @Override
    public Request method(String method) {
        this.method = Objects.requireNonNull(method).toUpperCase(Locale.ENGLISH);
        return this;
    }

    @Override
    public String getPath() {
        return this.path;
    }

    @Override
    public Request path(String path) {
        URI uri = this.newURI(path);
        if (uri == null) {
            this.path = path;
            this.query = null;
        } else {
            Object rawPath = uri.getRawPath();
            if (rawPath == null) {
                rawPath = "";
            }
            if (!((String)rawPath).startsWith("/")) {
                rawPath = "/" + (String)rawPath;
            }
            this.path = rawPath;
            String query = uri.getRawQuery();
            if (query != null) {
                this.query = query;
                this.params.clear();
                this.extractParams(query);
            }
            if (uri.isAbsolute()) {
                this.path = this.buildURI(false).toString();
            }
        }
        this.uri = null;
        return this;
    }

    @Override
    public String getQuery() {
        return this.query;
    }

    @Override
    public URI getURI() {
        if (this.uri == null) {
            this.uri = this.buildURI(true);
        }
        boolean isNullURI = this.uri == NULL_URI;
        return isNullURI ? null : this.uri;
    }

    @Override
    public HttpVersion getVersion() {
        return this.version;
    }

    public boolean isVersionExplicit() {
        return this.versionExplicit;
    }

    @Override
    public Request version(HttpVersion version) {
        this.version = Objects.requireNonNull(version);
        this.versionExplicit = true;
        return this;
    }

    @Override
    public Request param(String name, String value) {
        return this.param(name, value, false);
    }

    private Request param(String name, String value, boolean fromQuery) {
        this.params.add(name, value);
        if (!fromQuery) {
            this.query = this.query != null ? this.query + "&" + this.urlEncode(name) + "=" + this.urlEncode(value) : this.buildQuery();
            this.uri = null;
        }
        return this;
    }

    @Override
    public Fields getParams() {
        return new Fields(this.params, true);
    }

    @Override
    public String getAgent() {
        return this.headers.get(HttpHeader.USER_AGENT);
    }

    @Override
    public Request agent(String agent) {
        this.headers.put(HttpHeader.USER_AGENT, agent);
        return this;
    }

    @Override
    public Request accept(String ... accepts) {
        StringBuilder result = new StringBuilder();
        for (String accept : accepts) {
            if (result.length() > 0) {
                result.append(", ");
            }
            result.append(accept);
        }
        if (result.length() > 0) {
            this.headers.put(HttpHeader.ACCEPT, result.toString());
        }
        return this;
    }

    @Override
    @Deprecated
    public Request header(String name, String value) {
        if (value == null) {
            this.headers.remove(name);
        } else {
            this.headers.add(name, value);
        }
        return this;
    }

    @Override
    @Deprecated
    public Request header(HttpHeader header, String value) {
        if (value == null) {
            this.headers.remove(header);
        } else {
            this.headers.add(header, value);
        }
        return this;
    }

    @Override
    public List<HttpCookie> getCookies() {
        return this.cookies != null ? this.cookies : Collections.emptyList();
    }

    @Override
    public Request cookie(HttpCookie cookie) {
        if (this.cookies == null) {
            this.cookies = new ArrayList<HttpCookie>();
        }
        this.cookies.add(cookie);
        return this;
    }

    @Override
    public Request tag(Object tag) {
        this.tag = tag;
        return this;
    }

    @Override
    public Object getTag() {
        return this.tag;
    }

    @Override
    public Request attribute(String name, Object value) {
        if (this.attributes == null) {
            this.attributes = new HashMap<String, Object>(4);
        }
        this.attributes.put(name, value);
        return this;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes != null ? this.attributes : Collections.emptyMap();
    }

    @Override
    public HttpFields getHeaders() {
        return this.headers;
    }

    @Override
    public Request headers(Consumer<HttpFields.Mutable> consumer) {
        consumer.accept(this.headers);
        return this;
    }

    public HttpRequest addHeader(HttpField header) {
        this.headers.add(header);
        return this;
    }

    @Override
    public <T extends Request.RequestListener> List<T> getRequestListeners(Class<T> type) {
        if (type == null || this.requestListeners == null) {
            return this.requestListeners != null ? this.requestListeners : Collections.emptyList();
        }
        ArrayList<Request.RequestListener> result = new ArrayList<Request.RequestListener>();
        for (Request.RequestListener listener : this.requestListeners) {
            if (!type.isInstance(listener)) continue;
            result.add(listener);
        }
        return result;
    }

    @Override
    public Request listener(Request.Listener listener) {
        return this.requestListener(listener);
    }

    @Override
    public Request onRequestQueued(final Request.QueuedListener listener) {
        return this.requestListener(new Request.QueuedListener(){

            @Override
            public void onQueued(Request request) {
                listener.onQueued(request);
            }
        });
    }

    @Override
    public Request onRequestBegin(final Request.BeginListener listener) {
        return this.requestListener(new Request.BeginListener(){

            @Override
            public void onBegin(Request request) {
                listener.onBegin(request);
            }
        });
    }

    @Override
    public Request onRequestHeaders(final Request.HeadersListener listener) {
        return this.requestListener(new Request.HeadersListener(){

            @Override
            public void onHeaders(Request request) {
                listener.onHeaders(request);
            }
        });
    }

    @Override
    public Request onRequestCommit(final Request.CommitListener listener) {
        return this.requestListener(new Request.CommitListener(){

            @Override
            public void onCommit(Request request) {
                listener.onCommit(request);
            }
        });
    }

    @Override
    public Request onRequestContent(final Request.ContentListener listener) {
        return this.requestListener(new Request.ContentListener(){

            @Override
            public void onContent(Request request, ByteBuffer content) {
                listener.onContent(request, content);
            }
        });
    }

    @Override
    public Request onRequestSuccess(final Request.SuccessListener listener) {
        return this.requestListener(new Request.SuccessListener(){

            @Override
            public void onSuccess(Request request) {
                listener.onSuccess(request);
            }
        });
    }

    @Override
    public Request onRequestFailure(final Request.FailureListener listener) {
        return this.requestListener(new Request.FailureListener(){

            @Override
            public void onFailure(Request request, Throwable failure) {
                listener.onFailure(request, failure);
            }
        });
    }

    private Request requestListener(Request.RequestListener listener) {
        if (this.requestListeners == null) {
            this.requestListeners = new ArrayList<Request.RequestListener>();
        }
        this.requestListeners.add(listener);
        return this;
    }

    @Override
    public Request onResponseBegin(final Response.BeginListener listener) {
        this.responseListeners.add(new Response.BeginListener(){

            @Override
            public void onBegin(Response response) {
                listener.onBegin(response);
            }
        });
        return this;
    }

    @Override
    public Request onResponseHeader(final Response.HeaderListener listener) {
        this.responseListeners.add(new Response.HeaderListener(){

            @Override
            public boolean onHeader(Response response, HttpField field) {
                return listener.onHeader(response, field);
            }
        });
        return this;
    }

    @Override
    public Request onResponseHeaders(final Response.HeadersListener listener) {
        this.responseListeners.add(new Response.HeadersListener(){

            @Override
            public void onHeaders(Response response) {
                listener.onHeaders(response);
            }
        });
        return this;
    }

    @Override
    public Request onResponseContent(final Response.ContentListener listener) {
        this.responseListeners.add(new Response.ContentListener(){

            @Override
            public void onContent(Response response, ByteBuffer content) {
                listener.onContent(response, content);
            }
        });
        return this;
    }

    @Override
    public Request onResponseContentAsync(final Response.AsyncContentListener listener) {
        this.responseListeners.add(new Response.AsyncContentListener(){

            @Override
            public void onContent(Response response, ByteBuffer content, Callback callback) {
                listener.onContent(response, content, callback);
            }
        });
        return this;
    }

    @Override
    public Request onResponseContentDemanded(final Response.DemandedContentListener listener) {
        this.responseListeners.add(new Response.DemandedContentListener(){

            @Override
            public void onBeforeContent(Response response, LongConsumer demand) {
                listener.onBeforeContent(response, demand);
            }

            @Override
            public void onContent(Response response, LongConsumer demand, ByteBuffer content, Callback callback) {
                listener.onContent(response, demand, content, callback);
            }
        });
        return this;
    }

    @Override
    public Request onResponseSuccess(final Response.SuccessListener listener) {
        this.responseListeners.add(new Response.SuccessListener(){

            @Override
            public void onSuccess(Response response) {
                listener.onSuccess(response);
            }
        });
        return this;
    }

    @Override
    public Request onResponseFailure(final Response.FailureListener listener) {
        this.responseListeners.add(new Response.FailureListener(){

            @Override
            public void onFailure(Response response, Throwable failure) {
                listener.onFailure(response, failure);
            }
        });
        return this;
    }

    @Override
    public Request onComplete(final Response.CompleteListener listener) {
        this.responseListeners.add(new Response.CompleteListener(){

            @Override
            public void onComplete(Result result) {
                listener.onComplete(result);
            }
        });
        return this;
    }

    public Request pushListener(BiFunction<Request, Request, Response.CompleteListener> listener) {
        this.pushListener = listener;
        return this;
    }

    public HttpRequest trailers(Supplier<HttpFields> trailers) {
        this.trailers = trailers;
        return this;
    }

    public HttpRequest upgradeProtocol(String upgradeProtocol) {
        this.upgradeProtocol = upgradeProtocol;
        return this;
    }

    @Override
    public ContentProvider getContent() {
        if (this.content instanceof RequestContentAdapter) {
            return ((RequestContentAdapter)this.content).getContentProvider();
        }
        return null;
    }

    @Override
    public Request content(ContentProvider content) {
        return this.content(content, null);
    }

    @Override
    public Request content(ContentProvider content, String contentType) {
        if (contentType != null) {
            this.headers.put(HttpHeader.CONTENT_TYPE, contentType);
        }
        return this.body(ContentProvider.toRequestContent(content));
    }

    @Override
    public Request.Content getBody() {
        return this.content;
    }

    @Override
    public Request body(Request.Content content) {
        this.content = content;
        return this;
    }

    @Override
    public Request file(Path file) throws IOException {
        return this.file(file, "application/octet-stream");
    }

    @Override
    public Request file(Path file, String contentType) throws IOException {
        return this.body(new PathRequestContent(contentType, file));
    }

    @Override
    public boolean isFollowRedirects() {
        return this.followRedirects;
    }

    @Override
    public Request followRedirects(boolean follow) {
        this.followRedirects = follow;
        return this;
    }

    @Override
    public long getIdleTimeout() {
        return this.idleTimeout;
    }

    @Override
    public Request idleTimeout(long timeout, TimeUnit unit) {
        this.idleTimeout = unit.toMillis(timeout);
        return this;
    }

    @Override
    public long getTimeout() {
        return this.timeout;
    }

    @Override
    public Request timeout(long timeout, TimeUnit unit) {
        this.timeout = unit.toMillis(timeout);
        return this;
    }

    @Override
    public ContentResponse send() throws InterruptedException, TimeoutException, ExecutionException {
        FutureResponseListener listener = new FutureResponseListener(this);
        this.send(listener);
        try {
            return listener.get();
        }
        catch (ExecutionException x) {
            if (x.getCause() instanceof TimeoutException) {
                TimeoutException t = (TimeoutException)x.getCause();
                this.abort(t);
                throw t;
            }
            this.abort(x);
            throw x;
        }
        catch (Throwable x) {
            this.abort(x);
            throw x;
        }
    }

    @Override
    public void send(Response.CompleteListener listener) {
        this.sendAsync(this.client::send, listener);
    }

    void sendAsync(HttpDestination destination, Response.CompleteListener listener) {
        this.sendAsync(destination::send, listener);
    }

    private void sendAsync(BiConsumer<HttpRequest, List<Response.ResponseListener>> sender, Response.CompleteListener listener) {
        if (listener != null) {
            this.responseListeners.add(listener);
        }
        sender.accept(this, this.responseListeners);
    }

    void sent() {
        long timeout;
        if (this.timeoutNanoTime == Long.MAX_VALUE && (timeout = this.getTimeout()) > 0L) {
            this.timeoutNanoTime = NanoTime.now() + TimeUnit.MILLISECONDS.toNanos(timeout);
        }
    }

    long getTimeoutNanoTime() {
        return this.timeoutNanoTime;
    }

    protected List<Response.ResponseListener> getResponseListeners() {
        return this.responseListeners;
    }

    public BiFunction<Request, Request, Response.CompleteListener> getPushListener() {
        return this.pushListener;
    }

    public Supplier<HttpFields> getTrailers() {
        return this.trailers;
    }

    public String getUpgradeProtocol() {
        return this.upgradeProtocol;
    }

    @Override
    public boolean abort(Throwable cause) {
        if (this.aborted.compareAndSet(null, Objects.requireNonNull(cause))) {
            return this.conversation.abort(cause);
        }
        return false;
    }

    @Override
    public Throwable getAbortCause() {
        return this.aborted.get();
    }

    boolean normalized() {
        boolean result = this.normalized;
        this.normalized = true;
        return result;
    }

    private String buildQuery() {
        StringBuilder result = new StringBuilder();
        Iterator iterator = this.params.iterator();
        while (iterator.hasNext()) {
            Fields.Field field = (Fields.Field)iterator.next();
            List values = field.getValues();
            for (int i = 0; i < values.size(); ++i) {
                if (i > 0) {
                    result.append("&");
                }
                result.append(field.getName()).append("=");
                result.append(this.urlEncode((String)values.get(i)));
            }
            if (!iterator.hasNext()) continue;
            result.append("&");
        }
        return result.toString();
    }

    private String urlEncode(String value) {
        if (value == null) {
            return "";
        }
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private void extractParams(String query) {
        if (query != null) {
            for (String nameValue : query.split("&")) {
                String name;
                String[] parts = nameValue.split("=");
                if (parts.length <= 0 || (name = this.urlDecode(parts[0])).trim().length() == 0) continue;
                this.param(name, parts.length < 2 ? "" : this.urlDecode(parts[1]), true);
            }
        }
    }

    private String urlDecode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }

    private URI buildURI(boolean withQuery) {
        URI result;
        Object path = this.getPath();
        String query = this.getQuery();
        if (query != null && withQuery) {
            path = (String)path + "?" + query;
        }
        if ((result = this.newURI((String)path)) == null) {
            return NULL_URI;
        }
        if (!result.isAbsolute()) {
            result = URI.create(new Origin(this.getScheme(), this.getHost(), this.getPort()).asString() + (String)path);
        }
        return result;
    }

    private URI newURI(String path) {
        try {
            if ("*".equals(path)) {
                return null;
            }
            URI result = new URI(path);
            return result.isOpaque() ? null : result;
        }
        catch (URISyntaxException x) {
            return null;
        }
    }

    public String toString() {
        return String.format("%s[%s %s %s]@%x", this.getClass().getSimpleName(), this.getMethod(), this.getPath(), this.getVersion(), this.hashCode());
    }
}

