/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.executor.ThreadLocalContextManager
 *  com.google.common.base.Throwables
 *  com.google.common.primitives.Ints
 *  io.atlassian.fugue.Suppliers
 *  io.atlassian.util.concurrent.Promise
 *  io.atlassian.util.concurrent.Promises
 *  io.atlassian.util.concurrent.ThreadFactories
 *  io.atlassian.util.concurrent.ThreadFactories$Type
 *  org.apache.commons.lang3.ObjectUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 */
package com.atlassian.httpclient.apache.httpcomponents;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.httpclient.apache.httpcomponents.BannedHostResolver;
import com.atlassian.httpclient.apache.httpcomponents.BoundedHttpAsyncClient;
import com.atlassian.httpclient.apache.httpcomponents.BoundedHttpResponseParserFactory;
import com.atlassian.httpclient.apache.httpcomponents.DefaultHostResolver;
import com.atlassian.httpclient.apache.httpcomponents.DefaultResponse;
import com.atlassian.httpclient.apache.httpcomponents.EntityTooLargeException;
import com.atlassian.httpclient.apache.httpcomponents.MavenUtils;
import com.atlassian.httpclient.apache.httpcomponents.PromiseHttpAsyncClient;
import com.atlassian.httpclient.apache.httpcomponents.RedirectStrategy;
import com.atlassian.httpclient.apache.httpcomponents.RequestEntityEffect;
import com.atlassian.httpclient.apache.httpcomponents.SettableFuturePromiseHttpPromiseAsyncClient;
import com.atlassian.httpclient.apache.httpcomponents.cache.FlushableHttpCacheStorage;
import com.atlassian.httpclient.apache.httpcomponents.cache.FlushableHttpCacheStorageImpl;
import com.atlassian.httpclient.apache.httpcomponents.cache.LoggingHttpCacheStorage;
import com.atlassian.httpclient.apache.httpcomponents.proxy.ProxyConfigFactory;
import com.atlassian.httpclient.apache.httpcomponents.proxy.ProxyCredentialsProvider;
import com.atlassian.httpclient.api.HostResolver;
import com.atlassian.httpclient.api.HttpClient;
import com.atlassian.httpclient.api.HttpStatus;
import com.atlassian.httpclient.api.Request;
import com.atlassian.httpclient.api.Response;
import com.atlassian.httpclient.api.ResponsePromise;
import com.atlassian.httpclient.api.ResponsePromises;
import com.atlassian.httpclient.api.ResponseTooLargeException;
import com.atlassian.httpclient.api.factory.HttpClientOptions;
import com.atlassian.httpclient.base.AbstractHttpClient;
import com.atlassian.httpclient.base.event.HttpRequestCompletedEvent;
import com.atlassian.httpclient.base.event.HttpRequestFailedEvent;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.executor.ThreadLocalContextManager;
import com.google.common.base.Throwables;
import com.google.common.primitives.Ints;
import io.atlassian.fugue.Suppliers;
import io.atlassian.util.concurrent.Promise;
import io.atlassian.util.concurrent.Promises;
import io.atlassian.util.concurrent.ThreadFactories;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.ProxyAuthenticationStrategy;
import org.apache.http.impl.client.cache.CacheConfig;
import org.apache.http.impl.client.cache.CachingHttpAsyncClient;
import org.apache.http.impl.conn.DefaultSchemePortResolver;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.ManagedNHttpClientConnectionFactory;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.client.HttpAsyncClient;
import org.apache.http.nio.conn.NoopIOSessionStrategy;
import org.apache.http.nio.conn.SchemeIOSessionStrategy;
import org.apache.http.nio.conn.ssl.SSLIOSessionStrategy;
import org.apache.http.nio.reactor.IOReactorException;
import org.apache.http.nio.reactor.IOReactorExceptionHandler;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;

