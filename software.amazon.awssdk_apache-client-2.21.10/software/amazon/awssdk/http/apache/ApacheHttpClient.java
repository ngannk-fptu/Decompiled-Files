/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.ConnectionReuseStrategy
 *  org.apache.http.Header
 *  org.apache.http.HeaderIterator
 *  org.apache.http.HttpResponse
 *  org.apache.http.client.CredentialsProvider
 *  org.apache.http.client.HttpClient
 *  org.apache.http.client.methods.HttpRequestBase
 *  org.apache.http.client.methods.HttpUriRequest
 *  org.apache.http.client.protocol.HttpClientContext
 *  org.apache.http.config.Registry
 *  org.apache.http.config.RegistryBuilder
 *  org.apache.http.config.SocketConfig
 *  org.apache.http.conn.ConnectionKeepAliveStrategy
 *  org.apache.http.conn.DnsResolver
 *  org.apache.http.conn.HttpClientConnectionManager
 *  org.apache.http.conn.SchemePortResolver
 *  org.apache.http.conn.routing.HttpRoutePlanner
 *  org.apache.http.conn.socket.ConnectionSocketFactory
 *  org.apache.http.conn.socket.PlainConnectionSocketFactory
 *  org.apache.http.conn.ssl.NoopHostnameVerifier
 *  org.apache.http.conn.ssl.SSLConnectionSocketFactory
 *  org.apache.http.conn.ssl.SSLInitializationException
 *  org.apache.http.impl.client.HttpClientBuilder
 *  org.apache.http.impl.client.HttpClients
 *  org.apache.http.impl.conn.DefaultSchemePortResolver
 *  org.apache.http.impl.conn.PoolingHttpClientConnectionManager
 *  org.apache.http.pool.PoolStats
 *  org.apache.http.protocol.HttpContext
 *  org.apache.http.protocol.HttpRequestExecutor
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.annotations.SdkTestInternalApi
 *  software.amazon.awssdk.http.AbortableInputStream
 *  software.amazon.awssdk.http.ExecutableHttpRequest
 *  software.amazon.awssdk.http.HttpExecuteRequest
 *  software.amazon.awssdk.http.HttpExecuteResponse
 *  software.amazon.awssdk.http.HttpMetric
 *  software.amazon.awssdk.http.SdkHttpClient
 *  software.amazon.awssdk.http.SdkHttpClient$Builder
 *  software.amazon.awssdk.http.SdkHttpConfigurationOption
 *  software.amazon.awssdk.http.SdkHttpFullResponse$Builder
 *  software.amazon.awssdk.http.SdkHttpResponse
 *  software.amazon.awssdk.http.TlsKeyManagersProvider
 *  software.amazon.awssdk.http.TlsTrustManagersProvider
 *  software.amazon.awssdk.metrics.MetricCollector
 *  software.amazon.awssdk.metrics.NoOpMetricCollector
 *  software.amazon.awssdk.utils.AttributeMap
 *  software.amazon.awssdk.utils.AttributeMap$Builder
 *  software.amazon.awssdk.utils.AttributeMap$Key
 *  software.amazon.awssdk.utils.Logger
 *  software.amazon.awssdk.utils.NumericUtils
 *  software.amazon.awssdk.utils.Validate
 */
