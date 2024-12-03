/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.client5.http.impl.nio;

import org.apache.hc.client5.http.DnsResolver;
import org.apache.hc.client5.http.HttpRoute;
import org.apache.hc.client5.http.SchemePortResolver;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.TlsConfig;
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManager;
import org.apache.hc.client5.http.ssl.ConscryptClientTlsStrategy;
import org.apache.hc.client5.http.ssl.DefaultClientTlsStrategy;
import org.apache.hc.core5.function.Resolver;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.URIScheme;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.http.nio.ssl.TlsStrategy;
import org.apache.hc.core5.pool.PoolConcurrencyPolicy;
import org.apache.hc.core5.pool.PoolReusePolicy;
import org.apache.hc.core5.util.ReflectionUtils;
import org.apache.hc.core5.util.TimeValue;

public class PoolingAsyncClientConnectionManagerBuilder {
    private TlsStrategy tlsStrategy;
    private SchemePortResolver schemePortResolver;
    private DnsResolver dnsResolver;
    private PoolConcurrencyPolicy poolConcurrencyPolicy;
    private PoolReusePolicy poolReusePolicy;
    private boolean systemProperties;
    private int maxConnTotal;
    private int maxConnPerRoute;
    private Resolver<HttpRoute, ConnectionConfig> connectionConfigResolver;
    private Resolver<HttpHost, TlsConfig> tlsConfigResolver;

    public static PoolingAsyncClientConnectionManagerBuilder create() {
        return new PoolingAsyncClientConnectionManagerBuilder();
    }

    PoolingAsyncClientConnectionManagerBuilder() {
    }

    public final PoolingAsyncClientConnectionManagerBuilder setTlsStrategy(TlsStrategy tlsStrategy) {
        this.tlsStrategy = tlsStrategy;
        return this;
    }

    public final PoolingAsyncClientConnectionManagerBuilder setDnsResolver(DnsResolver dnsResolver) {
        this.dnsResolver = dnsResolver;
        return this;
    }

    public final PoolingAsyncClientConnectionManagerBuilder setSchemePortResolver(SchemePortResolver schemePortResolver) {
        this.schemePortResolver = schemePortResolver;
        return this;
    }

    public final PoolingAsyncClientConnectionManagerBuilder setPoolConcurrencyPolicy(PoolConcurrencyPolicy poolConcurrencyPolicy) {
        this.poolConcurrencyPolicy = poolConcurrencyPolicy;
        return this;
    }

    public final PoolingAsyncClientConnectionManagerBuilder setConnPoolPolicy(PoolReusePolicy poolReusePolicy) {
        this.poolReusePolicy = poolReusePolicy;
        return this;
    }

    public final PoolingAsyncClientConnectionManagerBuilder setMaxConnTotal(int maxConnTotal) {
        this.maxConnTotal = maxConnTotal;
        return this;
    }

    public final PoolingAsyncClientConnectionManagerBuilder setMaxConnPerRoute(int maxConnPerRoute) {
        this.maxConnPerRoute = maxConnPerRoute;
        return this;
    }

    public final PoolingAsyncClientConnectionManagerBuilder setDefaultConnectionConfig(ConnectionConfig config) {
        this.connectionConfigResolver = route -> config;
        return this;
    }

    public final PoolingAsyncClientConnectionManagerBuilder setConnectionConfigResolver(Resolver<HttpRoute, ConnectionConfig> connectionConfigResolver) {
        this.connectionConfigResolver = connectionConfigResolver;
        return this;
    }

    public final PoolingAsyncClientConnectionManagerBuilder setDefaultTlsConfig(TlsConfig config) {
        this.tlsConfigResolver = host -> config;
        return this;
    }

    public final PoolingAsyncClientConnectionManagerBuilder setTlsConfigResolver(Resolver<HttpHost, TlsConfig> tlsConfigResolver) {
        this.tlsConfigResolver = tlsConfigResolver;
        return this;
    }

    @Deprecated
    public final PoolingAsyncClientConnectionManagerBuilder setConnectionTimeToLive(TimeValue timeToLive) {
        this.setDefaultConnectionConfig(ConnectionConfig.custom().setTimeToLive(timeToLive).build());
        return this;
    }

    @Deprecated
    public final PoolingAsyncClientConnectionManagerBuilder setValidateAfterInactivity(TimeValue validateAfterInactivity) {
        this.setDefaultConnectionConfig(ConnectionConfig.custom().setValidateAfterInactivity(validateAfterInactivity).build());
        return this;
    }

    public final PoolingAsyncClientConnectionManagerBuilder useSystemProperties() {
        this.systemProperties = true;
        return this;
    }

    public PoolingAsyncClientConnectionManager build() {
        TlsStrategy tlsStrategyCopy = this.tlsStrategy != null ? this.tlsStrategy : (ReflectionUtils.determineJRELevel() <= 8 && ConscryptClientTlsStrategy.isSupported() ? (this.systemProperties ? ConscryptClientTlsStrategy.getSystemDefault() : ConscryptClientTlsStrategy.getDefault()) : (this.systemProperties ? DefaultClientTlsStrategy.getSystemDefault() : DefaultClientTlsStrategy.getDefault()));
        PoolingAsyncClientConnectionManager poolingmgr = new PoolingAsyncClientConnectionManager(RegistryBuilder.create().register(URIScheme.HTTPS.getId(), tlsStrategyCopy).build(), this.poolConcurrencyPolicy, this.poolReusePolicy, null, this.schemePortResolver, this.dnsResolver);
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

