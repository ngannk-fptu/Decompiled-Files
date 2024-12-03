/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.apache.http.FormattedHeader
 *  org.apache.http.Header
 *  org.apache.http.HttpHost
 *  org.apache.http.HttpResponse
 *  org.apache.http.annotation.Contract
 *  org.apache.http.annotation.ThreadingBehavior
 *  org.apache.http.config.Lookup
 *  org.apache.http.protocol.HTTP
 *  org.apache.http.protocol.HttpContext
 *  org.apache.http.util.Args
 *  org.apache.http.util.CharArrayBuffer
 */
package org.apache.http.impl.client;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.FormattedHeader;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.auth.AuthOption;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthSchemeProvider;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.MalformedChallengeException;
import org.apache.http.client.AuthCache;
import org.apache.http.client.AuthenticationStrategy;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Lookup;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;
import org.apache.http.util.CharArrayBuffer;

@Contract(threading=ThreadingBehavior.IMMUTABLE)
abstract class AuthenticationStrategyImpl
implements AuthenticationStrategy {
    private final Log log = LogFactory.getLog(this.getClass());
    private static final List<String> DEFAULT_SCHEME_PRIORITY = Collections.unmodifiableList(Arrays.asList("Negotiate", "Kerberos", "NTLM", "CredSSP", "Digest", "Basic"));
    private final int challengeCode;
    private final String headerName;

    AuthenticationStrategyImpl(int challengeCode, String headerName) {
        this.challengeCode = challengeCode;
        this.headerName = headerName;
    }

    @Override
    public boolean isAuthenticationRequested(HttpHost authhost, HttpResponse response, HttpContext context) {
        Args.notNull((Object)response, (String)"HTTP response");
        int status = response.getStatusLine().getStatusCode();
        return status == this.challengeCode;
    }

    @Override
    public Map<String, Header> getChallenges(HttpHost authhost, HttpResponse response, HttpContext context) throws MalformedChallengeException {
        Args.notNull((Object)response, (String)"HTTP response");
        Header[] headers = response.getHeaders(this.headerName);
        HashMap<String, Header> map = new HashMap<String, Header>(headers.length);
        for (Header header : headers) {
            int pos;
            CharArrayBuffer buffer;
            if (header instanceof FormattedHeader) {
                buffer = ((FormattedHeader)header).getBuffer();
                pos = ((FormattedHeader)header).getValuePos();
            } else {
                String s = header.getValue();
                if (s == null) {
                    throw new MalformedChallengeException("Header value is null");
                }
                buffer = new CharArrayBuffer(s.length());
                buffer.append(s);
                pos = 0;
            }
            while (pos < buffer.length() && HTTP.isWhitespace((char)buffer.charAt(pos))) {
                ++pos;
            }
            int beginIndex = pos;
            while (pos < buffer.length() && !HTTP.isWhitespace((char)buffer.charAt(pos))) {
                ++pos;
            }
            int endIndex = pos;
            String s = buffer.substring(beginIndex, endIndex);
            map.put(s.toLowerCase(Locale.ROOT), header);
        }
        return map;
    }

    abstract Collection<String> getPreferredAuthSchemes(RequestConfig var1);

    @Override
    public Queue<AuthOption> select(Map<String, Header> challenges, HttpHost authhost, HttpResponse response, HttpContext context) throws MalformedChallengeException {
        Args.notNull(challenges, (String)"Map of auth challenges");
        Args.notNull((Object)authhost, (String)"Host");
        Args.notNull((Object)response, (String)"HTTP response");
        Args.notNull((Object)context, (String)"HTTP context");
        HttpClientContext clientContext = HttpClientContext.adapt(context);
        LinkedList<AuthOption> options = new LinkedList<AuthOption>();
        Lookup<AuthSchemeProvider> registry = clientContext.getAuthSchemeRegistry();
        if (registry == null) {
            this.log.debug((Object)"Auth scheme registry not set in the context");
            return options;
        }
        CredentialsProvider credsProvider = clientContext.getCredentialsProvider();
        if (credsProvider == null) {
            this.log.debug((Object)"Credentials provider not set in the context");
            return options;
        }
        RequestConfig config = clientContext.getRequestConfig();
        Collection<String> authPrefs = this.getPreferredAuthSchemes(config);
        if (authPrefs == null) {
            authPrefs = DEFAULT_SCHEME_PRIORITY;
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)("Authentication schemes in the order of preference: " + authPrefs));
        }
        for (String id : authPrefs) {
            Header challenge = challenges.get(id.toLowerCase(Locale.ROOT));
            if (challenge != null) {
                AuthSchemeProvider authSchemeProvider = (AuthSchemeProvider)registry.lookup(id);
                if (authSchemeProvider == null) {
                    if (!this.log.isWarnEnabled()) continue;
                    this.log.warn((Object)("Authentication scheme " + id + " not supported"));
                    continue;
                }
                AuthScheme authScheme = authSchemeProvider.create(context);
                authScheme.processChallenge(challenge);
                AuthScope authScope = new AuthScope(authhost, authScheme.getRealm(), authScheme.getSchemeName());
                Credentials credentials = credsProvider.getCredentials(authScope);
                if (credentials == null) continue;
                options.add(new AuthOption(authScheme, credentials));
                continue;
            }
            if (!this.log.isDebugEnabled()) continue;
            this.log.debug((Object)("Challenge for " + id + " authentication scheme not available"));
        }
        return options;
    }

    @Override
    public void authSucceeded(HttpHost authhost, AuthScheme authScheme, HttpContext context) {
        Args.notNull((Object)authhost, (String)"Host");
        Args.notNull((Object)authScheme, (String)"Auth scheme");
        Args.notNull((Object)context, (String)"HTTP context");
        HttpClientContext clientContext = HttpClientContext.adapt(context);
        if (this.isCachable(authScheme)) {
            AuthCache authCache = clientContext.getAuthCache();
            if (authCache == null) {
                authCache = new BasicAuthCache();
                clientContext.setAuthCache(authCache);
            }
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)("Caching '" + authScheme.getSchemeName() + "' auth scheme for " + authhost));
            }
            authCache.put(authhost, authScheme);
        }
    }

    protected boolean isCachable(AuthScheme authScheme) {
        if (authScheme == null || !authScheme.isComplete()) {
            return false;
        }
        String schemeName = authScheme.getSchemeName();
        return schemeName.equalsIgnoreCase("Basic");
    }

    @Override
    public void authFailed(HttpHost authhost, AuthScheme authScheme, HttpContext context) {
        Args.notNull((Object)authhost, (String)"Host");
        Args.notNull((Object)context, (String)"HTTP context");
        HttpClientContext clientContext = HttpClientContext.adapt(context);
        AuthCache authCache = clientContext.getAuthCache();
        if (authCache != null) {
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)("Clearing cached auth scheme for " + authhost));
            }
            authCache.remove(authhost);
        }
    }
}

