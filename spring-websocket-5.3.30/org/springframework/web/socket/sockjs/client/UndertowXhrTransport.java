/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.undertow.client.ClientCallback
 *  io.undertow.client.ClientConnection
 *  io.undertow.client.ClientExchange
 *  io.undertow.client.ClientRequest
 *  io.undertow.client.ClientResponse
 *  io.undertow.client.UndertowClient
 *  io.undertow.connector.ByteBufferPool
 *  io.undertow.connector.PooledByteBuffer
 *  io.undertow.server.DefaultByteBufferPool
 *  io.undertow.util.AttachmentKey
 *  io.undertow.util.HeaderMap
 *  io.undertow.util.HttpString
 *  io.undertow.util.Methods
 *  io.undertow.util.StringReadChannelListener
 *  org.springframework.http.HttpHeaders
 *  org.springframework.http.HttpStatus
 *  org.springframework.http.ResponseEntity
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.MultiValueMap
 *  org.springframework.util.StreamUtils
 *  org.springframework.util.StringUtils
 *  org.springframework.util.concurrent.SettableListenableFuture
 *  org.springframework.web.client.HttpServerErrorException
 *  org.xnio.ChannelListener
 *  org.xnio.ChannelListeners
 *  org.xnio.IoUtils
 *  org.xnio.OptionMap
 *  org.xnio.Options
 *  org.xnio.Xnio
 *  org.xnio.XnioWorker
 *  org.xnio.channels.StreamSinkChannel
 *  org.xnio.channels.StreamSourceChannel
 */
package org.springframework.web.socket.sockjs.client;

import io.undertow.client.ClientCallback;
import io.undertow.client.ClientConnection;
import io.undertow.client.ClientExchange;
import io.undertow.client.ClientRequest;
import io.undertow.client.ClientResponse;
import io.undertow.client.UndertowClient;
import io.undertow.connector.ByteBufferPool;
import io.undertow.connector.PooledByteBuffer;
import io.undertow.server.DefaultByteBufferPool;
import io.undertow.util.AttachmentKey;
import io.undertow.util.HeaderMap;
import io.undertow.util.HttpString;
import io.undertow.util.Methods;
import io.undertow.util.StringReadChannelListener;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
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
import org.xnio.ChannelListener;
import org.xnio.ChannelListeners;
import org.xnio.IoUtils;
import org.xnio.OptionMap;
import org.xnio.Options;
import org.xnio.Xnio;
import org.xnio.XnioWorker;
import org.xnio.channels.StreamSinkChannel;
import org.xnio.channels.StreamSourceChannel;

