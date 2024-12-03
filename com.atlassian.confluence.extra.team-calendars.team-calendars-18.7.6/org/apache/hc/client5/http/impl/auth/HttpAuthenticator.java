/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.hc.client5.http.impl.auth;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Queue;
import org.apache.hc.client5.http.AuthenticationStrategy;
import org.apache.hc.client5.http.auth.AuthChallenge;
import org.apache.hc.client5.http.auth.AuthExchange;
import org.apache.hc.client5.http.auth.AuthScheme;
import org.apache.hc.client5.http.auth.AuthenticationException;
import org.apache.hc.client5.http.auth.ChallengeType;
import org.apache.hc.client5.http.auth.CredentialsProvider;
import org.apache.hc.client5.http.auth.MalformedChallengeException;
import org.apache.hc.client5.http.impl.auth.AuthChallengeParser;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.http.FormattedHeader;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.message.BasicHeader;
import org.apache.hc.core5.http.message.ParserCursor;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.util.Asserts;
import org.apache.hc.core5.util.CharArrayBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Contract(threading=ThreadingBehavior.STATELESS)
public final class HttpAuthenticator {
    private static final Logger LOG = LoggerFactory.getLogger(HttpAuthenticator.class);
    private final AuthChallengeParser parser = new AuthChallengeParser();

