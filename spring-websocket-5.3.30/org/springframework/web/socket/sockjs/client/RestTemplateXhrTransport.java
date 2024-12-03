/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.task.SimpleAsyncTaskExecutor
 *  org.springframework.core.task.TaskExecutor
 *  org.springframework.http.HttpHeaders
 *  org.springframework.http.HttpMethod
 *  org.springframework.http.HttpStatus
 *  org.springframework.http.ResponseEntity
 *  org.springframework.http.ResponseEntity$BodyBuilder
 *  org.springframework.http.StreamingHttpOutputMessage
 *  org.springframework.http.client.ClientHttpRequest
 *  org.springframework.http.client.ClientHttpResponse
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.StreamUtils
 *  org.springframework.util.concurrent.SettableListenableFuture
 *  org.springframework.web.client.HttpServerErrorException
 *  org.springframework.web.client.RequestCallback
 *  org.springframework.web.client.ResponseExtractor
 *  org.springframework.web.client.RestOperations
 *  org.springframework.web.client.RestTemplate
 *  org.springframework.web.client.UnknownHttpStatusCodeException
 */
package org.springframework.web.socket.sockjs.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Map;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.StreamingHttpOutputMessage;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StreamUtils;
import org.springframework.util.concurrent.SettableListenableFuture;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.UnknownHttpStatusCodeException;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.sockjs.client.AbstractXhrTransport;
import org.springframework.web.socket.sockjs.client.TransportRequest;
import org.springframework.web.socket.sockjs.client.XhrClientSockJsSession;
import org.springframework.web.socket.sockjs.frame.SockJsFrame;

