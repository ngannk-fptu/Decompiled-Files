/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.commons.httpclient.auth;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import org.apache.commons.httpclient.auth.AuthChallengeException;
import org.apache.commons.httpclient.auth.AuthPolicy;
import org.apache.commons.httpclient.auth.AuthScheme;
import org.apache.commons.httpclient.auth.AuthState;
import org.apache.commons.httpclient.auth.AuthenticationException;
import org.apache.commons.httpclient.auth.MalformedChallengeException;
import org.apache.commons.httpclient.params.HttpParams;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class AuthChallengeProcessor {
    private static final Log LOG = LogFactory.getLog(AuthChallengeProcessor.class);
    private HttpParams params = null;

    public AuthChallengeProcessor(HttpParams params) {
        if (params == null) {
            throw new IllegalArgumentException("Parameter collection may not be null");
        }
        this.params = params;
    }

    public AuthScheme selectAuthScheme(Map challenges) throws AuthChallengeException {
        if (challenges == null) {
            throw new IllegalArgumentException("Challenge map may not be null");
        }
        Collection authPrefs = (Collection)this.params.getParameter("http.auth.scheme-priority");
        if (authPrefs == null || authPrefs.isEmpty()) {
            authPrefs = AuthPolicy.getDefaultAuthPrefs();
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug((Object)("Supported authentication schemes in the order of preference: " + authPrefs));
        }
        AuthScheme authscheme = null;
        String challenge = null;
        for (String id : authPrefs) {
            challenge = (String)challenges.get(id.toLowerCase(Locale.ENGLISH));
            if (challenge != null) {
                if (LOG.isInfoEnabled()) {
                    LOG.info((Object)(id + " authentication scheme selected"));
                }
                try {
                    authscheme = AuthPolicy.getAuthScheme(id);
                    break;
                }
                catch (IllegalStateException e) {
                    throw new AuthChallengeException(e.getMessage());
                }
            }
            if (!LOG.isDebugEnabled()) continue;
            LOG.debug((Object)("Challenge for " + id + " authentication scheme not available"));
        }
        if (authscheme == null) {
            throw new AuthChallengeException("Unable to respond to any of these challenges: " + challenges);
        }
        return authscheme;
    }

    public AuthScheme processChallenge(AuthState state, Map challenges) throws MalformedChallengeException, AuthenticationException {
        String challenge;
        if (state == null) {
            throw new IllegalArgumentException("Authentication state may not be null");
        }
        if (challenges == null) {
            throw new IllegalArgumentException("Challenge map may not be null");
        }
        if (state.isPreemptive() || state.getAuthScheme() == null) {
            state.setAuthScheme(this.selectAuthScheme(challenges));
        }
        AuthScheme authscheme = state.getAuthScheme();
        String id = authscheme.getSchemeName();
        if (LOG.isDebugEnabled()) {
            LOG.debug((Object)("Using authentication scheme: " + id));
        }
        if ((challenge = (String)challenges.get(id.toLowerCase(Locale.ENGLISH))) == null) {
            throw new AuthenticationException(id + " authorization challenge expected, but not found");
        }
        authscheme.processChallenge(challenge);
        LOG.debug((Object)"Authorization challenge processed");
        return authscheme;
    }
}

