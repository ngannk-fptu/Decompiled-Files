/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.apache.http.Header
 *  org.apache.http.HttpException
 *  org.apache.http.HttpHost
 *  org.apache.http.HttpRequest
 *  org.apache.http.HttpResponse
 *  org.apache.http.protocol.HttpContext
 *  org.apache.http.util.Asserts
 */
package org.apache.http.impl.auth;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthOption;
import org.apache.http.auth.AuthProtocolState;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.ContextAwareAuthScheme;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.MalformedChallengeException;
import org.apache.http.client.AuthenticationStrategy;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Asserts;

public class HttpAuthenticator {
    private final Log log;

    public HttpAuthenticator(Log log) {
        this.log = log != null ? log : LogFactory.getLog(this.getClass());
    }

    public HttpAuthenticator() {
        this(null);
    }

    public boolean isAuthenticationRequested(HttpHost host, HttpResponse response, AuthenticationStrategy authStrategy, AuthState authState, HttpContext context) {
        if (authStrategy.isAuthenticationRequested(host, response, context)) {
            this.log.debug((Object)"Authentication required");
            if (authState.getState() == AuthProtocolState.SUCCESS) {
                authStrategy.authFailed(host, authState.getAuthScheme(), context);
            }
            return true;
        }
        switch (authState.getState()) {
            case CHALLENGED: 
            case HANDSHAKE: {
                this.log.debug((Object)"Authentication succeeded");
                authState.setState(AuthProtocolState.SUCCESS);
                authStrategy.authSucceeded(host, authState.getAuthScheme(), context);
                break;
            }
            case SUCCESS: {
                break;
            }
            default: {
                authState.setState(AuthProtocolState.UNCHALLENGED);
            }
        }
        return false;
    }

    public boolean handleAuthChallenge(HttpHost host, HttpResponse response, AuthenticationStrategy authStrategy, AuthState authState, HttpContext context) {
        try {
            Map<String, Header> challenges;
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)(host.toHostString() + " requested authentication"));
            }
            if ((challenges = authStrategy.getChallenges(host, response, context)).isEmpty()) {
                this.log.debug((Object)"Response contains no authentication challenges");
                return false;
            }
            AuthScheme authScheme = authState.getAuthScheme();
            switch (authState.getState()) {
                case FAILURE: {
                    return false;
                }
                case SUCCESS: {
                    authState.reset();
                    break;
                }
                case CHALLENGED: 
                case HANDSHAKE: {
                    if (authScheme == null) {
                        this.log.debug((Object)"Auth scheme is null");
                        authStrategy.authFailed(host, null, context);
                        authState.reset();
                        authState.setState(AuthProtocolState.FAILURE);
                        return false;
                    }
                }
                case UNCHALLENGED: {
                    if (authScheme == null) break;
                    String id = authScheme.getSchemeName();
                    Header challenge = challenges.get(id.toLowerCase(Locale.ROOT));
                    if (challenge != null) {
                        this.log.debug((Object)"Authorization challenge processed");
                        authScheme.processChallenge(challenge);
                        if (authScheme.isComplete()) {
                            this.log.debug((Object)"Authentication failed");
                            authStrategy.authFailed(host, authState.getAuthScheme(), context);
                            authState.reset();
                            authState.setState(AuthProtocolState.FAILURE);
                            return false;
                        }
                        authState.setState(AuthProtocolState.HANDSHAKE);
                        return true;
                    }
                    authState.reset();
                }
            }
            Queue<AuthOption> authOptions = authStrategy.select(challenges, host, response, context);
            if (authOptions != null && !authOptions.isEmpty()) {
                if (this.log.isDebugEnabled()) {
                    this.log.debug((Object)("Selected authentication options: " + authOptions));
                }
                authState.setState(AuthProtocolState.CHALLENGED);
                authState.update(authOptions);
                return true;
            }
            return false;
        }
        catch (MalformedChallengeException ex) {
            if (this.log.isWarnEnabled()) {
                this.log.warn((Object)("Malformed challenge: " + ex.getMessage()));
            }
            authState.reset();
            return false;
        }
    }

    public void generateAuthResponse(HttpRequest request, AuthState authState, HttpContext context) throws HttpException, IOException {
        block13: {
            AuthScheme authScheme = authState.getAuthScheme();
            Credentials creds = authState.getCredentials();
            switch (authState.getState()) {
                case FAILURE: {
                    return;
                }
                case SUCCESS: {
                    this.ensureAuthScheme(authScheme);
                    if (!authScheme.isConnectionBased()) break;
                    return;
                }
                case CHALLENGED: {
                    Queue<AuthOption> authOptions = authState.getAuthOptions();
                    if (authOptions != null) {
                        while (!authOptions.isEmpty()) {
                            AuthOption authOption = authOptions.remove();
                            authScheme = authOption.getAuthScheme();
                            creds = authOption.getCredentials();
                            authState.update(authScheme, creds);
                            if (this.log.isDebugEnabled()) {
                                this.log.debug((Object)("Generating response to an authentication challenge using " + authScheme.getSchemeName() + " scheme"));
                            }
                            try {
                                Header header = this.doAuth(authScheme, creds, request, context);
                                request.addHeader(header);
                                break;
                            }
                            catch (AuthenticationException ex) {
                                if (!this.log.isWarnEnabled()) continue;
                                this.log.warn((Object)(authScheme + " authentication error: " + ex.getMessage()));
                            }
                        }
                        return;
                    }
                    this.ensureAuthScheme(authScheme);
                }
            }
            if (authScheme != null) {
                try {
                    Header header = this.doAuth(authScheme, creds, request, context);
                    request.addHeader(header);
                }
                catch (AuthenticationException ex) {
                    if (!this.log.isErrorEnabled()) break block13;
                    this.log.error((Object)(authScheme + " authentication error: " + ex.getMessage()));
                }
            }
        }
    }

    private void ensureAuthScheme(AuthScheme authScheme) {
        Asserts.notNull((Object)authScheme, (String)"Auth scheme");
    }

    private Header doAuth(AuthScheme authScheme, Credentials creds, HttpRequest request, HttpContext context) throws AuthenticationException {
        return authScheme instanceof ContextAwareAuthScheme ? ((ContextAwareAuthScheme)authScheme).authenticate(creds, request, context) : authScheme.authenticate(creds, request);
    }
}

