/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.annotation.Internal
 *  org.apache.hc.core5.http.ConnectionReuseStrategy
 *  org.apache.hc.core5.http.Header
 *  org.apache.hc.core5.http.HttpHost
 *  org.apache.hc.core5.http.HttpRequestInterceptor
 *  org.apache.hc.core5.http.HttpResponseInterceptor
 *  org.apache.hc.core5.http.config.Lookup
 *  org.apache.hc.core5.http.config.NamedElementChain
 *  org.apache.hc.core5.http.config.NamedElementChain$Node
 *  org.apache.hc.core5.http.config.Registry
 *  org.apache.hc.core5.http.config.RegistryBuilder
 *  org.apache.hc.core5.http.impl.io.HttpRequestExecutor
 *  org.apache.hc.core5.http.protocol.DefaultHttpProcessor
 *  org.apache.hc.core5.http.protocol.HttpProcessor
 *  org.apache.hc.core5.http.protocol.HttpProcessorBuilder
 *  org.apache.hc.core5.http.protocol.RequestContent
 *  org.apache.hc.core5.http.protocol.RequestTargetHost
 *  org.apache.hc.core5.http.protocol.RequestUserAgent
 *  org.apache.hc.core5.pool.ConnPoolControl
 *  org.apache.hc.core5.util.Args
 *  org.apache.hc.core5.util.TimeValue
 *  org.apache.hc.core5.util.Timeout
 *  org.apache.hc.core5.util.VersionInfo
 */
package org.apache.hc.client5.http.impl.classic;

import java.io.Closeable;
import java.net.ProxySelector;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.hc.client5.http.AuthenticationStrategy;
import org.apache.hc.client5.http.ConnectionKeepAliveStrategy;
import org.apache.hc.client5.http.HttpRequestRetryStrategy;
import org.apache.hc.client5.http.SchemePortResolver;
import org.apache.hc.client5.http.UserTokenHandler;
import org.apache.hc.client5.http.auth.AuthSchemeFactory;
import org.apache.hc.client5.http.auth.CredentialsProvider;
import org.apache.hc.client5.http.classic.BackoffManager;
import org.apache.hc.client5.http.classic.ConnectionBackoffStrategy;
import org.apache.hc.client5.http.classic.ExecChainHandler;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.cookie.CookieSpecFactory;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.apache.hc.client5.http.entity.InputStreamFactory;
import org.apache.hc.client5.http.impl.ChainElement;
import org.apache.hc.client5.http.impl.CookieSpecSupport;
import org.apache.hc.client5.http.impl.DefaultAuthenticationStrategy;
import org.apache.hc.client5.http.impl.DefaultClientConnectionReuseStrategy;
import org.apache.hc.client5.http.impl.DefaultConnectionKeepAliveStrategy;
import org.apache.hc.client5.http.impl.DefaultHttpRequestRetryStrategy;
import org.apache.hc.client5.http.impl.DefaultRedirectStrategy;
import org.apache.hc.client5.http.impl.DefaultSchemePortResolver;
import org.apache.hc.client5.http.impl.DefaultUserTokenHandler;
import org.apache.hc.client5.http.impl.IdleConnectionEvictor;
import org.apache.hc.client5.http.impl.NoopUserTokenHandler;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.auth.BasicSchemeFactory;
import org.apache.hc.client5.http.impl.auth.DigestSchemeFactory;
import org.apache.hc.client5.http.impl.auth.KerberosSchemeFactory;
import org.apache.hc.client5.http.impl.auth.NTLMSchemeFactory;
import org.apache.hc.client5.http.impl.auth.SPNegoSchemeFactory;
import org.apache.hc.client5.http.impl.auth.SystemDefaultCredentialsProvider;
import org.apache.hc.client5.http.impl.classic.BackoffStrategyExec;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.ConnectExec;
import org.apache.hc.client5.http.impl.classic.ContentCompressionExec;
import org.apache.hc.client5.http.impl.classic.ExecChainElement;
import org.apache.hc.client5.http.impl.classic.HttpRequestRetryExec;
import org.apache.hc.client5.http.impl.classic.InternalHttpClient;
import org.apache.hc.client5.http.impl.classic.MainClientExec;
import org.apache.hc.client5.http.impl.classic.ProtocolExec;
import org.apache.hc.client5.http.impl.classic.RedirectExec;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.impl.routing.DefaultProxyRoutePlanner;
import org.apache.hc.client5.http.impl.routing.DefaultRoutePlanner;
import org.apache.hc.client5.http.impl.routing.SystemDefaultRoutePlanner;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.protocol.RedirectStrategy;
import org.apache.hc.client5.http.protocol.RequestAddCookies;
import org.apache.hc.client5.http.protocol.RequestClientConnControl;
import org.apache.hc.client5.http.protocol.RequestDefaultHeaders;
import org.apache.hc.client5.http.protocol.RequestExpectContinue;
import org.apache.hc.client5.http.protocol.ResponseProcessCookies;
import org.apache.hc.client5.http.routing.HttpRoutePlanner;
import org.apache.hc.core5.annotation.Internal;
import org.apache.hc.core5.http.ConnectionReuseStrategy;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.HttpRequestInterceptor;
import org.apache.hc.core5.http.HttpResponseInterceptor;
import org.apache.hc.core5.http.config.Lookup;
import org.apache.hc.core5.http.config.NamedElementChain;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.http.impl.io.HttpRequestExecutor;
import org.apache.hc.core5.http.protocol.DefaultHttpProcessor;
import org.apache.hc.core5.http.protocol.HttpProcessor;
import org.apache.hc.core5.http.protocol.HttpProcessorBuilder;
import org.apache.hc.core5.http.protocol.RequestContent;
import org.apache.hc.core5.http.protocol.RequestTargetHost;
import org.apache.hc.core5.http.protocol.RequestUserAgent;
import org.apache.hc.core5.pool.ConnPoolControl;
import org.apache.hc.core5.util.Args;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.apache.hc.core5.util.VersionInfo;

