/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  io.atlassian.fugue.Option
 */
package com.atlassian.marketplace.client.http;

import com.atlassian.marketplace.client.http.RequestDecorator;
import com.google.common.base.Preconditions;
import io.atlassian.fugue.Option;

public class HttpConfiguration {
    public static final int DEFAULT_CONNECT_TIMEOUT_MILLIS = 15000;
    public static final int DEFAULT_READ_TIMEOUT_MILLIS = 15000;
    public static final int DEFAULT_MAX_CONNECTIONS = 10;
    public static final int DEFAULT_MAX_CACHE_ENTRIES = 100;
    public static final long DEFAULT_MAX_CACHE_OBJECT_SIZE = 60000L;
    private final int connectTimeoutMillis;
    private final int readTimeoutMillis;
    private final int maxConnections;
    private final int maxCacheEntries;
    private final long maxCacheObjectSize;
    private final Option<Credentials> credentials;
    private final Option<Integer> maxRedirects;
    private final Option<ProxyConfiguration> proxy;
    private final Option<RequestDecorator> requestDecorator;

    public static Builder builder() {
        return new Builder();
    }

    public static HttpConfiguration defaults() {
        return HttpConfiguration.builder().build();
    }

    private HttpConfiguration(Builder builder) {
        this.connectTimeoutMillis = builder.connectTimeoutMillis;
        this.readTimeoutMillis = builder.readTimeoutMillis;
        this.maxConnections = builder.maxConnections;
        this.maxCacheEntries = builder.maxCacheEntries;
        this.maxCacheObjectSize = builder.maxCacheObjectSize;
        this.credentials = builder.credentials;
        this.maxRedirects = builder.maxRedirects;
        this.proxy = builder.proxy;
        this.requestDecorator = builder.requestDecorator;
    }

    public int getConnectTimeoutMillis() {
        return this.connectTimeoutMillis;
    }

    public int getReadTimeoutMillis() {
        return this.readTimeoutMillis;
    }

    public int getMaxConnections() {
        return this.maxConnections;
    }

    public int getMaxCacheEntries() {
        return this.maxCacheEntries;
    }

    public long getMaxCacheObjectSize() {
        return this.maxCacheObjectSize;
    }

    public Option<Integer> getMaxRedirects() {
        return this.maxRedirects;
    }

    public Option<Credentials> getCredentials() {
        return this.credentials;
    }

    public boolean hasCredentials() {
        return this.credentials.isDefined();
    }

    public Option<ProxyConfiguration> getProxyConfiguration() {
        return this.proxy;
    }

    public boolean hasProxy() {
        return this.proxy.isDefined();
    }

    public Option<RequestDecorator> getRequestDecorator() {
        return this.requestDecorator;
    }

    public boolean equals(Object other) {
        if (other instanceof HttpConfiguration) {
            HttpConfiguration o = (HttpConfiguration)other;
            return o.credentials.equals(this.credentials) && o.proxy.equals(this.proxy) && o.requestDecorator.equals(this.requestDecorator) && o.connectTimeoutMillis == this.connectTimeoutMillis && o.readTimeoutMillis == this.readTimeoutMillis && o.maxCacheEntries == this.maxCacheEntries && o.maxCacheObjectSize == this.maxCacheObjectSize && o.maxConnections == this.maxConnections;
        }
        return false;
    }

    public int hashCode() {
        return (int)((long)(this.credentials.hashCode() + this.proxy.hashCode() + this.requestDecorator.hashCode() + this.connectTimeoutMillis + this.readTimeoutMillis + this.maxCacheEntries) + this.maxCacheObjectSize + (long)this.maxConnections);
    }

    public String toString() {
        return "HttpConfiguration(" + this.credentials + ", " + this.proxy + ", " + this.requestDecorator + ", " + this.connectTimeoutMillis + ", " + this.readTimeoutMillis + ", " + this.maxCacheEntries + ", " + this.maxCacheObjectSize + ", " + this.maxConnections + ")";
    }