    public boolean isChallenged(HttpHost host, ChallengeType challengeType, HttpResponse response, AuthExchange authExchange, HttpContext context) {
        int challengeCode;
        switch (challengeType) {
            case TARGET: {
                challengeCode = 401;
                break;
            }
            case PROXY: {
                challengeCode = 407;
                break;
            }
            default: {
                throw new IllegalStateException("Unexpected challenge type: " + (Object)((Object)challengeType));
            }
        }
        HttpClientContext clientContext = HttpClientContext.adapt(context);
        String exchangeId = clientContext.getExchangeId();
        if (response.getCode() == challengeCode) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("{} Authentication required", (Object)exchangeId);
            }
            return true;
        }
        switch (authExchange.getState()) {
            case CHALLENGED: 
            case HANDSHAKE: {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("{} Authentication succeeded", (Object)exchangeId);
                }
                authExchange.setState(AuthExchange.State.SUCCESS);
                break;
            }
            case SUCCESS: {
                break;
            }
            default: {
                authExchange.setState(AuthExchange.State.UNCHALLENGED);
            }
        }
        return false;
    }

    public boolean updateAuthState(HttpHost host, ChallengeType challengeType, HttpResponse response, AuthenticationStrategy authStrategy, AuthExchange authExchange, HttpContext context) {
        HttpClientContext clientContext = HttpClientContext.adapt(context);
        String exchangeId = clientContext.getExchangeId();
        if (LOG.isDebugEnabled()) {
            LOG.debug("{} {} requested authentication", (Object)exchangeId, (Object)host.toHostString());
        }
        Header[] headers = response.getHeaders(challengeType == ChallengeType.PROXY ? "Proxy-Authenticate" : "WWW-Authenticate");
        HashMap<String, AuthChallenge> challengeMap = new HashMap<String, AuthChallenge>();
        for (Header header : headers) {
            List<AuthChallenge> authChallenges;
            int pos;
            CharArrayBuffer buffer;
            if (header instanceof FormattedHeader) {
                buffer = ((FormattedHeader)header).getBuffer();
                pos = ((FormattedHeader)header).getValuePos();
            } else {
                String s = header.getValue();
                if (s == null) continue;
                buffer = new CharArrayBuffer(s.length());
                buffer.append(s);
                pos = 0;
            }
            ParserCursor cursor = new ParserCursor(pos, buffer.length());
            try {
                authChallenges = this.parser.parse(challengeType, buffer, cursor);
            }
            catch (ParseException ex) {
                if (!LOG.isWarnEnabled()) continue;
                LOG.warn("{} Malformed challenge: {}", (Object)exchangeId, (Object)header.getValue());
                continue;
            }
            for (AuthChallenge authChallenge : authChallenges) {
                String schemeName = authChallenge.getSchemeName().toLowerCase(Locale.ROOT);
                if (challengeMap.containsKey(schemeName)) continue;
                challengeMap.put(schemeName, authChallenge);
            }
        }
        if (challengeMap.isEmpty()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("{} Response contains no valid authentication challenges", (Object)exchangeId);
            }
            authExchange.reset();
            return false;
        }
        switch (authExchange.getState()) {
            case FAILURE: {
                return false;
            }
            case SUCCESS: {
                authExchange.reset();
                break;
            }
            case CHALLENGED: 
            case HANDSHAKE: {
                Asserts.notNull(authExchange.getAuthScheme(), "AuthScheme");
            }
            case UNCHALLENGED: {
                AuthScheme authScheme = authExchange.getAuthScheme();
                if (authScheme == null) break;
                String schemeName = authScheme.getName();
                AuthChallenge challenge = (AuthChallenge)challengeMap.get(schemeName.toLowerCase(Locale.ROOT));
                if (challenge != null) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("{} Authorization challenge processed", (Object)exchangeId);
                    }
                    try {
                        authScheme.processChallenge(challenge, context);
                    }
                    catch (MalformedChallengeException ex) {
                        if (LOG.isWarnEnabled()) {
                            LOG.warn("{} {}", (Object)exchangeId, (Object)ex.getMessage());
                        }
                        authExchange.reset();
                        authExchange.setState(AuthExchange.State.FAILURE);
                        return false;
                    }
                    if (authScheme.isChallengeComplete()) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("{} Authentication failed", (Object)exchangeId);
                        }
                        authExchange.reset();
                        authExchange.setState(AuthExchange.State.FAILURE);
                        return false;
                    }
                    authExchange.setState(AuthExchange.State.HANDSHAKE);
                    return true;
                }
                authExchange.reset();
            }
        }
        List<AuthScheme> preferredSchemes = authStrategy.select(challengeType, challengeMap, context);
        CredentialsProvider credsProvider = clientContext.getCredentialsProvider();
        if (credsProvider == null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("{} Credentials provider not set in the context", (Object)exchangeId);
            }
            return false;
        }
        LinkedList<AuthScheme> authOptions = new LinkedList<AuthScheme>();
        if (LOG.isDebugEnabled()) {
            LOG.debug("{} Selecting authentication options", (Object)exchangeId);
        }
        for (AuthScheme authScheme : preferredSchemes) {
            try {
                String schemeName = authScheme.getName();
                AuthChallenge challenge = (AuthChallenge)challengeMap.get(schemeName.toLowerCase(Locale.ROOT));
                authScheme.processChallenge(challenge, context);
                if (!authScheme.isResponseReady(host, credsProvider, context)) continue;
                authOptions.add(authScheme);
            }
            catch (AuthenticationException | MalformedChallengeException ex) {
                if (!LOG.isWarnEnabled()) continue;
                LOG.warn(ex.getMessage());
            }
        }
        if (!authOptions.isEmpty()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("{} Selected authentication options: {}", (Object)exchangeId, authOptions);
            }
            authExchange.reset();
            authExchange.setState(AuthExchange.State.CHALLENGED);
            authExchange.setOptions(authOptions);
            return true;
        }
        return false;
    }

    public void addAuthResponse(HttpHost host, ChallengeType challengeType, HttpRequest request, AuthExchange authExchange, HttpContext context) {
        block14: {
            HttpClientContext clientContext = HttpClientContext.adapt(context);
            String exchangeId = clientContext.getExchangeId();
            AuthScheme authScheme = authExchange.getAuthScheme();
            switch (authExchange.getState()) {
                case FAILURE: {
                    return;
                }
                case SUCCESS: {
                    Asserts.notNull(authScheme, "AuthScheme");
                    if (!authScheme.isConnectionBased()) break;
                    return;
                }
                case HANDSHAKE: {
                    Asserts.notNull(authScheme, "AuthScheme");
                    break;
                }
                case CHALLENGED: {
                    Queue<AuthScheme> authOptions = authExchange.getAuthOptions();
                    if (authOptions != null) {
                        while (!authOptions.isEmpty()) {
                            authScheme = authOptions.remove();
                            authExchange.select(authScheme);
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("{} Generating response to an authentication challenge using {} scheme", (Object)exchangeId, (Object)authScheme.getName());
                            }
                            try {
                                String authResponse = authScheme.generateAuthResponse(host, request, context);
                                BasicHeader header = new BasicHeader(challengeType == ChallengeType.TARGET ? "Authorization" : "Proxy-Authorization", authResponse);
                                request.addHeader(header);
                                break;
                            }
                            catch (AuthenticationException ex) {
                                if (!LOG.isWarnEnabled()) continue;
                                LOG.warn("{} {} authentication error: {}", new Object[]{exchangeId, authScheme, ex.getMessage()});
                            }
                        }
                        return;
                    }
                    Asserts.notNull(authScheme, "AuthScheme");
                }
            }
            if (authScheme != null) {
                try {
                    String authResponse = authScheme.generateAuthResponse(host, request, context);
                    BasicHeader header = new BasicHeader(challengeType == ChallengeType.TARGET ? "Authorization" : "Proxy-Authorization", authResponse);
                    request.addHeader(header);
                }
                catch (AuthenticationException ex) {
                    if (!LOG.isErrorEnabled()) break block14;
                    LOG.error("{} {} authentication error: {}", new Object[]{exchangeId, authScheme, ex.getMessage()});
                }
            }
        }
    }
}

