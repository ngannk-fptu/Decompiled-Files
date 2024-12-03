/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.annotation.Internal
 *  org.apache.hc.core5.concurrent.DefaultThreadFactory
 *  org.apache.hc.core5.function.Callback
 *  org.apache.hc.core5.function.Decorator
 *  org.apache.hc.core5.http.ConnectionReuseStrategy
 *  org.apache.hc.core5.http.Header
 *  org.apache.hc.core5.http.HttpHost
 *  org.apache.hc.core5.http.HttpRequestInterceptor
 *  org.apache.hc.core5.http.HttpResponseInterceptor
 *  org.apache.hc.core5.http.config.CharCodingConfig
 *  org.apache.hc.core5.http.config.Http1Config
 *  org.apache.hc.core5.http.config.Lookup
 *  org.apache.hc.core5.http.config.NamedElementChain
 *  org.apache.hc.core5.http.config.NamedElementChain$Node
 *  org.apache.hc.core5.http.config.Registry
 *  org.apache.hc.core5.http.config.RegistryBuilder
 *  org.apache.hc.core5.http.nio.AsyncPushConsumer
 *  org.apache.hc.core5.http.nio.HandlerFactory
 *  org.apache.hc.core5.http.nio.command.ShutdownCommand
 *  org.apache.hc.core5.http.protocol.DefaultHttpProcessor
 *  org.apache.hc.core5.http.protocol.HttpProcessor
 *  org.apache.hc.core5.http.protocol.HttpProcessorBuilder
 *  org.apache.hc.core5.http.protocol.RequestTargetHost
 *  org.apache.hc.core5.http.protocol.RequestUserAgent
 *  org.apache.hc.core5.http2.HttpVersionPolicy
 *  org.apache.hc.core5.http2.config.H2Config
 *  org.apache.hc.core5.http2.protocol.H2RequestConnControl
 *  org.apache.hc.core5.http2.protocol.H2RequestContent
 *  org.apache.hc.core5.http2.protocol.H2RequestTargetHost
 *  org.apache.hc.core5.io.CloseMode
 *  org.apache.hc.core5.pool.ConnPoolControl
 *  org.apache.hc.core5.reactor.Command
 *  org.apache.hc.core5.reactor.Command$Priority
 *  org.apache.hc.core5.reactor.DefaultConnectingIOReactor
 *  org.apache.hc.core5.reactor.IOEventHandlerFactory
 *  org.apache.hc.core5.reactor.IOReactorConfig
 *  org.apache.hc.core5.reactor.IOSession
 *  org.apache.hc.core5.reactor.IOSessionListener
 *  org.apache.hc.core5.util.Args
 *  org.apache.hc.core5.util.TimeValue
 *  org.apache.hc.core5.util.VersionInfo
 */
package org.apache.hc.client5.http.impl.async;

