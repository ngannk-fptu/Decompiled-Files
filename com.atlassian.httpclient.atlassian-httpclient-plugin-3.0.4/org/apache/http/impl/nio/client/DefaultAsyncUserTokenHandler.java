/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.nio.client;

import java.security.Principal;
import javax.net.ssl.SSLSession;
import org.apache.http.HttpConnection;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.Credentials;
import org.apache.http.client.UserTokenHandler;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.nio.conn.ManagedNHttpClientConnection;
import org.apache.http.protocol.HttpContext;

@Contract(threading=ThreadingBehavior.IMMUTABLE)
public class DefaultAsyncUserTokenHandler
implements UserTokenHandler {
    public static final DefaultAsyncUserTokenHandler INSTANCE = new DefaultAsyncUserTokenHandler();

    @Override
    public Object getUserToken(HttpContext context) {
        SSLSession sslsession;
        HttpConnection conn;
        HttpClientContext clientContext = HttpClientContext.adapt(context);
        Principal userPrincipal = null;
        AuthState targetAuthState = clientContext.getTargetAuthState();
        if (targetAuthState != null && (userPrincipal = DefaultAsyncUserTokenHandler.getAuthPrincipal(targetAuthState)) == null) {
            AuthState proxyAuthState = clientContext.getProxyAuthState();
            userPrincipal = DefaultAsyncUserTokenHandler.getAuthPrincipal(proxyAuthState);
        }
        if (userPrincipal == null && (conn = clientContext.getConnection()).isOpen() && conn instanceof ManagedNHttpClientConnection && (sslsession = ((ManagedNHttpClientConnection)conn).getSSLSession()) != null) {
            userPrincipal = sslsession.getLocalPrincipal();
        }
        return userPrincipal;
    }

    private static Principal getAuthPrincipal(AuthState authState) {
        Credentials creds;
        AuthScheme scheme = authState.getAuthScheme();
        if (scheme != null && scheme.isComplete() && scheme.isConnectionBased() && (creds = authState.getCredentials()) != null) {
            return creds.getUserPrincipal();
        }
        return null;
    }
}

