/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.client5.http.impl.async;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadFactory;
import org.apache.hc.client5.http.AuthenticationStrategy;
import org.apache.hc.client5.http.DnsResolver;
import org.apache.hc.client5.http.HttpRequestRetryStrategy;
import org.apache.hc.client5.http.SchemePortResolver;
import org.apache.hc.client5.http.async.AsyncExecChainHandler;
import org.apache.hc.client5.http.auth.AuthSchemeFactory;
import org.apache.hc.client5.http.auth.CredentialsProvider;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.cookie.CookieSpecFactory;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.apache.hc.client5.http.impl.ChainElement;
import org.apache.hc.client5.http.impl.CookieSpecSupport;
import org.apache.hc.client5.http.impl.DefaultAuthenticationStrategy;
import org.apache.hc.client5.http.impl.DefaultHttpRequestRetryStrategy;
import org.apache.hc.client5.http.impl.DefaultRedirectStrategy;
import org.apache.hc.client5.http.impl.DefaultSchemePortResolver;
import org.apache.hc.client5.http.impl.async.AsyncConnectExec;
import org.apache.hc.client5.http.impl.async.AsyncExecChainElement;
import org.apache.hc.client5.http.impl.async.AsyncHttpRequestRetryExec;
import org.apache.hc.client5.http.impl.async.AsyncProtocolExec;
import org.apache.hc.client5.http.impl.async.AsyncPushConsumerRegistry;
import org.apache.hc.client5.http.impl.async.AsyncRedirectExec;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.H2AsyncClientEventHandlerFactory;
import org.apache.hc.client5.http.impl.async.H2AsyncMainClientExec;
import org.apache.hc.client5.http.impl.async.InternalH2AsyncClient;
import org.apache.hc.client5.http.impl.async.LoggingExceptionCallback;
import org.apache.hc.client5.http.impl.async.LoggingIOSessionDecorator;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.auth.BasicSchemeFactory;
import org.apache.hc.client5.http.impl.auth.DigestSchemeFactory;
import org.apache.hc.client5.http.impl.auth.KerberosSchemeFactory;
import org.apache.hc.client5.http.impl.auth.NTLMSchemeFactory;
import org.apache.hc.client5.http.impl.auth.SPNegoSchemeFactory;
import org.apache.hc.client5.http.impl.auth.SystemDefaultCredentialsProvider;
import org.apache.hc.client5.http.impl.nio.MultihomeConnectionInitiator;
import org.apache.hc.client5.http.impl.routing.DefaultRoutePlanner;
import org.apache.hc.client5.http.protocol.RedirectStrategy;
import org.apache.hc.client5.http.protocol.RequestAddCookies;
import org.apache.hc.client5.http.protocol.RequestAuthCache;
import org.apache.hc.client5.http.protocol.RequestDefaultHeaders;
import org.apache.hc.client5.http.protocol.RequestExpectContinue;
import org.apache.hc.client5.http.protocol.ResponseProcessCookies;
import org.apache.hc.client5.http.routing.HttpRoutePlanner;
import org.apache.hc.client5.http.ssl.DefaultClientTlsStrategy;
import org.apache.hc.core5.annotation.Internal;
import org.apache.hc.core5.concurrent.DefaultThreadFactory;
import org.apache.hc.core5.function.Callback;
import org.apache.hc.core5.function.Resolver;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpRequestInterceptor;
import org.apache.hc.core5.http.HttpResponseInterceptor;
import org.apache.hc.core5.http.config.CharCodingConfig;
import org.apache.hc.core5.http.config.Lookup;
import org.apache.hc.core5.http.config.NamedElementChain;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.http.nio.AsyncPushConsumer;
import org.apache.hc.core5.http.nio.HandlerFactory;
import org.apache.hc.core5.http.nio.command.ShutdownCommand;
import org.apache.hc.core5.http.nio.ssl.TlsStrategy;
import org.apache.hc.core5.http.protocol.DefaultHttpProcessor;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.http.protocol.HttpProcessor;
import org.apache.hc.core5.http.protocol.HttpProcessorBuilder;
import org.apache.hc.core5.http.protocol.RequestTargetHost;
import org.apache.hc.core5.http.protocol.RequestUserAgent;
import org.apache.hc.core5.http2.config.H2Config;
import org.apache.hc.core5.http2.nio.pool.H2ConnPool;
import org.apache.hc.core5.http2.protocol.H2RequestConnControl;
import org.apache.hc.core5.http2.protocol.H2RequestContent;
import org.apache.hc.core5.http2.protocol.H2RequestTargetHost;
import org.apache.hc.core5.io.CloseMode;
import org.apache.hc.core5.reactor.Command;
import org.apache.hc.core5.reactor.DefaultConnectingIOReactor;
import org.apache.hc.core5.reactor.IOReactorConfig;
import org.apache.hc.core5.reactor.IOSession;
import org.apache.hc.core5.util.Args;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.VersionInfo;