import java.io.Closeable;
import java.net.ProxySelector;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadFactory;
import org.apache.hc.client5.http.AuthenticationStrategy;
import org.apache.hc.client5.http.ConnectionKeepAliveStrategy;
import org.apache.hc.client5.http.HttpRequestRetryStrategy;
import org.apache.hc.client5.http.SchemePortResolver;
import org.apache.hc.client5.http.UserTokenHandler;
import org.apache.hc.client5.http.async.AsyncExecChainHandler;
import org.apache.hc.client5.http.auth.AuthSchemeFactory;
import org.apache.hc.client5.http.auth.CredentialsProvider;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.config.TlsConfig;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.cookie.CookieSpecFactory;
import org.apache.hc.client5.http.cookie.CookieStore;
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
import org.apache.hc.client5.http.impl.async.AsyncConnectExec;
import org.apache.hc.client5.http.impl.async.AsyncExecChainElement;
import org.apache.hc.client5.http.impl.async.AsyncHttpRequestRetryExec;
import org.apache.hc.client5.http.impl.async.AsyncProtocolExec;
import org.apache.hc.client5.http.impl.async.AsyncPushConsumerRegistry;
import org.apache.hc.client5.http.impl.async.AsyncRedirectExec;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClientProtocolNegotiationStarter;
import org.apache.hc.client5.http.impl.async.HttpAsyncMainClientExec;
import org.apache.hc.client5.http.impl.async.InternalHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.LoggingExceptionCallback;
import org.apache.hc.client5.http.impl.async.LoggingIOSessionDecorator;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.auth.BasicSchemeFactory;
import org.apache.hc.client5.http.impl.auth.DigestSchemeFactory;
import org.apache.hc.client5.http.impl.auth.KerberosSchemeFactory;
import org.apache.hc.client5.http.impl.auth.NTLMSchemeFactory;
import org.apache.hc.client5.http.impl.auth.SPNegoSchemeFactory;
import org.apache.hc.client5.http.impl.auth.SystemDefaultCredentialsProvider;
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManagerBuilder;
import org.apache.hc.client5.http.impl.routing.DefaultProxyRoutePlanner;
import org.apache.hc.client5.http.impl.routing.DefaultRoutePlanner;
import org.apache.hc.client5.http.impl.routing.SystemDefaultRoutePlanner;
import org.apache.hc.client5.http.nio.AsyncClientConnectionManager;
import org.apache.hc.client5.http.protocol.RedirectStrategy;
import org.apache.hc.client5.http.protocol.RequestAddCookies;
import org.apache.hc.client5.http.protocol.RequestDefaultHeaders;
import org.apache.hc.client5.http.protocol.RequestExpectContinue;
import org.apache.hc.client5.http.protocol.ResponseProcessCookies;
import org.apache.hc.client5.http.routing.HttpRoutePlanner;
import org.apache.hc.core5.annotation.Internal;
import org.apache.hc.core5.concurrent.DefaultThreadFactory;
import org.apache.hc.core5.function.Callback;
import org.apache.hc.core5.function.Decorator;
import org.apache.hc.core5.http.ConnectionReuseStrategy;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.HttpRequestInterceptor;
import org.apache.hc.core5.http.HttpResponseInterceptor;
import org.apache.hc.core5.http.config.CharCodingConfig;
import org.apache.hc.core5.http.config.Http1Config;
import org.apache.hc.core5.http.config.Lookup;
import org.apache.hc.core5.http.config.NamedElementChain;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.http.nio.AsyncPushConsumer;
import org.apache.hc.core5.http.nio.HandlerFactory;
import org.apache.hc.core5.http.nio.command.ShutdownCommand;
import org.apache.hc.core5.http.protocol.DefaultHttpProcessor;
import org.apache.hc.core5.http.protocol.HttpProcessor;
import org.apache.hc.core5.http.protocol.HttpProcessorBuilder;
import org.apache.hc.core5.http.protocol.RequestTargetHost;
import org.apache.hc.core5.http.protocol.RequestUserAgent;
import org.apache.hc.core5.http2.HttpVersionPolicy;
import org.apache.hc.core5.http2.config.H2Config;
import org.apache.hc.core5.http2.protocol.H2RequestConnControl;
import org.apache.hc.core5.http2.protocol.H2RequestContent;
import org.apache.hc.core5.http2.protocol.H2RequestTargetHost;
import org.apache.hc.core5.io.CloseMode;
import org.apache.hc.core5.pool.ConnPoolControl;
import org.apache.hc.core5.reactor.Command;
import org.apache.hc.core5.reactor.DefaultConnectingIOReactor;
import org.apache.hc.core5.reactor.IOEventHandlerFactory;
import org.apache.hc.core5.reactor.IOReactorConfig;
import org.apache.hc.core5.reactor.IOSession;
import org.apache.hc.core5.reactor.IOSessionListener;
import org.apache.hc.core5.util.Args;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.VersionInfo;

