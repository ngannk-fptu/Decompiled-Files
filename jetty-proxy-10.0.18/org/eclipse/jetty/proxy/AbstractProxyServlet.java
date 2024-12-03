/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.AsyncContext
 *  javax.servlet.ServletConfig
 *  javax.servlet.ServletException
 *  javax.servlet.UnavailableException
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.eclipse.jetty.client.ContinueProtocolHandler
 *  org.eclipse.jetty.client.HttpClient
 *  org.eclipse.jetty.client.HttpClientTransport
 *  org.eclipse.jetty.client.ProtocolHandler
 *  org.eclipse.jetty.client.ProtocolHandlers
 *  org.eclipse.jetty.client.api.Request
 *  org.eclipse.jetty.client.api.Response
 *  org.eclipse.jetty.client.api.Response$CompleteListener
 *  org.eclipse.jetty.client.dynamic.HttpClientTransportDynamic
 *  org.eclipse.jetty.http.HttpField
 *  org.eclipse.jetty.http.HttpFields
 *  org.eclipse.jetty.http.HttpFields$Mutable
 *  org.eclipse.jetty.http.HttpHeader
 *  org.eclipse.jetty.http.HttpHeaderValue
 *  org.eclipse.jetty.http.HttpScheme
 *  org.eclipse.jetty.io.ClientConnectionFactory$Info
 *  org.eclipse.jetty.io.ClientConnector
 *  org.eclipse.jetty.util.HttpCookieStore$Empty
 *  org.eclipse.jetty.util.StringUtil
 *  org.eclipse.jetty.util.component.LifeCycle
 *  org.eclipse.jetty.util.ssl.SslContextFactory$Client
 *  org.eclipse.jetty.util.thread.QueuedThreadPool
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.proxy;