public class UndertowXhrTransport
extends AbstractXhrTransport {
    private static final AttachmentKey<String> RESPONSE_BODY = AttachmentKey.create(String.class);
    private final OptionMap optionMap;
    private final UndertowClient httpClient;
    private final XnioWorker worker;
    private final ByteBufferPool bufferPool;

    public UndertowXhrTransport() throws IOException {
        this(OptionMap.builder().parse(Options.WORKER_NAME, "SockJSClient").getMap());
    }

    public UndertowXhrTransport(OptionMap optionMap) throws IOException {
        Assert.notNull((Object)optionMap, (String)"OptionMap is required");
        this.optionMap = optionMap;
        this.httpClient = UndertowClient.getInstance();
        this.worker = Xnio.getInstance().createWorker(optionMap);
        this.bufferPool = new DefaultByteBufferPool(false, 1024, -1, 2);
    }

    public UndertowClient getHttpClient() {
        return this.httpClient;
    }

    public XnioWorker getWorker() {
        return this.worker;
    }

    @Override
    protected void connectInternal(TransportRequest request, WebSocketHandler handler, URI receiveUrl, HttpHeaders handshakeHeaders, XhrClientSockJsSession session, SettableListenableFuture<WebSocketSession> connectFuture) {
        this.executeReceiveRequest(request, receiveUrl, handshakeHeaders, session, connectFuture);
    }

    private void executeReceiveRequest(final TransportRequest transportRequest, final URI url, final HttpHeaders headers, final XhrClientSockJsSession session, final SettableListenableFuture<WebSocketSession> connectFuture) {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace((Object)("Starting XHR receive request for " + url));
        }
        ClientCallback<ClientConnection> clientCallback = new ClientCallback<ClientConnection>(){

            public void completed(ClientConnection connection) {
                ClientRequest request = new ClientRequest().setMethod(Methods.POST).setPath(url.getPath());
                HttpString headerName = HttpString.tryFromString((String)"Host");
                request.getRequestHeaders().add(headerName, url.getHost());
                UndertowXhrTransport.addHttpHeaders(request, headers);
                HttpHeaders httpHeaders = transportRequest.getHttpRequestHeaders();
                connection.sendRequest(request, UndertowXhrTransport.this.createReceiveCallback(transportRequest, url, httpHeaders, session, (SettableListenableFuture<WebSocketSession>)connectFuture));
            }

            public void failed(IOException ex) {
                throw new SockJsTransportFailureException("Failed to execute request to " + url, ex);
            }
        };
        this.httpClient.connect((ClientCallback)clientCallback, url, this.worker, this.bufferPool, this.optionMap);
    }

    private static void addHttpHeaders(ClientRequest request, HttpHeaders headers) {
        HeaderMap headerMap = request.getRequestHeaders();
        headers.forEach((key, values) -> {
            for (String value : values) {
                headerMap.add(HttpString.tryFromString((String)key), value);
            }
        });
    }

    private ClientCallback<ClientExchange> createReceiveCallback(final TransportRequest transportRequest, final URI url, final HttpHeaders headers, final XhrClientSockJsSession sockJsSession, final SettableListenableFuture<WebSocketSession> connectFuture) {
        return new ClientCallback<ClientExchange>(){

            public void completed(final ClientExchange exchange) {
                exchange.setResponseListener((ClientCallback)new ClientCallback<ClientExchange>(){

                    public void completed(ClientExchange result) {
                        ClientResponse response = result.getResponse();
                        if (response.getResponseCode() != 200) {
                            HttpStatus status = HttpStatus.valueOf((int)response.getResponseCode());
                            IoUtils.safeClose((Closeable)result.getConnection());
                            this.onFailure((Throwable)new HttpServerErrorException(status, "Unexpected XHR receive status"));
                        } else {
                            SockJsResponseListener listener = new SockJsResponseListener(transportRequest, result.getConnection(), url, headers, sockJsSession, (SettableListenableFuture<WebSocketSession>)connectFuture);
                            listener.setup(result.getResponseChannel());
                        }
                        if (UndertowXhrTransport.this.logger.isTraceEnabled()) {
                            UndertowXhrTransport.this.logger.trace((Object)("XHR receive headers: " + UndertowXhrTransport.toHttpHeaders(response.getResponseHeaders())));
                        }
                        try {
                            StreamSinkChannel channel = result.getRequestChannel();
                            channel.shutdownWrites();
                            if (!channel.flush()) {
                                channel.getWriteSetter().set(ChannelListeners.flushingChannelListener(null, null));
                                channel.resumeWrites();
                            }
                        }
                        catch (IOException exc) {
                            IoUtils.safeClose((Closeable)result.getConnection());
                            this.onFailure(exc);
                        }
                    }

                    public void failed(IOException exc) {
                        IoUtils.safeClose((Closeable)exchange.getConnection());
                        this.onFailure(exc);
                    }
                });
            }

            public void failed(IOException exc) {
                this.onFailure(exc);
            }

            private void onFailure(Throwable failure) {
                if (connectFuture.setException(failure)) {
                    return;
                }
                if (sockJsSession.isDisconnected()) {
                    sockJsSession.afterTransportClosed(null);
                } else {
                    sockJsSession.handleTransportError(failure);
                    sockJsSession.afterTransportClosed(new CloseStatus(1006, failure.getMessage()));
                }
            }
        };
    }

    private static HttpHeaders toHttpHeaders(HeaderMap headerMap) {
        HttpHeaders httpHeaders = new HttpHeaders();
        for (HttpString name : headerMap.getHeaderNames()) {
            for (String value : headerMap.get(name)) {
                httpHeaders.add(name.toString(), value);
            }
        }
        return httpHeaders;
    }

    @Override
    protected ResponseEntity<String> executeInfoRequestInternal(URI infoUrl, HttpHeaders headers) {
        return this.executeRequest(infoUrl, Methods.GET, headers, null);
    }

    @Override
    protected ResponseEntity<String> executeSendRequestInternal(URI url, HttpHeaders headers, TextMessage message) {
        return this.executeRequest(url, Methods.POST, headers, (String)message.getPayload());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected ResponseEntity<String> executeRequest(URI url, HttpString method, HttpHeaders headers, @Nullable String body) {
        ResponseEntity responseEntity;
        CountDownLatch latch = new CountDownLatch(1);
        CopyOnWriteArrayList<ClientResponse> responses = new CopyOnWriteArrayList<ClientResponse>();
        ClientConnection connection = (ClientConnection)this.httpClient.connect(url, this.worker, this.bufferPool, this.optionMap).get();
        try {
            ClientRequest request = new ClientRequest().setMethod(method).setPath(url.getPath());
            request.getRequestHeaders().add(HttpString.tryFromString((String)"Host"), url.getHost());
            if (StringUtils.hasLength((String)body)) {
                HttpString headerName = HttpString.tryFromString((String)"Content-Length");
                request.getRequestHeaders().add(headerName, (long)body.length());
            }
            UndertowXhrTransport.addHttpHeaders(request, headers);
            connection.sendRequest(request, this.createRequestCallback(body, responses, latch));
            latch.await();
            ClientResponse response = (ClientResponse)responses.iterator().next();
            HttpStatus status = HttpStatus.valueOf((int)response.getResponseCode());
            HttpHeaders responseHeaders = UndertowXhrTransport.toHttpHeaders(response.getResponseHeaders());
            String responseBody = (String)response.getAttachment(RESPONSE_BODY);
            responseEntity = responseBody != null ? new ResponseEntity((Object)responseBody, (MultiValueMap)responseHeaders, status) : new ResponseEntity((MultiValueMap)responseHeaders, status);
        }
        catch (Throwable throwable) {
            try {
                IoUtils.safeClose((Closeable)connection);
                throw throwable;
            }
            catch (IOException ex) {
                throw new SockJsTransportFailureException("Failed to execute request to " + url, ex);
            }
            catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                throw new SockJsTransportFailureException("Interrupted while processing request to " + url, ex);
            }
        }
        IoUtils.safeClose((Closeable)connection);
        return responseEntity;
    }

    private ClientCallback<ClientExchange> createRequestCallback(final @Nullable String body, final List<ClientResponse> responses, final CountDownLatch latch) {
        return new ClientCallback<ClientExchange>(){

            public void completed(ClientExchange result) {
                result.setResponseListener((ClientCallback)new ClientCallback<ClientExchange>(){

                    public void completed(final ClientExchange result) {
                        responses.add(result.getResponse());
                        new StringReadChannelListener(result.getConnection().getBufferPool()){

                            protected void stringDone(String string) {
                                result.getResponse().putAttachment(RESPONSE_BODY, (Object)string);
                                latch.countDown();
                            }

                            protected void error(IOException ex) {
                                this.onFailure(latch, ex);
                            }
                        }.setup(result.getResponseChannel());
                    }

                    public void failed(IOException ex) {
                        this.onFailure(latch, ex);
                    }
                });
                try {
                    if (body != null) {
                        result.getRequestChannel().write(ByteBuffer.wrap(body.getBytes()));
                    }
                    result.getRequestChannel().shutdownWrites();
                    if (!result.getRequestChannel().flush()) {
                        result.getRequestChannel().getWriteSetter().set(ChannelListeners.flushingChannelListener(null, null));
                        result.getRequestChannel().resumeWrites();
                    }
                }
                catch (IOException ex) {
                    this.onFailure(latch, ex);
                }
            }

            public void failed(IOException ex) {
                this.onFailure(latch, ex);
            }

            private void onFailure(CountDownLatch latch2, IOException ex) {
                latch2.countDown();
                throw new SockJsTransportFailureException("Failed to execute request", ex);
            }
        };
    }

    private class SockJsResponseListener
    implements ChannelListener<StreamSourceChannel> {
        private final TransportRequest request;
        private final ClientConnection connection;
        private final URI url;
        private final HttpHeaders headers;
        private final XhrClientSockJsSession session;
        private final SettableListenableFuture<WebSocketSession> connectFuture;
        private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        public SockJsResponseListener(TransportRequest request, ClientConnection connection, URI url, HttpHeaders headers, XhrClientSockJsSession sockJsSession, SettableListenableFuture<WebSocketSession> connectFuture) {
            this.request = request;
            this.connection = connection;
            this.url = url;
            this.headers = headers;
            this.session = sockJsSession;
            this.connectFuture = connectFuture;
        }

        public void setup(StreamSourceChannel channel) {
            channel.suspendReads();
            channel.getReadSetter().set((ChannelListener)this);
            channel.resumeReads();
        }

        public void handleEvent(StreamSourceChannel channel) {
            if (this.session.isDisconnected()) {
                if (UndertowXhrTransport.this.logger.isDebugEnabled()) {
                    UndertowXhrTransport.this.logger.debug((Object)"SockJS sockJsSession closed, closing response.");
                }
                IoUtils.safeClose((Closeable)this.connection);
                throw new SockJsException("Session closed.", this.session.getId(), null);
            }
            try (PooledByteBuffer pooled = UndertowXhrTransport.this.bufferPool.allocate();){
                int r;
                do {
                    ByteBuffer buffer = pooled.getBuffer();
                    buffer.clear();
                    r = channel.read(buffer);
                    buffer.flip();
                    if (r == 0) {
                        return;
                    }
                    if (r == -1) {
                        this.onSuccess();
                        continue;
                    }
                    while (buffer.hasRemaining()) {
                        byte b = buffer.get();
                        if (b == 10) {
                            this.handleFrame();
                            continue;
                        }
                        this.outputStream.write(b);
                    }
                } while (r > 0);
            }
            catch (IOException exc) {
                this.onFailure(exc);
            }
        }

        private void handleFrame() {
            String content = StreamUtils.copyToString((ByteArrayOutputStream)this.outputStream, (Charset)SockJsFrame.CHARSET);
            this.outputStream.reset();
            if (UndertowXhrTransport.this.logger.isTraceEnabled()) {
                UndertowXhrTransport.this.logger.trace((Object)("XHR content received: " + content));
            }
            if (!AbstractXhrTransport.PRELUDE.equals(content)) {
                this.session.handleFrame(content);
            }
        }

        public void onSuccess() {
            if (this.outputStream.size() > 0) {
                this.handleFrame();
            }
            if (UndertowXhrTransport.this.logger.isTraceEnabled()) {
                UndertowXhrTransport.this.logger.trace((Object)"XHR receive request completed.");
            }
            IoUtils.safeClose((Closeable)this.connection);
            UndertowXhrTransport.this.executeReceiveRequest(this.request, this.url, this.headers, this.session, (SettableListenableFuture<WebSocketSession>)this.connectFuture);
        }

        public void onFailure(Throwable failure) {
            IoUtils.safeClose((Closeable)this.connection);
            if (this.connectFuture.setException(failure)) {
                return;
            }
            if (this.session.isDisconnected()) {
                this.session.afterTransportClosed(null);
            } else {
                this.session.handleTransportError(failure);
                this.session.afterTransportClosed(new CloseStatus(1006, failure.getMessage()));
            }
        }
    }
}

