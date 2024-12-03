/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.nio.client;

import java.net.ProxySelector;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.auth.AuthSchemeProvider;
import org.apache.http.client.AuthenticationStrategy;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.UserTokenHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.protocol.RequestAddCookies;
import org.apache.http.client.protocol.RequestAuthCache;
import org.apache.http.client.protocol.RequestClientConnControl;
import org.apache.http.client.protocol.RequestDefaultHeaders;
import org.apache.http.client.protocol.RequestExpectContinue;
import org.apache.http.client.protocol.ResponseProcessCookies;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.Lookup;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.SchemePortResolver;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.conn.util.PublicSuffixMatcher;
import org.apache.http.conn.util.PublicSuffixMatcherLoader;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.NoConnectionReuseStrategy;
import org.apache.http.impl.auth.BasicSchemeFactory;
import org.apache.http.impl.auth.DigestSchemeFactory;
import org.apache.http.impl.auth.KerberosSchemeFactory;
import org.apache.http.impl.auth.NTLMSchemeFactory;
import org.apache.http.impl.auth.SPNegoSchemeFactory;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.client.NoopUserTokenHandler;
import org.apache.http.impl.client.ProxyAuthenticationStrategy;
import org.apache.http.impl.client.SystemDefaultCredentialsProvider;
import org.apache.http.impl.client.TargetAuthenticationStrategy;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.conn.DefaultRoutePlanner;
import org.apache.http.impl.conn.DefaultSchemePortResolver;
import org.apache.http.impl.conn.SystemDefaultDnsResolver;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;
import org.apache.http.impl.cookie.DefaultCookieSpecProvider;
import org.apache.http.impl.cookie.IgnoreSpecProvider;
import org.apache.http.impl.cookie.NetscapeDraftSpecProvider;
import org.apache.http.impl.cookie.RFC6265CookieSpecProvider;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.DefaultAsyncUserTokenHandler;
import org.apache.http.impl.nio.client.IOReactorUtils;
import org.apache.http.impl.nio.client.InternalHttpAsyncClient;
import org.apache.http.impl.nio.client.MainClientExec;
import org.apache.http.impl.nio.conn.ManagedNHttpClientConnectionFactory;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.NHttpClientEventHandler;
import org.apache.http.nio.conn.NHttpClientConnectionManager;
import org.apache.http.nio.conn.NoopIOSessionStrategy;
import org.apache.http.nio.conn.SchemeIOSessionStrategy;
import org.apache.http.nio.conn.ssl.SSLIOSessionStrategy;
import org.apache.http.nio.protocol.HttpAsyncRequestExecutor;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpProcessorBuilder;
import org.apache.http.protocol.RequestContent;
import org.apache.http.protocol.RequestTargetHost;
import org.apache.http.protocol.RequestUserAgent;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.TextUtils;
import org.apache.http.util.VersionInfo;

public class HttpAsyncClientBuilder {
    private NHttpClientConnectionManager connManager;
    private boolean connManagerShared;
    private SchemePortResolver schemePortResolver;
    private SchemeIOSessionStrategy sslStrategy;
    private HostnameVerifier hostnameVerifier;
    private SSLContext sslcontext;
    private ConnectionReuseStrategy reuseStrategy;
    private ConnectionKeepAliveStrategy keepAliveStrategy;
    private AuthenticationStrategy targetAuthStrategy;
    private AuthenticationStrategy proxyAuthStrategy;
    private UserTokenHandler userTokenHandler;
    private HttpProcessor httpprocessor;
    private LinkedList<HttpRequestInterceptor> requestFirst;
    private LinkedList<HttpRequestInterceptor> requestLast;
    private LinkedList<HttpResponseInterceptor> responseFirst;
    private LinkedList<HttpResponseInterceptor> responseLast;
    private HttpRoutePlanner routePlanner;
    private RedirectStrategy redirectStrategy;
    private Lookup<AuthSchemeProvider> authSchemeRegistry;
    private Lookup<CookieSpecProvider> cookieSpecRegistry;
    private CookieStore cookieStore;
    private CredentialsProvider credentialsProvider;
    private String userAgent;
    private HttpHost proxy;
    private Collection<? extends Header> defaultHeaders;
    private IOReactorConfig defaultIOReactorConfig;
    private ConnectionConfig defaultConnectionConfig;
    private RequestConfig defaultRequestConfig;
    private ThreadFactory threadFactory;
    private NHttpClientEventHandler eventHandler;
    private PublicSuffixMatcher publicSuffixMatcher;
    private boolean systemProperties;
    private boolean cookieManagementDisabled;
    private boolean authCachingDisabled;
    private boolean connectionStateDisabled;
    private int maxConnTotal = 0;
    private int maxConnPerRoute = 0;
    private long connTimeToLive = -1L;
    private TimeUnit connTimeToLiveTimeUnit = TimeUnit.MILLISECONDS;