public class HttpClientBuilder {
    private HttpRequestExecutor requestExec;
    private HttpClientConnectionManager connManager;
    private boolean connManagerShared;
    private SchemePortResolver schemePortResolver;
    private ConnectionReuseStrategy reuseStrategy;
    private ConnectionKeepAliveStrategy keepAliveStrategy;
    private AuthenticationStrategy targetAuthStrategy;
    private AuthenticationStrategy proxyAuthStrategy;
    private UserTokenHandler userTokenHandler;
    private LinkedList<RequestInterceptorEntry> requestInterceptors;
    private LinkedList<ResponseInterceptorEntry> responseInterceptors;
    private LinkedList<ExecInterceptorEntry> execInterceptors;
    private HttpRequestRetryStrategy retryStrategy;
    private HttpRoutePlanner routePlanner;
    private RedirectStrategy redirectStrategy;
    private ConnectionBackoffStrategy connectionBackoffStrategy;
    private BackoffManager backoffManager;
    private Lookup<AuthSchemeFactory> authSchemeRegistry;
    private Lookup<CookieSpecFactory> cookieSpecRegistry;
    private LinkedHashMap<String, InputStreamFactory> contentDecoderMap;
    private CookieStore cookieStore;
    private CredentialsProvider credentialsProvider;
    private String userAgent;
    private HttpHost proxy;
    private Collection<? extends Header> defaultHeaders;
    private RequestConfig defaultRequestConfig;
    private boolean evictExpiredConnections;
    private boolean evictIdleConnections;
    private TimeValue maxIdleTime;
    private boolean systemProperties;
    private boolean redirectHandlingDisabled;
    private boolean automaticRetriesDisabled;
    private boolean contentCompressionDisabled;
    private boolean cookieManagementDisabled;
    private boolean authCachingDisabled;
    private boolean connectionStateDisabled;
    private boolean defaultUserAgentDisabled;
    private List<Closeable> closeables;