    public static enum ProxyAuthMethod {
        BASIC,
        DIGEST,
        NTLM;


        public static Option<ProxyAuthMethod> fromKey(String key) {
            for (ProxyAuthMethod a : ProxyAuthMethod.values()) {
                if (!a.name().equalsIgnoreCase(key)) continue;
                return Option.some((Object)((Object)a));
            }
            return Option.none();
        }
    }

    public static class ProxyAuthParams {
        private final Credentials credentials;
        private final ProxyAuthMethod authMethod;
        private final Option<String> ntlmDomain;
        private final Option<String> ntlmWorkstation;

        public ProxyAuthParams(Credentials credentials, ProxyAuthMethod authMethod, Option<String> ntlmDomain, Option<String> ntlmWorkstation) {
            this.credentials = (Credentials)Preconditions.checkNotNull((Object)credentials, (Object)"credentials");
            this.authMethod = (ProxyAuthMethod)((Object)Preconditions.checkNotNull((Object)((Object)authMethod), (Object)"authMethod"));
            this.ntlmDomain = (Option)Preconditions.checkNotNull(ntlmDomain, (Object)"ntlmDomain");
            this.ntlmWorkstation = (Option)Preconditions.checkNotNull(ntlmWorkstation, (Object)"ntlmWorkstation");
        }

        public ProxyAuthParams(Credentials credentials, ProxyAuthMethod authMethod) {
            this(credentials, authMethod, (Option<String>)Option.none(String.class), (Option<String>)Option.none(String.class));
        }

        public Credentials getCredentials() {
            return this.credentials;
        }

        public ProxyAuthMethod getAuthMethod() {
            return this.authMethod;
        }

        public Option<String> getNtlmDomain() {
            return this.ntlmDomain;
        }

        public Option<String> getNtlmWorkstation() {
            return this.ntlmWorkstation;
        }

        public boolean equals(Object other) {
            if (other instanceof ProxyAuthParams) {
                ProxyAuthParams o = (ProxyAuthParams)other;
                return o.credentials.equals(this.credentials) && o.authMethod.equals((Object)this.authMethod) && o.ntlmDomain.equals(this.ntlmDomain) && o.ntlmWorkstation.equals(this.ntlmWorkstation);
            }
            return false;
        }

        public int hashCode() {
            return this.credentials.hashCode() + this.authMethod.hashCode() + this.ntlmDomain.hashCode() + this.ntlmWorkstation.hashCode();
        }

        public String toString() {
            return "AuthParams(" + this.credentials + ", " + (Object)((Object)this.authMethod) + ", " + this.ntlmDomain + ", " + this.ntlmWorkstation + ")";
        }
    }

    public static class ProxyHost {
        public static final int DEFAULT_PORT = 80;
        private final String hostname;
        private final int port;

        public ProxyHost(String hostname, int port) {
            this.hostname = (String)Preconditions.checkNotNull((Object)hostname);
            this.port = port;
        }

        public ProxyHost(String hostname) {
            this(hostname, 80);
        }

        public String getHostname() {
            return this.hostname;
        }

        public int getPort() {
            return this.port;
        }

        public boolean equals(Object other) {
            if (other instanceof ProxyHost) {
                ProxyHost o = (ProxyHost)other;
                return o.hostname.equals(this.hostname) && o.port == this.port;
            }
            return false;
        }

        public int hashCode() {
            return this.hostname.hashCode() + this.port;
        }

        public String toString() {
            return this.hostname + ":" + this.port;
        }
    }

    public static class ProxyConfiguration {
        private final Option<ProxyHost> proxyHost;
        private final Option<ProxyAuthParams> authParams;

        public static Builder builder() {
            return new Builder();
        }

        private ProxyConfiguration(Builder builder) {
            this.proxyHost = builder.proxyHost;
            this.authParams = builder.authParams;
        }

        public Option<ProxyHost> getProxyHost() {
            return this.proxyHost;
        }

        public boolean hasAuth() {
            return this.authParams.isDefined();
        }