public class H2AsyncClientBuilder {
    private IOReactorConfig ioReactorConfig;
    private H2Config h2Config;
    private CharCodingConfig charCodingConfig;
    private SchemePortResolver schemePortResolver;
    private AuthenticationStrategy targetAuthStrategy;
    private AuthenticationStrategy proxyAuthStrategy;
    private LinkedList<RequestInterceptorEntry> requestInterceptors;
    private LinkedList<ResponseInterceptorEntry> responseInterceptors;
    private LinkedList<ExecInterceptorEntry> execInterceptors;
    private HttpRoutePlanner routePlanner;
    private RedirectStrategy redirectStrategy;
    private HttpRequestRetryStrategy retryStrategy;
    private Lookup<AuthSchemeFactory> authSchemeRegistry;
    private Lookup<CookieSpecFactory> cookieSpecRegistry;
    private CookieStore cookieStore;
    private CredentialsProvider credentialsProvider;
    private String userAgent;
    private Collection<? extends Header> defaultHeaders;
    private RequestConfig defaultRequestConfig;
    private boolean evictIdleConnections;
    private TimeValue maxIdleTime;
    private boolean systemProperties;
    private boolean automaticRetriesDisabled;
    private boolean redirectHandlingDisabled;
    private boolean cookieManagementDisabled;
    private boolean authCachingDisabled;
    private DnsResolver dnsResolver;
    private TlsStrategy tlsStrategy;
    private ThreadFactory threadFactory;
    private List<Closeable> closeables;

    public static H2AsyncClientBuilder create() {
        return new H2AsyncClientBuilder();
    }

    protected H2AsyncClientBuilder() {
    }

    public final H2AsyncClientBuilder setH2Config(H2Config h2Config) {
        this.h2Config = h2Config;
        return this;
    }

    public final H2AsyncClientBuilder setIOReactorConfig(IOReactorConfig ioReactorConfig) {
        this.ioReactorConfig = ioReactorConfig;
        return this;
    }

    public final H2AsyncClientBuilder setCharCodingConfig(CharCodingConfig charCodingConfig) {
        this.charCodingConfig = charCodingConfig;
        return this;
    }

    public final H2AsyncClientBuilder setTargetAuthenticationStrategy(AuthenticationStrategy targetAuthStrategy) {
        this.targetAuthStrategy = targetAuthStrategy;
        return this;
    }

    public final H2AsyncClientBuilder setProxyAuthenticationStrategy(AuthenticationStrategy proxyAuthStrategy) {
        this.proxyAuthStrategy = proxyAuthStrategy;
        return this;
    }

    public final H2AsyncClientBuilder addResponseInterceptorFirst(HttpResponseInterceptor interceptor) {
        Args.notNull(interceptor, "Interceptor");
        if (this.responseInterceptors == null) {
            this.responseInterceptors = new LinkedList();
        }
        this.responseInterceptors.add(new ResponseInterceptorEntry(ResponseInterceptorEntry.Position.FIRST, interceptor));
        return this;
    }

    public final H2AsyncClientBuilder addResponseInterceptorLast(HttpResponseInterceptor interceptor) {
        Args.notNull(interceptor, "Interceptor");
        if (this.responseInterceptors == null) {
            this.responseInterceptors = new LinkedList();
        }
        this.responseInterceptors.add(new ResponseInterceptorEntry(ResponseInterceptorEntry.Position.LAST, interceptor));
        return this;
    }

