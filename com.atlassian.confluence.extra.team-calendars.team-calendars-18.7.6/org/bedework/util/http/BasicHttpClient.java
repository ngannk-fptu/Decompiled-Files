/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.Header
 *  org.apache.http.HeaderElement
 *  org.apache.http.HttpEntity
 *  org.apache.http.HttpException
 *  org.apache.http.HttpHost
 *  org.apache.http.HttpResponse
 *  org.apache.http.StatusLine
 *  org.apache.http.auth.AuthScope
 *  org.apache.http.auth.Credentials
 *  org.apache.http.auth.UsernamePasswordCredentials
 *  org.apache.http.client.methods.HttpDelete
 *  org.apache.http.client.methods.HttpEntityEnclosingRequestBase
 *  org.apache.http.client.methods.HttpGet
 *  org.apache.http.client.methods.HttpHead
 *  org.apache.http.client.methods.HttpOptions
 *  org.apache.http.client.methods.HttpPost
 *  org.apache.http.client.methods.HttpPut
 *  org.apache.http.client.methods.HttpRequestBase
 *  org.apache.http.client.methods.HttpUriRequest
 *  org.apache.http.conn.ClientConnectionManager
 *  org.apache.http.conn.ConnectionKeepAliveStrategy
 *  org.apache.http.conn.routing.HttpRoute
 *  org.apache.http.conn.scheme.PlainSocketFactory
 *  org.apache.http.conn.scheme.Scheme
 *  org.apache.http.conn.scheme.SchemeRegistry
 *  org.apache.http.conn.scheme.SchemeSocketFactory
 *  org.apache.http.conn.ssl.SSLSocketFactory
 *  org.apache.http.entity.ByteArrayEntity
 *  org.apache.http.impl.client.DefaultHttpClient
 *  org.apache.http.impl.conn.PoolingClientConnectionManager
 *  org.apache.http.message.BasicHeaderElementIterator
 *  org.apache.http.params.HttpParams
 *  org.apache.http.pool.PoolStats
 *  org.apache.http.protocol.HttpContext
 *  org.apache.http.util.EntityUtils
 */
package org.bedework.util.http;

import java.io.InputStream;
import java.net.URI;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SchemeSocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.params.HttpParams;
import org.apache.http.pool.PoolStats;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.bedework.util.misc.Util;