    public static HttpAsyncClientBuilder create() {
        return new HttpAsyncClientBuilder();
    }

    protected HttpAsyncClientBuilder() {
    }

    public final HttpAsyncClientBuilder setPublicSuffixMatcher(PublicSuffixMatcher publicSuffixMatcher) {
        this.publicSuffixMatcher = publicSuffixMatcher;
        return this;
    }

    public final HttpAsyncClientBuilder setConnectionManager(NHttpClientConnectionManager connManager) {
        this.connManager = connManager;
        return this;
    }

    public final HttpAsyncClientBuilder setConnectionManagerShared(boolean shared) {
        this.connManagerShared = shared;
        return this;
    }

    public final HttpAsyncClientBuilder setSchemePortResolver(SchemePortResolver schemePortResolver) {
        this.schemePortResolver = schemePortResolver;
        return this;
    }

    public final HttpAsyncClientBuilder setMaxConnTotal(int maxConnTotal) {
        this.maxConnTotal = maxConnTotal;
        return this;
    }

    public final HttpAsyncClientBuilder setMaxConnPerRoute(int maxConnPerRoute) {
        this.maxConnPerRoute = maxConnPerRoute;
        return this;
    }

    public final HttpAsyncClientBuilder setConnectionTimeToLive(long connTimeToLive, TimeUnit connTimeToLiveTimeUnit) {
        this.connTimeToLive = connTimeToLive;
        this.connTimeToLiveTimeUnit = connTimeToLiveTimeUnit;
        return this;
    }

    public final HttpAsyncClientBuilder setConnectionReuseStrategy(ConnectionReuseStrategy reuseStrategy) {
        this.reuseStrategy = reuseStrategy;
        return this;
    }

    public final HttpAsyncClientBuilder setKeepAliveStrategy(ConnectionKeepAliveStrategy keepAliveStrategy) {
        this.keepAliveStrategy = keepAliveStrategy;
        return this;
    }

    public final HttpAsyncClientBuilder setUserTokenHandler(UserTokenHandler userTokenHandler) {
        this.userTokenHandler = userTokenHandler;
        return this;
    }

    public final HttpAsyncClientBuilder setTargetAuthenticationStrategy(AuthenticationStrategy targetAuthStrategy) {
        this.targetAuthStrategy = targetAuthStrategy;
        return this;
    }

    public final HttpAsyncClientBuilder setProxyAuthenticationStrategy(AuthenticationStrategy proxyAuthStrategy) {
        this.proxyAuthStrategy = proxyAuthStrategy;
        return this;
    }

    public final HttpAsyncClientBuilder setHttpProcessor(HttpProcessor httpprocessor) {
        this.httpprocessor = httpprocessor;
        return this;
    }

    public final HttpAsyncClientBuilder addInterceptorFirst(HttpResponseInterceptor itcp) {
        if (itcp == null) {
            return this;
        }
        if (this.responseFirst == null) {
            this.responseFirst = new LinkedList();
        }
        this.responseFirst.addFirst(itcp);
        return this;
    }

    public final HttpAsyncClientBuilder addInterceptorLast(HttpResponseInterceptor itcp) {
        if (itcp == null) {
            return this;
        }
        if (this.responseLast == null) {
            this.responseLast = new LinkedList();
        }
        this.responseLast.addLast(itcp);
        return this;
    }

    public final HttpAsyncClientBuilder addInterceptorFirst(HttpRequestInterceptor itcp) {
        if (itcp == null) {
            return this;
        }
        if (this.requestFirst == null) {
            this.requestFirst = new LinkedList();
        }
        this.requestFirst.addFirst(itcp);
        return this;
    }

    public final HttpAsyncClientBuilder addInterceptorLast(HttpRequestInterceptor itcp) {
        if (itcp == null) {
            return this;
        }
        if (this.requestLast == null) {
            this.requestLast = new LinkedList();
        }
        this.requestLast.addLast(itcp);
        return this;
    }

    public final HttpAsyncClientBuilder setRoutePlanner(HttpRoutePlanner routePlanner) {
        this.routePlanner = routePlanner;
        return this;
    }

    public final HttpAsyncClientBuilder setRedirectStrategy(RedirectStrategy redirectStrategy) {
        this.redirectStrategy = redirectStrategy;
        return this;
    }