package software.amazon.awssdk.http.apache;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpResponse;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.DnsResolver;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.SchemePortResolver;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLInitializationException;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultSchemePortResolver;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.pool.PoolStats;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestExecutor;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.annotations.SdkTestInternalApi;
import software.amazon.awssdk.http.AbortableInputStream;
import software.amazon.awssdk.http.ExecutableHttpRequest;
import software.amazon.awssdk.http.HttpExecuteRequest;
import software.amazon.awssdk.http.HttpExecuteResponse;
import software.amazon.awssdk.http.HttpMetric;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.http.SdkHttpConfigurationOption;
import software.amazon.awssdk.http.SdkHttpFullResponse;
import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.http.TlsKeyManagersProvider;
import software.amazon.awssdk.http.TlsTrustManagersProvider;
import software.amazon.awssdk.http.apache.ProxyConfiguration;
import software.amazon.awssdk.http.apache.internal.ApacheHttpRequestConfig;
import software.amazon.awssdk.http.apache.internal.DefaultConfiguration;
import software.amazon.awssdk.http.apache.internal.SdkConnectionReuseStrategy;
import software.amazon.awssdk.http.apache.internal.SdkProxyRoutePlanner;
import software.amazon.awssdk.http.apache.internal.conn.ClientConnectionManagerFactory;
import software.amazon.awssdk.http.apache.internal.conn.ClientConnectionRequestFactory;
import software.amazon.awssdk.http.apache.internal.conn.IdleConnectionReaper;
import software.amazon.awssdk.http.apache.internal.conn.SdkConnectionKeepAliveStrategy;
import software.amazon.awssdk.http.apache.internal.conn.SdkTlsSocketFactory;
import software.amazon.awssdk.http.apache.internal.impl.ApacheHttpRequestFactory;
import software.amazon.awssdk.http.apache.internal.impl.ApacheSdkHttpClient;
import software.amazon.awssdk.http.apache.internal.impl.ConnectionManagerAwareHttpClient;
import software.amazon.awssdk.http.apache.internal.utils.ApacheUtils;
import software.amazon.awssdk.metrics.MetricCollector;
import software.amazon.awssdk.metrics.NoOpMetricCollector;
import software.amazon.awssdk.utils.AttributeMap;
import software.amazon.awssdk.utils.Logger;
import software.amazon.awssdk.utils.NumericUtils;
import software.amazon.awssdk.utils.Validate;