    public final H2AsyncClientBuilder addExecInterceptorBefore(String existing, String name, AsyncExecChainHandler interceptor) {
        Args.notBlank(existing, "Existing");
        Args.notBlank(name, "Name");
        Args.notNull(interceptor, "Interceptor");
        if (this.execInterceptors == null) {
            this.execInterceptors = new LinkedList();
        }
        this.execInterceptors.add(new ExecInterceptorEntry(ExecInterceptorEntry.Position.BEFORE, name, interceptor, existing));
        return this;
    }

    public final H2AsyncClientBuilder addExecInterceptorAfter(String existing, String name, AsyncExecChainHandler interceptor) {
        Args.notBlank(existing, "Existing");
        Args.notBlank(name, "Name");
        Args.notNull(interceptor, "Interceptor");
        if (this.execInterceptors == null) {
            this.execInterceptors = new LinkedList();
        }
        this.execInterceptors.add(new ExecInterceptorEntry(ExecInterceptorEntry.Position.AFTER, name, interceptor, existing));
        return this;
    }

    public final H2AsyncClientBuilder replaceExecInterceptor(String existing, AsyncExecChainHandler interceptor) {
        Args.notBlank(existing, "Existing");
        Args.notNull(interceptor, "Interceptor");
        if (this.execInterceptors == null) {
            this.execInterceptors = new LinkedList();
        }
        this.execInterceptors.add(new ExecInterceptorEntry(ExecInterceptorEntry.Position.REPLACE, existing, interceptor, existing));
        return this;
    }

    public final H2AsyncClientBuilder addExecInterceptorFirst(String name, AsyncExecChainHandler interceptor) {
        Args.notNull(name, "Name");
        Args.notNull(interceptor, "Interceptor");
        if (this.execInterceptors == null) {
            this.execInterceptors = new LinkedList();
        }
        this.execInterceptors.add(new ExecInterceptorEntry(ExecInterceptorEntry.Position.FIRST, name, interceptor, null));
        return this;
    }

    public final H2AsyncClientBuilder addExecInterceptorLast(String name, AsyncExecChainHandler interceptor) {
        Args.notNull(name, "Name");
        Args.notNull(interceptor, "Interceptor");
        if (this.execInterceptors == null) {
            this.execInterceptors = new LinkedList();
        }
        this.execInterceptors.add(new ExecInterceptorEntry(ExecInterceptorEntry.Position.LAST, name, interceptor, null));
        return this;
    }

    public final H2AsyncClientBuilder addRequestInterceptorFirst(HttpRequestInterceptor interceptor) {
        Args.notNull(interceptor, "Interceptor");
        if (this.requestInterceptors == null) {
            this.requestInterceptors = new LinkedList();
        }
        this.requestInterceptors.add(new RequestInterceptorEntry(RequestInterceptorEntry.Position.FIRST, interceptor));
        return this;
    }

    public final H2AsyncClientBuilder addRequestInterceptorLast(HttpRequestInterceptor interceptor) {
        Args.notNull(interceptor, "Interceptor");
        if (this.requestInterceptors == null) {
            this.requestInterceptors = new LinkedList();
        }
        this.requestInterceptors.add(new RequestInterceptorEntry(RequestInterceptorEntry.Position.LAST, interceptor));
        return this;
    }

    public final H2AsyncClientBuilder setRetryStrategy(HttpRequestRetryStrategy retryStrategy) {
        this.retryStrategy = retryStrategy;
        return this;
    }

    public H2AsyncClientBuilder setRedirectStrategy(RedirectStrategy redirectStrategy) {
        this.redirectStrategy = redirectStrategy;
        return this;
    }

    public final H2AsyncClientBuilder setSchemePortResolver(SchemePortResolver schemePortResolver) {
        this.schemePortResolver = schemePortResolver;
        return this;
    }

    public final H2AsyncClientBuilder setDnsResolver(DnsResolver dnsResolver) {
        this.dnsResolver = dnsResolver;
        return this;
    }