    public final HttpAsyncClientBuilder setDefaultCookieStore(CookieStore cookieStore) {
        this.cookieStore = cookieStore;
        return this;
    }

    public final HttpAsyncClientBuilder setDefaultCredentialsProvider(CredentialsProvider credentialsProvider) {
        this.credentialsProvider = credentialsProvider;
        return this;
    }

    public final HttpAsyncClientBuilder setDefaultAuthSchemeRegistry(Lookup<AuthSchemeProvider> authSchemeRegistry) {
        this.authSchemeRegistry = authSchemeRegistry;
        return this;
    }

    public final HttpAsyncClientBuilder setDefaultCookieSpecRegistry(Lookup<CookieSpecProvider> cookieSpecRegistry) {
        this.cookieSpecRegistry = cookieSpecRegistry;
        return this;
    }

    public final HttpAsyncClientBuilder setUserAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    public final HttpAsyncClientBuilder setProxy(HttpHost proxy) {
        this.proxy = proxy;
        return this;
    }

    public final HttpAsyncClientBuilder setSSLStrategy(SchemeIOSessionStrategy strategy) {
        this.sslStrategy = strategy;
        return this;
    }

    public final HttpAsyncClientBuilder setSSLContext(SSLContext sslcontext) {
        this.sslcontext = sslcontext;
        return this;
    }

    @Deprecated
    public final HttpAsyncClientBuilder setHostnameVerifier(X509HostnameVerifier hostnameVerifier) {
        this.hostnameVerifier = hostnameVerifier;
        return this;
    }

    public final HttpAsyncClientBuilder setSSLHostnameVerifier(HostnameVerifier hostnameVerifier) {
        this.hostnameVerifier = hostnameVerifier;
        return this;
    }

    public final HttpAsyncClientBuilder setDefaultHeaders(Collection<? extends Header> defaultHeaders) {
        this.defaultHeaders = defaultHeaders;
        return this;
    }

    public final HttpAsyncClientBuilder setDefaultIOReactorConfig(IOReactorConfig config) {
        this.defaultIOReactorConfig = config;
        return this;
    }

    public final HttpAsyncClientBuilder setDefaultConnectionConfig(ConnectionConfig config) {
        this.defaultConnectionConfig = config;
        return this;
    }

    public final HttpAsyncClientBuilder setDefaultRequestConfig(RequestConfig config) {
        this.defaultRequestConfig = config;
        return this;
    }

    public final HttpAsyncClientBuilder setThreadFactory(ThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
        return this;
    }

    public final HttpAsyncClientBuilder setEventHandler(NHttpClientEventHandler eventHandler) {
        this.eventHandler = eventHandler;
        return this;
    }

    public final HttpAsyncClientBuilder disableConnectionState() {
        this.connectionStateDisabled = true;
        return this;
    }

    public final HttpAsyncClientBuilder disableCookieManagement() {
        this.cookieManagementDisabled = true;
        return this;
    }

    public final HttpAsyncClientBuilder disableAuthCaching() {
        this.authCachingDisabled = true;
        return this;
    }

    public final HttpAsyncClientBuilder useSystemProperties() {
        this.systemProperties = true;
        return this;
    }

    private static String[] split(String s) {
        if (TextUtils.isBlank(s)) {
            return null;
        }
        return s.split(" *, *");
    }