@SdkPublicApi
public final class ApacheHttpClient
implements SdkHttpClient {
    public static final String CLIENT_NAME = "Apache";
    private static final Logger log = Logger.loggerFor(ApacheHttpClient.class);
    private final ApacheHttpRequestFactory apacheHttpRequestFactory = new ApacheHttpRequestFactory();
    private final ConnectionManagerAwareHttpClient httpClient;
    private final ApacheHttpRequestConfig requestConfig;
    private final AttributeMap resolvedOptions;

    @SdkTestInternalApi
    ApacheHttpClient(ConnectionManagerAwareHttpClient httpClient, ApacheHttpRequestConfig requestConfig, AttributeMap resolvedOptions) {
        this.httpClient = httpClient;
        this.requestConfig = requestConfig;
        this.resolvedOptions = resolvedOptions;
    }

    private ApacheHttpClient(DefaultBuilder builder, AttributeMap resolvedOptions) {
        this.httpClient = this.createClient(builder, resolvedOptions);
        this.requestConfig = this.createRequestConfig(builder, resolvedOptions);
        this.resolvedOptions = resolvedOptions;
    }

    public static Builder builder() {
        return new DefaultBuilder();
    }

    public static SdkHttpClient create() {
        return new DefaultBuilder().build();
    }

    private ConnectionManagerAwareHttpClient createClient(DefaultBuilder configuration, AttributeMap standardOptions) {
        ApacheConnectionManagerFactory cmFactory = new ApacheConnectionManagerFactory();
        HttpClientBuilder builder = HttpClients.custom();
        HttpClientConnectionManager cm = cmFactory.create(configuration, standardOptions);
        builder.setRequestExecutor(new HttpRequestExecutor()).disableContentCompression().setKeepAliveStrategy(this.buildKeepAliveStrategy(standardOptions)).disableRedirectHandling().disableAutomaticRetries().setUserAgent("").setConnectionReuseStrategy((ConnectionReuseStrategy)new SdkConnectionReuseStrategy()).setConnectionManager(ClientConnectionManagerFactory.wrap(cm));
        this.addProxyConfig(builder, configuration);
        if (this.useIdleConnectionReaper(standardOptions)) {
            IdleConnectionReaper.getInstance().registerConnectionManager(cm, ((Duration)standardOptions.get((AttributeMap.Key)SdkHttpConfigurationOption.CONNECTION_MAX_IDLE_TIMEOUT)).toMillis());
        }
        return new ApacheSdkHttpClient((HttpClient)builder.build(), cm);
    }

    private void addProxyConfig(HttpClientBuilder builder, DefaultBuilder configuration) {
        ProxyConfiguration proxyConfiguration = configuration.proxyConfiguration;
        Validate.isTrue((configuration.httpRoutePlanner == null || !this.isProxyEnabled(proxyConfiguration) ? 1 : 0) != 0, (String)"The httpRoutePlanner and proxyConfiguration can't both be configured.", (Object[])new Object[0]);
        Validate.isTrue((configuration.credentialsProvider == null || !this.isAuthenticatedProxy(proxyConfiguration) ? 1 : 0) != 0, (String)"The credentialsProvider and proxyConfiguration username/password can't both be configured.", (Object[])new Object[0]);
        Object routePlanner = configuration.httpRoutePlanner;
        if (this.isProxyEnabled(proxyConfiguration)) {
            log.debug(() -> "Configuring Proxy. Proxy Host: " + proxyConfiguration.host());
            routePlanner = new SdkProxyRoutePlanner(proxyConfiguration.host(), proxyConfiguration.port(), proxyConfiguration.scheme(), proxyConfiguration.nonProxyHosts());
        }
        CredentialsProvider credentialsProvider = configuration.credentialsProvider;
        if (this.isAuthenticatedProxy(proxyConfiguration)) {
            credentialsProvider = ApacheUtils.newProxyCredentialsProvider(proxyConfiguration);
        }
        if (routePlanner != null) {
            builder.setRoutePlanner(routePlanner);
        }
        if (credentialsProvider != null) {
            builder.setDefaultCredentialsProvider(credentialsProvider);
        }
    }

    private ConnectionKeepAliveStrategy buildKeepAliveStrategy(AttributeMap standardOptions) {
        long maxIdle = ((Duration)standardOptions.get((AttributeMap.Key)SdkHttpConfigurationOption.CONNECTION_MAX_IDLE_TIMEOUT)).toMillis();
        return maxIdle > 0L ? new SdkConnectionKeepAliveStrategy(maxIdle) : null;
    }

    private boolean useIdleConnectionReaper(AttributeMap standardOptions) {
        return Boolean.TRUE.equals(standardOptions.get((AttributeMap.Key)SdkHttpConfigurationOption.REAP_IDLE_CONNECTIONS));
    }

    private boolean isAuthenticatedProxy(ProxyConfiguration proxyConfiguration) {
        return proxyConfiguration.username() != null && proxyConfiguration.password() != null;
    }

    private boolean isProxyEnabled(ProxyConfiguration proxyConfiguration) {
        return proxyConfiguration.host() != null && proxyConfiguration.port() > 0;
    }

    public ExecutableHttpRequest prepareRequest(HttpExecuteRequest request) {
        final MetricCollector metricCollector = request.metricCollector().orElseGet(NoOpMetricCollector::create);
        metricCollector.reportMetric(HttpMetric.HTTP_CLIENT_NAME, (Object)this.clientName());
        final HttpRequestBase apacheRequest = this.toApacheRequest(request);
        return new ExecutableHttpRequest(){

            public HttpExecuteResponse call() throws IOException {
                HttpExecuteResponse executeResponse = ApacheHttpClient.this.execute(apacheRequest, metricCollector);
                ApacheHttpClient.this.collectPoolMetric(metricCollector);
                return executeResponse;
            }

            public void abort() {
                apacheRequest.abort();
            }
        };
    }

    public void close() {
        HttpClientConnectionManager cm = this.httpClient.getHttpClientConnectionManager();
        IdleConnectionReaper.getInstance().deregisterConnectionManager(cm);
        cm.shutdown();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private HttpExecuteResponse execute(HttpRequestBase apacheRequest, MetricCollector metricCollector) throws IOException {
        HttpClientContext localRequestContext = ApacheUtils.newClientContext(this.requestConfig.proxyConfiguration());
        ClientConnectionRequestFactory.THREAD_LOCAL_REQUEST_METRIC_COLLECTOR.set(metricCollector);
        try {
            HttpResponse httpResponse = this.httpClient.execute((HttpUriRequest)apacheRequest, (HttpContext)localRequestContext);
            HttpExecuteResponse httpExecuteResponse = this.createResponse(httpResponse, apacheRequest);
            return httpExecuteResponse;
        }
        finally {
            ClientConnectionRequestFactory.THREAD_LOCAL_REQUEST_METRIC_COLLECTOR.remove();
        }
    }

    private HttpRequestBase toApacheRequest(HttpExecuteRequest request) {
        return this.apacheHttpRequestFactory.create(request, this.requestConfig);
    }

    private HttpExecuteResponse createResponse(HttpResponse apacheHttpResponse, HttpRequestBase apacheRequest) throws IOException {
        SdkHttpFullResponse.Builder responseBuilder = SdkHttpResponse.builder().statusCode(apacheHttpResponse.getStatusLine().getStatusCode()).statusText(apacheHttpResponse.getStatusLine().getReasonPhrase());
        HeaderIterator headerIterator = apacheHttpResponse.headerIterator();
        while (headerIterator.hasNext()) {
            Header header = headerIterator.nextHeader();
            responseBuilder.appendHeader(header.getName(), header.getValue());
        }
        AbortableInputStream responseBody = apacheHttpResponse.getEntity() != null ? this.toAbortableInputStream(apacheHttpResponse, apacheRequest) : null;
        return HttpExecuteResponse.builder().response((SdkHttpResponse)responseBuilder.build()).responseBody(responseBody).build();
    }

    private AbortableInputStream toAbortableInputStream(HttpResponse apacheHttpResponse, HttpRequestBase apacheRequest) throws IOException {
        return AbortableInputStream.create((InputStream)apacheHttpResponse.getEntity().getContent(), () -> ((HttpRequestBase)apacheRequest).abort());
    }

    private ApacheHttpRequestConfig createRequestConfig(DefaultBuilder builder, AttributeMap resolvedOptions) {
        return ApacheHttpRequestConfig.builder().socketTimeout((Duration)resolvedOptions.get((AttributeMap.Key)SdkHttpConfigurationOption.READ_TIMEOUT)).connectionTimeout((Duration)resolvedOptions.get((AttributeMap.Key)SdkHttpConfigurationOption.CONNECTION_TIMEOUT)).connectionAcquireTimeout((Duration)resolvedOptions.get((AttributeMap.Key)SdkHttpConfigurationOption.CONNECTION_ACQUIRE_TIMEOUT)).proxyConfiguration(builder.proxyConfiguration).localAddress(Optional.ofNullable(builder.localAddress).orElse(null)).expectContinueEnabled(Optional.ofNullable(builder.expectContinueEnabled).orElse(DefaultConfiguration.EXPECT_CONTINUE_ENABLED)).build();
    }

    private void collectPoolMetric(MetricCollector metricCollector) {
        HttpClientConnectionManager cm = this.httpClient.getHttpClientConnectionManager();
        if (cm instanceof PoolingHttpClientConnectionManager && !(metricCollector instanceof NoOpMetricCollector)) {
            PoolingHttpClientConnectionManager poolingCm = (PoolingHttpClientConnectionManager)cm;
            PoolStats totalStats = poolingCm.getTotalStats();
            metricCollector.reportMetric(HttpMetric.MAX_CONCURRENCY, (Object)totalStats.getMax());
            metricCollector.reportMetric(HttpMetric.AVAILABLE_CONCURRENCY, (Object)totalStats.getAvailable());
            metricCollector.reportMetric(HttpMetric.LEASED_CONCURRENCY, (Object)totalStats.getLeased());
            metricCollector.reportMetric(HttpMetric.PENDING_CONCURRENCY_ACQUIRES, (Object)totalStats.getPending());
        }
    }

    public String clientName() {
        return CLIENT_NAME;
    }

    private static class ApacheConnectionManagerFactory {
        private ApacheConnectionManagerFactory() {
        }

        public HttpClientConnectionManager create(DefaultBuilder configuration, AttributeMap standardOptions) {
            ConnectionSocketFactory sslsf = this.getPreferredSocketFactory(configuration, standardOptions);
            PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(this.createSocketFactoryRegistry(sslsf), null, (SchemePortResolver)DefaultSchemePortResolver.INSTANCE, configuration.dnsResolver, ((Duration)standardOptions.get((AttributeMap.Key)SdkHttpConfigurationOption.CONNECTION_TIME_TO_LIVE)).toMillis(), TimeUnit.MILLISECONDS);
            cm.setDefaultMaxPerRoute(((Integer)standardOptions.get((AttributeMap.Key)SdkHttpConfigurationOption.MAX_CONNECTIONS)).intValue());
            cm.setMaxTotal(((Integer)standardOptions.get((AttributeMap.Key)SdkHttpConfigurationOption.MAX_CONNECTIONS)).intValue());
            cm.setDefaultSocketConfig(this.buildSocketConfig(standardOptions));
            return cm;
        }

        private ConnectionSocketFactory getPreferredSocketFactory(DefaultBuilder configuration, AttributeMap standardOptions) {
            return Optional.ofNullable(configuration.socketFactory).orElseGet(() -> new SdkTlsSocketFactory(this.getSslContext(standardOptions), this.getHostNameVerifier(standardOptions)));
        }

        private HostnameVerifier getHostNameVerifier(AttributeMap standardOptions) {
            return (Boolean)standardOptions.get((AttributeMap.Key)SdkHttpConfigurationOption.TRUST_ALL_CERTIFICATES) != false ? NoopHostnameVerifier.INSTANCE : SSLConnectionSocketFactory.getDefaultHostnameVerifier();
        }

        private SSLContext getSslContext(AttributeMap standardOptions) {
            Validate.isTrue((standardOptions.get((AttributeMap.Key)SdkHttpConfigurationOption.TLS_TRUST_MANAGERS_PROVIDER) == null || (Boolean)standardOptions.get((AttributeMap.Key)SdkHttpConfigurationOption.TRUST_ALL_CERTIFICATES) == false ? 1 : 0) != 0, (String)"A TlsTrustManagerProvider can't be provided if TrustAllCertificates is also set", (Object[])new Object[0]);
            TrustManager[] trustManagers = null;
            if (standardOptions.get((AttributeMap.Key)SdkHttpConfigurationOption.TLS_TRUST_MANAGERS_PROVIDER) != null) {
                trustManagers = ((TlsTrustManagersProvider)standardOptions.get((AttributeMap.Key)SdkHttpConfigurationOption.TLS_TRUST_MANAGERS_PROVIDER)).trustManagers();
            }
            if (((Boolean)standardOptions.get((AttributeMap.Key)SdkHttpConfigurationOption.TRUST_ALL_CERTIFICATES)).booleanValue()) {
                log.warn(() -> "SSL Certificate verification is disabled. This is not a safe setting and should only be used for testing.");
                trustManagers = ApacheConnectionManagerFactory.trustAllTrustManager();
            }
            TlsKeyManagersProvider provider = (TlsKeyManagersProvider)standardOptions.get((AttributeMap.Key)SdkHttpConfigurationOption.TLS_KEY_MANAGERS_PROVIDER);
            KeyManager[] keyManagers = provider.keyManagers();
            try {
                SSLContext sslcontext = SSLContext.getInstance("TLS");
                sslcontext.init(keyManagers, trustManagers, null);
                return sslcontext;
            }
            catch (KeyManagementException | NoSuchAlgorithmException ex) {
                throw new SSLInitializationException(ex.getMessage(), (Throwable)ex);
            }
        }

        private static TrustManager[] trustAllTrustManager() {
            return new TrustManager[]{new X509TrustManager(){

                @Override
                public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                    log.debug(() -> "Accepting a client certificate: " + x509Certificates[0].getSubjectDN());
                }

                @Override
                public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                    log.debug(() -> "Accepting a client certificate: " + x509Certificates[0].getSubjectDN());
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }};
        }

        private SocketConfig buildSocketConfig(AttributeMap standardOptions) {
            return SocketConfig.custom().setSoKeepAlive(((Boolean)standardOptions.get((AttributeMap.Key)SdkHttpConfigurationOption.TCP_KEEPALIVE)).booleanValue()).setSoTimeout(NumericUtils.saturatedCast((long)((Duration)standardOptions.get((AttributeMap.Key)SdkHttpConfigurationOption.READ_TIMEOUT)).toMillis())).setTcpNoDelay(true).build();
        }

        private Registry<ConnectionSocketFactory> createSocketFactoryRegistry(ConnectionSocketFactory sslSocketFactory) {
            return RegistryBuilder.create().register("http", (Object)PlainConnectionSocketFactory.getSocketFactory()).register("https", (Object)sslSocketFactory).build();
        }
    }

    private static final class DefaultBuilder
    implements Builder {
        private final AttributeMap.Builder standardOptions = AttributeMap.builder();
        private ProxyConfiguration proxyConfiguration = (ProxyConfiguration)ProxyConfiguration.builder().build();
        private InetAddress localAddress;
        private Boolean expectContinueEnabled;
        private HttpRoutePlanner httpRoutePlanner;
        private CredentialsProvider credentialsProvider;
        private DnsResolver dnsResolver;
        private ConnectionSocketFactory socketFactory;

        private DefaultBuilder() {
        }

        @Override
        public Builder socketTimeout(Duration socketTimeout) {
            this.standardOptions.put((AttributeMap.Key)SdkHttpConfigurationOption.READ_TIMEOUT, (Object)socketTimeout);
            return this;
        }

        public void setSocketTimeout(Duration socketTimeout) {
            this.socketTimeout(socketTimeout);
        }

        @Override
        public Builder connectionTimeout(Duration connectionTimeout) {
            this.standardOptions.put((AttributeMap.Key)SdkHttpConfigurationOption.CONNECTION_TIMEOUT, (Object)connectionTimeout);
            return this;
        }

        public void setConnectionTimeout(Duration connectionTimeout) {
            this.connectionTimeout(connectionTimeout);
        }

        @Override
        public Builder connectionAcquisitionTimeout(Duration connectionAcquisitionTimeout) {
            Validate.isPositive((Duration)connectionAcquisitionTimeout, (String)"connectionAcquisitionTimeout");
            this.standardOptions.put((AttributeMap.Key)SdkHttpConfigurationOption.CONNECTION_ACQUIRE_TIMEOUT, (Object)connectionAcquisitionTimeout);
            return this;
        }

        public void setConnectionAcquisitionTimeout(Duration connectionAcquisitionTimeout) {
            this.connectionAcquisitionTimeout(connectionAcquisitionTimeout);
        }

        @Override
        public Builder maxConnections(Integer maxConnections) {
            this.standardOptions.put((AttributeMap.Key)SdkHttpConfigurationOption.MAX_CONNECTIONS, (Object)maxConnections);
            return this;
        }

        public void setMaxConnections(Integer maxConnections) {
            this.maxConnections(maxConnections);
        }

        @Override
        public Builder proxyConfiguration(ProxyConfiguration proxyConfiguration) {
            this.proxyConfiguration = proxyConfiguration;
            return this;
        }

        public void setProxyConfiguration(ProxyConfiguration proxyConfiguration) {
            this.proxyConfiguration(proxyConfiguration);
        }

        @Override
        public Builder localAddress(InetAddress localAddress) {
            this.localAddress = localAddress;
            return this;
        }

        public void setLocalAddress(InetAddress localAddress) {
            this.localAddress(localAddress);
        }

        @Override
        public Builder expectContinueEnabled(Boolean expectContinueEnabled) {
            this.expectContinueEnabled = expectContinueEnabled;
            return this;
        }

        public void setExpectContinueEnabled(Boolean useExpectContinue) {
            this.expectContinueEnabled = useExpectContinue;
        }

        @Override
        public Builder connectionTimeToLive(Duration connectionTimeToLive) {
            this.standardOptions.put((AttributeMap.Key)SdkHttpConfigurationOption.CONNECTION_TIME_TO_LIVE, (Object)connectionTimeToLive);
            return this;
        }

        public void setConnectionTimeToLive(Duration connectionTimeToLive) {
            this.connectionTimeToLive(connectionTimeToLive);
        }

        @Override
        public Builder connectionMaxIdleTime(Duration maxIdleConnectionTimeout) {
            this.standardOptions.put((AttributeMap.Key)SdkHttpConfigurationOption.CONNECTION_MAX_IDLE_TIMEOUT, (Object)maxIdleConnectionTimeout);
            return this;
        }

        public void setConnectionMaxIdleTime(Duration connectionMaxIdleTime) {
            this.connectionMaxIdleTime(connectionMaxIdleTime);
        }

        @Override
        public Builder useIdleConnectionReaper(Boolean useIdleConnectionReaper) {
            this.standardOptions.put((AttributeMap.Key)SdkHttpConfigurationOption.REAP_IDLE_CONNECTIONS, (Object)useIdleConnectionReaper);
            return this;
        }

        public void setUseIdleConnectionReaper(Boolean useIdleConnectionReaper) {
            this.useIdleConnectionReaper(useIdleConnectionReaper);
        }

        @Override
        public Builder dnsResolver(DnsResolver dnsResolver) {
            this.dnsResolver = dnsResolver;
            return this;
        }

        public void setDnsResolver(DnsResolver dnsResolver) {
            this.dnsResolver(dnsResolver);
        }

        @Override
        public Builder socketFactory(ConnectionSocketFactory socketFactory) {
            this.socketFactory = socketFactory;
            return this;
        }

        public void setSocketFactory(ConnectionSocketFactory socketFactory) {
            this.socketFactory(socketFactory);
        }

        @Override
        public Builder httpRoutePlanner(HttpRoutePlanner httpRoutePlanner) {
            this.httpRoutePlanner = httpRoutePlanner;
            return this;
        }

        public void setHttpRoutePlanner(HttpRoutePlanner httpRoutePlanner) {
            this.httpRoutePlanner(httpRoutePlanner);
        }

        @Override
        public Builder credentialsProvider(CredentialsProvider credentialsProvider) {
            this.credentialsProvider = credentialsProvider;
            return this;
        }

        public void setCredentialsProvider(CredentialsProvider credentialsProvider) {
            this.credentialsProvider(credentialsProvider);
        }

        @Override
        public Builder tcpKeepAlive(Boolean keepConnectionAlive) {
            this.standardOptions.put((AttributeMap.Key)SdkHttpConfigurationOption.TCP_KEEPALIVE, (Object)keepConnectionAlive);
            return this;
        }

        public void setTcpKeepAlive(Boolean keepConnectionAlive) {
            this.tcpKeepAlive(keepConnectionAlive);
        }

        @Override
        public Builder tlsKeyManagersProvider(TlsKeyManagersProvider tlsKeyManagersProvider) {
            this.standardOptions.put((AttributeMap.Key)SdkHttpConfigurationOption.TLS_KEY_MANAGERS_PROVIDER, (Object)tlsKeyManagersProvider);
            return this;
        }

        public void setTlsKeyManagersProvider(TlsKeyManagersProvider tlsKeyManagersProvider) {
            this.tlsKeyManagersProvider(tlsKeyManagersProvider);
        }

        @Override
        public Builder tlsTrustManagersProvider(TlsTrustManagersProvider tlsTrustManagersProvider) {
            this.standardOptions.put((AttributeMap.Key)SdkHttpConfigurationOption.TLS_TRUST_MANAGERS_PROVIDER, (Object)tlsTrustManagersProvider);
            return this;
        }

        public void setTlsTrustManagersProvider(TlsTrustManagersProvider tlsTrustManagersProvider) {
            this.tlsTrustManagersProvider(tlsTrustManagersProvider);
        }

        public SdkHttpClient buildWithDefaults(AttributeMap serviceDefaults) {
            AttributeMap resolvedOptions = this.standardOptions.build().merge(serviceDefaults).merge(SdkHttpConfigurationOption.GLOBAL_HTTP_DEFAULTS);
            return new ApacheHttpClient(this, resolvedOptions);
        }
    }

    public static interface Builder
    extends SdkHttpClient.Builder<Builder> {
        public Builder socketTimeout(Duration var1);

        public Builder connectionTimeout(Duration var1);

        public Builder connectionAcquisitionTimeout(Duration var1);

        public Builder maxConnections(Integer var1);

        public Builder proxyConfiguration(ProxyConfiguration var1);

        public Builder localAddress(InetAddress var1);

        public Builder expectContinueEnabled(Boolean var1);

        public Builder connectionTimeToLive(Duration var1);

        public Builder connectionMaxIdleTime(Duration var1);

        public Builder useIdleConnectionReaper(Boolean var1);

        public Builder dnsResolver(DnsResolver var1);

        public Builder socketFactory(ConnectionSocketFactory var1);

        public Builder httpRoutePlanner(HttpRoutePlanner var1);

        public Builder credentialsProvider(CredentialsProvider var1);

        public Builder tcpKeepAlive(Boolean var1);

        public Builder tlsKeyManagersProvider(TlsKeyManagersProvider var1);

        public Builder tlsTrustManagersProvider(TlsTrustManagersProvider var1);
    }
}

