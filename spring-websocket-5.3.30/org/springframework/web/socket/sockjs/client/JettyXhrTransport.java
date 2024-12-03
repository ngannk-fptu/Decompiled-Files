/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.client.HttpClient
 *  org.eclipse.jetty.client.api.ContentProvider
 *  org.eclipse.jetty.client.api.ContentResponse
 *  org.eclipse.jetty.client.api.Request
 *  org.eclipse.jetty.client.api.Response
 *  org.eclipse.jetty.client.api.Response$CompleteListener
 *  org.eclipse.jetty.client.api.Response$Listener$Adapter
 *  org.eclipse.jetty.client.util.StringContentProvider
 *  org.eclipse.jetty.http.HttpFields
 *  org.eclipse.jetty.http.HttpMethod
 *  org.springframework.context.Lifecycle
 *  org.springframework.http.HttpHeaders
 *  org.springframework.http.HttpStatus
 *  org.springframework.http.ResponseEntity
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.MultiValueMap
 *  org.springframework.util.StreamUtils
 *  org.springframework.util.concurrent.SettableListenableFuture
 *  org.springframework.web.client.HttpServerErrorException
 */
package org.springframework.web.socket.sockjs.client;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Enumeration;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentProvider;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpMethod;
import org.springframework.context.Lifecycle;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StreamUtils;
import org.springframework.util.concurrent.SettableListenableFuture;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.sockjs.SockJsException;
import org.springframework.web.socket.sockjs.SockJsTransportFailureException;
import org.springframework.web.socket.sockjs.client.AbstractXhrTransport;
import org.springframework.web.socket.sockjs.client.TransportRequest;
import org.springframework.web.socket.sockjs.client.XhrClientSockJsSession;
import org.springframework.web.socket.sockjs.frame.SockJsFrame;

