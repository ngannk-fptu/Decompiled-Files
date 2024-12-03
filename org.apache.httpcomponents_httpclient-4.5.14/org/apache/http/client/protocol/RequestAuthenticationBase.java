/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.apache.http.Header
 *  org.apache.http.HttpRequest
 *  org.apache.http.HttpRequestInterceptor
 *  org.apache.http.protocol.HttpContext
 *  org.apache.http.util.Asserts
 */
package org.apache.http.client.protocol;

import java.util.Queue;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.auth.AuthOption;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.ContextAwareAuthScheme;
import org.apache.http.auth.Credentials;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Asserts;

@Deprecated
abstract class RequestAuthenticationBase
implements HttpRequestInterceptor {
    final Log log = LogFactory.getLog(this.getClass());

    void process(AuthState authState, HttpRequest request, HttpContext context) {
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
                                Header header = this.authenticate(authScheme, creds, request, context);
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
                    Header header = this.authenticate(authScheme, creds, request, context);
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

    private Header authenticate(AuthScheme authScheme, Credentials creds, HttpRequest request, HttpContext context) throws AuthenticationException {
        Asserts.notNull((Object)authScheme, (String)"Auth scheme");
        if (authScheme instanceof ContextAwareAuthScheme) {
            return ((ContextAwareAuthScheme)authScheme).authenticate(creds, request, context);
        }
        return authScheme.authenticate(creds, request);
    }
}