    /*
     * WARNING - void declaration
     */
    public CloseableHttpAsyncClient build() {
        void var13_26;
        RequestConfig defaultRequestConfig;
        RedirectStrategy redirectStrategy;
        CredentialsProvider defaultCredentialsProvider;
        CookieStore cookieStore;
        Lookup<CookieSpecProvider> cookieSpecRegistry;
        Lookup<AuthSchemeProvider> authSchemeRegistry;
        HttpRoutePlanner routePlanner;
        HttpProcessor httpprocessor;
        SchemePortResolver schemePortResolver;
        UserTokenHandler userTokenHandler;
        AuthenticationStrategy proxyAuthStrategy;
        AuthenticationStrategy targetAuthStrategy;
        ConnectionKeepAliveStrategy keepAliveStrategy;
        ConnectionReuseStrategy reuseStrategy;
        NHttpClientConnectionManager connManager;
        PublicSuffixMatcher publicSuffixMatcher = this.publicSuffixMatcher;
        if (publicSuffixMatcher == null) {
            publicSuffixMatcher = PublicSuffixMatcherLoader.getDefault();
        }
        if ((connManager = this.connManager) == null) {
            SchemeIOSessionStrategy sslStrategy = this.sslStrategy;
            if (sslStrategy == null) {
                SSLContext sslcontext = this.sslcontext;
                if (sslcontext == null) {
                    sslcontext = this.systemProperties ? SSLContexts.createSystemDefault() : SSLContexts.createDefault();
                }
                String[] supportedProtocols = this.systemProperties ? HttpAsyncClientBuilder.split(System.getProperty("https.protocols")) : null;
                String[] supportedCipherSuites = this.systemProperties ? HttpAsyncClientBuilder.split(System.getProperty("https.cipherSuites")) : null;
                HostnameVerifier hostnameVerifier = this.hostnameVerifier;
                if (hostnameVerifier == null) {
                    hostnameVerifier = new DefaultHostnameVerifier(publicSuffixMatcher);
                }
                sslStrategy = new SSLIOSessionStrategy(sslcontext, supportedProtocols, supportedCipherSuites, hostnameVerifier);
            }
            ConnectingIOReactor ioReactor = IOReactorUtils.create(this.defaultIOReactorConfig != null ? this.defaultIOReactorConfig : IOReactorConfig.DEFAULT, this.threadFactory);
            PoolingNHttpClientConnectionManager poolingmgr = new PoolingNHttpClientConnectionManager(ioReactor, ManagedNHttpClientConnectionFactory.INSTANCE, RegistryBuilder.create().register("http", NoopIOSessionStrategy.INSTANCE).register("https", (NoopIOSessionStrategy)sslStrategy).build(), DefaultSchemePortResolver.INSTANCE, SystemDefaultDnsResolver.INSTANCE, this.connTimeToLive, this.connTimeToLiveTimeUnit);
            if (this.defaultConnectionConfig != null) {
                poolingmgr.setDefaultConnectionConfig(this.defaultConnectionConfig);
            }
            if (this.systemProperties) {
                String s = System.getProperty("http.keepAlive", "true");
                if ("true".equalsIgnoreCase(s)) {
                    s = System.getProperty("http.maxConnections", "5");
                    int max = Integer.parseInt(s);
                    poolingmgr.setDefaultMaxPerRoute(max);
                    poolingmgr.setMaxTotal(2 * max);
                }
            } else {
                if (this.maxConnTotal > 0) {
                    poolingmgr.setMaxTotal(this.maxConnTotal);
                }
                if (this.maxConnPerRoute > 0) {
                    poolingmgr.setDefaultMaxPerRoute(this.maxConnPerRoute);
                }
            }
            connManager = poolingmgr;
        }
        if ((reuseStrategy = this.reuseStrategy) == null) {
            String s;
            reuseStrategy = this.systemProperties ? ("true".equalsIgnoreCase(s = System.getProperty("http.keepAlive", "true")) ? DefaultConnectionReuseStrategy.INSTANCE : NoConnectionReuseStrategy.INSTANCE) : DefaultConnectionReuseStrategy.INSTANCE;
        }
        if ((keepAliveStrategy = this.keepAliveStrategy) == null) {
            keepAliveStrategy = DefaultConnectionKeepAliveStrategy.INSTANCE;
        }
        if ((targetAuthStrategy = this.targetAuthStrategy) == null) {
            targetAuthStrategy = TargetAuthenticationStrategy.INSTANCE;
        }
        if ((proxyAuthStrategy = this.proxyAuthStrategy) == null) {
            proxyAuthStrategy = ProxyAuthenticationStrategy.INSTANCE;
        }
        if ((userTokenHandler = this.userTokenHandler) == null) {
            userTokenHandler = !this.connectionStateDisabled ? DefaultAsyncUserTokenHandler.INSTANCE : NoopUserTokenHandler.INSTANCE;
        }
        if ((schemePortResolver = this.schemePortResolver) == null) {
            schemePortResolver = DefaultSchemePortResolver.INSTANCE;
        }
        if ((httpprocessor = this.httpprocessor) == null) {
            String userAgent = this.userAgent;
            if (userAgent == null) {
                if (this.systemProperties) {
                    userAgent = System.getProperty("http.agent");
                }
                if (userAgent == null) {
                    userAgent = VersionInfo.getUserAgent("Apache-HttpAsyncClient", "org.apache.http.nio.client", this.getClass());
                }
            }
            HttpProcessorBuilder b = HttpProcessorBuilder.create();
            if (this.requestFirst != null) {
                for (HttpRequestInterceptor httpRequestInterceptor : this.requestFirst) {
                    b.addFirst(httpRequestInterceptor);
                }
            }
            if (this.responseFirst != null) {
                for (HttpResponseInterceptor httpResponseInterceptor : this.responseFirst) {
                    b.addFirst(httpResponseInterceptor);
                }
            }
            b.addAll(new RequestDefaultHeaders(this.defaultHeaders), new RequestContent(), new RequestTargetHost(), new RequestClientConnControl(), new RequestUserAgent(userAgent), new RequestExpectContinue());
            if (!this.cookieManagementDisabled) {
                b.add(new RequestAddCookies());
            }
            if (!this.authCachingDisabled) {
                b.add(new RequestAuthCache());
            }
            if (!this.cookieManagementDisabled) {
                b.add(new ResponseProcessCookies());
            }
            if (this.requestLast != null) {
                for (HttpRequestInterceptor httpRequestInterceptor : this.requestLast) {
                    b.addLast(httpRequestInterceptor);
                }
            }
            if (this.responseLast != null) {
                for (HttpResponseInterceptor httpResponseInterceptor : this.responseLast) {
                    b.addLast(httpResponseInterceptor);
                }
            }
            httpprocessor = b.build();
        }
        if ((routePlanner = this.routePlanner) == null) {
            routePlanner = this.proxy != null ? new DefaultProxyRoutePlanner(this.proxy, schemePortResolver) : (this.systemProperties ? new SystemDefaultRoutePlanner(schemePortResolver, ProxySelector.getDefault()) : new DefaultRoutePlanner(schemePortResolver));
        }
        if ((authSchemeRegistry = this.authSchemeRegistry) == null) {
            authSchemeRegistry = RegistryBuilder.create().register("Basic", new BasicSchemeFactory()).register("Digest", (BasicSchemeFactory)((Object)new DigestSchemeFactory())).register("NTLM", (BasicSchemeFactory)((Object)new NTLMSchemeFactory())).register("Negotiate", (BasicSchemeFactory)((Object)new SPNegoSchemeFactory())).register("Kerberos", (BasicSchemeFactory)((Object)new KerberosSchemeFactory())).build();
        }
        if ((cookieSpecRegistry = this.cookieSpecRegistry) == null) {
            DefaultCookieSpecProvider defaultCookieSpecProvider = new DefaultCookieSpecProvider(publicSuffixMatcher);
            RFC6265CookieSpecProvider laxStandardProvider = new RFC6265CookieSpecProvider(RFC6265CookieSpecProvider.CompatibilityLevel.RELAXED, publicSuffixMatcher);
            RFC6265CookieSpecProvider strictStandardProvider = new RFC6265CookieSpecProvider(RFC6265CookieSpecProvider.CompatibilityLevel.STRICT, publicSuffixMatcher);
            cookieSpecRegistry = RegistryBuilder.create().register("default", defaultCookieSpecProvider).register("best-match", defaultCookieSpecProvider).register("compatibility", defaultCookieSpecProvider).register("standard", (DefaultCookieSpecProvider)((Object)laxStandardProvider)).register("standard-strict", (DefaultCookieSpecProvider)((Object)strictStandardProvider)).register("netscape", (DefaultCookieSpecProvider)((Object)new NetscapeDraftSpecProvider())).register("ignoreCookies", (DefaultCookieSpecProvider)((Object)new IgnoreSpecProvider())).build();
        }
        if ((cookieStore = this.cookieStore) == null) {
            BasicCookieStore basicCookieStore = new BasicCookieStore();
        }
        if ((defaultCredentialsProvider = this.credentialsProvider) == null) {
            defaultCredentialsProvider = this.systemProperties ? new SystemDefaultCredentialsProvider() : new BasicCredentialsProvider();
        }
        if ((redirectStrategy = this.redirectStrategy) == null) {
            redirectStrategy = DefaultRedirectStrategy.INSTANCE;
        }
        if ((defaultRequestConfig = this.defaultRequestConfig) == null) {
            defaultRequestConfig = RequestConfig.DEFAULT;
        }
        MainClientExec exec = new MainClientExec(httpprocessor, routePlanner, redirectStrategy, targetAuthStrategy, proxyAuthStrategy, userTokenHandler);
        ThreadFactory threadFactory = null;
        NHttpClientEventHandler eventHandler = null;
        if (!this.connManagerShared) {
            threadFactory = this.threadFactory;
            if (threadFactory == null) {
                threadFactory = Executors.defaultThreadFactory();
            }
            if ((eventHandler = this.eventHandler) == null) {
                eventHandler = new HttpAsyncRequestExecutor();
            }
        }
        return new InternalHttpAsyncClient(connManager, reuseStrategy, keepAliveStrategy, threadFactory, eventHandler, exec, cookieSpecRegistry, authSchemeRegistry, (CookieStore)var13_26, defaultCredentialsProvider, defaultRequestConfig);
    }
}