public class JettyXhrTransport
extends AbstractXhrTransport
implements Lifecycle {
    private final HttpClient httpClient;

    public JettyXhrTransport(HttpClient httpClient) {
        Assert.notNull((Object)httpClient, (String)"'httpClient' is required");
        this.httpClient = httpClient;
    }

    public HttpClient getHttpClient() {
        return this.httpClient;
    }

    public void start() {
        try {
            if (!this.httpClient.isRunning()) {
                this.httpClient.start();
            }
        }
        catch (Exception ex) {
            throw new SockJsException("Failed to start JettyXhrTransport", ex);
        }
    }

    public void stop() {
        try {
            if (this.httpClient.isRunning()) {
                this.httpClient.stop();
            }
        }
        catch (Exception ex) {
            throw new SockJsException("Failed to stop JettyXhrTransport", ex);
        }
    }

    public boolean isRunning() {
        return this.httpClient.isRunning();
    }

    @Override
    protected void connectInternal(TransportRequest transportRequest, WebSocketHandler handler, URI url, HttpHeaders handshakeHeaders, XhrClientSockJsSession session, SettableListenableFuture<WebSocketSession> connectFuture) {
        HttpHeaders httpHeaders = transportRequest.getHttpRequestHeaders();
        SockJsResponseListener listener = new SockJsResponseListener(url, httpHeaders, session, connectFuture);
        this.executeReceiveRequest(url, handshakeHeaders, listener);
    }

    private void executeReceiveRequest(URI url, HttpHeaders headers, SockJsResponseListener listener) {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace((Object)("Starting XHR receive request, url=" + url));
        }
        Request httpRequest = this.httpClient.newRequest(url).method(HttpMethod.POST);
        JettyXhrTransport.addHttpHeaders(httpRequest, headers);
        httpRequest.send((Response.CompleteListener)listener);
    }

    @Override
    protected ResponseEntity<String> executeInfoRequestInternal(URI infoUrl, HttpHeaders headers) {
        return this.executeRequest(infoUrl, HttpMethod.GET, headers, null);
    }

    @Override
    public ResponseEntity<String> executeSendRequestInternal(URI url, HttpHeaders headers, TextMessage message) {
        return this.executeRequest(url, HttpMethod.POST, headers, (String)message.getPayload());
    }

    protected ResponseEntity<String> executeRequest(URI url, HttpMethod method, HttpHeaders headers, @Nullable String body) {
        ContentResponse response;
        Request httpRequest = this.httpClient.newRequest(url).method(method);
        JettyXhrTransport.addHttpHeaders(httpRequest, headers);
        if (body != null) {
            httpRequest.content((ContentProvider)new StringContentProvider(body));
        }
        try {
            response = httpRequest.send();
        }
        catch (Exception ex) {
            throw new SockJsTransportFailureException("Failed to execute request to " + url, ex);
        }
        HttpStatus status = HttpStatus.valueOf((int)response.getStatus());
        HttpHeaders responseHeaders = JettyXhrTransport.toHttpHeaders(response.getHeaders());
        return response.getContent() != null ? new ResponseEntity((Object)response.getContentAsString(), (MultiValueMap)responseHeaders, status) : new ResponseEntity((MultiValueMap)responseHeaders, status);
    }

    private static void addHttpHeaders(Request request, HttpHeaders headers) {
        headers.forEach((key, values) -> {
            for (String value : values) {
                request.header(key, value);
            }
        });
    }

    private static HttpHeaders toHttpHeaders(HttpFields httpFields) {
        HttpHeaders responseHeaders = new HttpHeaders();
        Enumeration names = httpFields.getFieldNames();
        while (names.hasMoreElements()) {
            String name = (String)names.nextElement();
            Enumeration values = httpFields.getValues(name);
            while (values.hasMoreElements()) {
                String value = (String)values.nextElement();
                responseHeaders.add(name, value);
            }
        }
        return responseHeaders;
    }

    private class SockJsResponseListener
    extends Response.Listener.Adapter {
        private final URI transportUrl;
        private final HttpHeaders receiveHeaders;
        private final XhrClientSockJsSession sockJsSession;
        private final SettableListenableFuture<WebSocketSession> connectFuture;
        private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        public SockJsResponseListener(URI url, HttpHeaders headers, XhrClientSockJsSession sockJsSession, SettableListenableFuture<WebSocketSession> connectFuture) {
            this.transportUrl = url;
            this.receiveHeaders = headers;
            this.connectFuture = connectFuture;
            this.sockJsSession = sockJsSession;
        }

        public void onBegin(Response response) {
            if (response.getStatus() != 200) {
                HttpStatus status = HttpStatus.valueOf((int)response.getStatus());
                response.abort((Throwable)new HttpServerErrorException(status, "Unexpected XHR receive status"));
            }
        }

        public void onHeaders(Response response) {
            if (JettyXhrTransport.this.logger.isTraceEnabled()) {
                JettyXhrTransport.this.logger.trace((Object)("XHR receive headers: " + JettyXhrTransport.toHttpHeaders(response.getHeaders())));
            }
        }

        public void onContent(Response response, ByteBuffer buffer) {
            while (true) {
                if (this.sockJsSession.isDisconnected()) {
                    if (JettyXhrTransport.this.logger.isDebugEnabled()) {
                        JettyXhrTransport.this.logger.debug((Object)"SockJS sockJsSession closed, closing response.");
                    }
                    response.abort((Throwable)((Object)new SockJsException("Session closed.", this.sockJsSession.getId(), null)));
                    return;
                }
                if (buffer.remaining() == 0) break;
                byte b = buffer.get();
                if (b == 10) {
                    this.handleFrame();
                    continue;
                }
                this.outputStream.write(b);
            }
        }

        private void handleFrame() {
            String content = StreamUtils.copyToString((ByteArrayOutputStream)this.outputStream, (Charset)SockJsFrame.CHARSET);
            this.outputStream.reset();
            if (JettyXhrTransport.this.logger.isTraceEnabled()) {
                JettyXhrTransport.this.logger.trace((Object)("XHR content received: " + content));
            }
            if (!AbstractXhrTransport.PRELUDE.equals(content)) {
                this.sockJsSession.handleFrame(content);
            }
        }

        public void onSuccess(Response response) {
            if (this.outputStream.size() > 0) {
                this.handleFrame();
            }
            if (JettyXhrTransport.this.logger.isTraceEnabled()) {
                JettyXhrTransport.this.logger.trace((Object)"XHR receive request completed.");
            }
            JettyXhrTransport.this.executeReceiveRequest(this.transportUrl, this.receiveHeaders, this);
        }

        public void onFailure(Response response, Throwable failure) {
            if (this.connectFuture.setException(failure)) {
                return;
            }
            if (this.sockJsSession.isDisconnected()) {
                this.sockJsSession.afterTransportClosed(null);
            } else {
                this.sockJsSession.handleTransportError(failure);
                this.sockJsSession.afterTransportClosed(new CloseStatus(1006, failure.getMessage()));
            }
        }
    }
}