public class HttpAsyncClientBuilder {
    @Deprecated
    private TlsConfig tlsConfig;
    private AsyncClientConnectionManager connManager;
    private boolean connManagerShared;
    private IOReactorConfig ioReactorConfig;
    private IOSessionListener ioSessionListener;
    private Callback<Exception> ioReactorExceptionCallback;
    private Http1Config h1Config;
    private H2Config h2Config;
    private CharCodingConfig charCodingConfig;
    private SchemePortResolver schemePortResolver;
    private ConnectionKeepAliveStrategy keepAliveStrategy;
    private UserTokenHandler userTokenHandler;
    private AuthenticationStrategy targetAuthStrategy;
    private AuthenticationStrategy proxyAuthStrategy;
    private Decorator<IOSession> ioSessionDecorator;
    private LinkedList<RequestInterceptorEntry> requestInterceptors;
    private LinkedList<ResponseInterceptorEntry> responseInterceptors;
    private LinkedList<ExecInterceptorEntry> execInterceptors;
    private HttpRoutePlanner routePlanner;
    private RedirectStrategy redirectStrategy;
    private HttpRequestRetryStrategy retryStrategy;
    private ConnectionReuseStrategy reuseStrategy;
    private Lookup<AuthSchemeFactory> authSchemeRegistry;
    private Lookup<CookieSpecFactory> cookieSpecRegistry;
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
    private boolean automaticRetriesDisabled;
    private boolean redirectHandlingDisabled;
    private boolean cookieManagementDisabled;
    private boolean authCachingDisabled;
    private boolean connectionStateDisabled;
    private ThreadFactory threadFactory;
    private List<Closeable> closeables;

    public static HttpAsyncClientBuilder create() {
        return new HttpAsyncClientBuilder();
    }

    protected HttpAsyncClientBuilder() {
    }

    @Deprecated
    public final HttpAsyncClientBuilder setVersionPolicy(HttpVersionPolicy versionPolicy) {
        this.tlsConfig = versionPolicy != null ? TlsConfig.custom().setVersionPolicy(versionPolicy).build() : null;
        return this;
    }

    public final HttpAsyncClientBuilder setHttp1Config(Http1Config h1Config) {
        this.h1Config = h1Config;
        return this;
    }

    public final HttpAsyncClientBuilder setH2Config(H2Config h2Config) {
        this.h2Config = h2Config;
        return this;
    }

    public final HttpAsyncClientBuilder setConnectionManager(AsyncClientConnectionManager connManager) {
        this.connManager = connManager;
        return this;
    }

    public final HttpAsyncClientBuilder setConnectionManagerShared(boolean shared) {
        this.connManagerShared = shared;
        return this;
    }

    public final HttpAsyncClientBuilder setIOReactorConfig(IOReactorConfig ioReactorConfig) {
        this.ioReactorConfig = ioReactorConfig;
        return this;
    }

    public final HttpAsyncClientBuilder setIOSessionListener(IOSessionListener ioSessionListener) {
        this.ioSessionListener = ioSessionListener;
        return this;
    }

    public final HttpAsyncClientBuilder setIoReactorExceptionCallback(Callback<Exception> ioReactorExceptionCallback) {
        this.ioReactorExceptionCallback = ioReactorExceptionCallback;
        return this;
    }