    public static HttpClientBuilder create() {
        return new HttpClientBuilder();
    }

    protected HttpClientBuilder() {
    }

    public final HttpClientBuilder setRequestExecutor(HttpRequestExecutor requestExec) {
        this.requestExec = requestExec;
        return this;
    }

    public final HttpClientBuilder setConnectionManager(HttpClientConnectionManager connManager) {
        this.connManager = connManager;
        return this;
    }

    public final HttpClientBuilder setConnectionManagerShared(boolean shared) {
        this.connManagerShared = shared;
        return this;
    }

    public final HttpClientBuilder setConnectionReuseStrategy(ConnectionReuseStrategy reuseStrategy) {
        this.reuseStrategy = reuseStrategy;
        return this;
    }

    public final HttpClientBuilder setKeepAliveStrategy(ConnectionKeepAliveStrategy keepAliveStrategy) {
        this.keepAliveStrategy = keepAliveStrategy;
        return this;
    }

    public final HttpClientBuilder setTargetAuthenticationStrategy(AuthenticationStrategy targetAuthStrategy) {
        this.targetAuthStrategy = targetAuthStrategy;
        return this;
    }

    public final HttpClientBuilder setProxyAuthenticationStrategy(AuthenticationStrategy proxyAuthStrategy) {
        this.proxyAuthStrategy = proxyAuthStrategy;
        return this;
    }

    public final HttpClientBuilder setUserTokenHandler(UserTokenHandler userTokenHandler) {
        this.userTokenHandler = userTokenHandler;
        return this;
    }

    public final HttpClientBuilder disableConnectionState() {
        this.connectionStateDisabled = true;
        return this;
    }

    public final HttpClientBuilder setSchemePortResolver(SchemePortResolver schemePortResolver) {
        this.schemePortResolver = schemePortResolver;
        return this;
    }

    public final HttpClientBuilder setUserAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    public final HttpClientBuilder setDefaultHeaders(Collection<? extends Header> defaultHeaders) {
        this.defaultHeaders = defaultHeaders;
        return this;
    }

    public final HttpClientBuilder addResponseInterceptorFirst(HttpResponseInterceptor interceptor) {
        Args.notNull((Object)interceptor, (String)"Interceptor");
        if (this.responseInterceptors == null) {
            this.responseInterceptors = new LinkedList();
        }
        this.responseInterceptors.add(new ResponseInterceptorEntry(ResponseInterceptorEntry.Position.FIRST, interceptor));
        return this;
    }

    public final HttpClientBuilder addResponseInterceptorLast(HttpResponseInterceptor interceptor) {
        Args.notNull((Object)interceptor, (String)"Interceptor");
        if (this.responseInterceptors == null) {
            this.responseInterceptors = new LinkedList();
        }
        this.responseInterceptors.add(new ResponseInterceptorEntry(ResponseInterceptorEntry.Position.LAST, interceptor));
        return this;
    }

    public final HttpClientBuilder addRequestInterceptorFirst(HttpRequestInterceptor interceptor) {
        Args.notNull((Object)interceptor, (String)"Interceptor");
        if (this.requestInterceptors == null) {
            this.requestInterceptors = new LinkedList();
        }
        this.requestInterceptors.add(new RequestInterceptorEntry(RequestInterceptorEntry.Position.FIRST, interceptor));
        return this;
    }

    public final HttpClientBuilder addRequestInterceptorLast(HttpRequestInterceptor interceptor) {
        Args.notNull((Object)interceptor, (String)"Interceptor");
        if (this.requestInterceptors == null) {
            this.requestInterceptors = new LinkedList();
        }
        this.requestInterceptors.add(new RequestInterceptorEntry(RequestInterceptorEntry.Position.LAST, interceptor));
        return this;
    }

