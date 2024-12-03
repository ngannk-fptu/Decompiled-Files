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
 *  org.apache.http.HttpResponseFactory
 *  org.apache.http.impl.SocketHttpClientConnection
 *  org.apache.http.io.HttpMessageParser
 *  org.apache.http.io.SessionInputBuffer
 *  org.apache.http.io.SessionOutputBuffer
 *  org.apache.http.params.BasicHttpParams
 *  org.apache.http.params.HttpParams
 *  org.apache.http.params.HttpProtocolParams
 *  org.apache.http.protocol.HttpContext
 *  org.apache.http.util.Args
 */
package org.apache.http.impl.conn;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseFactory;
import org.apache.http.conn.ManagedHttpClientConnection;
import org.apache.http.conn.OperatedClientConnection;
import org.apache.http.impl.SocketHttpClientConnection;
import org.apache.http.impl.conn.DefaultHttpResponseParser;
import org.apache.http.impl.conn.LoggingSessionInputBuffer;
import org.apache.http.impl.conn.LoggingSessionOutputBuffer;
import org.apache.http.impl.conn.Wire;
import org.apache.http.io.HttpMessageParser;
import org.apache.http.io.SessionInputBuffer;
import org.apache.http.io.SessionOutputBuffer;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;

@Deprecated
public class DefaultClientConnection
extends SocketHttpClientConnection
implements OperatedClientConnection,
ManagedHttpClientConnection,
HttpContext {
    private final Log log = LogFactory.getLog(this.getClass());
    private final Log headerLog = LogFactory.getLog((String)"org.apache.http.headers");
    private final Log wireLog = LogFactory.getLog((String)"org.apache.http.wire");
    private volatile Socket socket;
    private HttpHost targetHost;
    private boolean connSecure;
    private volatile boolean shutdown;
    private final Map<String, Object> attributes = new HashMap<String, Object>();

    @Override
    public String getId() {
        return null;
    }

    @Override
    public final HttpHost getTargetHost() {
        return this.targetHost;
    }

    @Override
    public final boolean isSecure() {
        return this.connSecure;
    }

    @Override
    public final Socket getSocket() {
        return this.socket;
    }

    @Override
    public SSLSession getSSLSession() {
        if (this.socket instanceof SSLSocket) {
            return ((SSLSocket)this.socket).getSession();
        }
        return null;
    }

    @Override
    public void opening(Socket sock, HttpHost target) throws IOException {
        this.assertNotOpen();
        this.socket = sock;
        this.targetHost = target;
        if (this.shutdown) {
            sock.close();
            throw new InterruptedIOException("Connection already shutdown");
        }
    }

    @Override
    public void openCompleted(boolean secure, HttpParams params) throws IOException {
        Args.notNull((Object)params, (String)"Parameters");
        this.assertNotOpen();
        this.connSecure = secure;
        this.bind(this.socket, params);
    }

    public void shutdown() throws IOException {
        this.shutdown = true;
        try {
            Socket sock;
            super.shutdown();
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)("Connection " + this + " shut down"));
            }
            if ((sock = this.socket) != null) {
                sock.close();
            }
        }
        catch (IOException ex) {
            this.log.debug((Object)"I/O error shutting down connection", (Throwable)ex);
        }
    }

    public void close() throws IOException {
        try {
            super.close();
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)("Connection " + this + " closed"));
            }
        }
        catch (IOException ex) {
            this.log.debug((Object)"I/O error closing connection", (Throwable)ex);
        }
    }

    protected SessionInputBuffer createSessionInputBuffer(Socket socket, int bufferSize, HttpParams params) throws IOException {
        SessionInputBuffer inBuffer = super.createSessionInputBuffer(socket, bufferSize > 0 ? bufferSize : 8192, params);
        if (this.wireLog.isDebugEnabled()) {
            inBuffer = new LoggingSessionInputBuffer(inBuffer, new Wire(this.wireLog), HttpProtocolParams.getHttpElementCharset((HttpParams)params));
        }
        return inBuffer;
    }

    protected SessionOutputBuffer createSessionOutputBuffer(Socket socket, int bufferSize, HttpParams params) throws IOException {
        SessionOutputBuffer outbuffer = super.createSessionOutputBuffer(socket, bufferSize > 0 ? bufferSize : 8192, params);
        if (this.wireLog.isDebugEnabled()) {
            outbuffer = new LoggingSessionOutputBuffer(outbuffer, new Wire(this.wireLog), HttpProtocolParams.getHttpElementCharset((HttpParams)params));
        }
        return outbuffer;
    }

    protected HttpMessageParser<HttpResponse> createResponseParser(SessionInputBuffer buffer, HttpResponseFactory responseFactory, HttpParams params) {
        return new DefaultHttpResponseParser(buffer, null, responseFactory, params);
    }

    @Override
    public void bind(Socket socket) throws IOException {
        this.bind(socket, (HttpParams)new BasicHttpParams());
    }

    @Override
    public void update(Socket sock, HttpHost target, boolean secure, HttpParams params) throws IOException {
        this.assertOpen();
        Args.notNull((Object)target, (String)"Target host");
        Args.notNull((Object)params, (String)"Parameters");
        if (sock != null) {
            this.socket = sock;
            this.bind(sock, params);
        }
        this.targetHost = target;
        this.connSecure = secure;
    }

    public HttpResponse receiveResponseHeader() throws HttpException, IOException {
        HttpResponse response = super.receiveResponseHeader();
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)("Receiving response: " + response.getStatusLine()));
        }
        if (this.headerLog.isDebugEnabled()) {
            Header[] headers;
            this.headerLog.debug((Object)("<< " + response.getStatusLine().toString()));
            for (Header header : headers = response.getAllHeaders()) {
                this.headerLog.debug((Object)("<< " + header.toString()));
            }
        }
        return response;
    }

    public void sendRequestHeader(HttpRequest request) throws HttpException, IOException {
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)("Sending request: " + request.getRequestLine()));
        }
        super.sendRequestHeader(request);
        if (this.headerLog.isDebugEnabled()) {
            Header[] headers;
            this.headerLog.debug((Object)(">> " + request.getRequestLine().toString()));
            for (Header header : headers = request.getAllHeaders()) {
                this.headerLog.debug((Object)(">> " + header.toString()));
            }
        }
    }

    public Object getAttribute(String id) {
        return this.attributes.get(id);
    }

    public Object removeAttribute(String id) {
        return this.attributes.remove(id);
    }

    public void setAttribute(String id, Object obj) {
        this.attributes.put(id, obj);
    }
}