public final class ApacheAsyncHttpClient<C>
extends AbstractHttpClient
implements HttpClient,
DisposableBean {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private static final Supplier<String> httpClientVersion = Suppliers.memoize(() -> MavenUtils.getVersion("com.atlassian.httpclient", "atlassian-httpclient-api"));
    private final Function<Object, Void> eventConsumer;
    private final Supplier<String> applicationName;
    private final ThreadLocalContextManager<C> threadLocalContextManager;
    private final ExecutorService callbackExecutor;
    private final HttpClientOptions httpClientOptions;
    private final CachingHttpAsyncClient httpClient;
    private final CloseableHttpAsyncClient nonCachingHttpClient;
    private final FlushableHttpCacheStorage httpCacheStorage;

    public ApacheAsyncHttpClient(EventPublisher eventConsumer, ApplicationProperties applicationProperties, ThreadLocalContextManager<C> threadLocalContextManager) {
        this(eventConsumer, applicationProperties, threadLocalContextManager, new HttpClientOptions());
    }

    public ApacheAsyncHttpClient(EventPublisher eventConsumer, ApplicationProperties applicationProperties, ThreadLocalContextManager<C> threadLocalContextManager, HttpClientOptions options) {
        this(new DefaultApplicationNameSupplier(applicationProperties), new EventConsumerFunction(eventConsumer), threadLocalContextManager, options);
    }

    public ApacheAsyncHttpClient(String applicationName) {
        this(applicationName, new HttpClientOptions());
    }

    public ApacheAsyncHttpClient(String applicationName, HttpClientOptions options) {
        this(Suppliers.ofInstance((Object)applicationName), (Object input) -> null, new NoOpThreadLocalContextManager(), options);
    }

    public ApacheAsyncHttpClient(Supplier<String> applicationName, Function<Object, Void> eventConsumer, ThreadLocalContextManager<C> threadLocalContextManager, HttpClientOptions options) {
        this.eventConsumer = Objects.requireNonNull(eventConsumer, "eventConsumer can't be null");
        this.applicationName = Objects.requireNonNull(applicationName, "applicationName can't be null");
        this.threadLocalContextManager = Objects.requireNonNull(threadLocalContextManager, "threadLocalContextManager can't be null");
        this.httpClientOptions = Objects.requireNonNull(options, "options can't be null");
        try {
            IOReactorConfig reactorConfig = IOReactorConfig.custom().setIoThreadCount(options.getIoThreadCount()).setSelectInterval(options.getIoSelectInterval()).setInterestOpQueued(true).build();
            DefaultConnectingIOReactor ioReactor = new DefaultConnectingIOReactor(reactorConfig);
            ioReactor.setExceptionHandler(new IOReactorExceptionHandler(){

                @Override
                public boolean handle(IOException e) {
                    ApacheAsyncHttpClient.this.log.error("IO exception in reactor ", (Throwable)e);
                    return false;
                }

                @Override
                public boolean handle(RuntimeException e) {
                    ApacheAsyncHttpClient.this.log.error("Fatal runtime error", (Throwable)e);
                    return false;
                }
            });
            List<String> bannedAddresses = options.getBlacklistedAddresses();
            HostResolver resolver = bannedAddresses.isEmpty() ? DefaultHostResolver.INSTANCE : new BannedHostResolver(bannedAddresses);
            PoolingNHttpClientConnectionManager connectionManager = new PoolingNHttpClientConnectionManager(ioReactor, new ManagedNHttpClientConnectionFactory(null, new BoundedHttpResponseParserFactory(this.httpClientOptions), null), this.getRegistry(options), DefaultSchemePortResolver.INSTANCE, resolver::resolve, options.getConnectionPoolTimeToLive(), TimeUnit.MILLISECONDS){

                @Override
                protected void finalize() {
                }
            };
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout((int)options.getConnectionTimeout()).setConnectionRequestTimeout((int)options.getLeaseTimeout()).setCookieSpec(options.getIgnoreCookies() ? "ignoreCookies" : "default").setSocketTimeout((int)options.getSocketTimeout()).build();
            connectionManager.setDefaultMaxPerRoute(options.getMaxConnectionsPerHost());
            connectionManager.setMaxTotal(options.getMaxTotalConnections());
            HttpAsyncClientBuilder clientBuilder = HttpAsyncClients.custom().setThreadFactory(ThreadFactories.namedThreadFactory((String)(options.getThreadPrefix() + "-io"), (ThreadFactories.Type)ThreadFactories.Type.DAEMON)).setDefaultIOReactorConfig(reactorConfig).setConnectionManager(connectionManager).setRedirectStrategy(new RedirectStrategy()).setUserAgent(this.getUserAgent(options)).setDefaultRequestConfig(requestConfig);
            ProxyConfigFactory.getProxyConfig(options).forEach(proxyConfig -> {
                clientBuilder.setRoutePlanner(new SystemDefaultRoutePlanner(DefaultSchemePortResolver.INSTANCE, proxyConfig.toProxySelector()));
                ProxyCredentialsProvider.build(options).forEach(credsProvider -> {
                    clientBuilder.setProxyAuthenticationStrategy(ProxyAuthenticationStrategy.INSTANCE);
                    clientBuilder.setDefaultCredentialsProvider((CredentialsProvider)credsProvider);
                });
            });
            this.nonCachingHttpClient = new BoundedHttpAsyncClient(clientBuilder.build(), Ints.saturatedCast((long)options.getMaxEntitySize()));
            CacheConfig cacheConfig = CacheConfig.custom().setMaxCacheEntries(options.getMaxCacheEntries()).setSharedCache(false).setNeverCacheHTTP10ResponsesWithQueryString(false).setMaxObjectSize(options.getMaxCacheObjectSize()).build();
            this.httpCacheStorage = new LoggingHttpCacheStorage(new FlushableHttpCacheStorageImpl(cacheConfig));
            this.httpClient = new CachingHttpAsyncClient((HttpAsyncClient)this.nonCachingHttpClient, this.httpCacheStorage, cacheConfig);
            this.callbackExecutor = options.getCallbackExecutor();
            this.nonCachingHttpClient.start();
        }
        catch (IOReactorException e) {
            throw new RuntimeException("Reactor " + options.getThreadPrefix() + "not set up correctly", e);
        }
    }

    private Registry<SchemeIOSessionStrategy> getRegistry(HttpClientOptions options) {
        try {
            SSLContext sslContext = options.trustSelfSignedCertificates() ? SSLContexts.custom().setProtocol("TLS").loadTrustMaterial(null, new TrustSelfSignedStrategy()).build() : SSLContexts.createSystemDefault();
            SSLIOSessionStrategy sslioSessionStrategy = new SSLIOSessionStrategy(sslContext, (String[])ObjectUtils.firstNonNull((Object[])new String[][]{options.getSupportedProtocols(), ApacheAsyncHttpClient.split(System.getProperty("https.protocols"))}), ApacheAsyncHttpClient.split(System.getProperty("https.cipherSuites")), options.trustSelfSignedCertificates() ? this.getSelfSignedVerifier() : SSLIOSessionStrategy.getDefaultHostnameVerifier());
            return RegistryBuilder.create().register("http", NoopIOSessionStrategy.INSTANCE).register("https", (NoopIOSessionStrategy)((Object)sslioSessionStrategy)).build();
        }
        catch (KeyManagementException | KeyStoreException | NoSuchAlgorithmException e) {
            return this.getFallbackRegistry(e);
        }
    }

    private HostnameVerifier getSelfSignedVerifier() {
        return (host, session) -> {
            this.log.debug("Verification for certificates from {} disabled", (Object)host);
            return true;
        };
    }

    private Registry<SchemeIOSessionStrategy> getFallbackRegistry(GeneralSecurityException e) {
        this.log.error("Error when creating scheme session strategy registry", (Throwable)e);
        return RegistryBuilder.create().register("http", NoopIOSessionStrategy.INSTANCE).register("https", (NoopIOSessionStrategy)((Object)SSLIOSessionStrategy.getDefaultStrategy())).build();
    }

    private String getUserAgent(HttpClientOptions options) {
        return String.format("Atlassian HttpClient %s / %s / %s", httpClientVersion.get(), this.applicationName.get(), options.getUserAgent());
    }

    @Override
    public final ResponsePromise execute(Request request) {
        try {
            return this.doExecute(request);
        }
        catch (Throwable t) {
            return ResponsePromises.toResponsePromise((Promise<Response>)Promises.rejected((Throwable)t));
        }
    }

    private ResponsePromise doExecute(Request request) {
        HttpRequestBase op;
        this.httpClientOptions.getRequestPreparer().accept(request);
        long start = System.currentTimeMillis();
        String uri = request.getUri().toString();
        Request.Method method = request.getMethod();
        switch (method) {
            case GET: {
                op = new HttpGet(uri);
                break;
            }
            case POST: {
                op = new HttpPost(uri);
                break;
            }
            case PUT: {
                op = new HttpPut(uri);
                break;
            }
            case DELETE: {
                op = new HttpDelete(uri);
                break;
            }
            case OPTIONS: {
                op = new HttpOptions(uri);
                break;
            }
            case HEAD: {
                op = new HttpHead(uri);
                break;
            }
            case TRACE: {
                op = new HttpTrace(uri);
                break;
            }
            default: {
                throw new UnsupportedOperationException(method.toString());
            }
        }
        if (request.hasEntity()) {
            new RequestEntityEffect(request).apply(op);
        }
        for (Map.Entry<String, String> entry : request.getHeaders().entrySet()) {
            op.setHeader(entry.getKey(), entry.getValue());
        }
        PromiseHttpAsyncClient asyncClient = this.getPromiseHttpAsyncClient(request);
        return ResponsePromises.toResponsePromise((Promise<Response>)asyncClient.execute(op, new BasicHttpContext()).fold(ex -> {
            long requestDuration = System.currentTimeMillis() - start;
            Throwable exception = this.maybeTranslate((Throwable)ex);
            this.publishEvent(request, requestDuration, exception);
            Throwables.throwIfUnchecked((Throwable)exception);
            throw new RuntimeException(exception);
        }, httpResponse -> {
            long requestDuration = System.currentTimeMillis() - start;
            this.publishEvent(request, requestDuration, httpResponse.getStatusLine().getStatusCode());
            try {
                return this.translate((HttpResponse)httpResponse);
            }
            catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }));
    }

    private void publishEvent(Request request, long requestDuration, int statusCode) {
        if (HttpStatus.OK.code <= statusCode && statusCode < HttpStatus.MULTIPLE_CHOICES.code) {
            this.eventConsumer.apply(new HttpRequestCompletedEvent(request.getUri().toString(), request.getMethod().name(), statusCode, requestDuration, request.getAttributes()));
        } else {
            this.eventConsumer.apply(new HttpRequestFailedEvent(request.getUri().toString(), request.getMethod().name(), statusCode, requestDuration, request.getAttributes()));
        }
    }

    private void publishEvent(Request request, long requestDuration, Throwable ex) {
        this.eventConsumer.apply(new HttpRequestFailedEvent(request.getUri().toString(), request.getMethod().name(), ex.toString(), requestDuration, request.getAttributes()));
    }

    private PromiseHttpAsyncClient getPromiseHttpAsyncClient(Request request) {
        return new SettableFuturePromiseHttpPromiseAsyncClient<C>(request.isCacheDisabled() ? this.nonCachingHttpClient : this.httpClient, this.threadLocalContextManager, this.callbackExecutor);
    }

    private Throwable maybeTranslate(Throwable ex) {
        if (ex instanceof EntityTooLargeException) {
            EntityTooLargeException tooLarge = (EntityTooLargeException)ex;
            try {
                return new ResponseTooLargeException(this.translate(tooLarge.getResponse()), ex.getMessage());
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
        return ex;
    }

    private Response translate(HttpResponse httpResponse) throws IOException {
        Header[] httpHeaders;
        StatusLine status = httpResponse.getStatusLine();
        DefaultResponse.DefaultResponseBuilder responseBuilder = DefaultResponse.builder().setMaxEntitySize(this.httpClientOptions.getMaxEntitySize()).setStatusCode(status.getStatusCode()).setStatusText(status.getReasonPhrase());
        for (Header httpHeader : httpHeaders = httpResponse.getAllHeaders()) {
            responseBuilder.setHeader(httpHeader.getName(), httpHeader.getValue());
        }
        HttpEntity entity = httpResponse.getEntity();
        if (entity != null) {
            responseBuilder.setEntityStream(entity.getContent());
        }
        return (Response)responseBuilder.build();
    }

    public void destroy() throws Exception {
        this.callbackExecutor.shutdown();
        this.nonCachingHttpClient.close();
    }

    @Override
    public void flushCacheByUriPattern(Pattern urlPattern) {
        this.httpCacheStorage.flushByUriPattern(urlPattern);
    }

    private static String[] split(String s) {
        if (TextUtils.isBlank(s)) {
            return null;
        }
        return s.split(" *, *");
    }

    private static class EventConsumerFunction
    implements Function<Object, Void> {
        private final EventPublisher eventPublisher;

        EventConsumerFunction(EventPublisher eventPublisher) {
            this.eventPublisher = eventPublisher;
        }

        @Override
        public Void apply(Object event) {
            this.eventPublisher.publish(event);
            return null;
        }
    }

    private static final class DefaultApplicationNameSupplier
    implements Supplier<String> {
        private final ApplicationProperties applicationProperties;

        DefaultApplicationNameSupplier(ApplicationProperties applicationProperties) {
            this.applicationProperties = Objects.requireNonNull(applicationProperties);
        }

        @Override
        public String get() {
            return String.format("%s-%s (%s)", this.applicationProperties.getDisplayName(), this.applicationProperties.getVersion(), this.applicationProperties.getBuildNumber());
        }
    }

    private static final class NoOpThreadLocalContextManager<C>
    implements ThreadLocalContextManager<C> {
        private NoOpThreadLocalContextManager() {
        }

        public C getThreadLocalContext() {
            return null;
        }

        public void setThreadLocalContext(C context) {
        }

        public void clearThreadLocalContext() {
        }
    }
}

