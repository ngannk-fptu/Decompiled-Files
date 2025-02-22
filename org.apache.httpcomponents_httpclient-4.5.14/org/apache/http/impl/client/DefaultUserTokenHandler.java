/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.HttpConnection
 *  org.apache.http.annotation.Contract
 *  org.apache.http.annotation.ThreadingBehavior
 *  org.apache.http.protocol.HttpContext
 */
package org.apache.http.impl.client;

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
import org.apache.http.conn.ManagedHttpClientConnection;
import org.apache.http.protocol.HttpContext;

@Contract(threading=ThreadingBehavior.IMMUTABLE)
public class DefaultUserTokenHandler
implements UserTokenHandler {
    public static final DefaultUserTokenHandler INSTANCE = new DefaultUserTokenHandler();

    @Override
    public Object getUserToken(HttpContext context) {
        SSLSession sslsession;
        HttpConnection conn;
        HttpClientContext clientContext = HttpClientContext.adapt(context);
        Principal userPrincipal = null;
        AuthState targetAuthState = clientContext.getTargetAuthState();
        if (targetAuthState != null && (userPrincipal = DefaultUserTokenHandler.getAuthPrincipal(targetAuthState)) == null) {
            AuthState proxyAuthState = clientContext.getProxyAuthState();
            userPrincipal = DefaultUserTokenHandler.getAuthPrincipal(proxyAuthState);
        }
        if (userPrincipal == null && (conn = clientContext.getConnection()).isOpen() && conn instanceof ManagedHttpClientConnection && (sslsession = ((ManagedHttpClientConnection)conn).getSSLSession()) != null) {
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