    public final HttpAsyncClientBuilder setCharCodingConfig(CharCodingConfig charCodingConfig) {
        this.charCodingConfig = charCodingConfig;
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

    public final HttpAsyncClientBuilder setIoSessionDecorator(Decorator<IOSession> ioSessionDecorator) {
        this.ioSessionDecorator = ioSessionDecorator;
        return this;
    }

    public final HttpAsyncClientBuilder addResponseInterceptorFirst(HttpResponseInterceptor interceptor) {
        Args.notNull((Object)interceptor, (String)"Interceptor");
        if (this.responseInterceptors == null) {
            this.responseInterceptors = new LinkedList();
        }
        this.responseInterceptors.add(new ResponseInterceptorEntry(ResponseInterceptorEntry.Position.FIRST, interceptor));
        return this;
    }

    public final HttpAsyncClientBuilder addResponseInterceptorLast(HttpResponseInterceptor interceptor) {
        Args.notNull((Object)interceptor, (String)"Interceptor");
        if (this.responseInterceptors == null) {
            this.responseInterceptors = new LinkedList();
        }
        this.responseInterceptors.add(new ResponseInterceptorEntry(ResponseInterceptorEntry.Position.LAST, interceptor));
        return this;
    }

    public final HttpAsyncClientBuilder addExecInterceptorBefore(String existing, String name, AsyncExecChainHandler interceptor) {
        Args.notBlank((CharSequence)existing, (String)"Existing");
        Args.notBlank((CharSequence)name, (String)"Name");
        Args.notNull((Object)interceptor, (String)"Interceptor");
        if (this.execInterceptors == null) {
            this.execInterceptors = new LinkedList();
        }
        this.execInterceptors.add(new ExecInterceptorEntry(ExecInterceptorEntry.Position.BEFORE, name, interceptor, existing));
        return this;
    }

    public final HttpAsyncClientBuilder addExecInterceptorAfter(String existing, String name, AsyncExecChainHandler interceptor) {
        Args.notBlank((CharSequence)existing, (String)"Existing");
        Args.notBlank((CharSequence)name, (String)"Name");
        Args.notNull((Object)interceptor, (String)"Interceptor");
        if (this.execInterceptors == null) {
            this.execInterceptors = new LinkedList();
        }
        this.execInterceptors.add(new ExecInterceptorEntry(ExecInterceptorEntry.Position.AFTER, name, interceptor, existing));
        return this;
    }

    public final HttpAsyncClientBuilder replaceExecInterceptor(String existing, AsyncExecChainHandler interceptor) {
        Args.notBlank((CharSequence)existing, (String)"Existing");
        Args.notNull((Object)interceptor, (String)"Interceptor");
        if (this.execInterceptors == null) {
            this.execInterceptors = new LinkedList();
        }
        this.execInterceptors.add(new ExecInterceptorEntry(ExecInterceptorEntry.Position.REPLACE, existing, interceptor, existing));
        return this;
    }

    public final HttpAsyncClientBuilder addExecInterceptorFirst(String name, AsyncExecChainHandler interceptor) {
        Args.notNull((Object)name, (String)"Name");
        Args.notNull((Object)interceptor, (String)"Interceptor");
        if (this.execInterceptors == null) {
            this.execInterceptors = new LinkedList();
        }
        this.execInterceptors.add(new ExecInterceptorEntry(ExecInterceptorEntry.Position.FIRST, name, interceptor, null));
        return this;
    }

    public final HttpAsyncClientBuilder addExecInterceptorLast(String name, AsyncExecChainHandler interceptor) {
        Args.notNull((Object)name, (String)"Name");
        Args.notNull((Object)interceptor, (String)"Interceptor");
        if (this.execInterceptors == null) {
            this.execInterceptors = new LinkedList();
        }
        this.execInterceptors.add(new ExecInterceptorEntry(ExecInterceptorEntry.Position.LAST, name, interceptor, null));
        return this;
    }

    public final HttpAsyncClientBuilder addRequestInterceptorFirst(HttpRequestInterceptor interceptor) {
        Args.notNull((Object)interceptor, (String)"Interceptor");
        if (this.requestInterceptors == null) {
            this.requestInterceptors = new LinkedList();
        }
        this.requestInterceptors.add(new RequestInterceptorEntry(RequestInterceptorEntry.Position.FIRST, interceptor));
        return this;
    }

    public final HttpAsyncClientBuilder addRequestInterceptorLast(HttpRequestInterceptor interceptor) {
        Args.notNull((Object)interceptor, (String)"Interceptor");
        if (this.requestInterceptors == null) {
            this.requestInterceptors = new LinkedList();
        }
        this.requestInterceptors.add(new RequestInterceptorEntry(RequestInterceptorEntry.Position.LAST, interceptor));
        return this;
    }

    public final HttpAsyncClientBuilder setRetryStrategy(HttpRequestRetryStrategy retryStrategy) {
        this.retryStrategy = retryStrategy;
        return this;
    }

    public HttpAsyncClientBuilder setRedirectStrategy(RedirectStrategy redirectStrategy) {
        this.redirectStrategy = redirectStrategy;
        return this;
    }

    public final HttpAsyncClientBuilder setSchemePortResolver(SchemePortResolver schemePortResolver) {
        this.schemePortResolver = schemePortResolver;
        return this;
    }

    public final HttpAsyncClientBuilder setThreadFactory(ThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
        return this;
    }

    public final HttpAsyncClientBuilder setUserAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    public final HttpAsyncClientBuilder setDefaultHeaders(Collection<? extends Header> defaultHeaders) {
        this.defaultHeaders = defaultHeaders;
        return this;
    }

    public final HttpAsyncClientBuilder setProxy(HttpHost proxy) {
        this.proxy = proxy;
        return this;
    }

    public final HttpAsyncClientBuilder setRoutePlanner(HttpRoutePlanner routePlanner) {
        this.routePlanner = routePlanner;
        return this;
    }

    public final HttpAsyncClientBuilder setDefaultCredentialsProvider(CredentialsProvider credentialsProvider) {
        this.credentialsProvider = credentialsProvider;
        return this;
    }

    public final HttpAsyncClientBuilder setDefaultAuthSchemeRegistry(Lookup<AuthSchemeFactory> authSchemeRegistry) {
        this.authSchemeRegistry = authSchemeRegistry;
        return this;
    }

    public final HttpAsyncClientBuilder setDefaultCookieSpecRegistry(Lookup<CookieSpecFactory> cookieSpecRegistry) {
        this.cookieSpecRegistry = cookieSpecRegistry;
        return this;
    }

    public final HttpAsyncClientBuilder setDefaultCookieStore(CookieStore cookieStore) {
        this.cookieStore = cookieStore;
        return this;
    }

    public final HttpAsyncClientBuilder setDefaultRequestConfig(RequestConfig config) {
        this.defaultRequestConfig = config;
        return this;
    }

    public final HttpAsyncClientBuilder useSystemProperties() {
        this.systemProperties = true;
        return this;
    }

    public final HttpAsyncClientBuilder disableConnectionState() {
        this.connectionStateDisabled = true;
        return this;
    }

    public final HttpAsyncClientBuilder disableRedirectHandling() {
        this.redirectHandlingDisabled = true;
        return this;
    }

    public final HttpAsyncClientBuilder disableAutomaticRetries() {
        this.automaticRetriesDisabled = true;
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

    public final HttpAsyncClientBuilder evictExpiredConnections() {
        this.evictExpiredConnections = true;
        return this;
    }

    public final HttpAsyncClientBuilder evictIdleConnections(TimeValue maxIdleTime) {
        this.evictIdleConnections = true;
        this.maxIdleTime = maxIdleTime;
        return this;
    }

    @Internal
    protected void customizeExecChain(NamedElementChain<AsyncExecChainHandler> execChainDefinition) {
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

    public CloseableHttpAsyncClient build() {
        CredentialsProvider credentialsProviderCopy;
        CookieStore cookieStoreCopy;
        Lookup<CookieSpecFactory> cookieSpecRegistryCopy;
        Object reuseStrategyCopy;
        ArrayList<Closeable> closeablesCopy;
        HttpRoutePlanner routePlannerCopy;
        String userAgentCopy;
        AuthenticationStrategy proxyAuthStrategyCopy;
        AuthenticationStrategy targetAuthStrategyCopy;
        UserTokenHandler userTokenHandlerCopy;
        ConnectionKeepAliveStrategy keepAliveStrategyCopy;
        AsyncClientConnectionManager connManagerCopy = this.connManager;
        if (connManagerCopy == null) {
            connManagerCopy = PoolingAsyncClientConnectionManagerBuilder.create().build();
        }
        if ((keepAliveStrategyCopy = this.keepAliveStrategy) == null) {
            keepAliveStrategyCopy = DefaultConnectionKeepAliveStrategy.INSTANCE;
        }
        if ((userTokenHandlerCopy = this.userTokenHandler) == null) {
            userTokenHandlerCopy = !this.connectionStateDisabled ? DefaultUserTokenHandler.INSTANCE : NoopUserTokenHandler.INSTANCE;
        }
        if ((targetAuthStrategyCopy = this.targetAuthStrategy) == null) {
            targetAuthStrategyCopy = DefaultAuthenticationStrategy.INSTANCE;
        }
        if ((proxyAuthStrategyCopy = this.proxyAuthStrategy) == null) {
            proxyAuthStrategyCopy = DefaultAuthenticationStrategy.INSTANCE;
        }
        if ((userAgentCopy = this.userAgent) == null) {
            if (this.systemProperties) {
                userAgentCopy = this.getProperty("http.agent", null);
            }
            if (userAgentCopy == null) {
                userAgentCopy = VersionInfo.getSoftwareInfo((String)"Apache-HttpAsyncClient", (String)"org.apache.hc.client5", this.getClass());
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
        b.addAll(new HttpRequestInterceptor[]{new RequestDefaultHeaders(this.defaultHeaders), new RequestUserAgent(userAgentCopy), new RequestExpectContinue(), new H2RequestContent(), new H2RequestTargetHost(), new H2RequestConnControl()});
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
        namedElementChain.addLast((Object)new HttpAsyncMainClientExec(httpProcessor, keepAliveStrategyCopy, userTokenHandlerCopy), ChainElement.MAIN_TRANSPORT.name());
        namedElementChain.addFirst((Object)new AsyncConnectExec((HttpProcessor)new DefaultHttpProcessor(new HttpRequestInterceptor[]{new RequestTargetHost(), new RequestUserAgent(userAgentCopy)}), proxyAuthStrategyCopy, this.schemePortResolver != null ? this.schemePortResolver : DefaultSchemePortResolver.INSTANCE, this.authCachingDisabled), ChainElement.CONNECT.name());
        namedElementChain.addFirst((Object)new AsyncProtocolExec(targetAuthStrategyCopy, proxyAuthStrategyCopy, this.schemePortResolver != null ? this.schemePortResolver : DefaultSchemePortResolver.INSTANCE, this.authCachingDisabled), ChainElement.PROTOCOL.name());
        if (!this.automaticRetriesDisabled) {
            HttpRequestRetryStrategy retryStrategyCopy = this.retryStrategy;
            if (retryStrategyCopy == null) {
                retryStrategyCopy = DefaultHttpRequestRetryStrategy.INSTANCE;
            }
            namedElementChain.addFirst((Object)new AsyncHttpRequestRetryExec(retryStrategyCopy), ChainElement.RETRY.name());
        }
        if ((routePlannerCopy = this.routePlanner) == null) {
            SchemePortResolver schemePortResolverCopy = this.schemePortResolver;
            if (schemePortResolverCopy == null) {
                schemePortResolverCopy = DefaultSchemePortResolver.INSTANCE;
            }
            if (this.proxy != null) {
                routePlannerCopy = new DefaultProxyRoutePlanner(this.proxy, schemePortResolverCopy);
            } else if (this.systemProperties) {
                ProxySelector defaultProxySelector = AccessController.doPrivileged(ProxySelector::getDefault);
                routePlannerCopy = new SystemDefaultRoutePlanner(schemePortResolverCopy, defaultProxySelector);
            } else {
                routePlannerCopy = new DefaultRoutePlanner(schemePortResolverCopy);
            }
        }
        if (!this.redirectHandlingDisabled) {
            RedirectStrategy redirectStrategyCopy = this.redirectStrategy;
            if (redirectStrategyCopy == null) {
                redirectStrategyCopy = DefaultRedirectStrategy.INSTANCE;
            }
            namedElementChain.addFirst((Object)new AsyncRedirectExec(routePlannerCopy, redirectStrategyCopy), ChainElement.REDIRECT.name());
        }
        ArrayList<Closeable> arrayList = closeablesCopy = this.closeables != null ? new ArrayList<Closeable>(this.closeables) : null;
        if (!this.connManagerShared) {
            if (closeablesCopy == null) {
                closeablesCopy = new ArrayList(1);
            }
            if ((this.evictExpiredConnections || this.evictIdleConnections) && connManagerCopy instanceof ConnPoolControl) {
                IdleConnectionEvictor connectionEvictor = new IdleConnectionEvictor((ConnPoolControl)connManagerCopy, this.maxIdleTime, this.maxIdleTime);
                closeablesCopy.add(connectionEvictor::shutdown);
                connectionEvictor.start();
            }
            closeablesCopy.add((Closeable)((Object)connManagerCopy));
        }
        if ((reuseStrategyCopy = this.reuseStrategy) == null) {
            String s;
            reuseStrategyCopy = this.systemProperties ? ("true".equalsIgnoreCase(s = this.getProperty("http.keepAlive", "true")) ? DefaultClientConnectionReuseStrategy.INSTANCE : (request, response, context) -> false) : DefaultClientConnectionReuseStrategy.INSTANCE;
        }
        AsyncPushConsumerRegistry pushConsumerRegistry = new AsyncPushConsumerRegistry();
        HttpAsyncClientProtocolNegotiationStarter ioEventHandlerFactory = new HttpAsyncClientProtocolNegotiationStarter(HttpProcessorBuilder.create().build(), (HandlerFactory<AsyncPushConsumer>)((HandlerFactory)(request, context) -> pushConsumerRegistry.get(request)), this.h2Config != null ? this.h2Config : H2Config.DEFAULT, this.h1Config != null ? this.h1Config : Http1Config.DEFAULT, this.charCodingConfig != null ? this.charCodingConfig : CharCodingConfig.DEFAULT, (ConnectionReuseStrategy)reuseStrategyCopy);
        DefaultConnectingIOReactor ioReactor = new DefaultConnectingIOReactor((IOEventHandlerFactory)ioEventHandlerFactory, this.ioReactorConfig != null ? this.ioReactorConfig : IOReactorConfig.DEFAULT, this.threadFactory != null ? this.threadFactory : new DefaultThreadFactory("httpclient-dispatch", true), this.ioSessionDecorator != null ? this.ioSessionDecorator : LoggingIOSessionDecorator.INSTANCE, this.ioReactorExceptionCallback != null ? this.ioReactorExceptionCallback : LoggingExceptionCallback.INSTANCE, this.ioSessionListener, ioSession -> ioSession.enqueue((Command)new ShutdownCommand(CloseMode.GRACEFUL), Command.Priority.IMMEDIATE));
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
        this.customizeExecChain((NamedElementChain<AsyncExecChainHandler>)namedElementChain);
        AsyncExecChainElement execChain = null;
        for (NamedElementChain.Node current = namedElementChain.getLast(); current != null; current = current.getPrevious()) {
            execChain = new AsyncExecChainElement((AsyncExecChainHandler)current.getValue(), execChain);
        }
        Registry authSchemeRegistryCopy = this.authSchemeRegistry;
        if (authSchemeRegistryCopy == null) {
            authSchemeRegistryCopy = RegistryBuilder.create().register("Basic", (Object)BasicSchemeFactory.INSTANCE).register("Digest", (Object)DigestSchemeFactory.INSTANCE).register("NTLM", (Object)NTLMSchemeFactory.INSTANCE).register("Negotiate", (Object)SPNegoSchemeFactory.DEFAULT).register("Kerberos", (Object)KerberosSchemeFactory.DEFAULT).build();
        }
        if ((cookieSpecRegistryCopy = this.cookieSpecRegistry) == null) {
            cookieSpecRegistryCopy = CookieSpecSupport.createDefault();
        }
        if ((cookieStoreCopy = this.cookieStore) == null) {
            cookieStoreCopy = new BasicCookieStore();
        }
        if ((credentialsProviderCopy = this.credentialsProvider) == null) {
            credentialsProviderCopy = this.systemProperties ? new SystemDefaultCredentialsProvider() : new BasicCredentialsProvider();
        }
        return new InternalHttpAsyncClient(ioReactor, execChain, pushConsumerRegistry, this.threadFactory != null ? this.threadFactory : new DefaultThreadFactory("httpclient-main", true), connManagerCopy, routePlannerCopy, this.tlsConfig, cookieSpecRegistryCopy, (Lookup<AuthSchemeFactory>)authSchemeRegistryCopy, cookieStoreCopy, credentialsProviderCopy, this.defaultRequestConfig, closeablesCopy);
    }

    private String getProperty(String key, String defaultValue) {
        return AccessController.doPrivileged(() -> System.getProperty(key, defaultValue));
    }

    private static class ExecInterceptorEntry {
        final Position position;
        final String name;
        final AsyncExecChainHandler interceptor;
        final String existing;

        private ExecInterceptorEntry(Position position, String name, AsyncExecChainHandler interceptor, String existing) {
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

