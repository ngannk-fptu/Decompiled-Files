/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.http.ClassicHttpRequest
 *  org.apache.hc.core5.http.ClassicHttpResponse
 *  org.apache.hc.core5.http.ConnectionReuseStrategy
 *  org.apache.hc.core5.http.HttpEntity
 *  org.apache.hc.core5.http.HttpException
 *  org.apache.hc.core5.http.HttpHost
 *  org.apache.hc.core5.http.HttpRequest
 *  org.apache.hc.core5.http.HttpRequestInterceptor
 *  org.apache.hc.core5.http.HttpResponse
 *  org.apache.hc.core5.http.Method
 *  org.apache.hc.core5.http.config.CharCodingConfig
 *  org.apache.hc.core5.http.config.Http1Config
 *  org.apache.hc.core5.http.config.Lookup
 *  org.apache.hc.core5.http.config.RegistryBuilder
 *  org.apache.hc.core5.http.impl.io.HttpRequestExecutor
 *  org.apache.hc.core5.http.io.HttpClientConnection
 *  org.apache.hc.core5.http.io.HttpConnectionFactory
 *  org.apache.hc.core5.http.io.entity.EntityUtils
 *  org.apache.hc.core5.http.message.BasicClassicHttpRequest
 *  org.apache.hc.core5.http.message.StatusLine
 *  org.apache.hc.core5.http.protocol.BasicHttpContext
 *  org.apache.hc.core5.http.protocol.DefaultHttpProcessor
 *  org.apache.hc.core5.http.protocol.HttpContext
 *  org.apache.hc.core5.http.protocol.HttpProcessor
 *  org.apache.hc.core5.http.protocol.RequestTargetHost
 *  org.apache.hc.core5.http.protocol.RequestUserAgent
 *  org.apache.hc.core5.util.Args
 */
package org.apache.hc.client5.http.impl.classic;

import java.io.IOException;
import java.net.Socket;
import org.apache.hc.client5.http.AuthenticationStrategy;
import org.apache.hc.client5.http.HttpRoute;
import org.apache.hc.client5.http.RouteInfo;
import org.apache.hc.client5.http.auth.AuthExchange;
import org.apache.hc.client5.http.auth.AuthSchemeFactory;
import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.ChallengeType;
import org.apache.hc.client5.http.auth.Credentials;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.DefaultAuthenticationStrategy;
import org.apache.hc.client5.http.impl.DefaultClientConnectionReuseStrategy;
import org.apache.hc.client5.http.impl.TunnelRefusedException;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.auth.BasicSchemeFactory;
import org.apache.hc.client5.http.impl.auth.DigestSchemeFactory;
import org.apache.hc.client5.http.impl.auth.HttpAuthenticator;
import org.apache.hc.client5.http.impl.auth.KerberosSchemeFactory;
import org.apache.hc.client5.http.impl.auth.NTLMSchemeFactory;
import org.apache.hc.client5.http.impl.auth.SPNegoSchemeFactory;
import org.apache.hc.client5.http.impl.io.ManagedHttpClientConnectionFactory;
import org.apache.hc.client5.http.io.ManagedHttpClientConnection;
import org.apache.hc.client5.http.protocol.RequestClientConnControl;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ConnectionReuseStrategy;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpRequestInterceptor;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.Method;
import org.apache.hc.core5.http.config.CharCodingConfig;
import org.apache.hc.core5.http.config.Http1Config;
import org.apache.hc.core5.http.config.Lookup;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.http.impl.io.HttpRequestExecutor;
import org.apache.hc.core5.http.io.HttpClientConnection;
import org.apache.hc.core5.http.io.HttpConnectionFactory;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.message.BasicClassicHttpRequest;
import org.apache.hc.core5.http.message.StatusLine;
import org.apache.hc.core5.http.protocol.BasicHttpContext;
import org.apache.hc.core5.http.protocol.DefaultHttpProcessor;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.http.protocol.HttpProcessor;
import org.apache.hc.core5.http.protocol.RequestTargetHost;
import org.apache.hc.core5.http.protocol.RequestUserAgent;
import org.apache.hc.core5.util.Args;

public class ProxyClient {
    private final HttpConnectionFactory<ManagedHttpClientConnection> connFactory;
    private final RequestConfig requestConfig;
    private final HttpProcessor httpProcessor;
    private final HttpRequestExecutor requestExec;
    private final AuthenticationStrategy proxyAuthStrategy;
    private final HttpAuthenticator authenticator;
    private final AuthExchange proxyAuthExchange;
    private final Lookup<AuthSchemeFactory> authSchemeRegistry;
    private final ConnectionReuseStrategy reuseStrategy;

