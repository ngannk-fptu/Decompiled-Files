/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.apache.http.HttpException
 *  org.apache.http.HttpHost
 *  org.apache.http.HttpRequest
 *  org.apache.http.HttpRequestInterceptor
 *  org.apache.http.annotation.Contract
 *  org.apache.http.annotation.ThreadingBehavior
 *  org.apache.http.protocol.HttpContext
 *  org.apache.http.util.Args
 */
package org.apache.http.client.protocol;

import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.auth.AuthProtocolState;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.Credentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.routing.RouteInfo;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;

@Contract(threading=ThreadingBehavior.IMMUTABLE)
public class RequestAuthCache
implements HttpRequestInterceptor {
    private final Log log = LogFactory.getLog(this.getClass());

    public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
        AuthScheme authScheme;
        AuthScheme authScheme2;
        AuthState targetState;
        Args.notNull((Object)request, (String)"HTTP request");
        Args.notNull((Object)context, (String)"HTTP context");
        HttpClientContext clientContext = HttpClientContext.adapt(context);
        AuthCache authCache = clientContext.getAuthCache();
        if (authCache == null) {
            this.log.debug((Object)"Auth cache not set in the context");
            return;
        }
        CredentialsProvider credsProvider = clientContext.getCredentialsProvider();
        if (credsProvider == null) {
            this.log.debug((Object)"Credentials provider not set in the context");
            return;
        }
        RouteInfo route = clientContext.getHttpRoute();
        if (route == null) {
            this.log.debug((Object)"Route info not set in the context");
            return;
        }
        HttpHost target = clientContext.getTargetHost();
        if (target == null) {
            this.log.debug((Object)"Target host not set in the context");
            return;
        }
        if (target.getPort() < 0) {
            target = new HttpHost(target.getHostName(), route.getTargetHost().getPort(), target.getSchemeName());
        }
        if ((targetState = clientContext.getTargetAuthState()) != null && targetState.getState() == AuthProtocolState.UNCHALLENGED && (authScheme2 = authCache.get(target)) != null) {
            this.doPreemptiveAuth(target, authScheme2, targetState, credsProvider);
        }
        HttpHost proxy = route.getProxyHost();
        AuthState proxyState = clientContext.getProxyAuthState();
        if (proxy != null && proxyState != null && proxyState.getState() == AuthProtocolState.UNCHALLENGED && (authScheme = authCache.get(proxy)) != null) {
            this.doPreemptiveAuth(proxy, authScheme, proxyState, credsProvider);
        }
    }

    private void doPreemptiveAuth(HttpHost host, AuthScheme authScheme, AuthState authState, CredentialsProvider credsProvider) {
        AuthScope authScope;
        Credentials creds;
        String schemeName = authScheme.getSchemeName();
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)("Re-using cached '" + schemeName + "' auth scheme for " + host));
        }
        if ((creds = credsProvider.getCredentials(authScope = new AuthScope(host, AuthScope.ANY_REALM, schemeName))) != null) {
            authState.update(authScheme, creds);
        } else {
            this.log.debug((Object)"No credentials for preemptive authentication");
        }
    }
}

