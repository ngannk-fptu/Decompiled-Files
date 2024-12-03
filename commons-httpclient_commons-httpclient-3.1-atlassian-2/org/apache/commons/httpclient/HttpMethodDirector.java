/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.commons.httpclient;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.commons.httpclient.CircularRedirectException;
import org.apache.commons.httpclient.ConnectMethod;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpConnection;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.HttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpRecoverableException;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.InvalidRedirectLocationException;
import org.apache.commons.httpclient.RedirectException;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.auth.AuthChallengeException;
import org.apache.commons.httpclient.auth.AuthChallengeParser;
import org.apache.commons.httpclient.auth.AuthChallengeProcessor;
import org.apache.commons.httpclient.auth.AuthScheme;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.auth.AuthState;
import org.apache.commons.httpclient.auth.AuthenticationException;
import org.apache.commons.httpclient.auth.CredentialsNotAvailableException;
import org.apache.commons.httpclient.auth.CredentialsProvider;
import org.apache.commons.httpclient.auth.MalformedChallengeException;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpParams;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

class HttpMethodDirector {
    public static final String WWW_AUTH_CHALLENGE = "WWW-Authenticate";
    public static final String WWW_AUTH_RESP = "Authorization";
    public static final String PROXY_AUTH_CHALLENGE = "Proxy-Authenticate";
    public static final String PROXY_AUTH_RESP = "Proxy-Authorization";
    private static final Log LOG = LogFactory.getLog(HttpMethodDirector.class);
    private ConnectMethod connectMethod;
    private HttpState state;
    private HostConfiguration hostConfiguration;
    private HttpConnectionManager connectionManager;
    private HttpClientParams params;
    private HttpConnection conn;
    private boolean releaseConnection = false;
    private AuthChallengeProcessor authProcessor = null;
    private Set redirectLocations = null;