    public final HttpClientBuilder addExecInterceptorBefore(String existing, String name, ExecChainHandler interceptor) {
        Args.notBlank((CharSequence)existing, (String)"Existing");
        Args.notBlank((CharSequence)name, (String)"Name");
        Args.notNull((Object)interceptor, (String)"Interceptor");
        if (this.execInterceptors == null) {
            this.execInterceptors = new LinkedList();
        }
        this.execInterceptors.add(new ExecInterceptorEntry(ExecInterceptorEntry.Position.BEFORE, name, interceptor, existing));
        return this;
    }

    public final HttpClientBuilder addExecInterceptorAfter(String existing, String name, ExecChainHandler interceptor) {
        Args.notBlank((CharSequence)existing, (String)"Existing");
        Args.notBlank((CharSequence)name, (String)"Name");
        Args.notNull((Object)interceptor, (String)"Interceptor");
        if (this.execInterceptors == null) {
            this.execInterceptors = new LinkedList();
        }
        this.execInterceptors.add(new ExecInterceptorEntry(ExecInterceptorEntry.Position.AFTER, name, interceptor, existing));
        return this;
    }

    public final HttpClientBuilder replaceExecInterceptor(String existing, ExecChainHandler interceptor) {
        Args.notBlank((CharSequence)existing, (String)"Existing");
        Args.notNull((Object)interceptor, (String)"Interceptor");
        if (this.execInterceptors == null) {
            this.execInterceptors = new LinkedList();
        }
        this.execInterceptors.add(new ExecInterceptorEntry(ExecInterceptorEntry.Position.REPLACE, existing, interceptor, existing));
        return this;
    }

    public final HttpClientBuilder addExecInterceptorFirst(String name, ExecChainHandler interceptor) {
        Args.notNull((Object)name, (String)"Name");
        Args.notNull((Object)interceptor, (String)"Interceptor");
        if (this.execInterceptors == null) {
            this.execInterceptors = new LinkedList();
        }
        this.execInterceptors.add(new ExecInterceptorEntry(ExecInterceptorEntry.Position.FIRST, name, interceptor, null));
        return this;
    }

    public final HttpClientBuilder addExecInterceptorLast(String name, ExecChainHandler interceptor) {
        Args.notNull((Object)name, (String)"Name");
        Args.notNull((Object)interceptor, (String)"Interceptor");
        if (this.execInterceptors == null) {
            this.execInterceptors = new LinkedList();
        }
        this.execInterceptors.add(new ExecInterceptorEntry(ExecInterceptorEntry.Position.LAST, name, interceptor, null));
        return this;
    }

    public final HttpClientBuilder disableCookieManagement() {
        this.cookieManagementDisabled = true;
        return this;
    }

    public final HttpClientBuilder disableContentCompression() {
        this.contentCompressionDisabled = true;
        return this;
    }

    public final HttpClientBuilder disableAuthCaching() {
        this.authCachingDisabled = true;
        return this;
    }

    public final HttpClientBuilder setRetryStrategy(HttpRequestRetryStrategy retryStrategy) {
        this.retryStrategy = retryStrategy;
        return this;
    }

    public final HttpClientBuilder disableAutomaticRetries() {
        this.automaticRetriesDisabled = true;
        return this;
    }

    public final HttpClientBuilder setProxy(HttpHost proxy) {
        this.proxy = proxy;
        return this;
    }

    public final HttpClientBuilder setRoutePlanner(HttpRoutePlanner routePlanner) {
        this.routePlanner = routePlanner;
        return this;
    }

    public final HttpClientBuilder setRedirectStrategy(RedirectStrategy redirectStrategy) {
        this.redirectStrategy = redirectStrategy;
        return this;
    }

    public final HttpClientBuilder disableRedirectHandling() {
        this.redirectHandlingDisabled = true;
        return this;
    }