import java.net.CookieStore;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.servlet.AsyncContext;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.client.ContinueProtocolHandler;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.HttpClientTransport;
import org.eclipse.jetty.client.ProtocolHandler;
import org.eclipse.jetty.client.ProtocolHandlers;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.client.dynamic.HttpClientTransportDynamic;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpHeaderValue;
import org.eclipse.jetty.http.HttpScheme;
import org.eclipse.jetty.io.ClientConnectionFactory;
import org.eclipse.jetty.io.ClientConnector;
import org.eclipse.jetty.util.HttpCookieStore;
import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.util.component.LifeCycle;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractProxyServlet
extends HttpServlet {
    protected static final String CLIENT_REQUEST_ATTRIBUTE = "org.eclipse.jetty.proxy.clientRequest";
    protected static final Set<String> HOP_HEADERS = Set.of("connection", "keep-alive", "proxy-authorization", "proxy-authenticate", "proxy-connection", "transfer-encoding", "te", "trailer", "upgrade");
    private final Set<String> _whiteList = new HashSet<String>();
    private final Set<String> _blackList = new HashSet<String>();
    protected Logger _log;
    private boolean _preserveHost;
    private String _hostHeader;
    private String _viaHost;
    private HttpClient _client;
    private long _timeout;

    public void init() throws ServletException {
        this._log = this.createLogger();
        ServletConfig config = this.getServletConfig();
        this._preserveHost = Boolean.parseBoolean(config.getInitParameter("preserveHost"));
        this._hostHeader = config.getInitParameter("hostHeader");
        this._viaHost = config.getInitParameter("viaHost");
        if (this._viaHost == null) {
            this._viaHost = AbstractProxyServlet.viaHost();
        }
        try {
            String blackList;
            this._client = this.createHttpClient();
            this.getServletContext().setAttribute(config.getServletName() + ".HttpClient", (Object)this._client);
            String whiteList = config.getInitParameter("whiteList");
            if (whiteList != null) {
                this.getWhiteListHosts().addAll(this.parseList(whiteList));
            }
            if ((blackList = config.getInitParameter("blackList")) != null) {
                this.getBlackListHosts().addAll(this.parseList(blackList));
            }
        }
        catch (Exception e) {
            throw new ServletException((Throwable)e);
        }
    }

    public void destroy() {
        block3: {
            try {
                LifeCycle.stop((Object)this._client);
            }
            catch (Exception x) {
                if (this._log == null) {
                    x.printStackTrace();
                }
                if (!this._log.isDebugEnabled()) break block3;
                this._log.debug("Failed to stop client", (Throwable)x);
            }
        }
    }

    public String getHostHeader() {
        return this._hostHeader;
    }

    public String getViaHost() {
        return this._viaHost;
    }

    private static String viaHost() {
        try {
            return InetAddress.getLocalHost().getHostName();
        }
        catch (UnknownHostException x) {
            return "localhost";
        }
    }

    public long getTimeout() {
        return this._timeout;
    }

    public void setTimeout(long timeout) {
        this._timeout = timeout;
    }

    public Set<String> getWhiteListHosts() {
        return this._whiteList;
    }

    public Set<String> getBlackListHosts() {
        return this._blackList;
    }

    protected Logger createLogger() {
        Object servletName = this.getServletConfig().getServletName();
        servletName = StringUtil.replace((String)servletName, (char)'-', (char)'.');
        if (((Object)((Object)this)).getClass().getPackage() != null && !((String)servletName).startsWith(((Object)((Object)this)).getClass().getPackage().getName())) {
            servletName = ((Object)((Object)this)).getClass().getName() + "." + (String)servletName;
        }
        return LoggerFactory.getLogger((String)servletName);
    }

    protected HttpClient createHttpClient() throws ServletException {
        Executor executor;
        ServletConfig config = this.getServletConfig();
        HttpClient client = this.newHttpClient();
        client.setFollowRedirects(false);
        client.setCookieStore((CookieStore)new HttpCookieStore.Empty());
        String value = config.getInitParameter("maxThreads");
        if (value == null || "-".equals(value)) {
            executor = (Executor)this.getServletContext().getAttribute("org.eclipse.jetty.server.Executor");
            if (executor == null) {
                throw new IllegalStateException("No server executor for proxy");
            }
        } else {
            QueuedThreadPool qtp = new QueuedThreadPool(Integer.parseInt(value));
            String servletName = config.getServletName();
            int dot = servletName.lastIndexOf(46);
            if (dot >= 0) {
                servletName = servletName.substring(dot + 1);
            }
            qtp.setName(servletName);
            executor = qtp;
        }
        client.setExecutor(executor);
        value = config.getInitParameter("maxConnections");
        if (value == null) {
            value = "256";
        }
        client.setMaxConnectionsPerDestination(Integer.parseInt(value));
        value = config.getInitParameter("idleTimeout");
        if (value == null) {
            value = "30000";
        }
        client.setIdleTimeout(Long.parseLong(value));
        value = config.getInitParameter("timeout");
        if (value == null) {
            value = "60000";
        }
        this._timeout = Long.parseLong(value);
        value = config.getInitParameter("requestBufferSize");
        if (value != null) {
            client.setRequestBufferSize(Integer.parseInt(value));
        }
        if ((value = config.getInitParameter("responseBufferSize")) != null) {
            client.setResponseBufferSize(Integer.parseInt(value));
        }
        try {
            client.start();
            client.getContentDecoderFactories().clear();
            ProtocolHandlers protocolHandlers = client.getProtocolHandlers();
            protocolHandlers.clear();
            protocolHandlers.put((ProtocolHandler)new ProxyContinueProtocolHandler());
            return client;
        }
        catch (Exception x) {
            throw new ServletException((Throwable)x);
        }
    }

    protected HttpClient newHttpClient() {
        int selectors = 1;
        String value = this.getServletConfig().getInitParameter("selectors");
        if (value != null) {
            selectors = Integer.parseInt(value);
        }
        ClientConnector clientConnector = this.newClientConnector();
        clientConnector.setSelectors(selectors);
        return this.newHttpClient(clientConnector);
    }

    protected HttpClient newHttpClient(ClientConnector clientConnector) {
        return new HttpClient((HttpClientTransport)new HttpClientTransportDynamic(clientConnector, new ClientConnectionFactory.Info[0]));
    }

    protected ClientConnector newClientConnector() {
        ClientConnector clientConnector = new ClientConnector();
        clientConnector.setSslContextFactory(new SslContextFactory.Client());
        return clientConnector;
    }

    protected HttpClient getHttpClient() {
        return this._client;
    }

    private Set<String> parseList(String list) {
        String[] hosts;
        HashSet<String> result = new HashSet<String>();
        for (String host : hosts = list.split(",")) {
            if ((host = host.trim()).length() == 0) continue;
            result.add(host);
        }
        return result;
    }

    public boolean validateDestination(String host, int port) {
        String hostPort = host + ":" + port;
        if (!this._whiteList.isEmpty() && !this._whiteList.contains(hostPort)) {
            if (this._log.isDebugEnabled()) {
                this._log.debug("Host {}:{} not whitelisted", (Object)host, (Object)port);
            }
            return false;
        }
        if (!this._blackList.isEmpty() && this._blackList.contains(hostPort)) {
            if (this._log.isDebugEnabled()) {
                this._log.debug("Host {}:{} blacklisted", (Object)host, (Object)port);
            }
            return false;
        }
        return true;
    }

    protected String rewriteTarget(HttpServletRequest clientRequest) {
        String query;
        if (!this.validateDestination(clientRequest.getServerName(), clientRequest.getServerPort())) {
            return null;
        }
        StringBuffer target = clientRequest.getRequestURL();
        if (HttpScheme.HTTPS.is(target.substring(0, 5))) {
            target.replace(4, 5, "");
        }
        if ((query = clientRequest.getQueryString()) != null) {
            target.append("?").append(query);
        }
        return target.toString();
    }

    protected void onProxyRewriteFailed(HttpServletRequest clientRequest, HttpServletResponse proxyResponse) {
        this.sendProxyResponseError(clientRequest, proxyResponse, 403);
    }

    protected boolean hasContent(HttpServletRequest clientRequest) {
        return clientRequest.getContentLength() > 0 || clientRequest.getContentType() != null || clientRequest.getHeader(HttpHeader.TRANSFER_ENCODING.asString()) != null;
    }

    protected boolean expects100Continue(HttpServletRequest request) {
        return HttpHeaderValue.CONTINUE.is(request.getHeader(HttpHeader.EXPECT.asString()));
    }

    protected Request newProxyRequest(HttpServletRequest request, String rewrittenTarget) {
        return this.getHttpClient().newRequest(rewrittenTarget).method(request.getMethod()).attribute(CLIENT_REQUEST_ATTRIBUTE, (Object)request);
    }

    protected void copyRequestHeaders(HttpServletRequest clientRequest, Request proxyRequest) {
        HttpFields.Mutable newHeaders = HttpFields.build();
        Set<String> headersToRemove = this.findConnectionHeaders(clientRequest);
        Enumeration headerNames = clientRequest.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = (String)headerNames.nextElement();
            String lowerHeaderName = headerName.toLowerCase(Locale.ENGLISH);
            if (HttpHeader.HOST.is(headerName) && !this._preserveHost || HOP_HEADERS.contains(lowerHeaderName) || headersToRemove != null && headersToRemove.contains(lowerHeaderName)) continue;
            Enumeration headerValues = clientRequest.getHeaders(headerName);
            while (headerValues.hasMoreElements()) {
                String headerValue = (String)headerValues.nextElement();
                if (headerValue == null) continue;
                newHeaders.add(headerName, headerValue);
            }
        }
        if (this._hostHeader != null) {
            newHeaders.add(HttpHeader.HOST, this._hostHeader);
        }
        proxyRequest.headers(headers -> headers.clear().add((HttpFields)newHeaders));
    }

    protected Set<String> findConnectionHeaders(HttpServletRequest clientRequest) {
        HashSet<String> hopHeaders = null;
        Enumeration connectionHeaders = clientRequest.getHeaders(HttpHeader.CONNECTION.asString());
        while (connectionHeaders.hasMoreElements()) {
            String[] values;
            String value = (String)connectionHeaders.nextElement();
            for (String name : values = value.split(",")) {
                name = name.trim().toLowerCase(Locale.ENGLISH);
                if (hopHeaders == null) {
                    hopHeaders = new HashSet<String>();
                }
                hopHeaders.add(name);
            }
        }
        return hopHeaders;
    }

    protected void addProxyHeaders(HttpServletRequest clientRequest, Request proxyRequest) {
        this.addViaHeader(proxyRequest);
        this.addXForwardedHeaders(clientRequest, proxyRequest);
    }

    protected void addViaHeader(Request proxyRequest) {
        HttpServletRequest clientRequest = (HttpServletRequest)proxyRequest.getAttributes().get(CLIENT_REQUEST_ATTRIBUTE);
        this.addViaHeader(clientRequest, proxyRequest);
    }

    protected void addViaHeader(HttpServletRequest clientRequest, Request proxyRequest) {
        String protocol = clientRequest.getProtocol();
        String[] parts = protocol.split("/", 2);
        String protocolPart = parts.length == 2 && "HTTP".equalsIgnoreCase(parts[0]) ? parts[1] : protocol;
        String viaHeaderValue = protocolPart + " " + this.getViaHost();
        proxyRequest.headers(headers -> headers.computeField(HttpHeader.VIA, (header, viaFields) -> {
            if (viaFields == null || viaFields.isEmpty()) {
                return new HttpField(header, viaHeaderValue);
            }
            String separator = ", ";
            Object newValue = viaFields.stream().flatMap(field -> Stream.of(field.getValues())).filter(value -> !StringUtil.isBlank((String)value)).collect(Collectors.joining(separator));
            if (((String)newValue).length() > 0) {
                newValue = (String)newValue + separator;
            }
            newValue = (String)newValue + viaHeaderValue;
            return new HttpField(HttpHeader.VIA, (String)newValue);
        }));
    }

    protected void addXForwardedHeaders(HttpServletRequest clientRequest, Request proxyRequest) {
        String localName;
        proxyRequest.headers(headers -> headers.add(HttpHeader.X_FORWARDED_FOR, clientRequest.getRemoteAddr()));
        proxyRequest.headers(headers -> headers.add(HttpHeader.X_FORWARDED_PROTO, clientRequest.getScheme()));
        String hostHeader = clientRequest.getHeader(HttpHeader.HOST.asString());
        if (hostHeader != null) {
            proxyRequest.headers(headers -> headers.add(HttpHeader.X_FORWARDED_HOST, hostHeader));
        }
        if ((localName = clientRequest.getLocalName()) != null) {
            proxyRequest.headers(headers -> headers.add(HttpHeader.X_FORWARDED_SERVER, localName));
        }
    }

    protected void sendProxyRequest(HttpServletRequest clientRequest, HttpServletResponse proxyResponse, Request proxyRequest) {
        if (this._log.isDebugEnabled()) {
            StringBuilder builder = new StringBuilder(clientRequest.getMethod());
            builder.append(" ").append(clientRequest.getRequestURI());
            String query = clientRequest.getQueryString();
            if (query != null) {
                builder.append("?").append(query);
            }
            builder.append(" ").append(clientRequest.getProtocol()).append(System.lineSeparator());
            Enumeration headerNames = clientRequest.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = (String)headerNames.nextElement();
                builder.append(headerName).append(": ");
                Enumeration headerValues = clientRequest.getHeaders(headerName);
                while (headerValues.hasMoreElements()) {
                    String headerValue = (String)headerValues.nextElement();
                    if (headerValue != null) {
                        builder.append(headerValue);
                    }
                    if (!headerValues.hasMoreElements()) continue;
                    builder.append(",");
                }
                builder.append(System.lineSeparator());
            }
            builder.append(System.lineSeparator());
            this._log.debug("{} proxying to upstream:{}{}{}{}{}", new Object[]{this.getRequestId(clientRequest), System.lineSeparator(), builder, proxyRequest, System.lineSeparator(), proxyRequest.getHeaders().toString().trim()});
        }
        proxyRequest.send(this.newProxyResponseListener(clientRequest, proxyResponse));
    }

    protected abstract Response.CompleteListener newProxyResponseListener(HttpServletRequest var1, HttpServletResponse var2);

    protected void onClientRequestFailure(HttpServletRequest clientRequest, Request proxyRequest, HttpServletResponse proxyResponse, Throwable failure) {
        boolean aborted = proxyRequest.abort(failure);
        if (!aborted) {
            int status = this.clientRequestStatus(failure);
            this.sendProxyResponseError(clientRequest, proxyResponse, status);
        }
    }

    protected int clientRequestStatus(Throwable failure) {
        return failure instanceof TimeoutException ? 408 : 500;
    }

    protected void onServerResponseHeaders(HttpServletRequest clientRequest, HttpServletResponse proxyResponse, Response serverResponse) {
        for (HttpField field : serverResponse.getHeaders()) {
            String newHeaderValue;
            String headerName = field.getName();
            String lowerHeaderName = headerName.toLowerCase(Locale.ENGLISH);
            if (HOP_HEADERS.contains(lowerHeaderName) || (newHeaderValue = this.filterServerResponseHeader(clientRequest, serverResponse, headerName, field.getValue())) == null) continue;
            proxyResponse.addHeader(headerName, newHeaderValue);
        }
        if (this._log.isDebugEnabled()) {
            StringBuilder builder = new StringBuilder(System.lineSeparator());
            builder.append(clientRequest.getProtocol()).append(" ").append(proxyResponse.getStatus()).append(" ").append(serverResponse.getReason()).append(System.lineSeparator());
            for (String headerName : proxyResponse.getHeaderNames()) {
                builder.append(headerName).append(": ");
                Iterator headerValues = proxyResponse.getHeaders(headerName).iterator();
                while (headerValues.hasNext()) {
                    String headerValue = (String)headerValues.next();
                    if (headerValue != null) {
                        builder.append(headerValue);
                    }
                    if (!headerValues.hasNext()) continue;
                    builder.append(",");
                }
                builder.append(System.lineSeparator());
            }
            this._log.debug("{} proxying to downstream:{}{}", new Object[]{this.getRequestId(clientRequest), System.lineSeparator(), builder});
        }
    }

    protected String filterServerResponseHeader(HttpServletRequest clientRequest, Response serverResponse, String headerName, String headerValue) {
        return headerValue;
    }

    protected void onProxyResponseSuccess(HttpServletRequest clientRequest, HttpServletResponse proxyResponse, Response serverResponse) {
        if (this._log.isDebugEnabled()) {
            this._log.debug("{} proxying successful", (Object)this.getRequestId(clientRequest));
        }
        AsyncContext asyncContext = clientRequest.getAsyncContext();
        asyncContext.complete();
    }

    protected void onProxyResponseFailure(HttpServletRequest clientRequest, HttpServletResponse proxyResponse, Response serverResponse, Throwable failure) {
        int serverStatus;
        if (this._log.isDebugEnabled()) {
            this._log.debug(this.getRequestId(clientRequest) + " proxying failed", failure);
        }
        int status = this.proxyResponseStatus(failure);
        int n = serverStatus = serverResponse == null ? status : serverResponse.getStatus();
        if (this.expects100Continue(clientRequest) && serverStatus >= 200) {
            status = serverStatus;
        }
        this.sendProxyResponseError(clientRequest, proxyResponse, status);
    }

    protected int proxyResponseStatus(Throwable failure) {
        return failure instanceof TimeoutException ? 504 : 502;
    }

    protected int getRequestId(HttpServletRequest clientRequest) {
        return System.identityHashCode(clientRequest);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void sendProxyResponseError(HttpServletRequest clientRequest, HttpServletResponse proxyResponse, int status) {
        try {
            if (!proxyResponse.isCommitted()) {
                proxyResponse.resetBuffer();
                proxyResponse.setHeader(HttpHeader.CONNECTION.asString(), HttpHeaderValue.CLOSE.asString());
            }
            proxyResponse.sendError(status);
        }
        catch (Exception e) {
            this._log.trace("IGNORED", (Throwable)e);
            try {
                proxyResponse.sendError(-1);
            }
            catch (Exception e2) {
                this._log.trace("IGNORED", (Throwable)e2);
            }
        }
        finally {
            if (clientRequest.isAsyncStarted()) {
                clientRequest.getAsyncContext().complete();
            }
        }
    }

    protected void onContinue(HttpServletRequest clientRequest, Request proxyRequest) {
        if (this._log.isDebugEnabled()) {
            this._log.debug("{} handling 100 Continue", (Object)this.getRequestId(clientRequest));
        }
    }

    class ProxyContinueProtocolHandler
    extends ContinueProtocolHandler {
        ProxyContinueProtocolHandler() {
        }

        protected void onContinue(Request request) {
            HttpServletRequest clientRequest = (HttpServletRequest)request.getAttributes().get(AbstractProxyServlet.CLIENT_REQUEST_ATTRIBUTE);
            AbstractProxyServlet.this.onContinue(clientRequest, request);
        }
    }

    protected static class TransparentDelegate {
        private final AbstractProxyServlet proxyServlet;
        private String _proxyTo;
        private String _prefix;

        protected TransparentDelegate(AbstractProxyServlet proxyServlet) {
            this.proxyServlet = proxyServlet;
        }

        protected void init(ServletConfig config) throws ServletException {
            this._proxyTo = config.getInitParameter("proxyTo");
            if (this._proxyTo == null) {
                throw new UnavailableException("Init parameter 'proxyTo' is required.");
            }
            String prefix = config.getInitParameter("prefix");
            if (prefix != null) {
                if (!prefix.startsWith("/")) {
                    throw new UnavailableException("Init parameter 'prefix' must start with a '/'.");
                }
                this._prefix = prefix;
            }
            String contextPath = config.getServletContext().getContextPath();
            String string = this._prefix = this._prefix == null ? contextPath : contextPath + this._prefix;
            if (this.proxyServlet._log.isDebugEnabled()) {
                this.proxyServlet._log.debug(config.getServletName() + " @ " + this._prefix + " to " + this._proxyTo);
            }
        }

        protected String rewriteTarget(HttpServletRequest request) {
            URI rewrittenURI;
            String query;
            String rest;
            String path = request.getRequestURI();
            if (!path.startsWith(this._prefix)) {
                return null;
            }
            StringBuilder uri = new StringBuilder(this._proxyTo);
            if (this._proxyTo.endsWith("/")) {
                uri.setLength(uri.length() - 1);
            }
            if (!(rest = path.substring(this._prefix.length())).isEmpty()) {
                if (!rest.startsWith("/")) {
                    uri.append("/");
                }
                uri.append(rest);
            }
            if ((query = request.getQueryString()) != null) {
                String separator = "://";
                if (uri.indexOf("/", uri.indexOf(separator) + separator.length()) < 0) {
                    uri.append("/");
                }
                uri.append("?").append(query);
            }
            if (!this.proxyServlet.validateDestination((rewrittenURI = URI.create(uri.toString()).normalize()).getHost(), rewrittenURI.getPort())) {
                return null;
            }
            return rewrittenURI.toString();
        }
    }
}