    public HttpMethodDirector(HttpConnectionManager connectionManager, HostConfiguration hostConfiguration, HttpClientParams params, HttpState state) {
        this.connectionManager = connectionManager;
        this.hostConfiguration = hostConfiguration;
        this.params = params;
        this.state = state;
        this.authProcessor = new AuthChallengeProcessor(this.params);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void executeMethod(HttpMethod method) throws IOException, HttpException {
        if (method == null) {
            throw new IllegalArgumentException("Method may not be null");
        }
        this.hostConfiguration.getParams().setDefaults(this.params);
        method.getParams().setDefaults(this.hostConfiguration.getParams());
        Collection defaults = (Collection)this.hostConfiguration.getParams().getParameter("http.default-headers");
        if (defaults != null) {
            Iterator i = defaults.iterator();
            while (i.hasNext()) {
                method.addRequestHeader((Header)i.next());
            }
        }
        try {
            int maxRedirects = this.params.getIntParameter("http.protocol.max-redirects", 100);
            int redirectCount = 0;
            while (true) {
                if (this.conn != null && !this.hostConfiguration.hostEquals(this.conn)) {
                    this.conn.setLocked(false);
                    this.conn.releaseConnection();
                    this.conn = null;
                }
                if (this.conn == null) {
                    this.conn = this.connectionManager.getConnectionWithTimeout(this.hostConfiguration, this.params.getConnectionManagerTimeout());
                    this.conn.setLocked(true);
                    if (this.params.isAuthenticationPreemptive() || this.state.isAuthenticationPreemptive()) {
                        LOG.debug((Object)"Preemptively sending default basic credentials");
                        method.getHostAuthState().setPreemptive();
                        method.getHostAuthState().setAuthAttempted(true);
                        if (this.conn.isProxied() && !this.conn.isSecure()) {
                            method.getProxyAuthState().setPreemptive();
                            method.getProxyAuthState().setAuthAttempted(true);
                        }
                    }
                }
                this.authenticate(method);
                this.executeWithRetry(method);
                if (this.connectMethod != null) {
                    this.fakeResponse(method);
                    break;
                }
                boolean retry = false;
                if (this.isRedirectNeeded(method) && this.processRedirectResponse(method)) {
                    retry = true;
                    if (++redirectCount >= maxRedirects) {
                        LOG.error((Object)"Narrowly avoided an infinite loop in execute");
                        throw new RedirectException("Maximum redirects (" + maxRedirects + ") exceeded");
                    }
                    if (LOG.isDebugEnabled()) {
                        LOG.debug((Object)("Execute redirect " + redirectCount + " of " + maxRedirects));
                    }
                }
                if (this.isAuthenticationNeeded(method) && this.processAuthenticationResponse(method)) {
                    LOG.debug((Object)"Retry authentication");
                    retry = true;
                }
                if (!retry) {
                    break;
                }
                if (method.getResponseBodyAsStream() == null) continue;
                method.getResponseBodyAsStream().close();
            }
        }
        finally {
            if (this.conn != null) {
                this.conn.setLocked(false);
            }
            if ((this.releaseConnection || method.getResponseBodyAsStream() == null) && this.conn != null) {
                this.conn.releaseConnection();
            }
        }
    }

    private void authenticate(HttpMethod method) {
        try {
            if (this.conn.isProxied() && !this.conn.isSecure()) {
                this.authenticateProxy(method);
            }
            this.authenticateHost(method);
        }
        catch (AuthenticationException e) {
            LOG.error((Object)e.getMessage(), (Throwable)e);
        }
    }

    private boolean cleanAuthHeaders(HttpMethod method, String name) {
        Header[] authheaders = method.getRequestHeaders(name);
        boolean clean = true;
        for (int i = 0; i < authheaders.length; ++i) {
            Header authheader = authheaders[i];
            if (authheader.isAutogenerated()) {
                method.removeRequestHeader(authheader);
                continue;
            }
            clean = false;
        }
        return clean;
    }

    private void authenticateHost(HttpMethod method) throws AuthenticationException {
        if (!this.cleanAuthHeaders(method, WWW_AUTH_RESP)) {
            return;
        }
        AuthState authstate = method.getHostAuthState();
        AuthScheme authscheme = authstate.getAuthScheme();
        if (authscheme == null) {
            return;
        }
        if (authstate.isAuthRequested() || !authscheme.isConnectionBased()) {
            Credentials credentials;
            String host = method.getParams().getVirtualHost();
            if (host == null) {
                host = this.conn.getHost();
            }
            int port = this.conn.getPort();
            AuthScope authscope = new AuthScope(host, port, authscheme.getRealm(), authscheme.getSchemeName());
            if (LOG.isDebugEnabled()) {
                LOG.debug((Object)("Authenticating with " + authscope));
            }
            if ((credentials = this.state.getCredentials(authscope)) != null) {
                String authstring = authscheme.authenticate(credentials, method);
                if (authstring != null) {
                    method.addRequestHeader(new Header(WWW_AUTH_RESP, authstring, true));
                }
            } else if (LOG.isWarnEnabled()) {
                LOG.warn((Object)("Required credentials not available for " + authscope));
                if (method.getHostAuthState().isPreemptive()) {
                    LOG.warn((Object)"Preemptive authentication requested but no default credentials available");
                }
            }
        }
    }

    private void authenticateProxy(HttpMethod method) throws AuthenticationException {
        if (!this.cleanAuthHeaders(method, PROXY_AUTH_RESP)) {
            return;
        }
        AuthState authstate = method.getProxyAuthState();
        AuthScheme authscheme = authstate.getAuthScheme();
        if (authscheme == null) {
            return;
        }
        if (authstate.isAuthRequested() || !authscheme.isConnectionBased()) {
            Credentials credentials;
            AuthScope authscope = new AuthScope(this.conn.getProxyHost(), this.conn.getProxyPort(), authscheme.getRealm(), authscheme.getSchemeName());
            if (LOG.isDebugEnabled()) {
                LOG.debug((Object)("Authenticating with " + authscope));
            }
            if ((credentials = this.state.getProxyCredentials(authscope)) != null) {
                String authstring = authscheme.authenticate(credentials, method);
                if (authstring != null) {
                    method.addRequestHeader(new Header(PROXY_AUTH_RESP, authstring, true));
                }
            } else if (LOG.isWarnEnabled()) {
                LOG.warn((Object)("Required proxy credentials not available for " + authscope));
                if (method.getProxyAuthState().isPreemptive()) {
                    LOG.warn((Object)"Preemptive authentication requested but no default proxy credentials available");
                }
            }
        }
    }

    private void applyConnectionParams(HttpMethod method) throws IOException {
        int timeout = 0;
        Object param = method.getParams().getParameter("http.socket.timeout");
        if (param == null) {
            param = this.conn.getParams().getParameter("http.socket.timeout");
        }
        if (param != null) {
            timeout = (Integer)param;
        }
        this.conn.setSocketTimeout(timeout);
    }

    private void executeWithRetry(HttpMethod method) throws IOException, HttpException {
        int execCount = 0;
        try {
            while (true) {
                ++execCount;
                try {
                    if (LOG.isTraceEnabled()) {
                        LOG.trace((Object)("Attempt number " + execCount + " to process request"));
                    }
                    if (this.conn.getParams().isStaleCheckingEnabled()) {
                        this.conn.closeIfStale();
                    }
                    if (!this.conn.isOpen()) {
                        this.conn.open();
                        if (this.conn.isProxied() && this.conn.isSecure() && !(method instanceof ConnectMethod) && !this.executeConnect()) {
                            return;
                        }
                    }
                    this.applyConnectionParams(method);
                    method.execute(this.state, this.conn);
                }
                catch (HttpException e) {
                    throw e;
                }
                catch (IOException e) {
                    Object handler;
                    LOG.debug((Object)"Closing the connection.");
                    this.conn.close();
                    if (method instanceof HttpMethodBase && (handler = ((HttpMethodBase)method).getMethodRetryHandler()) != null && !handler.retryMethod(method, this.conn, new HttpRecoverableException(e.getMessage()), execCount, method.isRequestSent())) {
                        LOG.debug((Object)"Method retry handler returned false. Automatic recovery will not be attempted");
                        throw e;
                    }
                    handler = (HttpMethodRetryHandler)method.getParams().getParameter("http.method.retry-handler");
                    if (handler == null) {
                        handler = new DefaultHttpMethodRetryHandler();
                    }
                    if (!handler.retryMethod(method, e, execCount)) {
                        LOG.debug((Object)"Method retry handler returned false. Automatic recovery will not be attempted");
                        throw e;
                    }
                    if (LOG.isInfoEnabled()) {
                        LOG.info((Object)("I/O exception (" + e.getClass().getName() + ") caught when processing request: " + e.getMessage()));
                    }
                    if (LOG.isDebugEnabled()) {
                        LOG.debug((Object)e.getMessage(), (Throwable)e);
                    }
                    LOG.info((Object)"Retrying request");
                    continue;
                }
                break;
            }
        }
        catch (IOException e) {
            if (this.conn.isOpen()) {
                LOG.debug((Object)"Closing the connection.");
                this.conn.close();
            }
            this.releaseConnection = true;
            throw e;
        }
        catch (RuntimeException e) {
            if (this.conn.isOpen()) {
                LOG.debug((Object)"Closing the connection.");
                this.conn.close();
            }
            this.releaseConnection = true;
            throw e;
        }
    }

    private boolean executeConnect() throws IOException, HttpException {
        int code;
        this.connectMethod = new ConnectMethod(this.hostConfiguration);
        this.connectMethod.getParams().setDefaults(this.hostConfiguration.getParams());
        while (true) {
            if (!this.conn.isOpen()) {
                this.conn.open();
            }
            if (this.params.isAuthenticationPreemptive() || this.state.isAuthenticationPreemptive()) {
                LOG.debug((Object)"Preemptively sending default basic credentials");
                this.connectMethod.getProxyAuthState().setPreemptive();
                this.connectMethod.getProxyAuthState().setAuthAttempted(true);
            }
            try {
                this.authenticateProxy(this.connectMethod);
            }
            catch (AuthenticationException e) {
                LOG.error((Object)e.getMessage(), (Throwable)e);
            }
            this.applyConnectionParams(this.connectMethod);
            this.connectMethod.execute(this.state, this.conn);
            code = this.connectMethod.getStatusCode();
            boolean retry = false;
            AuthState authstate = this.connectMethod.getProxyAuthState();
            authstate.setAuthRequested(code == 407);
            if (authstate.isAuthRequested() && this.processAuthenticationResponse(this.connectMethod)) {
                retry = true;
            }
            if (!retry) break;
            if (this.connectMethod.getResponseBodyAsStream() == null) continue;
            this.connectMethod.getResponseBodyAsStream().close();
        }
        if (code >= 200 && code < 300) {
            this.conn.tunnelCreated();
            this.connectMethod = null;
            return true;
        }
        return false;
    }

    private void fakeResponse(HttpMethod method) throws IOException, HttpException {
        LOG.debug((Object)"CONNECT failed, fake the response for the original method");
        if (method instanceof HttpMethodBase) {
            ((HttpMethodBase)method).fakeResponse(this.connectMethod.getStatusLine(), this.connectMethod.getResponseHeaderGroup(), this.conn, this.connectMethod.getResponseBodyAsStream());
            method.getProxyAuthState().setAuthScheme(this.connectMethod.getProxyAuthState().getAuthScheme());
            this.connectMethod = null;
        } else {
            this.releaseConnection = true;
            LOG.warn((Object)"Unable to fake response on method as it is not derived from HttpMethodBase.");
        }
    }

    private boolean processRedirectResponse(HttpMethod method) throws RedirectException {
        Header locationHeader = method.getResponseHeader("location");
        if (locationHeader == null) {
            LOG.error((Object)("Received redirect response " + method.getStatusCode() + " but no location header"));
            return false;
        }
        String location = locationHeader.getValue();
        if (LOG.isDebugEnabled()) {
            LOG.debug((Object)("Redirect requested to location '" + location + "'"));
        }
        URI redirectUri = null;
        URI currentUri = null;
        try {
            currentUri = new URI(this.conn.getProtocol().getScheme(), null, this.conn.getHost(), this.conn.getPort(), method.getPath());
            String charset = method.getParams().getUriCharset();
            redirectUri = new URI(location, true, charset);
            if (redirectUri.isRelativeURI()) {
                if (this.params.isParameterTrue("http.protocol.reject-relative-redirect")) {
                    LOG.warn((Object)("Relative redirect location '" + location + "' not allowed"));
                    return false;
                }
                LOG.debug((Object)"Redirect URI is not absolute - parsing as relative");
                redirectUri = new URI(currentUri, redirectUri);
            } else {
                method.getParams().setDefaults(this.params);
            }
            method.setURI(redirectUri);
            this.hostConfiguration.setHost(redirectUri);
        }
        catch (URIException ex) {
            throw new InvalidRedirectLocationException("Invalid redirect location: " + location, location, ex);
        }
        if (this.params.isParameterFalse("http.protocol.allow-circular-redirects")) {
            if (this.redirectLocations == null) {
                this.redirectLocations = new HashSet();
            }
            this.redirectLocations.add(currentUri);
            try {
                if (redirectUri.hasQuery()) {
                    redirectUri.setQuery(null);
                }
            }
            catch (URIException e) {
                return false;
            }
            if (this.redirectLocations.contains(redirectUri)) {
                throw new CircularRedirectException("Circular redirect to '" + redirectUri + "'");
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug((Object)("Redirecting from '" + currentUri.getEscapedURI() + "' to '" + redirectUri.getEscapedURI()));
        }
        method.getHostAuthState().invalidate();
        method.getProxyAuthState().invalidate();
        return true;
    }

    private boolean processAuthenticationResponse(HttpMethod method) {
        LOG.trace((Object)"enter HttpMethodBase.processAuthenticationResponse(HttpState, HttpConnection)");
        try {
            switch (method.getStatusCode()) {
                case 401: {
                    return this.processWWWAuthChallenge(method);
                }
                case 407: {
                    return this.processProxyAuthChallenge(method);
                }
            }
            return false;
        }
        catch (Exception e) {
            if (LOG.isErrorEnabled()) {
                LOG.error((Object)e.getMessage(), (Throwable)e);
            }
            return false;
        }
    }

    private boolean processWWWAuthChallenge(HttpMethod method) throws MalformedChallengeException, AuthenticationException {
        AuthScheme authscheme;
        AuthState authstate;
        block12: {
            authstate = method.getHostAuthState();
            Map challenges = AuthChallengeParser.parseChallenges(method.getResponseHeaders(WWW_AUTH_CHALLENGE));
            if (challenges.isEmpty()) {
                LOG.debug((Object)"Authentication challenge(s) not found");
                return false;
            }
            authscheme = null;
            try {
                authscheme = this.authProcessor.processChallenge(authstate, challenges);
            }
            catch (AuthChallengeException e) {
                if (!LOG.isWarnEnabled()) break block12;
                LOG.warn((Object)e.getMessage());
            }
        }
        if (authscheme == null) {
            return false;
        }
        String host = method.getParams().getVirtualHost();
        if (host == null) {
            host = this.conn.getHost();
        }
        int port = this.conn.getPort();
        AuthScope authscope = new AuthScope(host, port, authscheme.getRealm(), authscheme.getSchemeName());
        if (LOG.isDebugEnabled()) {
            LOG.debug((Object)("Authentication scope: " + authscope));
        }
        if (authstate.isAuthAttempted() && authscheme.isComplete()) {
            Credentials credentials = this.promptForCredentials(authscheme, method.getParams(), authscope);
            if (credentials == null) {
                if (LOG.isInfoEnabled()) {
                    LOG.info((Object)("Failure authenticating with " + authscope));
                }
                return false;
            }
            return true;
        }
        authstate.setAuthAttempted(true);
        Credentials credentials = this.state.getCredentials(authscope);
        if (credentials == null) {
            credentials = this.promptForCredentials(authscheme, method.getParams(), authscope);
        }
        if (credentials == null) {
            if (LOG.isInfoEnabled()) {
                LOG.info((Object)("No credentials available for " + authscope));
            }
            return false;
        }
        return true;
    }

    private boolean processProxyAuthChallenge(HttpMethod method) throws MalformedChallengeException, AuthenticationException {
        AuthScheme authscheme;
        AuthState authstate;
        block11: {
            authstate = method.getProxyAuthState();
            Map proxyChallenges = AuthChallengeParser.parseChallenges(method.getResponseHeaders(PROXY_AUTH_CHALLENGE));
            if (proxyChallenges.isEmpty()) {
                LOG.debug((Object)"Proxy authentication challenge(s) not found");
                return false;
            }
            authscheme = null;
            try {
                authscheme = this.authProcessor.processChallenge(authstate, proxyChallenges);
            }
            catch (AuthChallengeException e) {
                if (!LOG.isWarnEnabled()) break block11;
                LOG.warn((Object)e.getMessage());
            }
        }
        if (authscheme == null) {
            return false;
        }
        AuthScope authscope = new AuthScope(this.conn.getProxyHost(), this.conn.getProxyPort(), authscheme.getRealm(), authscheme.getSchemeName());
        if (LOG.isDebugEnabled()) {
            LOG.debug((Object)("Proxy authentication scope: " + authscope));
        }
        if (authstate.isAuthAttempted() && authscheme.isComplete()) {
            Credentials credentials = this.promptForProxyCredentials(authscheme, method.getParams(), authscope);
            if (credentials == null) {
                if (LOG.isInfoEnabled()) {
                    LOG.info((Object)("Failure authenticating with " + authscope));
                }
                return false;
            }
            return true;
        }
        authstate.setAuthAttempted(true);
        Credentials credentials = this.state.getProxyCredentials(authscope);
        if (credentials == null) {
            credentials = this.promptForProxyCredentials(authscheme, method.getParams(), authscope);
        }
        if (credentials == null) {
            if (LOG.isInfoEnabled()) {
                LOG.info((Object)("No credentials available for " + authscope));
            }
            return false;
        }
        return true;
    }

    private boolean isRedirectNeeded(HttpMethod method) {
        switch (method.getStatusCode()) {
            case 301: 
            case 302: 
            case 303: 
            case 307: {
                LOG.debug((Object)"Redirect required");
                return method.getFollowRedirects();
            }
        }
        return false;
    }

    private boolean isAuthenticationNeeded(HttpMethod method) {
        method.getHostAuthState().setAuthRequested(method.getStatusCode() == 401);
        method.getProxyAuthState().setAuthRequested(method.getStatusCode() == 407);
        if (method.getHostAuthState().isAuthRequested() || method.getProxyAuthState().isAuthRequested()) {
            LOG.debug((Object)"Authorization required");
            if (method.getDoAuthentication()) {
                return true;
            }
            LOG.info((Object)"Authentication requested but doAuthentication is disabled");
            return false;
        }
        return false;
    }

    private Credentials promptForCredentials(AuthScheme authScheme, HttpParams params, AuthScope authscope) {
        LOG.debug((Object)"Credentials required");
        Credentials creds = null;
        CredentialsProvider credProvider = (CredentialsProvider)params.getParameter("http.authentication.credential-provider");
        if (credProvider != null) {
            try {
                creds = credProvider.getCredentials(authScheme, authscope.getHost(), authscope.getPort(), false);
            }
            catch (CredentialsNotAvailableException e) {
                LOG.warn((Object)e.getMessage());
            }
            if (creds != null) {
                this.state.setCredentials(authscope, creds);
                if (LOG.isDebugEnabled()) {
                    LOG.debug((Object)(authscope + " new credentials given"));
                }
            }
        } else {
            LOG.debug((Object)"Credentials provider not available");
        }
        return creds;
    }

    private Credentials promptForProxyCredentials(AuthScheme authScheme, HttpParams params, AuthScope authscope) {
        LOG.debug((Object)"Proxy credentials required");
        Credentials creds = null;
        CredentialsProvider credProvider = (CredentialsProvider)params.getParameter("http.authentication.credential-provider");
        if (credProvider != null) {
            try {
                creds = credProvider.getCredentials(authScheme, authscope.getHost(), authscope.getPort(), true);
            }
            catch (CredentialsNotAvailableException e) {
                LOG.warn((Object)e.getMessage());
            }
            if (creds != null) {
                this.state.setProxyCredentials(authscope, creds);
                if (LOG.isDebugEnabled()) {
                    LOG.debug((Object)(authscope + " new credentials given"));
                }
            }
        } else {
            LOG.debug((Object)"Proxy credentials provider not available");
        }
        return creds;
    }

    public HostConfiguration getHostConfiguration() {
        return this.hostConfiguration;
    }

    public HttpState getState() {
        return this.state;
    }

    public HttpConnectionManager getConnectionManager() {
        return this.connectionManager;
    }

    public HttpParams getParams() {
        return this.params;
    }
}

