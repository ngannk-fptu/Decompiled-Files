/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.commons.httpclient.auth;

import java.util.HashMap;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpConnection;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthChallengeParser;
import org.apache.commons.httpclient.auth.AuthScheme;
import org.apache.commons.httpclient.auth.AuthenticationException;
import org.apache.commons.httpclient.auth.BasicScheme;
import org.apache.commons.httpclient.auth.CredentialsNotAvailableException;
import org.apache.commons.httpclient.auth.DigestScheme;
import org.apache.commons.httpclient.auth.InvalidCredentialsException;
import org.apache.commons.httpclient.auth.MalformedChallengeException;
import org.apache.commons.httpclient.auth.NTLMScheme;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class HttpAuthenticator {
    private static final Log LOG = LogFactory.getLog(HttpAuthenticator.class);
    public static final String WWW_AUTH = "WWW-Authenticate";
    public static final String WWW_AUTH_RESP = "Authorization";
    public static final String PROXY_AUTH = "Proxy-Authenticate";
    public static final String PROXY_AUTH_RESP = "Proxy-Authorization";

    public static AuthScheme selectAuthScheme(Header[] challenges) throws MalformedChallengeException {
        LOG.trace((Object)"enter HttpAuthenticator.selectAuthScheme(Header[])");
        if (challenges == null) {
            throw new IllegalArgumentException("Array of challenges may not be null");
        }
        if (challenges.length == 0) {
            throw new IllegalArgumentException("Array of challenges may not be empty");
        }
        String challenge = null;
        HashMap<String, String> challengemap = new HashMap<String, String>(challenges.length);
        for (int i = 0; i < challenges.length; ++i) {
            challenge = challenges[i].getValue();
            String s = AuthChallengeParser.extractScheme(challenge);
            challengemap.put(s, challenge);
        }
        challenge = (String)challengemap.get("ntlm");
        if (challenge != null) {
            return new NTLMScheme(challenge);
        }
        challenge = (String)challengemap.get("digest");
        if (challenge != null) {
            return new DigestScheme(challenge);
        }
        challenge = (String)challengemap.get("basic");
        if (challenge != null) {
            return new BasicScheme(challenge);
        }
        throw new UnsupportedOperationException("Authentication scheme(s) not supported: " + ((Object)challengemap).toString());
    }

    private static boolean doAuthenticateDefault(HttpMethod method, HttpConnection conn, HttpState state, boolean proxy) throws AuthenticationException {
        Credentials credentials;
        if (method == null) {
            throw new IllegalArgumentException("HTTP method may not be null");
        }
        if (state == null) {
            throw new IllegalArgumentException("HTTP state may not be null");
        }
        String host = null;
        if (conn != null) {
            host = proxy ? conn.getProxyHost() : conn.getHost();
        }
        Credentials credentials2 = credentials = proxy ? state.getProxyCredentials(null, host) : state.getCredentials(null, host);
        if (credentials == null) {
            return false;
        }
        if (!(credentials instanceof UsernamePasswordCredentials)) {
            throw new InvalidCredentialsException("Credentials cannot be used for basic authentication: " + credentials.toString());
        }
        String auth = BasicScheme.authenticate((UsernamePasswordCredentials)credentials, method.getParams().getCredentialCharset());
        if (auth != null) {
            String s = proxy ? PROXY_AUTH_RESP : WWW_AUTH_RESP;
            Header header = new Header(s, auth, true);
            method.addRequestHeader(header);
            return true;
        }
        return false;
    }

    public static boolean authenticateDefault(HttpMethod method, HttpConnection conn, HttpState state) throws AuthenticationException {
        LOG.trace((Object)"enter HttpAuthenticator.authenticateDefault(HttpMethod, HttpConnection, HttpState)");
        return HttpAuthenticator.doAuthenticateDefault(method, conn, state, false);
    }

    public static boolean authenticateProxyDefault(HttpMethod method, HttpConnection conn, HttpState state) throws AuthenticationException {
        LOG.trace((Object)"enter HttpAuthenticator.authenticateProxyDefault(HttpMethod, HttpState)");
        return HttpAuthenticator.doAuthenticateDefault(method, conn, state, true);
    }

    private static boolean doAuthenticate(AuthScheme authscheme, HttpMethod method, HttpConnection conn, HttpState state, boolean proxy) throws AuthenticationException {
        Credentials credentials;
        if (authscheme == null) {
            throw new IllegalArgumentException("Authentication scheme may not be null");
        }
        if (method == null) {
            throw new IllegalArgumentException("HTTP method may not be null");
        }
        if (state == null) {
            throw new IllegalArgumentException("HTTP state may not be null");
        }
        String host = null;
        if (conn != null) {
            if (proxy) {
                host = conn.getProxyHost();
            } else {
                host = method.getParams().getVirtualHost();
                if (host == null) {
                    host = conn.getHost();
                }
            }
        }
        String realm = authscheme.getRealm();
        if (LOG.isDebugEnabled()) {
            StringBuffer buffer = new StringBuffer();
            buffer.append("Using credentials for ");
            if (realm == null) {
                buffer.append("default");
            } else {
                buffer.append('\'');
                buffer.append(realm);
                buffer.append('\'');
            }
            buffer.append(" authentication realm at ");
            buffer.append(host);
            LOG.debug((Object)buffer.toString());
        }
        Credentials credentials2 = credentials = proxy ? state.getProxyCredentials(realm, host) : state.getCredentials(realm, host);
        if (credentials == null) {
            StringBuffer buffer = new StringBuffer();
            buffer.append("No credentials available for the ");
            if (realm == null) {
                buffer.append("default");
            } else {
                buffer.append('\'');
                buffer.append(realm);
                buffer.append('\'');
            }
            buffer.append(" authentication realm at ");
            buffer.append(host);
            throw new CredentialsNotAvailableException(buffer.toString());
        }
        String auth = authscheme.authenticate(credentials, method);
        if (auth != null) {
            String s = proxy ? PROXY_AUTH_RESP : WWW_AUTH_RESP;
            Header header = new Header(s, auth, true);
            method.addRequestHeader(header);
            return true;
        }
        return false;
    }

    public static boolean authenticate(AuthScheme authscheme, HttpMethod method, HttpConnection conn, HttpState state) throws AuthenticationException {
        LOG.trace((Object)"enter HttpAuthenticator.authenticate(AuthScheme, HttpMethod, HttpConnection, HttpState)");
        return HttpAuthenticator.doAuthenticate(authscheme, method, conn, state, false);
    }

    public static boolean authenticateProxy(AuthScheme authscheme, HttpMethod method, HttpConnection conn, HttpState state) throws AuthenticationException {
        LOG.trace((Object)"enter HttpAuthenticator.authenticateProxy(AuthScheme, HttpMethod, HttpState)");
        return HttpAuthenticator.doAuthenticate(authscheme, method, conn, state, true);
    }
}