    public final HttpClientBuilder setConnectionBackoffStrategy(ConnectionBackoffStrategy connectionBackoffStrategy) {
        this.connectionBackoffStrategy = connectionBackoffStrategy;
        return this;
    }

    public final HttpClientBuilder setBackoffManager(BackoffManager backoffManager) {
        this.backoffManager = backoffManager;
        return this;
    }

    public final HttpClientBuilder setDefaultCookieStore(CookieStore cookieStore) {
        this.cookieStore = cookieStore;
        return this;
    }

    public final HttpClientBuilder setDefaultCredentialsProvider(CredentialsProvider credentialsProvider) {
        this.credentialsProvider = credentialsProvider;
        return this;
    }

    public final HttpClientBuilder setDefaultAuthSchemeRegistry(Lookup<AuthSchemeFactory> authSchemeRegistry) {
        this.authSchemeRegistry = authSchemeRegistry;
        return this;
    }

    public final HttpClientBuilder setDefaultCookieSpecRegistry(Lookup<CookieSpecFactory> cookieSpecRegistry) {
        this.cookieSpecRegistry = cookieSpecRegistry;
        return this;
    }

    public final HttpClientBuilder setContentDecoderRegistry(LinkedHashMap<String, InputStreamFactory> contentDecoderMap) {
        this.contentDecoderMap = contentDecoderMap;
        return this;
    }

    public final HttpClientBuilder setDefaultRequestConfig(RequestConfig config) {
        this.defaultRequestConfig = config;
        return this;
    }

    public final HttpClientBuilder useSystemProperties() {
        this.systemProperties = true;
        return this;
    }

    public final HttpClientBuilder evictExpiredConnections() {
        this.evictExpiredConnections = true;
        return this;
    }

    public final HttpClientBuilder evictIdleConnections(TimeValue maxIdleTime) {
        this.evictIdleConnections = true;
        this.maxIdleTime = maxIdleTime;
        return this;
    }

    public final HttpClientBuilder disableDefaultUserAgent() {
        this.defaultUserAgentDisabled = true;
        return this;
    }

    @Internal
    protected void customizeExecChain(NamedElementChain<ExecChainHandler> execChainDefinition) {
    }

    @Internal
    protected void addCloseable(Closeable closeable) {
        if (closeable == null) {
            return;
        }
        if (this.closeables == null) {
            this.closeables = new ArrayList<Closeable>();
        }
        this.closeables.add(closeable);
    }

