/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.apache.http.Header
 *  org.apache.http.HttpHost
 *  org.apache.http.HttpResponse
 *  org.apache.http.annotation.Contract
 *  org.apache.http.annotation.ThreadingBehavior
 *  org.apache.http.protocol.HttpContext
 *  org.apache.http.util.Args
 */
package org.apache.http.impl.client;

import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.auth.AuthOption;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.MalformedChallengeException;
import org.apache.http.client.AuthCache;
import org.apache.http.client.AuthenticationHandler;
import org.apache.http.client.AuthenticationStrategy;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;

@Deprecated
@Contract(threading=ThreadingBehavior.IMMUTABLE)
class AuthenticationStrategyAdaptor
implements AuthenticationStrategy {
    private final Log log = LogFactory.getLog(this.getClass());
    private final AuthenticationHandler handler;

    public AuthenticationStrategyAdaptor(AuthenticationHandler handler) {
        this.handler = handler;
    }

    @Override
    public boolean isAuthenticationRequested(HttpHost authhost, HttpResponse response, HttpContext context) {
        return this.handler.isAuthenticationRequested(response, context);
    }

    @Override
    public Map<String, Header> getChallenges(HttpHost authhost, HttpResponse response, HttpContext context) throws MalformedChallengeException {
        return this.handler.getChallenges(response, context);
    }

    @Override
    public Queue<AuthOption> select(Map<String, Header> challenges, HttpHost authhost, HttpResponse response, HttpContext context) throws MalformedChallengeException {
        AuthScheme authScheme;
        Args.notNull(challenges, (String)"Map of auth challenges");
        Args.notNull((Object)authhost, (String)"Host");
        Args.notNull((Object)response, (String)"HTTP response");
        Args.notNull((Object)context, (String)"HTTP context");
        LinkedList<AuthOption> options = new LinkedList<AuthOption>();
        CredentialsProvider credsProvider = (CredentialsProvider)context.getAttribute("http.auth.credentials-provider");
        if (credsProvider == null) {
            this.log.debug((Object)"Credentials provider not set in the context");
            return options;
        }
        try {
            authScheme = this.handler.selectScheme(challenges, response, context);
        }
        catch (AuthenticationException ex) {
            if (this.log.isWarnEnabled()) {
                this.log.warn((Object)ex.getMessage(), (Throwable)((Object)ex));
            }
            return options;
        }
        String id = authScheme.getSchemeName();
        Header challenge = challenges.get(id.toLowerCase(Locale.ROOT));
        authScheme.processChallenge(challenge);
        AuthScope authScope = new AuthScope(authhost.getHostName(), authhost.getPort(), authScheme.getRealm(), authScheme.getSchemeName());
        Credentials credentials = credsProvider.getCredentials(authScope);
        if (credentials != null) {
            options.add(new AuthOption(authScheme, credentials));
        }
        return options;
    }

    @Override
    public void authSucceeded(HttpHost authhost, AuthScheme authScheme, HttpContext context) {
        AuthCache authCache = (AuthCache)context.getAttribute("http.auth.auth-cache");
        if (this.isCachable(authScheme)) {
            if (authCache == null) {
                authCache = new BasicAuthCache();
                context.setAttribute("http.auth.auth-cache", (Object)authCache);
            }
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)("Caching '" + authScheme.getSchemeName() + "' auth scheme for " + authhost));
            }
            authCache.put(authhost, authScheme);
        }
    }

    @Override
    public void authFailed(HttpHost authhost, AuthScheme authScheme, HttpContext context) {
        AuthCache authCache = (AuthCache)context.getAttribute("http.auth.auth-cache");
        if (authCache == null) {
            return;
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)("Removing from cache '" + authScheme.getSchemeName() + "' auth scheme for " + authhost));
        }
        authCache.remove(authhost);
    }

    private boolean isCachable(AuthScheme authScheme) {
        if (authScheme == null || !authScheme.isComplete()) {
            return false;
        }
        String schemeName = authScheme.getSchemeName();
        return schemeName.equalsIgnoreCase("Basic");
    }

    public AuthenticationHandler getHandler() {
        return this.handler;
    }
}