public class RestTemplateXhrTransport
extends AbstractXhrTransport {
    private final RestOperations restTemplate;
    private TaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();
    private static final ResponseExtractor<ResponseEntity<String>> textResponseExtractor = response -> {
        String body = StreamUtils.copyToString((InputStream)response.getBody(), (Charset)SockJsFrame.CHARSET);
        return ((ResponseEntity.BodyBuilder)ResponseEntity.status((int)response.getRawStatusCode()).headers(response.getHeaders())).body((Object)body);
    };

    public RestTemplateXhrTransport() {
        this((RestOperations)new RestTemplate());
    }

    public RestTemplateXhrTransport(RestOperations restTemplate) {
        Assert.notNull((Object)restTemplate, (String)"'restTemplate' is required");
        this.restTemplate = restTemplate;
    }

    public RestOperations getRestTemplate() {
        return this.restTemplate;
    }

    public void setTaskExecutor(TaskExecutor taskExecutor) {
        Assert.notNull((Object)taskExecutor, (String)"TaskExecutor must not be null");
        this.taskExecutor = taskExecutor;
    }

    public TaskExecutor getTaskExecutor() {
        return this.taskExecutor;
    }

    @Override
    protected void connectInternal(TransportRequest transportRequest, WebSocketHandler handler, URI receiveUrl, HttpHeaders handshakeHeaders, XhrClientSockJsSession session, SettableListenableFuture<WebSocketSession> connectFuture) {
        this.getTaskExecutor().execute(() -> {
            HttpHeaders httpHeaders = transportRequest.getHttpRequestHeaders();
            XhrRequestCallback requestCallback = new XhrRequestCallback(handshakeHeaders);
            XhrRequestCallback requestCallbackAfterHandshake = new XhrRequestCallback(httpHeaders);
            XhrReceiveExtractor responseExtractor = new XhrReceiveExtractor(session);
            while (true) {
                if (session.isDisconnected()) {
                    session.afterTransportClosed(null);
                    break;
                }
                try {
                    if (this.logger.isTraceEnabled()) {
                        this.logger.trace((Object)("Starting XHR receive request, url=" + receiveUrl));
                    }
                    this.getRestTemplate().execute(receiveUrl, HttpMethod.POST, (RequestCallback)requestCallback, (ResponseExtractor)responseExtractor);
                    requestCallback = requestCallbackAfterHandshake;
                }
                catch (Exception ex) {
                    if (!connectFuture.isDone()) {
                        connectFuture.setException((Throwable)ex);
                        break;
                    }
                    session.handleTransportError(ex);
                    session.afterTransportClosed(new CloseStatus(1006, ex.getMessage()));
                    break;
                }
            }
        });
    }

    @Override
    protected ResponseEntity<String> executeInfoRequestInternal(URI infoUrl, HttpHeaders headers) {
        XhrRequestCallback requestCallback = new XhrRequestCallback(headers);
        return (ResponseEntity)RestTemplateXhrTransport.nonNull(this.restTemplate.execute(infoUrl, HttpMethod.GET, (RequestCallback)requestCallback, textResponseExtractor));
    }

    @Override
    public ResponseEntity<String> executeSendRequestInternal(URI url, HttpHeaders headers, TextMessage message) {
        XhrRequestCallback requestCallback = new XhrRequestCallback(headers, (String)message.getPayload());
        return (ResponseEntity)RestTemplateXhrTransport.nonNull(this.restTemplate.execute(url, HttpMethod.POST, (RequestCallback)requestCallback, textResponseExtractor));
    }

    private static <T> T nonNull(@Nullable T result) {
        Assert.state((result != null ? 1 : 0) != 0, (String)"No result");
        return result;
    }

    private class XhrReceiveExtractor
    implements ResponseExtractor<Object> {
        private final XhrClientSockJsSession sockJsSession;

        public XhrReceiveExtractor(XhrClientSockJsSession sockJsSession) {
            this.sockJsSession = sockJsSession;
        }

        public Object extractData(ClientHttpResponse response) throws IOException {
            HttpStatus httpStatus = HttpStatus.resolve((int)response.getRawStatusCode());
            if (httpStatus == null) {
                throw new UnknownHttpStatusCodeException(response.getRawStatusCode(), response.getStatusText(), response.getHeaders(), null, null);
            }
            if (httpStatus != HttpStatus.OK) {
                throw new HttpServerErrorException(httpStatus, response.getStatusText(), response.getHeaders(), null, null);
            }
            if (RestTemplateXhrTransport.this.logger.isTraceEnabled()) {
                RestTemplateXhrTransport.this.logger.trace((Object)("XHR receive headers: " + response.getHeaders()));
            }
            InputStream is = response.getBody();
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            while (true) {
                if (this.sockJsSession.isDisconnected()) {
                    if (RestTemplateXhrTransport.this.logger.isDebugEnabled()) {
                        RestTemplateXhrTransport.this.logger.debug((Object)"SockJS sockJsSession closed, closing response.");
                    }
                    response.close();
                    break;
                }
                int b = is.read();
                if (b == -1) {
                    if (os.size() > 0) {
                        this.handleFrame(os);
                    }
                    if (!RestTemplateXhrTransport.this.logger.isTraceEnabled()) break;
                    RestTemplateXhrTransport.this.logger.trace((Object)"XHR receive completed");
                    break;
                }
                if (b == 10) {
                    this.handleFrame(os);
                    continue;
                }
                os.write(b);
            }
            return null;
        }

        private void handleFrame(ByteArrayOutputStream os) throws IOException {
            String content = os.toString(SockJsFrame.CHARSET.name());
            os.reset();
            if (RestTemplateXhrTransport.this.logger.isTraceEnabled()) {
                RestTemplateXhrTransport.this.logger.trace((Object)("XHR receive content: " + content));
            }
            if (!AbstractXhrTransport.PRELUDE.equals(content)) {
                this.sockJsSession.handleFrame(content);
            }
        }
    }

    private static class XhrRequestCallback
    implements RequestCallback {
        private final HttpHeaders headers;
        @Nullable
        private final String body;

        public XhrRequestCallback(HttpHeaders headers) {
            this(headers, null);
        }

        public XhrRequestCallback(HttpHeaders headers, @Nullable String body) {
            this.headers = headers;
            this.body = body;
        }

        public void doWithRequest(ClientHttpRequest request) throws IOException {
            request.getHeaders().putAll((Map)this.headers);
            if (this.body != null) {
                if (request instanceof StreamingHttpOutputMessage) {
                    ((StreamingHttpOutputMessage)request).setBody(outputStream -> StreamUtils.copy((String)this.body, (Charset)SockJsFrame.CHARSET, (OutputStream)outputStream));
                } else {
                    StreamUtils.copy((String)this.body, (Charset)SockJsFrame.CHARSET, (OutputStream)request.getBody());
                }
            }
        }
    }
}