    public CloseableHttpClient build() {
        ArrayList<Closeable> closeablesCopy;
        CredentialsProvider defaultCredentialsProvider;
        CookieStore defaultCookieStore;
        Lookup<CookieSpecFactory> cookieSpecRegistryCopy;
        HttpRoutePlanner routePlannerCopy;
        String userAgentCopy;
        UserTokenHandler userTokenHandlerCopy;
        AuthenticationStrategy proxyAuthStrategyCopy;
        AuthenticationStrategy targetAuthStrategyCopy;
        ConnectionKeepAliveStrategy keepAliveStrategyCopy;
        Object reuseStrategyCopy;
        HttpClientConnectionManager connManagerCopy;
        HttpRequestExecutor requestExecCopy = this.requestExec;
        if (requestExecCopy == null) {
            requestExecCopy = new HttpRequestExecutor();
        }
        if ((connManagerCopy = this.connManager) == null) {
            connManagerCopy = PoolingHttpClientConnectionManagerBuilder.create().build();
        }
        if ((reuseStrategyCopy = this.reuseStrategy) == null) {
            String s;
            reuseStrategyCopy = this.systemProperties ? ("true".equalsIgnoreCase(s = System.getProperty("http.keepAlive", "true")) ? DefaultClientConnectionReuseStrategy.INSTANCE : (request, response, context) -> false) : DefaultClientConnectionReuseStrategy.INSTANCE;
        }
        if ((keepAliveStrategyCopy = this.keepAliveStrategy) == null) {
            keepAliveStrategyCopy = DefaultConnectionKeepAliveStrategy.INSTANCE;
        }
        if ((targetAuthStrategyCopy = this.targetAuthStrategy) == null) {
            targetAuthStrategyCopy = DefaultAuthenticationStrategy.INSTANCE;
        }
        if ((proxyAuthStrategyCopy = this.proxyAuthStrategy) == null) {
            proxyAuthStrategyCopy = DefaultAuthenticationStrategy.INSTANCE;
        }
        if ((userTokenHandlerCopy = this.userTokenHandler) == null) {
            userTokenHandlerCopy = !this.connectionStateDisabled ? DefaultUserTokenHandler.INSTANCE : NoopUserTokenHandler.INSTANCE;
        }
        if ((userAgentCopy = this.userAgent) == null) {
            if (this.systemProperties) {
                userAgentCopy = System.getProperty("http.agent");
            }
            if (userAgentCopy == null && !this.defaultUserAgentDisabled) {
                userAgentCopy = VersionInfo.getSoftwareInfo((String)"Apache-HttpClient", (String)"org.apache.hc.client5", this.getClass());
            }
        }
        HttpProcessorBuilder b = HttpProcessorBuilder.create();
        if (this.requestInterceptors != null) {
            for (RequestInterceptorEntry requestInterceptorEntry : this.requestInterceptors) {
                if (requestInterceptorEntry.position != RequestInterceptorEntry.Position.FIRST) continue;
                b.addFirst(requestInterceptorEntry.interceptor);
            }
        }
        if (this.responseInterceptors != null) {
            for (ResponseInterceptorEntry responseInterceptorEntry : this.responseInterceptors) {
                if (responseInterceptorEntry.position != ResponseInterceptorEntry.Position.FIRST) continue;
                b.addFirst(responseInterceptorEntry.interceptor);
            }
        }
        b.addAll(new HttpRequestInterceptor[]{new RequestDefaultHeaders(this.defaultHeaders), new RequestContent(), new RequestTargetHost(), new RequestClientConnControl(), new RequestUserAgent(userAgentCopy), new RequestExpectContinue()});
        if (!this.cookieManagementDisabled) {
            b.add((HttpRequestInterceptor)RequestAddCookies.INSTANCE);
        }
        if (!this.cookieManagementDisabled) {
            b.add((HttpResponseInterceptor)ResponseProcessCookies.INSTANCE);
        }
        if (this.requestInterceptors != null) {
            for (RequestInterceptorEntry requestInterceptorEntry : this.requestInterceptors) {
                if (requestInterceptorEntry.position != RequestInterceptorEntry.Position.LAST) continue;
                b.addLast(requestInterceptorEntry.interceptor);
            }
        }
        if (this.responseInterceptors != null) {
            for (ResponseInterceptorEntry responseInterceptorEntry : this.responseInterceptors) {
                if (responseInterceptorEntry.position != ResponseInterceptorEntry.Position.LAST) continue;
                b.addLast(responseInterceptorEntry.interceptor);
            }
        }
        HttpProcessor httpProcessor = b.build();
        NamedElementChain namedElementChain = new NamedElementChain();
        namedElementChain.addLast((Object)new MainClientExec(connManagerCopy, httpProcessor, (ConnectionReuseStrategy)reuseStrategyCopy, keepAliveStrategyCopy, userTokenHandlerCopy), ChainElement.MAIN_TRANSPORT.name());
        namedElementChain.addFirst((Object)new ConnectExec((ConnectionReuseStrategy)reuseStrategyCopy, (HttpProcessor)new DefaultHttpProcessor(new HttpRequestInterceptor[]{new RequestTargetHost(), new RequestUserAgent(userAgentCopy)}), proxyAuthStrategyCopy, this.schemePortResolver != null ? this.schemePortResolver : DefaultSchemePortResolver.INSTANCE, this.authCachingDisabled), ChainElement.CONNECT.name());
        namedElementChain.addFirst((Object)new ProtocolExec(targetAuthStrategyCopy, proxyAuthStrategyCopy, this.schemePortResolver != null ? this.schemePortResolver : DefaultSchemePortResolver.INSTANCE, this.authCachingDisabled), ChainElement.PROTOCOL.name());
        if (!this.automaticRetriesDisabled) {
            HttpRequestRetryStrategy retryStrategyCopy = this.retryStrategy;
            if (retryStrategyCopy == null) {
                retryStrategyCopy = DefaultHttpRequestRetryStrategy.INSTANCE;
            }
            namedElementChain.addFirst((Object)new HttpRequestRetryExec(retryStrategyCopy), ChainElement.RETRY.name());
        }
        if ((routePlannerCopy = this.routePlanner) == null) {
            SchemePortResolver schemePortResolverCopy = this.schemePortResolver;
            if (schemePortResolverCopy == null) {
                schemePortResolverCopy = DefaultSchemePortResolver.INSTANCE;
            }
            routePlannerCopy = this.proxy != null ? new DefaultProxyRoutePlanner(this.proxy, schemePortResolverCopy) : (this.systemProperties ? new SystemDefaultRoutePlanner(schemePortResolverCopy, ProxySelector.getDefault()) : new DefaultRoutePlanner(schemePortResolverCopy));
        }
        if (!this.contentCompressionDisabled) {
            if (this.contentDecoderMap != null) {
                ArrayList<String> encodings = new ArrayList<String>(this.contentDecoderMap.keySet());
                RegistryBuilder b2 = RegistryBuilder.create();
                for (Map.Entry<String, InputStreamFactory> entry : this.contentDecoderMap.entrySet()) {
                    b2.register(entry.getKey(), (Object)entry.getValue());
                }
                Registry decoderRegistry = b2.build();
                namedElementChain.addFirst((Object)new ContentCompressionExec(encodings, (Lookup<InputStreamFactory>)decoderRegistry, true), ChainElement.COMPRESS.name());
            } else {
                namedElementChain.addFirst((Object)new ContentCompressionExec(true), ChainElement.COMPRESS.name());
            }
        }
        if (!this.redirectHandlingDisabled) {
            Object redirectStrategyCopy = this.redirectStrategy;
            if (redirectStrategyCopy == null) {
                redirectStrategyCopy = DefaultRedirectStrategy.INSTANCE;
            }
            namedElementChain.addFirst((Object)new RedirectExec(routePlannerCopy, (RedirectStrategy)redirectStrategyCopy), ChainElement.REDIRECT.name());
        }
        if (this.backoffManager != null && this.connectionBackoffStrategy != null) {
            namedElementChain.addFirst((Object)new BackoffStrategyExec(this.connectionBackoffStrategy, this.backoffManager), ChainElement.BACK_OFF.name());
        }
        if (this.execInterceptors != null) {
            for (ExecInterceptorEntry entry : this.execInterceptors) {
                switch (entry.position) {
                    case AFTER: {
                        namedElementChain.addAfter(entry.existing, (Object)entry.interceptor, entry.name);
                        break;
                    }
                    case BEFORE: {
                        namedElementChain.addBefore(entry.existing, (Object)entry.interceptor, entry.name);
                        break;
                    }
                    case REPLACE: {
                        namedElementChain.replace(entry.existing, (Object)entry.interceptor);
                        break;
                    }
                    case FIRST: {
                        namedElementChain.addFirst((Object)entry.interceptor, entry.name);
                        break;
                    }
                    case LAST: {
                        namedElementChain.addBefore(ChainElement.MAIN_TRANSPORT.name(), (Object)entry.interceptor, entry.name);
                    }
                }
            }
        }
        this.customizeExecChain((NamedElementChain<ExecChainHandler>)namedElementChain);
        ExecChainElement execChain = null;
        for (NamedElementChain.Node current = namedElementChain.getLast(); current != null; current = current.getPrevious()) {
            execChain = new ExecChainElement((ExecChainHandler)current.getValue(), execChain);
        }
        Registry authSchemeRegistryCopy = this.authSchemeRegistry;
        if (authSchemeRegistryCopy == null) {
            authSchemeRegistryCopy = RegistryBuilder.create().register("Basic", (Object)BasicSchemeFactory.INSTANCE).register("Digest", (Object)DigestSchemeFactory.INSTANCE).register("NTLM", (Object)NTLMSchemeFactory.INSTANCE).register("Negotiate", (Object)SPNegoSchemeFactory.DEFAULT).register("Kerberos", (Object)KerberosSchemeFactory.DEFAULT).build();
        }
        if ((cookieSpecRegistryCopy = this.cookieSpecRegistry) == null) {
            cookieSpecRegistryCopy = CookieSpecSupport.createDefault();
        }
        if ((defaultCookieStore = this.cookieStore) == null) {
            defaultCookieStore = new BasicCookieStore();
        }
        if ((defaultCredentialsProvider = this.credentialsProvider) == null) {
            defaultCredentialsProvider = this.systemProperties ? new SystemDefaultCredentialsProvider() : new BasicCredentialsProvider();
        }
        ArrayList<Closeable> arrayList = closeablesCopy = this.closeables != null ? new ArrayList<Closeable>(this.closeables) : null;
        if (!this.connManagerShared) {
            if (closeablesCopy == null) {
                closeablesCopy = new ArrayList(1);
            }
            if ((this.evictExpiredConnections || this.evictIdleConnections) && connManagerCopy instanceof ConnPoolControl) {
                IdleConnectionEvictor connectionEvictor = new IdleConnectionEvictor((ConnPoolControl)connManagerCopy, this.maxIdleTime, this.maxIdleTime);
                closeablesCopy.add(() -> {
                    connectionEvictor.shutdown();
                    try {
                        connectionEvictor.awaitTermination(Timeout.ofSeconds((long)1L));
                    }
                    catch (InterruptedException interrupted) {
                        Thread.currentThread().interrupt();
                    }
                });
                connectionEvictor.start();
            }
            closeablesCopy.add((Closeable)((Object)connManagerCopy));
        }
        return new InternalHttpClient(connManagerCopy, requestExecCopy, execChain, routePlannerCopy, cookieSpecRegistryCopy, (Lookup<AuthSchemeFactory>)authSchemeRegistryCopy, defaultCookieStore, defaultCredentialsProvider, this.defaultRequestConfig != null ? this.defaultRequestConfig : RequestConfig.DEFAULT, closeablesCopy);
    }

    private static class ExecInterceptorEntry {
        final Position position;
        final String name;
        final ExecChainHandler interceptor;
        final String existing;

        private ExecInterceptorEntry(Position position, String name, ExecChainHandler interceptor, String existing) {
            this.position = position;
            this.name = name;
            this.interceptor = interceptor;
            this.existing = existing;
        }

        static enum Position {
            BEFORE,
            AFTER,
            REPLACE,
            FIRST,
            LAST;

        }
    }

    private static class ResponseInterceptorEntry {
        final Position position;
        final HttpResponseInterceptor interceptor;

        private ResponseInterceptorEntry(Position position, HttpResponseInterceptor interceptor) {
            this.position = position;
            this.interceptor = interceptor;
        }

        static enum Position {
            FIRST,
            LAST;

        }
    }

    private static class RequestInterceptorEntry {
        final Position position;
        final HttpRequestInterceptor interceptor;

        private RequestInterceptorEntry(Position position, HttpRequestInterceptor interceptor) {
            this.position = position;
            this.interceptor = interceptor;
        }

        static enum Position {
            FIRST,
            LAST;

        }
    }
}

