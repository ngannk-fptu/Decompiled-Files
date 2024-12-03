/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.function.Resolver
 *  org.apache.hc.core5.http.HttpHost
 *  org.apache.hc.core5.http.URIScheme
 *  org.apache.hc.core5.http.config.Registry
 *  org.apache.hc.core5.http.config.RegistryBuilder
 *  org.apache.hc.core5.http.io.HttpConnectionFactory
 *  org.apache.hc.core5.http.io.SocketConfig
 *  org.apache.hc.core5.pool.PoolConcurrencyPolicy
 *  org.apache.hc.core5.pool.PoolReusePolicy
 *  org.apache.hc.core5.util.TimeValue
 */
package org.apache.hc.client5.http.impl.io;

import org.apache.hc.client5.http.DnsResolver;
import org.apache.hc.client5.http.HttpRoute;
import org.apache.hc.client5.http.SchemePortResolver;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.TlsConfig;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.io.ManagedHttpClientConnection;
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.client5.http.socket.LayeredConnectionSocketFactory;
import org.apache.hc.client5.http.socket.PlainConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.function.Resolver;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.URIScheme;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.http.io.HttpConnectionFactory;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.pool.PoolConcurrencyPolicy;
import org.apache.hc.core5.pool.PoolReusePolicy;
import org.apache.hc.core5.util.TimeValue;

public class PoolingHttpClientConnectionManagerBuilder {
    private HttpConnectionFactory<ManagedHttpClientConnection> connectionFactory;
    private LayeredConnectionSocketFactory sslSocketFactory;
    private SchemePortResolver schemePortResolver;
    private DnsResolver dnsResolver;
    private PoolConcurrencyPolicy poolConcurrencyPolicy;
    private PoolReusePolicy poolReusePolicy;
    private Resolver<HttpRoute, SocketConfig> socketConfigResolver;
    private Resolver<HttpRoute, ConnectionConfig> connectionConfigResolver;
    private Resolver<HttpHost, TlsConfig> tlsConfigResolver;
    private boolean systemProperties;
    private int maxConnTotal;
    private int maxConnPerRoute;

    public static PoolingHttpClientConnectionManagerBuilder create() {
        return new PoolingHttpClientConnectionManagerBuilder();
    }

    PoolingHttpClientConnectionManagerBuilder() {
    }

    public final PoolingHttpClientConnectionManagerBuilder setConnectionFactory(HttpConnectionFactory<ManagedHttpClientConnection> connectionFactory) {
        this.connectionFactory = connectionFactory;
        return this;
    }

    public final PoolingHttpClientConnectionManagerBuilder setSSLSocketFactory(LayeredConnectionSocketFactory sslSocketFactory) {
        this.sslSocketFactory = sslSocketFactory;
        return this;
    }

    public final PoolingHttpClientConnectionManagerBuilder setDnsResolver(DnsResolver dnsResolver) {
        this.dnsResolver = dnsResolver;
        return this;
    }

    public final PoolingHttpClientConnectionManagerBuilder setSchemePortResolver(SchemePortResolver schemePortResolver) {
        this.schemePortResolver = schemePortResolver;
        return this;
    }

    public final PoolingHttpClientConnectionManagerBuilder setPoolConcurrencyPolicy(PoolConcurrencyPolicy poolConcurrencyPolicy) {
        this.poolConcurrencyPolicy = poolConcurrencyPolicy;
        return this;
    }

    public final PoolingHttpClientConnectionManagerBuilder setConnPoolPolicy(PoolReusePolicy poolReusePolicy) {
        this.poolReusePolicy = poolReusePolicy;
        return this;
    }

    public final PoolingHttpClientConnectionManagerBuilder setMaxConnTotal(int maxConnTotal) {
        this.maxConnTotal = maxConnTotal;
        return this;
    }

    public final PoolingHttpClientConnectionManagerBuilder setMaxConnPerRoute(int maxConnPerRoute) {
        this.maxConnPerRoute = maxConnPerRoute;
        return this;
    }

    public final PoolingHttpClientConnectionManagerBuilder setDefaultSocketConfig(SocketConfig config) {
        this.socketConfigResolver = route -> config;
        return this;
    }

    public final PoolingHttpClientConnectionManagerBuilder setSocketConfigResolver(Resolver<HttpRoute, SocketConfig> socketConfigResolver) {
        this.socketConfigResolver = socketConfigResolver;
        return this;
    }

    public final PoolingHttpClientConnectionManagerBuilder setDefaultConnectionConfig(ConnectionConfig config) {
        this.connectionConfigResolver = route -> config;
        return this;
    }

    public final PoolingHttpClientConnectionManagerBuilder setConnectionConfigResolver(Resolver<HttpRoute, ConnectionConfig> connectionConfigResolver) {
        this.connectionConfigResolver = connectionConfigResolver;
        return this;
    }

    public final PoolingHttpClientConnectionManagerBuilder setDefaultTlsConfig(TlsConfig config) {
        this.tlsConfigResolver = host -> config;
        return this;
    }

    public final PoolingHttpClientConnectionManagerBuilder setTlsConfigResolver(Resolver<HttpHost, TlsConfig> tlsConfigResolver) {
        this.tlsConfigResolver = tlsConfigResolver;
        return this;
    }

    @Deprecated
    public final PoolingHttpClientConnectionManagerBuilder setConnectionTimeToLive(TimeValue timeToLive) {
        this.setDefaultConnectionConfig(ConnectionConfig.custom().setTimeToLive(timeToLive).build());
        return this;
    }

    @Deprecated
    public final PoolingHttpClientConnectionManagerBuilder setValidateAfterInactivity(TimeValue validateAfterInactivity) {
        this.setDefaultConnectionConfig(ConnectionConfig.custom().setValidateAfterInactivity(validateAfterInactivity).build());
        return this;
    }

    public final PoolingHttpClientConnectionManagerBuilder useSystemProperties() {
        this.systemProperties = true;
        return this;
    }

    public PoolingHttpClientConnectionManager build() {
        PoolingHttpClientConnectionManager poolingmgr = new PoolingHttpClientConnectionManager((Registry<ConnectionSocketFactory>)RegistryBuilder.create().register(URIScheme.HTTP.id, (Object)PlainConnectionSocketFactory.getSocketFactory()).register(URIScheme.HTTPS.id, (Object)(this.sslSocketFactory != null ? this.sslSocketFactory : (this.systemProperties ? SSLConnectionSocketFactory.getSystemSocketFactory() : SSLConnectionSocketFactory.getSocketFactory()))).build(), this.poolConcurrencyPolicy, this.poolReusePolicy, null, this.schemePortResolver, this.dnsResolver, this.connectionFactory);
        poolingmgr.setSocketConfigResolver(this.socketConfigResolver);
        poolingmgr.setConnectionConfigResolver(this.connectionConfigResolver);
        poolingmgr.setTlsConfigResolver(this.tlsConfigResolver);
        if (this.maxConnTotal > 0) {
            poolingmgr.setMaxTotal(this.maxConnTotal);
        }
        if (this.maxConnPerRoute > 0) {
            poolingmgr.setDefaultMaxPerRoute(this.maxConnPerRoute);
        }
        return poolingmgr;
    }
}