    public ProxyClient(HttpConnectionFactory<ManagedHttpClientConnection> connFactory, Http1Config h1Config, CharCodingConfig charCodingConfig, RequestConfig requestConfig) {
        this.connFactory = connFactory != null ? connFactory : ManagedHttpClientConnectionFactory.builder().http1Config(h1Config).charCodingConfig(charCodingConfig).build();
        this.requestConfig = requestConfig != null ? requestConfig : RequestConfig.DEFAULT;
        this.httpProcessor = new DefaultHttpProcessor(new HttpRequestInterceptor[]{new RequestTargetHost(), new RequestClientConnControl(), new RequestUserAgent()});
        this.requestExec = new HttpRequestExecutor();
        this.proxyAuthStrategy = new DefaultAuthenticationStrategy();
        this.authenticator = new HttpAuthenticator();
        this.proxyAuthExchange = new AuthExchange();
        this.authSchemeRegistry = RegistryBuilder.create().register("Basic", (Object)BasicSchemeFactory.INSTANCE).register("Digest", (Object)DigestSchemeFactory.INSTANCE).register("NTLM", (Object)NTLMSchemeFactory.INSTANCE).register("Negotiate", (Object)SPNegoSchemeFactory.DEFAULT).register("Kerberos", (Object)KerberosSchemeFactory.DEFAULT).build();
        this.reuseStrategy = DefaultClientConnectionReuseStrategy.INSTANCE;
    }

    public ProxyClient(RequestConfig requestConfig) {
        this(null, null, null, requestConfig);
    }

    public ProxyClient() {
        this(null, null, null, null);
    }

    public Socket tunnel(HttpHost proxy, HttpHost target, Credentials credentials) throws IOException, HttpException {
        HttpEntity entity;
        int status;
        ClassicHttpResponse response;
        Args.notNull((Object)proxy, (String)"Proxy host");
        Args.notNull((Object)target, (String)"Target host");
        Args.notNull((Object)credentials, (String)"Credentials");
        HttpHost host = target;
        if (host.getPort() <= 0) {
            host = new HttpHost(host.getSchemeName(), host.getHostName(), 80);
        }
        HttpRoute route = new HttpRoute(host, null, proxy, false, RouteInfo.TunnelType.TUNNELLED, RouteInfo.LayerType.PLAIN);
        ManagedHttpClientConnection conn = (ManagedHttpClientConnection)this.connFactory.createConnection(null);
        BasicHttpContext context = new BasicHttpContext();
        BasicClassicHttpRequest connect = new BasicClassicHttpRequest(Method.CONNECT, proxy, host.toHostString());
        BasicCredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(new AuthScope(proxy), credentials);
        context.setAttribute("http.request", (Object)connect);
        context.setAttribute("http.route", (Object)route);
        context.setAttribute("http.auth.credentials-provider", (Object)credsProvider);
        context.setAttribute("http.authscheme-registry", this.authSchemeRegistry);
        context.setAttribute("http.request-config", (Object)this.requestConfig);
        this.requestExec.preProcess((ClassicHttpRequest)connect, this.httpProcessor, (HttpContext)context);
        while (true) {
            if (!conn.isOpen()) {
                Socket socket = new Socket(proxy.getHostName(), proxy.getPort());
                conn.bind(socket);
            }
            this.authenticator.addAuthResponse(proxy, ChallengeType.PROXY, (HttpRequest)connect, this.proxyAuthExchange, (HttpContext)context);
            response = this.requestExec.execute((ClassicHttpRequest)connect, (HttpClientConnection)conn, (HttpContext)context);
            status = response.getCode();
            if (status < 200) {
                throw new HttpException("Unexpected response to CONNECT request: " + response);
            }
            if (!this.authenticator.isChallenged(proxy, ChallengeType.PROXY, (HttpResponse)response, this.proxyAuthExchange, (HttpContext)context) || !this.authenticator.updateAuthState(proxy, ChallengeType.PROXY, (HttpResponse)response, this.proxyAuthStrategy, this.proxyAuthExchange, (HttpContext)context)) break;
            if (this.reuseStrategy.keepAlive((HttpRequest)connect, (HttpResponse)response, (HttpContext)context)) {
                entity = response.getEntity();
                EntityUtils.consume((HttpEntity)entity);
            } else {
                conn.close();
            }
            connect.removeHeaders("Proxy-Authorization");
        }
        status = response.getCode();
        if (status > 299) {
            entity = response.getEntity();
            String responseMessage = entity != null ? EntityUtils.toString((HttpEntity)entity) : null;
            conn.close();
            throw new TunnelRefusedException("CONNECT refused by proxy: " + new StatusLine((HttpResponse)response), responseMessage);
        }
        return conn.getSocket();
    }
}

