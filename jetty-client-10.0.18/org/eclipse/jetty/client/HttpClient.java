/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.http.HttpCompliance
 *  org.eclipse.jetty.http.HttpField
 *  org.eclipse.jetty.http.HttpHeader
 *  org.eclipse.jetty.http.HttpMethod
 *  org.eclipse.jetty.http.HttpScheme
 *  org.eclipse.jetty.io.ArrayRetainableByteBufferPool
 *  org.eclipse.jetty.io.ByteBufferPool
 *  org.eclipse.jetty.io.ClientConnectionFactory
 *  org.eclipse.jetty.io.ClientConnector
 *  org.eclipse.jetty.io.MappedByteBufferPool
 *  org.eclipse.jetty.io.RetainableByteBufferPool
 *  org.eclipse.jetty.io.ssl.SslClientConnectionFactory
 *  org.eclipse.jetty.util.Fields
 *  org.eclipse.jetty.util.Jetty
 *  org.eclipse.jetty.util.ProcessorUtils
 *  org.eclipse.jetty.util.Promise
 *  org.eclipse.jetty.util.Promise$Wrapper
 *  org.eclipse.jetty.util.SocketAddressResolver
 *  org.eclipse.jetty.util.SocketAddressResolver$Async
 *  org.eclipse.jetty.util.annotation.ManagedAttribute
 *  org.eclipse.jetty.util.annotation.ManagedObject
 *  org.eclipse.jetty.util.component.ContainerLifeCycle
 *  org.eclipse.jetty.util.component.DumpableCollection
 *  org.eclipse.jetty.util.component.LifeCycle
 *  org.eclipse.jetty.util.ssl.SslContextFactory
 *  org.eclipse.jetty.util.ssl.SslContextFactory$Client
 *  org.eclipse.jetty.util.thread.QueuedThreadPool
 *  org.eclipse.jetty.util.thread.ScheduledExecutorScheduler
 *  org.eclipse.jetty.util.thread.Scheduler
 *  org.eclipse.jetty.util.thread.Sweeper
 *  org.eclipse.jetty.util.thread.ThreadPool$SizedThreadPool
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.client;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeoutException;
import org.eclipse.jetty.client.AbstractHttpClientTransport;
import org.eclipse.jetty.client.ContentDecoder;
import org.eclipse.jetty.client.ContinueProtocolHandler;
import org.eclipse.jetty.client.GZIPContentDecoder;
import org.eclipse.jetty.client.HttpAuthenticationStore;
import org.eclipse.jetty.client.HttpClientTransport;
import org.eclipse.jetty.client.HttpConversation;
import org.eclipse.jetty.client.HttpDestination;
import org.eclipse.jetty.client.HttpRequest;
import org.eclipse.jetty.client.Origin;
import org.eclipse.jetty.client.ProtocolHandler;
import org.eclipse.jetty.client.ProtocolHandlers;
import org.eclipse.jetty.client.ProxyAuthenticationProtocolHandler;
import org.eclipse.jetty.client.ProxyConfiguration;
import org.eclipse.jetty.client.RedirectProtocolHandler;
import org.eclipse.jetty.client.UpgradeProtocolHandler;
import org.eclipse.jetty.client.WWWAuthenticationProtocolHandler;
import org.eclipse.jetty.client.api.AuthenticationStore;
import org.eclipse.jetty.client.api.Connection;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Destination;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.client.http.HttpClientTransportOverHTTP;
import org.eclipse.jetty.client.util.FormRequestContent;
import org.eclipse.jetty.http.HttpCompliance;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.HttpScheme;
import org.eclipse.jetty.io.ArrayRetainableByteBufferPool;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.io.ClientConnectionFactory;
import org.eclipse.jetty.io.ClientConnector;
import org.eclipse.jetty.io.MappedByteBufferPool;
import org.eclipse.jetty.io.RetainableByteBufferPool;
import org.eclipse.jetty.io.ssl.SslClientConnectionFactory;
import org.eclipse.jetty.util.Fields;
import org.eclipse.jetty.util.Jetty;
import org.eclipse.jetty.util.ProcessorUtils;
import org.eclipse.jetty.util.Promise;
import org.eclipse.jetty.util.SocketAddressResolver;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.util.annotation.ManagedObject;
import org.eclipse.jetty.util.component.ContainerLifeCycle;
import org.eclipse.jetty.util.component.DumpableCollection;
import org.eclipse.jetty.util.component.LifeCycle;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ScheduledExecutorScheduler;
import org.eclipse.jetty.util.thread.Scheduler;
import org.eclipse.jetty.util.thread.Sweeper;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedObject(value="The HTTP client")
public class HttpClient
extends ContainerLifeCycle {
    public static final String USER_AGENT = "Jetty/" + Jetty.VERSION;
    private static final Logger LOG = LoggerFactory.getLogger(HttpClient.class);
    private final ConcurrentMap<Origin, HttpDestination> destinations = new ConcurrentHashMap<Origin, HttpDestination>();
    private final ProtocolHandlers handlers = new ProtocolHandlers();
    private final List<Request.Listener> requestListeners = new ArrayList<Request.Listener>();
    private final Set<ContentDecoder.Factory> decoderFactories = new ContentDecoderFactorySet();
    private final ProxyConfiguration proxyConfig = new ProxyConfiguration();
    private final HttpClientTransport transport;
    private final ClientConnector connector;
    private AuthenticationStore authenticationStore = new HttpAuthenticationStore();
    private CookieManager cookieManager;
    private CookieStore cookieStore;
    private SocketAddressResolver resolver;
    private HttpField agentField = new HttpField(HttpHeader.USER_AGENT, USER_AGENT);
    private boolean followRedirects = true;
    private int maxConnectionsPerDestination = 64;
    private int maxRequestsQueuedPerDestination = 1024;
    private int requestBufferSize = 4096;
    private int responseBufferSize = 16384;
    private int maxRedirects = 8;
    private long addressResolutionTimeout = 15000L;
    private boolean tcpNoDelay = true;
    private boolean strictEventOrdering = false;
    private HttpField encodingField;
    private long destinationIdleTimeout;
    private String name = ((Object)((Object)this)).getClass().getSimpleName() + "@" + Integer.toHexString(((Object)((Object)this)).hashCode());
    private HttpCompliance httpCompliance = HttpCompliance.RFC7230;
    private String defaultRequestContentType = "application/octet-stream";
    private boolean useInputDirectByteBuffers = true;
    private boolean useOutputDirectByteBuffers = true;
    private int maxResponseHeadersSize = -1;
    private Sweeper destinationSweeper;

    public HttpClient() {
        this(new HttpClientTransportOverHTTP());
    }

    public HttpClient(HttpClientTransport transport) {
        this.transport = Objects.requireNonNull(transport);
        this.addBean(transport);
        this.connector = (ClientConnector)((AbstractHttpClientTransport)transport).getContainedBeans(ClientConnector.class).stream().findFirst().orElseThrow();
        this.addBean(this.handlers);
        this.addBean(this.decoderFactories);
    }

    public void dump(Appendable out, String indent) throws IOException {
        this.dumpObjects(out, indent, new Object[]{new DumpableCollection("requestListeners", this.requestListeners)});
    }

    public HttpClientTransport getTransport() {
        return this.transport;
    }

    public SslContextFactory.Client getSslContextFactory() {
        return this.connector.getSslContextFactory();
    }

    protected void doStart() throws Exception {
        Scheduler scheduler;
        Executor executor = this.getExecutor();
        if (executor == null) {
            QueuedThreadPool threadPool = new QueuedThreadPool();
            threadPool.setName(this.name);
            executor = threadPool;
            this.setExecutor(executor);
        }
        int maxBucketSize = executor instanceof ThreadPool.SizedThreadPool ? ((ThreadPool.SizedThreadPool)executor).getMaxThreads() / 2 : ProcessorUtils.availableProcessors() * 2;
        ByteBufferPool byteBufferPool = this.getByteBufferPool();
        if (byteBufferPool == null) {
            this.setByteBufferPool((ByteBufferPool)new MappedByteBufferPool(2048, maxBucketSize));
        }
        if (this.getBean(RetainableByteBufferPool.class) == null) {
            this.addBean(new ArrayRetainableByteBufferPool(0, 2048, 65536, maxBucketSize));
        }
        if ((scheduler = this.getScheduler()) == null) {
            scheduler = new ScheduledExecutorScheduler(this.name + "-scheduler", false);
            this.setScheduler(scheduler);
        }
        if (this.resolver == null) {
            this.setSocketAddressResolver((SocketAddressResolver)new SocketAddressResolver.Async(this.getExecutor(), scheduler, this.getAddressResolutionTimeout()));
        }
        this.handlers.put(new ContinueProtocolHandler());
        this.handlers.put(new RedirectProtocolHandler(this));
        this.handlers.put(new WWWAuthenticationProtocolHandler(this));
        this.handlers.put(new ProxyAuthenticationProtocolHandler(this));
        this.handlers.put(new UpgradeProtocolHandler());
        this.decoderFactories.add(new GZIPContentDecoder.Factory(byteBufferPool));
        this.cookieManager = this.newCookieManager();
        this.cookieStore = this.cookieManager.getCookieStore();
        this.transport.setHttpClient(this);
        super.doStart();
        if (this.getDestinationIdleTimeout() > 0L) {
            this.destinationSweeper = new Sweeper(scheduler, 1000L);
            this.destinationSweeper.start();
        }
    }

    private CookieManager newCookieManager() {
        return new CookieManager(this.getCookieStore(), CookiePolicy.ACCEPT_ALL);
    }

    protected void doStop() throws Exception {
        if (this.destinationSweeper != null) {
            this.destinationSweeper.stop();
            this.destinationSweeper = null;
        }
        this.decoderFactories.clear();
        this.handlers.clear();
        this.destinations.values().forEach(HttpDestination::close);
        this.destinations.clear();
        this.requestListeners.clear();
        this.authenticationStore.clearAuthentications();
        this.authenticationStore.clearAuthenticationResults();
        super.doStop();
    }

    public List<Request.Listener> getRequestListeners() {
        return this.requestListeners;
    }

    public CookieStore getCookieStore() {
        return this.cookieStore;
    }

    public void setCookieStore(CookieStore cookieStore) {
        if (this.isStarted()) {
            throw new IllegalStateException();
        }
        this.cookieStore = Objects.requireNonNull(cookieStore);
        this.cookieManager = this.newCookieManager();
    }

    CookieManager getCookieManager() {
        return this.cookieManager;
    }

    Sweeper getDestinationSweeper() {
        return this.destinationSweeper;
    }

    public AuthenticationStore getAuthenticationStore() {
        return this.authenticationStore;
    }

    public void setAuthenticationStore(AuthenticationStore authenticationStore) {
        if (this.isStarted()) {
            throw new IllegalStateException();
        }
        this.authenticationStore = authenticationStore;
    }

    public Set<ContentDecoder.Factory> getContentDecoderFactories() {
        return this.decoderFactories;
    }

    public ContentResponse GET(String uri) throws InterruptedException, ExecutionException, TimeoutException {
        return this.GET(URI.create(uri));
    }

    public ContentResponse GET(URI uri) throws InterruptedException, ExecutionException, TimeoutException {
        return this.newRequest(uri).send();
    }

    public ContentResponse FORM(String uri, Fields fields) throws InterruptedException, ExecutionException, TimeoutException {
        return this.FORM(URI.create(uri), fields);
    }

    public ContentResponse FORM(URI uri, Fields fields) throws InterruptedException, ExecutionException, TimeoutException {
        return this.POST(uri).body(new FormRequestContent(fields)).send();
    }

    public Request POST(String uri) {
        return this.POST(URI.create(uri));
    }

    public Request POST(URI uri) {
        return this.newRequest(uri).method(HttpMethod.POST);
    }

    public Request newRequest(String host, int port) {
        return this.newRequest(new Origin("http", host, port).asString());
    }

    public Request newRequest(String uri) {
        return this.newRequest(URI.create(uri));
    }

    public Request newRequest(URI uri) {
        return this.newHttpRequest(this.newConversation(), uri);
    }

    protected Request copyRequest(HttpRequest oldRequest, URI newURI) {
        return oldRequest.copy(newURI);
    }

    protected HttpRequest newHttpRequest(HttpConversation conversation, URI uri) {
        return new HttpRequest(this, conversation, uri);
    }

    public Destination resolveDestination(Request request) {
        HttpClientTransport transport = this.getTransport();
        Origin origin = transport.newOrigin((HttpRequest)request);
        HttpDestination destination = this.resolveDestination(origin);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Resolved {} for {}", (Object)destination, (Object)request);
        }
        return destination;
    }

    public Origin createOrigin(HttpRequest request, Origin.Protocol protocol) {
        String scheme = request.getScheme();
        if (!(HttpScheme.HTTP.is(scheme) || HttpScheme.HTTPS.is(scheme) || HttpScheme.WS.is(scheme) || HttpScheme.WSS.is(scheme))) {
            throw new IllegalArgumentException("Invalid protocol " + scheme);
        }
        scheme = scheme.toLowerCase(Locale.ENGLISH);
        String host = request.getHost();
        host = host.toLowerCase(Locale.ENGLISH);
        int port = request.getPort();
        port = HttpClient.normalizePort(scheme, port);
        return new Origin(scheme, host, port, request.getTag(), protocol);
    }

    public HttpDestination resolveDestination(Origin origin) {
        return this.destinations.compute(origin, (k, v) -> {
            if (v == null || v.stale()) {
                HttpDestination newDestination = this.getTransport().newHttpDestination((Origin)k);
                this.addManaged((LifeCycle)newDestination);
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Created {}; existing: '{}'", (Object)newDestination, v);
                }
                return newDestination;
            }
            return v;
        });
    }

    protected boolean removeDestination(HttpDestination destination) {
        boolean removed = this.destinations.remove(destination.getOrigin(), destination);
        this.removeBean(destination);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Removed {}; result: {}", (Object)destination, (Object)removed);
        }
        return removed;
    }

    public List<Destination> getDestinations() {
        return new ArrayList<Destination>(this.destinations.values());
    }

    protected void send(HttpRequest request, List<Response.ResponseListener> listeners) {
        HttpDestination destination = (HttpDestination)this.resolveDestination(request);
        destination.send(request, listeners);
    }

    protected void newConnection(HttpDestination destination, final Promise<Connection> promise) {
        final ConcurrentHashMap<String, Object> context = new ConcurrentHashMap<String, Object>();
        context.put("org.eclipse.jetty.client", (Object)this);
        context.put("org.eclipse.jetty.client.destination", destination);
        Origin.Protocol protocol = destination.getOrigin().getProtocol();
        List<String> protocols = protocol != null ? protocol.getProtocols() : List.of("http/1.1");
        context.put("org.eclipse.jetty.client.connector.applicationProtocols", protocols);
        Origin.Address address = destination.getConnectAddress();
        this.resolver.resolve(address.getHost(), address.getPort(), (Promise)new Promise<List<InetSocketAddress>>(){

            public void succeeded(List<InetSocketAddress> socketAddresses) {
                this.connect(socketAddresses, 0, context);
            }

            public void failed(Throwable x) {
                promise.failed(x);
            }

            private void connect(final List<InetSocketAddress> socketAddresses, final int index, final Map<String, Object> context2) {
                context2.put("org.eclipse.jetty.client.connection.promise", new Promise.Wrapper<Connection>(promise){

                    public void failed(Throwable x) {
                        int nextIndex = index + 1;
                        if (nextIndex == socketAddresses.size()) {
                            super.failed(x);
                        } else {
                            this.connect(socketAddresses, nextIndex, context2);
                        }
                    }
                });
                HttpClient.this.transport.connect((SocketAddress)socketAddresses.get(index), context2);
            }
        });
    }

    private HttpConversation newConversation() {
        return new HttpConversation();
    }

    public ProtocolHandlers getProtocolHandlers() {
        return this.handlers;
    }

    protected ProtocolHandler findProtocolHandler(Request request, Response response) {
        return this.handlers.find(request, response);
    }

    public ByteBufferPool getByteBufferPool() {
        return this.connector.getByteBufferPool();
    }

    public void setByteBufferPool(ByteBufferPool byteBufferPool) {
        this.connector.setByteBufferPool(byteBufferPool);
    }

    @ManagedAttribute(value="The name of this HttpClient")
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ManagedAttribute(value="The timeout, in milliseconds, for connect() operations")
    public long getConnectTimeout() {
        return this.connector.getConnectTimeout().toMillis();
    }

    public void setConnectTimeout(long connectTimeout) {
        this.connector.setConnectTimeout(Duration.ofMillis(connectTimeout));
    }

    public long getAddressResolutionTimeout() {
        return this.addressResolutionTimeout;
    }

    public void setAddressResolutionTimeout(long addressResolutionTimeout) {
        this.addressResolutionTimeout = addressResolutionTimeout;
    }

    @ManagedAttribute(value="The timeout, in milliseconds, to close idle connections")
    public long getIdleTimeout() {
        return this.connector.getIdleTimeout().toMillis();
    }

    public void setIdleTimeout(long idleTimeout) {
        this.connector.setIdleTimeout(Duration.ofMillis(idleTimeout));
    }

    public SocketAddress getBindAddress() {
        return this.connector.getBindAddress();
    }

    public void setBindAddress(SocketAddress bindAddress) {
        this.connector.setBindAddress(bindAddress);
    }

    public HttpField getUserAgentField() {
        return this.agentField;
    }

    public void setUserAgentField(HttpField agent) {
        if (agent != null && agent.getHeader() != HttpHeader.USER_AGENT) {
            throw new IllegalArgumentException();
        }
        this.agentField = agent;
    }

    @ManagedAttribute(value="Whether HTTP redirects are followed")
    public boolean isFollowRedirects() {
        return this.followRedirects;
    }

    public void setFollowRedirects(boolean follow) {
        this.followRedirects = follow;
    }

    public Executor getExecutor() {
        return this.connector.getExecutor();
    }

    public void setExecutor(Executor executor) {
        this.connector.setExecutor(executor);
    }

    public Scheduler getScheduler() {
        return this.connector.getScheduler();
    }

    public void setScheduler(Scheduler scheduler) {
        this.connector.setScheduler(scheduler);
    }

    public SocketAddressResolver getSocketAddressResolver() {
        return this.resolver;
    }

    public void setSocketAddressResolver(SocketAddressResolver resolver) {
        if (this.isStarted()) {
            throw new IllegalStateException();
        }
        this.updateBean(this.resolver, resolver);
        this.resolver = resolver;
    }

    @ManagedAttribute(value="The max number of connections per each destination")
    public int getMaxConnectionsPerDestination() {
        return this.maxConnectionsPerDestination;
    }

    public void setMaxConnectionsPerDestination(int maxConnectionsPerDestination) {
        this.maxConnectionsPerDestination = maxConnectionsPerDestination;
    }

    @ManagedAttribute(value="The max number of requests queued per each destination")
    public int getMaxRequestsQueuedPerDestination() {
        return this.maxRequestsQueuedPerDestination;
    }

    public void setMaxRequestsQueuedPerDestination(int maxRequestsQueuedPerDestination) {
        this.maxRequestsQueuedPerDestination = maxRequestsQueuedPerDestination;
    }

    @ManagedAttribute(value="The request buffer size in bytes")
    public int getRequestBufferSize() {
        return this.requestBufferSize;
    }

    public void setRequestBufferSize(int requestBufferSize) {
        this.requestBufferSize = requestBufferSize;
    }

    @ManagedAttribute(value="The response buffer size in bytes")
    public int getResponseBufferSize() {
        return this.responseBufferSize;
    }

    public void setResponseBufferSize(int responseBufferSize) {
        this.responseBufferSize = responseBufferSize;
    }

    public int getMaxRedirects() {
        return this.maxRedirects;
    }

    public void setMaxRedirects(int maxRedirects) {
        this.maxRedirects = maxRedirects;
    }

    @ManagedAttribute(value="Whether the TCP_NODELAY option is enabled", name="tcpNoDelay")
    @Deprecated
    public boolean isTCPNoDelay() {
        return this.tcpNoDelay;
    }

    @Deprecated
    public void setTCPNoDelay(boolean tcpNoDelay) {
        this.tcpNoDelay = tcpNoDelay;
    }

    public HttpCompliance getHttpCompliance() {
        return this.httpCompliance;
    }

    public void setHttpCompliance(HttpCompliance httpCompliance) {
        this.httpCompliance = httpCompliance;
    }

    @ManagedAttribute(value="Whether request/response events must be strictly ordered")
    public boolean isStrictEventOrdering() {
        return this.strictEventOrdering;
    }

    public void setStrictEventOrdering(boolean strictEventOrdering) {
        this.strictEventOrdering = strictEventOrdering;
    }

    @ManagedAttribute(value="The time in ms after which idle destinations are removed, disabled when zero or negative")
    public long getDestinationIdleTimeout() {
        return this.destinationIdleTimeout;
    }

    public void setDestinationIdleTimeout(long destinationIdleTimeout) {
        if (this.isStarted()) {
            throw new IllegalStateException();
        }
        this.destinationIdleTimeout = destinationIdleTimeout;
    }

    @Deprecated
    @ManagedAttribute(value="Whether idle destinations are removed")
    public boolean isRemoveIdleDestinations() {
        return this.destinationIdleTimeout > 0L;
    }

    @Deprecated
    public void setRemoveIdleDestinations(boolean removeIdleDestinations) {
        this.setDestinationIdleTimeout(removeIdleDestinations ? 10000L : 0L);
    }

    @ManagedAttribute(value="Whether the connect() operation is blocking")
    public boolean isConnectBlocking() {
        return this.connector.isConnectBlocking();
    }

    public void setConnectBlocking(boolean connectBlocking) {
        this.connector.setConnectBlocking(connectBlocking);
    }

    @ManagedAttribute(value="The default content type for request content")
    public String getDefaultRequestContentType() {
        return this.defaultRequestContentType;
    }

    public void setDefaultRequestContentType(String contentType) {
        this.defaultRequestContentType = contentType;
    }

    @ManagedAttribute(value="Whether to use direct ByteBuffers for reading")
    public boolean isUseInputDirectByteBuffers() {
        return this.useInputDirectByteBuffers;
    }

    public void setUseInputDirectByteBuffers(boolean useInputDirectByteBuffers) {
        this.useInputDirectByteBuffers = useInputDirectByteBuffers;
    }

    @ManagedAttribute(value="Whether to use direct ByteBuffers for writing")
    public boolean isUseOutputDirectByteBuffers() {
        return this.useOutputDirectByteBuffers;
    }

    public void setUseOutputDirectByteBuffers(boolean useOutputDirectByteBuffers) {
        this.useOutputDirectByteBuffers = useOutputDirectByteBuffers;
    }

    @ManagedAttribute(value="The max size in bytes of the response headers")
    public int getMaxResponseHeadersSize() {
        return this.maxResponseHeadersSize;
    }

    public void setMaxResponseHeadersSize(int maxResponseHeadersSize) {
        this.maxResponseHeadersSize = maxResponseHeadersSize;
    }

    public ProxyConfiguration getProxyConfiguration() {
        return this.proxyConfig;
    }

    protected HttpField getAcceptEncodingField() {
        return this.encodingField;
    }

    @Deprecated
    protected String normalizeHost(String host) {
        return host;
    }

    public static int normalizePort(String scheme, int port) {
        if (port > 0) {
            return port;
        }
        return HttpScheme.getDefaultPort((String)scheme);
    }

    public boolean isDefaultPort(String scheme, int port) {
        return HttpScheme.getDefaultPort((String)scheme) == port;
    }

    public static boolean isSchemeSecure(String scheme) {
        return HttpScheme.HTTPS.is(scheme) || HttpScheme.WSS.is(scheme);
    }

    protected ClientConnectionFactory newSslClientConnectionFactory(SslContextFactory.Client sslContextFactory, ClientConnectionFactory connectionFactory) {
        if (sslContextFactory == null) {
            sslContextFactory = this.getSslContextFactory();
        }
        return new SslClientConnectionFactory((SslContextFactory)sslContextFactory, this.getByteBufferPool(), this.getExecutor(), connectionFactory);
    }

    private class ContentDecoderFactorySet
    implements Set<ContentDecoder.Factory> {
        private final Set<ContentDecoder.Factory> set = new HashSet<ContentDecoder.Factory>();

        private ContentDecoderFactorySet() {
        }

        @Override
        public boolean add(ContentDecoder.Factory e) {
            boolean result = this.set.add(e);
            this.invalidate();
            return result;
        }

        @Override
        public boolean addAll(Collection<? extends ContentDecoder.Factory> c) {
            boolean result = this.set.addAll(c);
            this.invalidate();
            return result;
        }

        @Override
        public boolean remove(Object o) {
            boolean result = this.set.remove(o);
            this.invalidate();
            return result;
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            boolean result = this.set.removeAll(c);
            this.invalidate();
            return result;
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            boolean result = this.set.retainAll(c);
            this.invalidate();
            return result;
        }

        @Override
        public void clear() {
            this.set.clear();
            this.invalidate();
        }

        @Override
        public int size() {
            return this.set.size();
        }

        @Override
        public boolean isEmpty() {
            return this.set.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return this.set.contains(o);
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return this.set.containsAll(c);
        }

        @Override
        public Iterator<ContentDecoder.Factory> iterator() {
            final Iterator<ContentDecoder.Factory> iterator = this.set.iterator();
            return new Iterator<ContentDecoder.Factory>(){

                @Override
                public boolean hasNext() {
                    return iterator.hasNext();
                }

                @Override
                public ContentDecoder.Factory next() {
                    return (ContentDecoder.Factory)iterator.next();
                }

                @Override
                public void remove() {
                    iterator.remove();
                    ContentDecoderFactorySet.this.invalidate();
                }
            };
        }

        @Override
        public Object[] toArray() {
            return this.set.toArray();
        }

        @Override
        public <T> T[] toArray(T[] a) {
            return this.set.toArray(a);
        }

        private void invalidate() {
            if (this.set.isEmpty()) {
                HttpClient.this.encodingField = null;
            } else {
                StringBuilder value = new StringBuilder();
                Iterator<ContentDecoder.Factory> iterator = this.set.iterator();
                while (iterator.hasNext()) {
                    ContentDecoder.Factory decoderFactory = iterator.next();
                    value.append(decoderFactory.getEncoding());
                    if (!iterator.hasNext()) continue;
                    value.append(",");
                }
                HttpClient.this.encodingField = new HttpField(HttpHeader.ACCEPT_ENCODING, value.toString());
            }
        }
    }
}