    public final H2AsyncClientBuilder setTlsStrategy(TlsStrategy tlsStrategy) {
        this.tlsStrategy = tlsStrategy;
        return this;
    }

    public final H2AsyncClientBuilder setThreadFactory(ThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
        return this;
    }

    public final H2AsyncClientBuilder setUserAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    public final H2AsyncClientBuilder setDefaultHeaders(Collection<? extends Header> defaultHeaders) {
        this.defaultHeaders = defaultHeaders;
        return this;
    }

    public final H2AsyncClientBuilder setRoutePlanner(HttpRoutePlanner routePlanner) {
        this.routePlanner = routePlanner;
        return this;
    }

    public final H2AsyncClientBuilder setDefaultCredentialsProvider(CredentialsProvider credentialsProvider) {
        this.credentialsProvider = credentialsProvider;
        return this;
    }

    public final H2AsyncClientBuilder setDefaultAuthSchemeRegistry(Lookup<AuthSchemeFactory> authSchemeRegistry) {
        this.authSchemeRegistry = authSchemeRegistry;
        return this;
    }

    public final H2AsyncClientBuilder setDefaultCookieSpecRegistry(Lookup<CookieSpecFactory> cookieSpecRegistry) {
        this.cookieSpecRegistry = cookieSpecRegistry;
        return this;
    }

    public final H2AsyncClientBuilder setDefaultCookieStore(CookieStore cookieStore) {
        this.cookieStore = cookieStore;
        return this;
    }

    public final H2AsyncClientBuilder setDefaultRequestConfig(RequestConfig config) {
        this.defaultRequestConfig = config;
        return this;
    }

    public final H2AsyncClientBuilder useSystemProperties() {
        this.systemProperties = true;
        return this;
    }

    public final H2AsyncClientBuilder disableRedirectHandling() {
        this.redirectHandlingDisabled = true;
        return this;
    }

    public final H2AsyncClientBuilder disableAutomaticRetries() {
        this.automaticRetriesDisabled = true;
        return this;
    }

    public final H2AsyncClientBuilder disableCookieManagement() {
        this.cookieManagementDisabled = true;
        return this;
    }

    public final H2AsyncClientBuilder disableAuthCaching() {
        this.authCachingDisabled = true;
        return this;
    }