public class BasicHttpClient
extends DefaultHttpClient {
    protected boolean debug;
    private transient Logger log;
    private static PoolingClientConnectionManager connManager;
    private static final IdleConnectionMonitorThread idleConnectionMonitor;
    private static final SchemeRegistry sr;
    protected static boolean sslDisabled;
    private HttpRequestBase method;
    private HttpResponse response;
    private ConnectionKeepAliveStrategy kas = new ConnectionKeepAliveStrategy(){

        public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
            BasicHeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator("Keep-Alive"));
            while (it.hasNext()) {
                HeaderElement he = it.nextElement();
                String param = he.getName();
                String value = he.getValue();
                if (value == null || !param.equalsIgnoreCase("timeout")) continue;
                try {
                    return Long.parseLong(value) * 1000L;
                }
                catch (NumberFormatException numberFormatException) {
                }
            }
            return 30000L;
        }
    };
    private int status;
    private Credentials credentials;
    private boolean hostSpecified;
    private String baseURIValue;
    private URI baseURI;

    public BasicHttpClient(int timeOut) throws HttpException {
        this(null, -1, null, timeOut, true);
    }

    public BasicHttpClient(int timeOut, boolean followRedirects) throws HttpException {
        this(null, -1, null, timeOut, followRedirects);
    }

    public BasicHttpClient(String host, int port, String scheme, int timeOut) throws HttpException {
        this(host, port, scheme, timeOut, true);
    }

    public BasicHttpClient(String host, int port, String scheme, int timeOut, boolean followRedirects) throws HttpException {
        super((ClientConnectionManager)connManager, null);
        this.setKeepAliveStrategy(this.kas);
        this.debug = this.getLogger().isDebugEnabled();
        if (sslDisabled) {
            this.warn("*******************************************************");
            this.warn(" SSL disabled");
            this.warn("*******************************************************");
        }
        HttpParams params = this.getParams();
        if (host != null) {
            this.hostSpecified = true;
            HttpHost httpHost = new HttpHost(host, port, scheme);
            params.setParameter("http.default-host", (Object)httpHost);
        }
        params.setIntParameter("http.connection.timeout", timeOut);
        params.setIntParameter("http.socket.timeout", timeOut * 2);
        params.setBooleanParameter("http.protocol.handle-redirects", followRedirects);
    }

    public static SSLSocketFactory getSslSocketFactory() {
        if (!sslDisabled) {
            return SSLSocketFactory.getSocketFactory();
        }
        try {
            final X509Certificate[] _AcceptedIssuers = new X509Certificate[]{};
            SSLContext ctx = SSLContext.getInstance("TLS");
            X509TrustManager tm = new X509TrustManager(){

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return _AcceptedIssuers;
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }
            };
            ctx.init(null, new TrustManager[]{tm}, new SecureRandom());
            return new SSLSocketFactory(ctx, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        }
        catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    public void setBaseURIValue(String val) {
        this.baseURIValue = val;
        this.baseURI = null;
    }

    public void setBaseURI(URI val) {
        this.baseURIValue = null;
        this.baseURI = val;
    }

    public static void setMaxConnections(int val) {
        connManager.setMaxTotal(val);
    }

    public static int getMaxConnections() {
        return connManager.getMaxTotal();
    }

    public static PoolStats getConnStats() {
        return connManager.getTotalStats();
    }

    public static void setDefaultMaxPerRoute(int val) {
        connManager.setDefaultMaxPerRoute(val);
    }

    public static int getDefaultMaxPerRoute() {
        return connManager.getDefaultMaxPerRoute();
    }

    public static void setHostLimit(String host, int port, int max) {
        HttpHost hostPort = new HttpHost(host, port);
        connManager.setMaxPerRoute(new HttpRoute(hostPort), max);
    }

    public void setCredentials(String user, String pw) {
        this.credentials = user == null ? null : new UsernamePasswordCredentials(user, pw);
    }

    public int sendRequest(String method, String url, List<Header> hdrs) throws HttpException {
        return this.sendRequest(method, url, hdrs, null, 0, null);
    }

    public int sendRequest(String methodName, String url, List<Header> hdrs, String contentType, int contentLen, byte[] content) throws HttpException {
        return this.sendRequest(methodName, url, hdrs, contentType, contentLen, content, null);
    }

    public int sendRequest(String methodName, String url, List<Header> hdrs, String contentType, int contentLen, byte[] content, HttpParams params) throws HttpException {
        int sz = 0;
        if (content != null) {
            sz = content.length;
        }
        if (this.debug) {
            this.debugMsg("About to send request: method=" + methodName + " url=" + url + " contentLen=" + contentLen + " content.length=" + sz + " contentType=" + contentType);
        }
        try {
            URI u = new URI(url);
            if (!this.hostSpecified && u.getHost() == null) {
                if (this.baseURI == null && this.baseURIValue != null) {
                    this.baseURI = new URI(this.baseURIValue);
                }
                if (this.baseURI == null) {
                    throw new HttpException("No base URI specified for non-absolute URI " + url);
                }
                if (this.baseURI.getHost() == null) {
                    throw new HttpException("Base URI must be absolute: " + this.baseURI);
                }
                u = this.baseURI.resolve(u);
            }
            if (this.debug) {
                this.debugMsg("      url resolves to " + u);
            }
            this.method = this.findMethod(methodName, u);
            if (this.credentials != null) {
                this.getCredentialsProvider().setCredentials(new AuthScope(u.getHost(), u.getPort()), this.credentials);
            }
            if (!Util.isEmpty(hdrs)) {
                for (Header hdr : hdrs) {
                    this.method.addHeader(hdr);
                }
            }
            if (this.method instanceof HttpEntityEnclosingRequestBase) {
                if (contentType == null) {
                    contentType = "text/xml";
                }
                if (content != null) {
                    this.setContent(content, contentType);
                }
            }
            if (params != null) {
                this.method.setParams(params);
            }
            this.response = this.execute((HttpUriRequest)this.method);
        }
        catch (HttpException he) {
            throw he;
        }
        catch (Throwable t) {
            throw new HttpException(t.getLocalizedMessage(), t);
        }
        this.status = this.response.getStatusLine().getStatusCode();
        return this.status;
    }

    public InputStream post(String url, List<Header> hdrs, HttpEntity entity) throws HttpException {
        HttpPost poster = new HttpPost(url);
        poster.setEntity(entity);
        try {
            this.response = this.execute((HttpUriRequest)poster);
            this.status = this.response.getStatusLine().getStatusCode();
            return this.getResponseBodyAsStream();
        }
        catch (HttpException he) {
            throw he;
        }
        catch (Throwable t) {
            throw new HttpException(t.getLocalizedMessage(), t);
        }
    }

    public void setContent(byte[] content, String contentType) throws HttpException {
        if (!(this.method instanceof HttpEntityEnclosingRequestBase)) {
            throw new HttpException("Invalid operation for method " + this.method.getMethod());
        }
        HttpEntityEnclosingRequestBase eem = (HttpEntityEnclosingRequestBase)this.method;
        ByteArrayEntity entity = new ByteArrayEntity(content);
        entity.setContentType(contentType);
        eem.setEntity((HttpEntity)entity);
    }

    protected HttpRequestBase findMethod(String name, URI uri) throws HttpException {
        String nm = name.toUpperCase();
        if ("PUT".equals(nm)) {
            return new HttpPut(uri);
        }
        if ("GET".equals(nm)) {
            return new HttpGet(uri);
        }
        if ("DELETE".equals(nm)) {
            return new HttpDelete(uri);
        }
        if ("POST".equals(nm)) {
            return new HttpPost(uri);
        }
        if ("PROPFIND".equals(nm)) {
            return new HttpPropfind(uri);
        }
        if ("MKCALENDAR".equals(nm)) {
            return new HttpMkcalendar(uri);
        }
        if ("MKCOL".equals(nm)) {
            return new HttpMkcol(uri);
        }
        if ("OPTIONS".equals(nm)) {
            return new HttpOptions(uri);
        }
        if ("REPORT".equals(nm)) {
            return new HttpReport(uri);
        }
        if ("HEAD".equals(nm)) {
            return new HttpHead(uri);
        }
        throw new HttpException("Illegal method: " + name);
    }

    public int delete(String path, List<Header> hdrs) throws HttpException {
        int respCode = this.sendRequest("DELETE", path, hdrs, null, 0, null);
        if (this.debug) {
            this.debugMsg("response code " + respCode);
        }
        return respCode;
    }

    public int putObject(String path, Object o, String contentType) throws HttpException {
        return this.putObject(path, null, o, contentType);
    }

    public int putObject(String path, List<Header> hdrs, Object o, String contentType) throws HttpException {
        String content = String.valueOf(o);
        int respCode = this.sendRequest("PUT", path, hdrs, contentType, content.length(), content.getBytes());
        if (this.debug) {
            this.debugMsg("response code " + respCode);
        }
        return respCode;
    }

    public String getResponseContentType() throws HttpException {
        HttpEntity ent = this.getResponseEntity();
        if (ent == null) {
            return null;
        }
        Header hdr = ent.getContentType();
        if (hdr == null) {
            return null;
        }
        return hdr.getValue();
    }

    public HttpEntity getResponseEntity() {
        if (this.response == null) {
            return null;
        }
        return this.response.getEntity();
    }

    public long getResponseContentLength() throws HttpException {
        HttpEntity ent = this.getResponseEntity();
        if (ent == null) {
            return 0L;
        }
        return ent.getContentLength();
    }

    public String getResponseCharSet() throws HttpException {
        HttpEntity ent = this.getResponseEntity();
        if (ent == null) {
            return null;
        }
        return EntityUtils.getContentCharSet((HttpEntity)ent);
    }

    public StatusLine getResponseStatusLine() throws HttpException {
        if (this.response == null) {
            return null;
        }
        return this.response.getStatusLine();
    }

    public InputStream getResponseBodyAsStream() throws HttpException {
        try {
            HttpEntity ent = this.getResponseEntity();
            if (ent == null) {
                return null;
            }
            return ent.getContent();
        }
        catch (Throwable t) {
            throw new HttpException(t.getLocalizedMessage(), t);
        }
    }

    public Header[] getHeaders() throws HttpException {
        if (this.response == null) {
            return null;
        }
        return this.response.getAllHeaders();
    }

    public Header getFirstHeader(String name) throws HttpException {
        if (this.response == null) {
            return null;
        }
        return this.response.getFirstHeader(name);
    }

    public String getFirstHeaderValue(String name) throws HttpException {
        Header h = this.getFirstHeader(name);
        if (h == null) {
            return null;
        }
        return h.getValue();
    }

    public InputStream get(String path) throws HttpException {
        return this.get(path, "application/text", null);
    }

    public InputStream get(String path, String contentType, List<Header> hdrs) throws HttpException {
        int respCode = this.sendRequest("GET", path, hdrs, contentType, 0, null);
        if (this.debug) {
            this.debugMsg("getFile: response code " + respCode);
        }
        if (respCode != 200) {
            return null;
        }
        return this.getResponseBodyAsStream();
    }

    public void release() throws HttpException {
        try {
            HttpEntity ent = this.getResponseEntity();
            if (ent != null) {
                InputStream is = ent.getContent();
                is.close();
            }
        }
        catch (Throwable t) {
            throw new HttpException(t.getLocalizedMessage(), t);
        }
    }

    public void close() {
        try {
            this.release();
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    protected Logger getLogger() {
        if (this.log == null) {
            this.log = Logger.getLogger(((Object)((Object)this)).getClass());
        }
        return this.log;
    }

    protected void debugMsg(String msg) {
        this.getLogger().debug(msg);
    }

    protected void error(Throwable t) {
        this.getLogger().error((Object)this, t);
    }

    protected void warn(String msg) {
        this.getLogger().warn(msg);
    }

    protected void logIt(String msg) {
        this.getLogger().info(msg);
    }

    protected void trace(String msg) {
        this.getLogger().debug(msg);
    }

    static {
        sr = new SchemeRegistry();
        sslDisabled = Boolean.getBoolean("org.bedework.disable.ssl");
        sr.register(new Scheme("http", 80, (SchemeSocketFactory)PlainSocketFactory.getSocketFactory()));
        sr.register(new Scheme("https", 443, (SchemeSocketFactory)BasicHttpClient.getSslSocketFactory()));
        sr.register(new Scheme("webcal", 80, (SchemeSocketFactory)PlainSocketFactory.getSocketFactory()));
        sr.register(new Scheme("webcals", 443, (SchemeSocketFactory)BasicHttpClient.getSslSocketFactory()));
        connManager = new PoolingClientConnectionManager(sr);
        connManager.setMaxTotal(200);
        connManager.setDefaultMaxPerRoute(20);
        HttpHost localhost = new HttpHost("localhost", 80);
        connManager.setMaxPerRoute(new HttpRoute(localhost), 50);
        HttpHost localhost8080 = new HttpHost("localhost", 8080);
        connManager.setMaxPerRoute(new HttpRoute(localhost8080), 50);
        idleConnectionMonitor = new IdleConnectionMonitorThread(connManager);
        idleConnectionMonitor.start();
    }

    public class HttpReport
    extends HttpEntityEnclosingRequestBase {
        public static final String METHOD_NAME = "REPORT";

        public HttpReport(URI uri) {
            this.setURI(uri);
        }

        public HttpReport(String uri) {
            this.setURI(URI.create(uri));
        }

        public String getMethod() {
            return METHOD_NAME;
        }
    }

    public class HttpPropfind
    extends HttpEntityEnclosingRequestBase {
        public static final String METHOD_NAME = "PROPFIND";

        public HttpPropfind(URI uri) {
            this.setURI(uri);
        }

        public HttpPropfind(String uri) {
            this.setURI(URI.create(uri));
        }

        public String getMethod() {
            return METHOD_NAME;
        }
    }

    public class HttpMkcol
    extends HttpEntityEnclosingRequestBase {
        public static final String METHOD_NAME = "MKCOL";

        public HttpMkcol(URI uri) {
            this.setURI(uri);
        }

        public HttpMkcol(String uri) {
            this.setURI(URI.create(uri));
        }

        public String getMethod() {
            return METHOD_NAME;
        }
    }

    public class HttpMkcalendar
    extends HttpEntityEnclosingRequestBase {
        public static final String METHOD_NAME = "MKCALENDAR";

        public HttpMkcalendar(URI uri) {
            this.setURI(uri);
        }

        public HttpMkcalendar(String uri) {
            this.setURI(URI.create(uri));
        }

        public String getMethod() {
            return METHOD_NAME;
        }
    }

    public static class IdleConnectionMonitorThread
    extends Thread {
        private final PoolingClientConnectionManager connMgr;
        private volatile boolean shutdown;

        public IdleConnectionMonitorThread(PoolingClientConnectionManager connMgr) {
            this.connMgr = connMgr;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        @Override
        public void run() {
            try {
                while (!this.shutdown) {
                    IdleConnectionMonitorThread idleConnectionMonitorThread = this;
                    synchronized (idleConnectionMonitorThread) {
                        this.wait(5000L);
                        this.connMgr.closeExpiredConnections();
                        this.connMgr.closeIdleConnections(30L, TimeUnit.SECONDS);
                    }
                }
                return;
            }
            catch (InterruptedException interruptedException) {
                // empty catch block
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void shutdown() {
            this.shutdown = true;
            IdleConnectionMonitorThread idleConnectionMonitorThread = this;
            synchronized (idleConnectionMonitorThread) {
                this.notifyAll();
            }
        }
    }
}

