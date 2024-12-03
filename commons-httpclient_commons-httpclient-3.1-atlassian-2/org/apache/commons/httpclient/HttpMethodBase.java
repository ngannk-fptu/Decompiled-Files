/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.commons.httpclient;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.util.Collection;
import java.util.Locale;
import org.apache.commons.httpclient.AutoCloseInputStream;
import org.apache.commons.httpclient.ChunkedInputStream;
import org.apache.commons.httpclient.ContentLengthInputStream;
import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HeaderElement;
import org.apache.commons.httpclient.HeaderGroup;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpConnection;
import org.apache.commons.httpclient.HttpContentTooLargeException;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpHost;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpParser;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.HttpVersion;
import org.apache.commons.httpclient.MethodRetryHandler;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.NoHttpResponseException;
import org.apache.commons.httpclient.ProtocolException;
import org.apache.commons.httpclient.ResponseConsumedWatcher;
import org.apache.commons.httpclient.StatusLine;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.Wire;
import org.apache.commons.httpclient.WireLogInputStream;
import org.apache.commons.httpclient.auth.AuthState;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.cookie.CookieSpec;
import org.apache.commons.httpclient.cookie.CookieVersionSupport;
import org.apache.commons.httpclient.cookie.MalformedCookieException;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.util.EncodingUtil;
import org.apache.commons.httpclient.util.ExceptionUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class HttpMethodBase
implements HttpMethod {
    private static final Log LOG = LogFactory.getLog(HttpMethodBase.class);
    private HeaderGroup requestHeaders = new HeaderGroup();
    protected StatusLine statusLine = null;
    private HeaderGroup responseHeaders = new HeaderGroup();
    private HeaderGroup responseTrailerHeaders = new HeaderGroup();
    private String path = null;
    private String queryString = null;
    private InputStream responseStream = null;
    private HttpConnection responseConnection = null;
    private byte[] responseBody = null;
    private boolean followRedirects = false;
    private boolean doAuthentication = true;
    private HttpMethodParams params = new HttpMethodParams();
    private AuthState hostAuthState = new AuthState();
    private AuthState proxyAuthState = new AuthState();
    private boolean used = false;
    private int recoverableExceptionCount = 0;
    private HttpHost httphost = null;
    private MethodRetryHandler methodRetryHandler;
    private boolean connectionCloseForced = false;
    private static final int RESPONSE_WAIT_TIME_MS = 3000;
    protected HttpVersion effectiveVersion = null;
    private volatile boolean aborted = false;
    private boolean requestSent = false;
    private CookieSpec cookiespec = null;
    private static final int DEFAULT_INITIAL_BUFFER_SIZE = 4096;

    public HttpMethodBase() {
    }

    public HttpMethodBase(String uri) throws IllegalArgumentException, IllegalStateException {
        try {
            if (uri == null || uri.equals("")) {
                uri = "/";
            }
            String charset = this.getParams().getUriCharset();
            this.setURI(new URI(uri, true, charset));
        }
        catch (URIException e) {
            throw new IllegalArgumentException("Invalid uri '" + uri + "': " + e.getMessage());
        }
    }

    @Override
    public abstract String getName();

    @Override
    public URI getURI() throws URIException {
        StringBuffer buffer = new StringBuffer();
        if (this.httphost != null) {
            buffer.append(this.httphost.getProtocol().getScheme());
            buffer.append("://");
            buffer.append(this.httphost.getHostName());
            int port = this.httphost.getPort();
            if (port != -1 && port != this.httphost.getProtocol().getDefaultPort()) {
                buffer.append(":");
                buffer.append(port);
            }
        }
        buffer.append(this.path);
        if (this.queryString != null) {
            buffer.append('?');
            buffer.append(this.queryString);
        }
        String charset = this.getParams().getUriCharset();
        return new URI(buffer.toString(), true, charset);
    }

    @Override
    public void setURI(URI uri) throws URIException {
        if (uri.isAbsoluteURI()) {
            this.httphost = new HttpHost(uri);
        }
        this.setPath(uri.getPath() == null ? "/" : uri.getEscapedPath());
        this.setQueryString(uri.getEscapedQuery());
    }

    @Override
    public void setFollowRedirects(boolean followRedirects) {
        this.followRedirects = followRedirects;
    }

    @Override
    public boolean getFollowRedirects() {
        return this.followRedirects;
    }

    public void setHttp11(boolean http11) {
        if (http11) {
            this.params.setVersion(HttpVersion.HTTP_1_1);
        } else {
            this.params.setVersion(HttpVersion.HTTP_1_0);
        }
    }

    @Override
    public boolean getDoAuthentication() {
        return this.doAuthentication;
    }

    @Override
    public void setDoAuthentication(boolean doAuthentication) {
        this.doAuthentication = doAuthentication;
    }

    public boolean isHttp11() {
        return this.params.getVersion().equals(HttpVersion.HTTP_1_1);
    }

    @Override
    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public void addRequestHeader(Header header) {
        LOG.trace((Object)"HttpMethodBase.addRequestHeader(Header)");
        if (header == null) {
            LOG.debug((Object)"null header value ignored");
        } else {
            this.getRequestHeaderGroup().addHeader(header);
        }
    }

    @Override
    public void addResponseFooter(Header footer) {
        this.getResponseTrailerHeaderGroup().addHeader(footer);
    }

    @Override
    public String getPath() {
        return this.path == null || this.path.equals("") ? "/" : this.path;
    }

    @Override
    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    @Override
    public void setQueryString(NameValuePair[] params) {
        LOG.trace((Object)"enter HttpMethodBase.setQueryString(NameValuePair[])");
        this.queryString = EncodingUtil.formUrlEncode(params, "UTF-8");
    }

    @Override
    public String getQueryString() {
        return this.queryString;
    }

    @Override
    public void setRequestHeader(String headerName, String headerValue) {
        Header header = new Header(headerName, headerValue);
        this.setRequestHeader(header);
    }

    @Override
    public void setRequestHeader(Header header) {
        Header[] headers = this.getRequestHeaderGroup().getHeaders(header.getName());
        for (int i = 0; i < headers.length; ++i) {
            this.getRequestHeaderGroup().removeHeader(headers[i]);
        }
        this.getRequestHeaderGroup().addHeader(header);
    }

    @Override
    public Header getRequestHeader(String headerName) {
        if (headerName == null) {
            return null;
        }
        return this.getRequestHeaderGroup().getCondensedHeader(headerName);
    }

    @Override
    public Header[] getRequestHeaders() {
        return this.getRequestHeaderGroup().getAllHeaders();
    }

    @Override
    public Header[] getRequestHeaders(String headerName) {
        return this.getRequestHeaderGroup().getHeaders(headerName);
    }

    protected HeaderGroup getRequestHeaderGroup() {
        return this.requestHeaders;
    }

    protected HeaderGroup getResponseTrailerHeaderGroup() {
        return this.responseTrailerHeaders;
    }

    protected HeaderGroup getResponseHeaderGroup() {
        return this.responseHeaders;
    }

    @Override
    public Header[] getResponseHeaders(String headerName) {
        return this.getResponseHeaderGroup().getHeaders(headerName);
    }

    @Override
    public int getStatusCode() {
        return this.statusLine.getStatusCode();
    }

    @Override
    public StatusLine getStatusLine() {
        return this.statusLine;
    }

    private boolean responseAvailable() {
        return this.responseBody != null || this.responseStream != null;
    }

    @Override
    public Header[] getResponseHeaders() {
        return this.getResponseHeaderGroup().getAllHeaders();
    }

    @Override
    public Header getResponseHeader(String headerName) {
        if (headerName == null) {
            return null;
        }
        return this.getResponseHeaderGroup().getCondensedHeader(headerName);
    }

    public long getResponseContentLength() {
        Header[] headers = this.getResponseHeaderGroup().getHeaders("Content-Length");
        if (headers.length == 0) {
            return -1L;
        }
        if (headers.length > 1) {
            LOG.warn((Object)"Multiple content-length headers detected");
        }
        for (int i = headers.length - 1; i >= 0; --i) {
            Header header = headers[i];
            try {
                return Long.parseLong(header.getValue());
            }
            catch (NumberFormatException e) {
                if (!LOG.isWarnEnabled()) continue;
                LOG.warn((Object)("Invalid content-length value: " + e.getMessage()));
                continue;
            }
        }
        return -1L;
    }

    @Override
    public byte[] getResponseBody() throws IOException {
        InputStream instream;
        if (this.responseBody == null && (instream = this.getResponseBodyAsStream()) != null) {
            int len;
            long contentLength = this.getResponseContentLength();
            if (contentLength > Integer.MAX_VALUE) {
                throw new IOException("Content too large to be buffered: " + contentLength + " bytes");
            }
            int limit = this.getParams().getIntParameter("http.method.response.buffer.warnlimit", 0x100000);
            if (contentLength == -1L || contentLength > (long)limit) {
                LOG.warn((Object)"Going to buffer response body of large or unknown size. Using getResponseBodyAsStream instead is recommended.");
            }
            LOG.debug((Object)"Buffering response body");
            ByteArrayOutputStream outstream = new ByteArrayOutputStream(contentLength > 0L ? (int)contentLength : 4096);
            byte[] buffer = new byte[4096];
            while ((len = instream.read(buffer)) > 0) {
                outstream.write(buffer, 0, len);
            }
            outstream.close();
            this.setResponseStream(null);
            this.responseBody = outstream.toByteArray();
        }
        return this.responseBody;
    }

    public byte[] getResponseBody(int maxlen) throws IOException {
        InputStream instream;
        if (maxlen < 0) {
            throw new IllegalArgumentException("maxlen must be positive");
        }
        if (this.responseBody == null && (instream = this.getResponseBodyAsStream()) != null) {
            int len;
            long contentLength = this.getResponseContentLength();
            if (contentLength != -1L && contentLength > (long)maxlen) {
                throw new HttpContentTooLargeException("Content-Length is " + contentLength, maxlen);
            }
            LOG.debug((Object)"Buffering response body");
            ByteArrayOutputStream rawdata = new ByteArrayOutputStream(contentLength > 0L ? (int)contentLength : 4096);
            byte[] buffer = new byte[2048];
            int pos = 0;
            while ((len = instream.read(buffer, 0, Math.min(buffer.length, maxlen - pos))) != -1) {
                rawdata.write(buffer, 0, len);
                if ((pos += len) < maxlen) continue;
            }
            this.setResponseStream(null);
            if (pos == maxlen && instream.read() != -1) {
                throw new HttpContentTooLargeException("Content-Length not known but larger than " + maxlen, maxlen);
            }
            this.responseBody = rawdata.toByteArray();
        }
        return this.responseBody;
    }

    @Override
    public InputStream getResponseBodyAsStream() throws IOException {
        if (this.responseStream != null) {
            return this.responseStream;
        }
        if (this.responseBody != null) {
            ByteArrayInputStream byteResponseStream = new ByteArrayInputStream(this.responseBody);
            LOG.debug((Object)"re-creating response stream from byte array");
            return byteResponseStream;
        }
        return null;
    }

    @Override
    public String getResponseBodyAsString() throws IOException {
        byte[] rawdata = null;
        if (this.responseAvailable()) {
            rawdata = this.getResponseBody();
        }
        if (rawdata != null) {
            return EncodingUtil.getString(rawdata, this.getResponseCharSet());
        }
        return null;
    }

    public String getResponseBodyAsString(int maxlen) throws IOException {
        if (maxlen < 0) {
            throw new IllegalArgumentException("maxlen must be positive");
        }
        byte[] rawdata = null;
        if (this.responseAvailable()) {
            rawdata = this.getResponseBody(maxlen);
        }
        if (rawdata != null) {
            return EncodingUtil.getString(rawdata, this.getResponseCharSet());
        }
        return null;
    }

    @Override
    public Header[] getResponseFooters() {
        return this.getResponseTrailerHeaderGroup().getAllHeaders();
    }

    @Override
    public Header getResponseFooter(String footerName) {
        if (footerName == null) {
            return null;
        }
        return this.getResponseTrailerHeaderGroup().getCondensedHeader(footerName);
    }

    protected void setResponseStream(InputStream responseStream) {
        this.responseStream = responseStream;
    }

    protected InputStream getResponseStream() {
        return this.responseStream;
    }

    @Override
    public String getStatusText() {
        return this.statusLine.getReasonPhrase();
    }

    @Override
    public void setStrictMode(boolean strictMode) {
        if (strictMode) {
            this.params.makeStrict();
        } else {
            this.params.makeLenient();
        }
    }

    @Override
    public boolean isStrictMode() {
        return false;
    }

    @Override
    public void addRequestHeader(String headerName, String headerValue) {
        this.addRequestHeader(new Header(headerName, headerValue));
    }

    protected boolean isConnectionCloseForced() {
        return this.connectionCloseForced;
    }

    protected void setConnectionCloseForced(boolean b) {
        if (LOG.isDebugEnabled()) {
            LOG.debug((Object)("Force-close connection: " + b));
        }
        this.connectionCloseForced = b;
    }

    protected boolean shouldCloseConnection(HttpConnection conn) {
        if (this.isConnectionCloseForced()) {
            LOG.debug((Object)"Should force-close connection.");
            return true;
        }
        Header connectionHeader = null;
        if (!conn.isTransparent()) {
            connectionHeader = this.responseHeaders.getFirstHeader("proxy-connection");
        }
        if (connectionHeader == null) {
            connectionHeader = this.responseHeaders.getFirstHeader("connection");
        }
        if (connectionHeader == null) {
            connectionHeader = this.requestHeaders.getFirstHeader("connection");
        }
        if (connectionHeader != null) {
            if (connectionHeader.getValue().equalsIgnoreCase("close")) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug((Object)("Should close connection in response to directive: " + connectionHeader.getValue()));
                }
                return true;
            }
            if (connectionHeader.getValue().equalsIgnoreCase("keep-alive")) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug((Object)("Should NOT close connection in response to directive: " + connectionHeader.getValue()));
                }
                return false;
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug((Object)("Unknown directive: " + connectionHeader.toExternalForm()));
            }
        }
        LOG.debug((Object)"Resorting to protocol version default close connection policy");
        if (this.effectiveVersion.greaterEquals(HttpVersion.HTTP_1_1)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug((Object)("Should NOT close connection, using " + this.effectiveVersion.toString()));
            }
        } else if (LOG.isDebugEnabled()) {
            LOG.debug((Object)("Should close connection, using " + this.effectiveVersion.toString()));
        }
        return this.effectiveVersion.lessEquals(HttpVersion.HTTP_1_0);
    }

    private void checkExecuteConditions(HttpState state, HttpConnection conn) throws HttpException {
        if (state == null) {
            throw new IllegalArgumentException("HttpState parameter may not be null");
        }
        if (conn == null) {
            throw new IllegalArgumentException("HttpConnection parameter may not be null");
        }
        if (this.aborted) {
            throw new IllegalStateException("Method has been aborted");
        }
        if (!this.validate()) {
            throw new ProtocolException("HttpMethodBase object not valid");
        }
    }

    @Override
    public int execute(HttpState state, HttpConnection conn) throws HttpException, IOException {
        LOG.trace((Object)"enter HttpMethodBase.execute(HttpState, HttpConnection)");
        this.responseConnection = conn;
        this.checkExecuteConditions(state, conn);
        this.statusLine = null;
        this.connectionCloseForced = false;
        conn.setLastResponseInputStream(null);
        if (this.effectiveVersion == null) {
            this.effectiveVersion = this.params.getVersion();
        }
        this.writeRequest(state, conn);
        this.requestSent = true;
        this.readResponse(state, conn);
        this.used = true;
        return this.statusLine.getStatusCode();
    }

    @Override
    public void abort() {
        if (this.aborted) {
            return;
        }
        this.aborted = true;
        HttpConnection conn = this.responseConnection;
        if (conn != null) {
            conn.close();
        }
    }

    @Override
    public boolean hasBeenUsed() {
        return this.used;
    }

    @Override
    public void recycle() {
        LOG.trace((Object)"enter HttpMethodBase.recycle()");
        this.releaseConnection();
        this.path = null;
        this.followRedirects = false;
        this.doAuthentication = true;
        this.queryString = null;
        this.getRequestHeaderGroup().clear();
        this.getResponseHeaderGroup().clear();
        this.getResponseTrailerHeaderGroup().clear();
        this.statusLine = null;
        this.effectiveVersion = null;
        this.aborted = false;
        this.used = false;
        this.params = new HttpMethodParams();
        this.responseBody = null;
        this.recoverableExceptionCount = 0;
        this.connectionCloseForced = false;
        this.hostAuthState.invalidate();
        this.proxyAuthState.invalidate();
        this.cookiespec = null;
        this.requestSent = false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void releaseConnection() {
        try {
            if (this.responseStream != null) {
                try {
                    this.responseStream.close();
                }
                catch (IOException iOException) {
                    // empty catch block
                }
            }
        }
        finally {
            this.ensureConnectionRelease();
        }
    }

    @Override
    public void removeRequestHeader(String headerName) {
        Header[] headers = this.getRequestHeaderGroup().getHeaders(headerName);
        for (int i = 0; i < headers.length; ++i) {
            this.getRequestHeaderGroup().removeHeader(headers[i]);
        }
    }

    @Override
    public void removeRequestHeader(Header header) {
        if (header == null) {
            return;
        }
        this.getRequestHeaderGroup().removeHeader(header);
    }

    @Override
    public boolean validate() {
        return true;
    }

    private CookieSpec getCookieSpec(HttpState state) {
        if (this.cookiespec == null) {
            int i = state.getCookiePolicy();
            this.cookiespec = i == -1 ? CookiePolicy.getCookieSpec(this.params.getCookiePolicy()) : CookiePolicy.getSpecByPolicy(i);
            this.cookiespec.setValidDateFormats((Collection)this.params.getParameter("http.dateparser.patterns"));
        }
        return this.cookiespec;
    }

    protected void addCookieRequestHeader(HttpState state, HttpConnection conn) throws IOException, HttpException {
        Cookie[] cookies;
        LOG.trace((Object)"enter HttpMethodBase.addCookieRequestHeader(HttpState, HttpConnection)");
        Header[] cookieheaders = this.getRequestHeaderGroup().getHeaders("Cookie");
        for (int i = 0; i < cookieheaders.length; ++i) {
            Header cookieheader = cookieheaders[i];
            if (!cookieheader.isAutogenerated()) continue;
            this.getRequestHeaderGroup().removeHeader(cookieheader);
        }
        CookieSpec matcher = this.getCookieSpec(state);
        String host = this.params.getVirtualHost();
        if (host == null) {
            host = conn.getHost();
        }
        if ((cookies = matcher.match(host, conn.getPort(), this.getPath(), conn.isSecure(), state.getCookies())) != null && cookies.length > 0) {
            if (this.getParams().isParameterTrue("http.protocol.single-cookie-header")) {
                String s = matcher.formatCookies(cookies);
                this.getRequestHeaderGroup().addHeader(new Header("Cookie", s, true));
            } else {
                for (int i = 0; i < cookies.length; ++i) {
                    String s = matcher.formatCookie(cookies[i]);
                    this.getRequestHeaderGroup().addHeader(new Header("Cookie", s, true));
                }
            }
            if (matcher instanceof CookieVersionSupport) {
                CookieVersionSupport versupport = (CookieVersionSupport)((Object)matcher);
                int ver = versupport.getVersion();
                boolean needVersionHeader = false;
                for (int i = 0; i < cookies.length; ++i) {
                    if (ver == cookies[i].getVersion()) continue;
                    needVersionHeader = true;
                }
                if (needVersionHeader) {
                    this.getRequestHeaderGroup().addHeader(versupport.getVersionHeader());
                }
            }
        }
    }

    protected void addHostRequestHeader(HttpState state, HttpConnection conn) throws IOException, HttpException {
        LOG.trace((Object)"enter HttpMethodBase.addHostRequestHeader(HttpState, HttpConnection)");
        String host = this.params.getVirtualHost();
        if (host != null) {
            LOG.debug((Object)("Using virtual host name: " + host));
        } else {
            host = conn.getHost();
        }
        int port = conn.getPort();
        if (LOG.isDebugEnabled()) {
            LOG.debug((Object)"Adding Host request header");
        }
        if (conn.getProtocol().getDefaultPort() != port) {
            host = host + ":" + port;
        }
        this.setRequestHeader("Host", host);
    }

    protected void addProxyConnectionHeader(HttpState state, HttpConnection conn) throws IOException, HttpException {
        LOG.trace((Object)"enter HttpMethodBase.addProxyConnectionHeader(HttpState, HttpConnection)");
        if (!conn.isTransparent() && this.getRequestHeader("Proxy-Connection") == null) {
            this.addRequestHeader("Proxy-Connection", "Keep-Alive");
        }
    }

    protected void addRequestHeaders(HttpState state, HttpConnection conn) throws IOException, HttpException {
        LOG.trace((Object)"enter HttpMethodBase.addRequestHeaders(HttpState, HttpConnection)");
        this.addUserAgentRequestHeader(state, conn);
        this.addHostRequestHeader(state, conn);
        this.addCookieRequestHeader(state, conn);
        this.addProxyConnectionHeader(state, conn);
    }

    protected void addUserAgentRequestHeader(HttpState state, HttpConnection conn) throws IOException, HttpException {
        LOG.trace((Object)"enter HttpMethodBase.addUserAgentRequestHeaders(HttpState, HttpConnection)");
        if (this.getRequestHeader("User-Agent") == null) {
            String agent = (String)this.getParams().getParameter("http.useragent");
            if (agent == null) {
                agent = "Jakarta Commons-HttpClient";
            }
            this.setRequestHeader("User-Agent", agent);
        }
    }

    protected void checkNotUsed() throws IllegalStateException {
        if (this.used) {
            throw new IllegalStateException("Already used.");
        }
    }

    protected void checkUsed() throws IllegalStateException {
        if (!this.used) {
            throw new IllegalStateException("Not Used.");
        }
    }

    protected static String generateRequestLine(HttpConnection connection, String name, String requestPath, String query, String version) {
        LOG.trace((Object)"enter HttpMethodBase.generateRequestLine(HttpConnection, String, String, String, String)");
        StringBuffer buf = new StringBuffer();
        buf.append(name);
        buf.append(" ");
        if (!connection.isTransparent()) {
            Protocol protocol = connection.getProtocol();
            buf.append(protocol.getScheme().toLowerCase(Locale.ENGLISH));
            buf.append("://");
            buf.append(connection.getHost());
            if (connection.getPort() != -1 && connection.getPort() != protocol.getDefaultPort()) {
                buf.append(":");
                buf.append(connection.getPort());
            }
        }
        if (requestPath == null) {
            buf.append("/");
        } else {
            if (!connection.isTransparent() && !requestPath.startsWith("/")) {
                buf.append("/");
            }
            buf.append(requestPath);
        }
        if (query != null) {
            if (query.indexOf("?") != 0) {
                buf.append("?");
            }
            buf.append(query);
        }
        buf.append(" ");
        buf.append(version);
        buf.append("\r\n");
        return buf.toString();
    }

    protected void processResponseBody(HttpState state, HttpConnection conn) {
    }

    protected void processResponseHeaders(HttpState state, HttpConnection conn) {
        CookieVersionSupport versupport;
        LOG.trace((Object)"enter HttpMethodBase.processResponseHeaders(HttpState, HttpConnection)");
        CookieSpec parser = this.getCookieSpec(state);
        Header[] headers = this.getResponseHeaderGroup().getHeaders("set-cookie");
        this.processCookieHeaders(parser, headers, state, conn);
        if (parser instanceof CookieVersionSupport && (versupport = (CookieVersionSupport)((Object)parser)).getVersion() > 0) {
            headers = this.getResponseHeaderGroup().getHeaders("set-cookie2");
            this.processCookieHeaders(parser, headers, state, conn);
        }
    }

    protected void processCookieHeaders(CookieSpec parser, Header[] headers, HttpState state, HttpConnection conn) {
        LOG.trace((Object)"enter HttpMethodBase.processCookieHeaders(Header[], HttpState, HttpConnection)");
        String host = this.params.getVirtualHost();
        if (host == null) {
            host = conn.getHost();
        }
        for (int i = 0; i < headers.length; ++i) {
            Cookie[] cookies;
            block7: {
                Header header = headers[i];
                cookies = null;
                try {
                    cookies = parser.parse(host, conn.getPort(), this.getPath(), conn.isSecure(), header);
                }
                catch (MalformedCookieException e) {
                    if (!LOG.isWarnEnabled()) break block7;
                    LOG.warn((Object)("Invalid cookie header: \"" + header.getValue() + "\". " + e.getMessage()));
                }
            }
            if (cookies == null) continue;
            for (int j = 0; j < cookies.length; ++j) {
                Cookie cookie = cookies[j];
                try {
                    parser.validate(host, conn.getPort(), this.getPath(), conn.isSecure(), cookie);
                    state.addCookie(cookie);
                    if (!LOG.isDebugEnabled()) continue;
                    LOG.debug((Object)("Cookie accepted: \"" + parser.formatCookie(cookie) + "\""));
                    continue;
                }
                catch (MalformedCookieException e) {
                    if (!LOG.isWarnEnabled()) continue;
                    LOG.warn((Object)("Cookie rejected: \"" + parser.formatCookie(cookie) + "\". " + e.getMessage()));
                }
            }
        }
    }

    protected void processStatusLine(HttpState state, HttpConnection conn) {
    }

    protected void readResponse(HttpState state, HttpConnection conn) throws IOException, HttpException {
        LOG.trace((Object)"enter HttpMethodBase.readResponse(HttpState, HttpConnection)");
        while (this.statusLine == null) {
            this.readStatusLine(state, conn);
            this.processStatusLine(state, conn);
            this.readResponseHeaders(state, conn);
            this.processResponseHeaders(state, conn);
            int status = this.statusLine.getStatusCode();
            if (status < 100 || status >= 200) continue;
            if (LOG.isInfoEnabled()) {
                LOG.info((Object)("Discarding unexpected response: " + this.statusLine.toString()));
            }
            this.statusLine = null;
        }
        this.readResponseBody(state, conn);
        this.processResponseBody(state, conn);
    }

    protected void readResponseBody(HttpState state, HttpConnection conn) throws IOException, HttpException {
        LOG.trace((Object)"enter HttpMethodBase.readResponseBody(HttpState, HttpConnection)");
        InputStream stream = this.readResponseBody(conn);
        if (stream == null) {
            this.responseBodyConsumed();
        } else {
            conn.setLastResponseInputStream(stream);
            this.setResponseStream(stream);
        }
    }

    private InputStream readResponseBody(HttpConnection conn) throws HttpException, IOException {
        LOG.trace((Object)"enter HttpMethodBase.readResponseBody(HttpConnection)");
        this.responseBody = null;
        InputStream is = conn.getResponseInputStream();
        if (Wire.CONTENT_WIRE.enabled()) {
            is = new WireLogInputStream(is, Wire.CONTENT_WIRE);
        }
        boolean canHaveBody = HttpMethodBase.canResponseHaveBody(this.statusLine.getStatusCode());
        InputStream result = null;
        Header transferEncodingHeader = this.responseHeaders.getFirstHeader("Transfer-Encoding");
        if (transferEncodingHeader != null) {
            HeaderElement[] encodings;
            int len;
            String transferEncoding = transferEncodingHeader.getValue();
            if (!"chunked".equalsIgnoreCase(transferEncoding) && !"identity".equalsIgnoreCase(transferEncoding) && LOG.isWarnEnabled()) {
                LOG.warn((Object)("Unsupported transfer encoding: " + transferEncoding));
            }
            if ((len = (encodings = transferEncodingHeader.getElements()).length) > 0 && "chunked".equalsIgnoreCase(encodings[len - 1].getName())) {
                if (conn.isResponseAvailable(conn.getParams().getSoTimeout())) {
                    result = new ChunkedInputStream(is, this);
                } else {
                    if (this.getParams().isParameterTrue("http.protocol.strict-transfer-encoding")) {
                        throw new ProtocolException("Chunk-encoded body declared but not sent");
                    }
                    LOG.warn((Object)"Chunk-encoded body missing");
                }
            } else {
                LOG.info((Object)"Response content is not chunk-encoded");
                this.setConnectionCloseForced(true);
                result = is;
            }
        } else {
            long expectedLength = this.getResponseContentLength();
            if (expectedLength == -1L) {
                if (canHaveBody && this.effectiveVersion.greaterEquals(HttpVersion.HTTP_1_1)) {
                    Header connectionHeader = this.responseHeaders.getFirstHeader("Connection");
                    String connectionDirective = null;
                    if (connectionHeader != null) {
                        connectionDirective = connectionHeader.getValue();
                    }
                    if (!"close".equalsIgnoreCase(connectionDirective)) {
                        LOG.info((Object)"Response content length is not known");
                        this.setConnectionCloseForced(true);
                    }
                }
                result = is;
            } else {
                result = new ContentLengthInputStream(is, expectedLength);
            }
        }
        if (!canHaveBody) {
            result = null;
        }
        if (result != null) {
            result = new AutoCloseInputStream(result, new ResponseConsumedWatcher(){

                @Override
                public void responseConsumed() {
                    HttpMethodBase.this.responseBodyConsumed();
                }
            });
        }
        return result;
    }

    protected void readResponseHeaders(HttpState state, HttpConnection conn) throws IOException, HttpException {
        LOG.trace((Object)"enter HttpMethodBase.readResponseHeaders(HttpState,HttpConnection)");
        this.getResponseHeaderGroup().clear();
        Header[] headers = HttpParser.parseHeaders(conn.getResponseInputStream(), this.getParams().getHttpElementCharset());
        this.getResponseHeaderGroup().setHeaders(headers);
    }

    protected void readStatusLine(HttpState state, HttpConnection conn) throws IOException, HttpException {
        String s;
        LOG.trace((Object)"enter HttpMethodBase.readStatusLine(HttpState, HttpConnection)");
        int maxGarbageLines = this.getParams().getIntParameter("http.protocol.status-line-garbage-limit", Integer.MAX_VALUE);
        int count = 0;
        while (true) {
            if ((s = conn.readLine(this.getParams().getHttpElementCharset())) == null && count == 0) {
                throw new NoHttpResponseException("The server " + conn.getHost() + " failed to respond");
            }
            if (s != null && StatusLine.startsWithHTTP(s)) break;
            if (s == null || count >= maxGarbageLines) {
                throw new ProtocolException("The server " + conn.getHost() + " failed to respond with a valid HTTP response");
            }
            ++count;
        }
        this.statusLine = new StatusLine(s);
        String versionStr = this.statusLine.getHttpVersion();
        if (this.getParams().isParameterFalse("http.protocol.unambiguous-statusline") && versionStr.equals("HTTP")) {
            this.getParams().setVersion(HttpVersion.HTTP_1_0);
            if (LOG.isWarnEnabled()) {
                LOG.warn((Object)("Ambiguous status line (HTTP protocol version missing):" + this.statusLine.toString()));
            }
        } else {
            this.effectiveVersion = HttpVersion.parse(versionStr);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void writeRequest(HttpState state, HttpConnection conn) throws IOException, HttpException {
        LOG.trace((Object)"enter HttpMethodBase.writeRequest(HttpState, HttpConnection)");
        this.writeRequestLine(state, conn);
        this.writeRequestHeaders(state, conn);
        conn.writeLine();
        if (Wire.HEADER_WIRE.enabled()) {
            Wire.HEADER_WIRE.output("\r\n");
        }
        HttpVersion ver = this.getParams().getVersion();
        Header expectheader = this.getRequestHeader("Expect");
        String expectvalue = null;
        if (expectheader != null) {
            expectvalue = expectheader.getValue();
        }
        if (expectvalue != null && expectvalue.compareToIgnoreCase("100-continue") == 0) {
            if (ver.greaterEquals(HttpVersion.HTTP_1_1)) {
                conn.flushRequestOutputStream();
                int readTimeout = conn.getParams().getSoTimeout();
                try {
                    conn.setSocketTimeout(3000);
                    this.readStatusLine(state, conn);
                    this.processStatusLine(state, conn);
                    this.readResponseHeaders(state, conn);
                    this.processResponseHeaders(state, conn);
                    if (this.statusLine.getStatusCode() == 100) {
                        this.statusLine = null;
                        LOG.debug((Object)"OK to continue received");
                    }
                    return;
                }
                catch (InterruptedIOException e) {
                    if (!ExceptionUtil.isSocketTimeoutException(e)) {
                        throw e;
                    }
                    this.removeRequestHeader("Expect");
                    LOG.info((Object)"100 (continue) read timeout. Resume sending the request");
                }
                finally {
                    conn.setSocketTimeout(readTimeout);
                }
            } else {
                this.removeRequestHeader("Expect");
                LOG.info((Object)"'Expect: 100-continue' handshake is only supported by HTTP/1.1 or higher");
            }
        }
        this.writeRequestBody(state, conn);
        conn.flushRequestOutputStream();
    }

    protected boolean writeRequestBody(HttpState state, HttpConnection conn) throws IOException, HttpException {
        return true;
    }

    protected void writeRequestHeaders(HttpState state, HttpConnection conn) throws IOException, HttpException {
        LOG.trace((Object)"enter HttpMethodBase.writeRequestHeaders(HttpState,HttpConnection)");
        this.addRequestHeaders(state, conn);
        String charset = this.getParams().getHttpElementCharset();
        Header[] headers = this.getRequestHeaders();
        for (int i = 0; i < headers.length; ++i) {
            String s = headers[i].toExternalForm();
            if (Wire.HEADER_WIRE.enabled()) {
                Wire.HEADER_WIRE.output(s);
            }
            conn.print(s, charset);
        }
    }

    protected void writeRequestLine(HttpState state, HttpConnection conn) throws IOException, HttpException {
        LOG.trace((Object)"enter HttpMethodBase.writeRequestLine(HttpState, HttpConnection)");
        String requestLine = this.getRequestLine(conn);
        if (Wire.HEADER_WIRE.enabled()) {
            Wire.HEADER_WIRE.output(requestLine);
        }
        conn.print(requestLine, this.getParams().getHttpElementCharset());
    }

    private String getRequestLine(HttpConnection conn) {
        return HttpMethodBase.generateRequestLine(conn, this.getName(), this.getPath(), this.getQueryString(), this.effectiveVersion.toString());
    }

    @Override
    public HttpMethodParams getParams() {
        return this.params;
    }

    @Override
    public void setParams(HttpMethodParams params) {
        if (params == null) {
            throw new IllegalArgumentException("Parameters may not be null");
        }
        this.params = params;
    }

    public HttpVersion getEffectiveVersion() {
        return this.effectiveVersion;
    }

    private static boolean canResponseHaveBody(int status) {
        LOG.trace((Object)"enter HttpMethodBase.canResponseHaveBody(int)");
        boolean result = true;
        if (status >= 100 && status <= 199 || status == 204 || status == 304) {
            result = false;
        }
        return result;
    }

    public String getProxyAuthenticationRealm() {
        return this.proxyAuthState.getRealm();
    }

    public String getAuthenticationRealm() {
        return this.hostAuthState.getRealm();
    }

    protected String getContentCharSet(Header contentheader) {
        NameValuePair param;
        HeaderElement[] values;
        LOG.trace((Object)"enter getContentCharSet( Header contentheader )");
        String charset = null;
        if (contentheader != null && (values = contentheader.getElements()).length == 1 && (param = values[0].getParameterByName("charset")) != null) {
            charset = param.getValue();
        }
        if (charset == null) {
            charset = this.getParams().getContentCharset();
            if (LOG.isDebugEnabled()) {
                LOG.debug((Object)("Default charset used: " + charset));
            }
        }
        return charset;
    }

    public String getRequestCharSet() {
        return this.getContentCharSet(this.getRequestHeader("Content-Type"));
    }

    public String getResponseCharSet() {
        return this.getContentCharSet(this.getResponseHeader("Content-Type"));
    }

    public int getRecoverableExceptionCount() {
        return this.recoverableExceptionCount;
    }

    protected void responseBodyConsumed() {
        this.responseStream = null;
        if (this.responseConnection != null) {
            this.responseConnection.setLastResponseInputStream(null);
            if (this.shouldCloseConnection(this.responseConnection)) {
                this.responseConnection.close();
            } else {
                try {
                    if (this.responseConnection.isResponseAvailable()) {
                        boolean logExtraInput = this.getParams().isParameterTrue("http.protocol.warn-extra-input");
                        if (logExtraInput) {
                            LOG.warn((Object)"Extra response data detected - closing connection");
                        }
                        this.responseConnection.close();
                    }
                }
                catch (IOException e) {
                    LOG.warn((Object)e.getMessage());
                    this.responseConnection.close();
                }
            }
        }
        this.connectionCloseForced = false;
        this.ensureConnectionRelease();
    }

    private void ensureConnectionRelease() {
        if (this.responseConnection != null) {
            this.responseConnection.releaseConnection();
            this.responseConnection = null;
        }
    }

    @Override
    public HostConfiguration getHostConfiguration() {
        HostConfiguration hostconfig = new HostConfiguration();
        hostconfig.setHost(this.httphost);
        return hostconfig;
    }

    public void setHostConfiguration(HostConfiguration hostconfig) {
        this.httphost = hostconfig != null ? new HttpHost(hostconfig.getHost(), hostconfig.getPort(), hostconfig.getProtocol()) : null;
    }

    public MethodRetryHandler getMethodRetryHandler() {
        return this.methodRetryHandler;
    }

    public void setMethodRetryHandler(MethodRetryHandler handler) {
        this.methodRetryHandler = handler;
    }

    void fakeResponse(StatusLine statusline, HeaderGroup responseheaders, HttpConnection conn, InputStream responseStream) {
        this.used = true;
        this.statusLine = statusline;
        this.responseHeaders = responseheaders;
        this.responseConnection = conn;
        this.responseBody = null;
        this.responseStream = responseStream;
    }

    @Override
    public AuthState getHostAuthState() {
        return this.hostAuthState;
    }

    @Override
    public AuthState getProxyAuthState() {
        return this.proxyAuthState;
    }

    public boolean isAborted() {
        return this.aborted;
    }

    @Override
    public boolean isRequestSent() {
        return this.requestSent;
    }
}