    public final H2AsyncClientBuilder evictIdleConnections(TimeValue maxIdleTime) {
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

    /*
     * WARNING - void declaration
     */
    public CloseableHttpAsyncClient build() {
        ArrayList<Closeable> closeablesCopy;
        TlsStrategy tlsStrategyCopy;
        CredentialsProvider credentialsProviderCopy;
        CookieStore cookieStoreCopy;
        Lookup<CookieSpecFactory> cookieSpecRegistryCopy;
        void var7_20;
        HttpRoutePlanner httpRoutePlanner;
        String userAgentCopy;
        AuthenticationStrategy proxyAuthStrategyCopy;
        NamedElementChain<AsyncExecChainHandler> execChainDefinition = new NamedElementChain<AsyncExecChainHandler>();
        execChainDefinition.addLast(new H2AsyncMainClientExec(), ChainElement.MAIN_TRANSPORT.name());
        AuthenticationStrategy targetAuthStrategyCopy = this.targetAuthStrategy;
        if (targetAuthStrategyCopy == null) {
            targetAuthStrategyCopy = DefaultAuthenticationStrategy.INSTANCE;
        }
        if ((proxyAuthStrategyCopy = this.proxyAuthStrategy) == null) {
            proxyAuthStrategyCopy = DefaultAuthenticationStrategy.INSTANCE;
        }
        if ((userAgentCopy = this.userAgent) == null) {
            if (this.systemProperties) {
                userAgentCopy = H2AsyncClientBuilder.getProperty("http.agent", null);
            }
            if (userAgentCopy == null) {
                userAgentCopy = VersionInfo.getSoftwareInfo("Apache-HttpAsyncClient", "org.apache.hc.client5", this.getClass());
            }
        }
        execChainDefinition.addFirst(new AsyncConnectExec(new DefaultHttpProcessor(new RequestTargetHost(), new RequestUserAgent(userAgentCopy)), proxyAuthStrategyCopy), ChainElement.CONNECT.name());
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
        b.addAll(new RequestDefaultHeaders(this.defaultHeaders), new RequestUserAgent(userAgentCopy), new RequestExpectContinue());
        if (!this.cookieManagementDisabled) {
            b.add(new RequestAddCookies());
        }
        if (!this.authCachingDisabled) {
            b.add(new RequestAuthCache());
        }
        if (!this.cookieManagementDisabled) {
            b.add(new ResponseProcessCookies());
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
        execChainDefinition.addFirst(new AsyncProtocolExec(httpProcessor, targetAuthStrategyCopy, proxyAuthStrategyCopy), ChainElement.PROTOCOL.name());
        if (!this.automaticRetriesDisabled) {
            void var7_16;
            HttpRequestRetryStrategy httpRequestRetryStrategy = this.retryStrategy;
            if (httpRequestRetryStrategy == null) {
                DefaultHttpRequestRetryStrategy defaultHttpRequestRetryStrategy = DefaultHttpRequestRetryStrategy.INSTANCE;
            }
            execChainDefinition.addFirst(new AsyncHttpRequestRetryExec((HttpRequestRetryStrategy)var7_16), ChainElement.RETRY.name());
        }
        if ((httpRoutePlanner = this.routePlanner) == null) {
            SchemePortResolver schemePortResolverCopy = this.schemePortResolver;
            if (schemePortResolverCopy == null) {
                schemePortResolverCopy = DefaultSchemePortResolver.INSTANCE;
            }
            DefaultRoutePlanner defaultRoutePlanner = new DefaultRoutePlanner(schemePortResolverCopy);
        }
        if (!this.redirectHandlingDisabled) {
            RedirectStrategy redirectStrategyCopy = this.redirectStrategy;
            if (redirectStrategyCopy == null) {
                redirectStrategyCopy = DefaultRedirectStrategy.INSTANCE;
            }
            execChainDefinition.addFirst(new AsyncRedirectExec((HttpRoutePlanner)var7_20, redirectStrategyCopy), ChainElement.REDIRECT.name());
        }
        final AsyncPushConsumerRegistry pushConsumerRegistry = new AsyncPushConsumerRegistry();
        H2AsyncClientEventHandlerFactory ioEventHandlerFactory = new H2AsyncClientEventHandlerFactory(new DefaultHttpProcessor(new H2RequestContent(), new H2RequestTargetHost(), new H2RequestConnControl()), new HandlerFactory<AsyncPushConsumer>(){

            @Override
            public AsyncPushConsumer create(HttpRequest request, HttpContext context) throws HttpException {
                return pushConsumerRegistry.get(request);
            }
        }, this.h2Config != null ? this.h2Config : H2Config.DEFAULT, this.charCodingConfig != null ? this.charCodingConfig : CharCodingConfig.DEFAULT);
        DefaultConnectingIOReactor ioReactor = new DefaultConnectingIOReactor(ioEventHandlerFactory, this.ioReactorConfig != null ? this.ioReactorConfig : IOReactorConfig.DEFAULT, this.threadFactory != null ? this.threadFactory : new DefaultThreadFactory("httpclient-dispatch", true), LoggingIOSessionDecorator.INSTANCE, LoggingExceptionCallback.INSTANCE, null, new Callback<IOSession>(){

            @Override
            public void execute(IOSession ioSession) {
                ioSession.enqueue(new ShutdownCommand(CloseMode.GRACEFUL), Command.Priority.IMMEDIATE);
            }
        });
        if (this.execInterceptors != null) {
            for (ExecInterceptorEntry entry : this.execInterceptors) {
                switch (entry.position) {
                    case AFTER: {
                        execChainDefinition.addAfter(entry.existing, entry.interceptor, entry.name);
                        break;
                    }
                    case BEFORE: {
                        execChainDefinition.addBefore(entry.existing, entry.interceptor, entry.name);
                        break;
                    }
                    case REPLACE: {
                        execChainDefinition.replace(entry.existing, entry.interceptor);
                        break;
                    }
                    case FIRST: {
                        execChainDefinition.addFirst(entry.interceptor, entry.name);
                        break;
                    }
                    case LAST: {
                        execChainDefinition.addBefore(ChainElement.MAIN_TRANSPORT.name(), entry.interceptor, entry.name);
                    }
                }
            }
        }
        this.customizeExecChain(execChainDefinition);
        AsyncExecChainElement execChain = null;
        for (NamedElementChain.Node current = execChainDefinition.getLast(); current != null; current = current.getPrevious()) {
            execChain = new AsyncExecChainElement((AsyncExecChainHandler)current.getValue(), execChain);
        }
        Lookup<AuthSchemeFactory> authSchemeRegistryCopy = this.authSchemeRegistry;
        if (authSchemeRegistryCopy == null) {
            authSchemeRegistryCopy = RegistryBuilder.create().register("Basic", BasicSchemeFactory.INSTANCE).register("Digest", (BasicSchemeFactory)((Object)DigestSchemeFactory.INSTANCE)).register("NTLM", (BasicSchemeFactory)((Object)NTLMSchemeFactory.INSTANCE)).register("Negotiate", (BasicSchemeFactory)((Object)SPNegoSchemeFactory.DEFAULT)).register("Kerberos", (BasicSchemeFactory)((Object)KerberosSchemeFactory.DEFAULT)).build();
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
        if ((tlsStrategyCopy = this.tlsStrategy) == null) {
            tlsStrategyCopy = this.systemProperties ? DefaultClientTlsStrategy.getSystemDefault() : DefaultClientTlsStrategy.getDefault();
        }
        MultihomeConnectionInitiator connectionInitiator = new MultihomeConnectionInitiator(ioReactor, this.dnsResolver);
        H2ConnPool connPool = new H2ConnPool(connectionInitiator, new Resolver<HttpHost, InetSocketAddress>(){

            @Override
            public InetSocketAddress resolve(HttpHost host) {
                return null;
            }
        }, tlsStrategyCopy);
        ArrayList<Closeable> arrayList = closeablesCopy = this.closeables != null ? new ArrayList<Closeable>(this.closeables) : null;
        if (closeablesCopy == null) {
            closeablesCopy = new ArrayList(1);
        }
        if (this.evictIdleConnections) {
            final IdleConnectionEvictor connectionEvictor = new IdleConnectionEvictor(connPool, this.maxIdleTime != null ? this.maxIdleTime : TimeValue.ofSeconds(30L));
            closeablesCopy.add(new Closeable(){

                @Override
                public void close() throws IOException {
                    connectionEvictor.shutdown();
                }
            });
            connectionEvictor.start();
        }
        closeablesCopy.add(connPool);
        return new InternalH2AsyncClient(ioReactor, execChain, pushConsumerRegistry, this.threadFactory != null ? this.threadFactory : new DefaultThreadFactory("httpclient-main", true), connPool, (HttpRoutePlanner)var7_20, cookieSpecRegistryCopy, authSchemeRegistryCopy, cookieStoreCopy, credentialsProviderCopy, this.defaultRequestConfig, closeablesCopy);
    }

    private static String getProperty(final String key, final String defaultValue) {
        return AccessController.doPrivileged(new PrivilegedAction<String>(){

            @Override
            public String run() {
                return System.getProperty(key, defaultValue);
            }
        });
    }

    static class IdleConnectionEvictor
    implements Closeable {
        private final Thread thread;

        public IdleConnectionEvictor(final H2ConnPool connPool, final TimeValue maxIdleTime) {
            this.thread = new DefaultThreadFactory("idle-connection-evictor", true).newThread(new Runnable(){

                @Override
                public void run() {
                    try {
                        while (!Thread.currentThread().isInterrupted()) {
                            maxIdleTime.sleep();
                            connPool.closeIdle(maxIdleTime);
                        }
                    }
                    catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                }
            });
        }

        public void start() {
            this.thread.start();
        }

        public void shutdown() {
            this.thread.interrupt();
        }

        @Override
        public void close() throws IOException {
            this.shutdown();
        }
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