        public Option<ProxyAuthParams> getAuthParams() {
            return this.authParams;
        }

        public boolean equals(Object other) {
            if (other instanceof ProxyConfiguration) {
                ProxyConfiguration o = (ProxyConfiguration)other;
                return o.proxyHost.equals(this.proxyHost) && o.authParams.equals(this.authParams);
            }
            return false;
        }

        public int hashCode() {
            return this.proxyHost.hashCode() + this.authParams.hashCode();
        }

        public String toString() {
            return "ProxyConfiguration(" + this.proxyHost + ", " + this.authParams + ")";
        }

        public static class Builder {
            private Option<ProxyHost> proxyHost = Option.none();
            private Option<ProxyAuthParams> authParams = Option.none();

            public ProxyConfiguration build() {
                return new ProxyConfiguration(this);
            }

            public Builder proxyHost(Option<ProxyHost> proxyHost) {
                this.proxyHost = (Option)Preconditions.checkNotNull(proxyHost);
                return this;
            }

            public Builder authParams(Option<ProxyAuthParams> authParams) {
                this.authParams = (Option)Preconditions.checkNotNull(authParams);
                return this;
            }
        }
    }

    public static class Builder {
        private int connectTimeoutMillis = 15000;
        private int readTimeoutMillis = 15000;
        private int maxConnections = 10;
        private int maxCacheEntries = 100;
        private long maxCacheObjectSize = 60000L;
        private Option<Credentials> credentials = Option.none();
        private Option<Integer> maxRedirects = Option.none();
        private Option<ProxyConfiguration> proxy = Option.none();
        private Option<RequestDecorator> requestDecorator = Option.none();

        public HttpConfiguration build() {
            return new HttpConfiguration(this);
        }

        public Builder connectTimeoutMillis(Integer connectTimeoutMillis) {
            this.connectTimeoutMillis = connectTimeoutMillis == null ? 15000 : connectTimeoutMillis;
            return this;
        }

        public Builder readTimeoutMillis(Integer readTimeoutMillis) {
            this.readTimeoutMillis = readTimeoutMillis == null ? 15000 : readTimeoutMillis;
            return this;
        }

        public Builder maxConnections(int maxConnections) {
            this.maxConnections = maxConnections;
            return this;
        }

        public Builder maxCacheEntries(int maxCacheEntries) {
            this.maxCacheEntries = maxCacheEntries;
            return this;
        }

        public Builder maxCacheObjectSize(long maxCacheObjectSize) {
            this.maxCacheObjectSize = maxCacheObjectSize;
            return this;
        }

        public Builder maxRedirects(Option<Integer> maxRedirects) {
            this.maxRedirects = (Option)Preconditions.checkNotNull(maxRedirects);
            return this;
        }

        public Builder credentials(Option<Credentials> credentials) {
            this.credentials = (Option)Preconditions.checkNotNull(credentials);
            return this;
        }

        public Builder proxyConfiguration(Option<ProxyConfiguration> proxy) {
            this.proxy = (Option)Preconditions.checkNotNull(proxy);
            return this;
        }

        public Builder requestDecorator(Option<RequestDecorator> requestDecorator) {
            this.requestDecorator = (Option)Preconditions.checkNotNull(requestDecorator);
            return this;
        }
    }

    public static class Credentials {
        private final String username;
        private final String password;

        public Credentials(String username, String password) {
            this.username = (String)Preconditions.checkNotNull((Object)username, (Object)"username");
            this.password = (String)Preconditions.checkNotNull((Object)password, (Object)"password");
        }

        public String getUsername() {
            return this.username;
        }

        public String getPassword() {
            return this.password;
        }

        public boolean equals(Object other) {
            if (other instanceof Credentials) {
                Credentials o = (Credentials)other;
                return o.username.equals(this.username) && o.password.equals(this.password);
            }
            return false;
        }

        public int hashCode() {
            return this.username.hashCode() + this.password.hashCode();
        }

        public String toString() {
            return "Credentials(" + this.username + ", " + this.password + ")";
        }
    }
}

