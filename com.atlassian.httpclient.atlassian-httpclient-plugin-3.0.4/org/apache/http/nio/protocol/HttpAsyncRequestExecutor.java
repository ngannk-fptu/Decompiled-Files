/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.nio.protocol;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.apache.http.ConnectionClosedException;
import org.apache.http.ExceptionLogger;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.ProtocolException;
import org.apache.http.ProtocolVersion;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.nio.ContentDecoder;
import org.apache.http.nio.ContentEncoder;
import org.apache.http.nio.NHttpClientConnection;
import org.apache.http.nio.NHttpClientEventHandler;
import org.apache.http.nio.NHttpConnection;
import org.apache.http.nio.protocol.HttpAsyncClientExchangeHandler;
import org.apache.http.nio.protocol.MessageState;
import org.apache.http.nio.protocol.Pipelined;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;
import org.apache.http.util.Asserts;

@Contract(threading=ThreadingBehavior.IMMUTABLE_CONDITIONAL)
public class HttpAsyncRequestExecutor
implements NHttpClientEventHandler {
    public static final int DEFAULT_WAIT_FOR_CONTINUE = 3000;
    public static final String HTTP_HANDLER = "http.nio.exchange-handler";
    private final int waitForContinue;
    private final ExceptionLogger exceptionLogger;
    static final String HTTP_EXCHANGE_STATE = "http.nio.http-exchange-state";

    public HttpAsyncRequestExecutor(int waitForContinue, ExceptionLogger exceptionLogger) {
        this.waitForContinue = Args.positive(waitForContinue, "Wait for continue time");
        this.exceptionLogger = exceptionLogger != null ? exceptionLogger : ExceptionLogger.NO_OP;
    }

    public HttpAsyncRequestExecutor(int waitForContinue) {
        this(waitForContinue, null);
    }

    public HttpAsyncRequestExecutor() {
        this(3000, null);
    }

    private static boolean pipelining(HttpAsyncClientExchangeHandler handler) {
        return handler.getClass().getAnnotation(Pipelined.class) != null;
    }

    @Override
    public void connected(NHttpClientConnection conn, Object attachment) throws IOException, HttpException {
        State state = new State();
        HttpContext context = conn.getContext();
        context.setAttribute(HTTP_EXCHANGE_STATE, state);
        this.requestReady(conn);
    }

    @Override
    public void closed(NHttpClientConnection conn) {
        HttpAsyncClientExchangeHandler handler = HttpAsyncRequestExecutor.getHandler(conn);
        if (handler == null) {
            return;
        }
        State state = HttpAsyncRequestExecutor.getState(conn);
        if (state != null && (state.getRequestState() != MessageState.READY || state.getResponseState() != MessageState.READY)) {
            handler.failed(new ConnectionClosedException("Connection closed unexpectedly"));
        }
        if (!handler.isDone() && HttpAsyncRequestExecutor.pipelining(handler)) {
            handler.failed(new ConnectionClosedException("Connection closed unexpectedly"));
        }
        if (state == null || handler.isDone()) {
            this.closeHandler(handler);
        }
    }

    @Override
    public void exception(NHttpClientConnection conn, Exception cause) {
        this.shutdownConnection(conn);
        HttpAsyncClientExchangeHandler handler = HttpAsyncRequestExecutor.getHandler(conn);
        if (handler != null) {
            handler.failed(cause);
        } else {
            this.log(cause);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void requestReady(NHttpClientConnection conn) throws IOException, HttpException {
        HttpAsyncClientExchangeHandler handler;
        HttpContext context;
        State state = HttpAsyncRequestExecutor.getState(conn);
        Asserts.notNull(state, "Connection state");
        Asserts.check(state.getRequestState() == MessageState.READY || state.getRequestState() == MessageState.COMPLETED, "Unexpected request state %s", (Object)state.getRequestState());
        if (state.getRequestState() == MessageState.COMPLETED) {
            conn.suspendOutput();
            return;
        }
        HttpContext httpContext = context = conn.getContext();
        synchronized (httpContext) {
            handler = HttpAsyncRequestExecutor.getHandler(conn);
            if (handler == null || handler.isDone()) {
                conn.suspendOutput();
                return;
            }
        }
        boolean pipelined = HttpAsyncRequestExecutor.pipelining(handler);
        HttpRequest request = handler.generateRequest();
        if (request == null) {
            conn.suspendOutput();
            return;
        }
        ProtocolVersion version = request.getRequestLine().getProtocolVersion();
        if (pipelined && version.lessEquals(HttpVersion.HTTP_1_0)) {
            throw new ProtocolException(version + " cannot be used with request pipelining");
        }
        state.setRequest(request);
        if (pipelined) {
            state.getRequestQueue().add(request);
        }
        if (request instanceof HttpEntityEnclosingRequest) {
            boolean expectContinue = ((HttpEntityEnclosingRequest)request).expectContinue();
            if (expectContinue && pipelined) {
                throw new ProtocolException("Expect-continue handshake cannot be used with request pipelining");
            }
            conn.submitRequest(request);
            if (expectContinue) {
                int timeout = conn.getSocketTimeout();
                state.setTimeout(timeout);
                conn.setSocketTimeout(this.waitForContinue);
                state.setRequestState(MessageState.ACK_EXPECTED);
            } else {
                HttpEntity entity = ((HttpEntityEnclosingRequest)request).getEntity();
                if (entity != null) {
                    state.setRequestState(MessageState.BODY_STREAM);
                } else {
                    handler.requestCompleted();
                    state.setRequestState(pipelined ? MessageState.READY : MessageState.COMPLETED);
                }
            }
        } else {
            conn.submitRequest(request);
            handler.requestCompleted();
            state.setRequestState(pipelined ? MessageState.READY : MessageState.COMPLETED);
        }
    }

    @Override
    public void outputReady(NHttpClientConnection conn, ContentEncoder encoder) throws IOException, HttpException {
        State state = HttpAsyncRequestExecutor.getState(conn);
        Asserts.notNull(state, "Connection state");
        Asserts.check(state.getRequestState() == MessageState.BODY_STREAM || state.getRequestState() == MessageState.ACK_EXPECTED, "Unexpected request state %s", (Object)state.getRequestState());
        HttpAsyncClientExchangeHandler handler = HttpAsyncRequestExecutor.getHandler(conn);
        Asserts.notNull(handler, "Client exchange handler");
        if (state.getRequestState() == MessageState.ACK_EXPECTED) {
            conn.suspendOutput();
            return;
        }
        handler.produceContent(encoder, conn);
        if (encoder.isCompleted()) {
            handler.requestCompleted();
            state.setRequestState(HttpAsyncRequestExecutor.pipelining(handler) ? MessageState.READY : MessageState.COMPLETED);
        }
    }

    @Override
    public void responseReceived(NHttpClientConnection conn) throws HttpException, IOException {
        HttpRequest request;
        State state = HttpAsyncRequestExecutor.getState(conn);
        Asserts.notNull(state, "Connection state");
        Asserts.check(state.getResponseState() == MessageState.READY, "Unexpected request state %s", (Object)state.getResponseState());
        HttpAsyncClientExchangeHandler handler = HttpAsyncRequestExecutor.getHandler(conn);
        Asserts.notNull(handler, "Client exchange handler");
        if (HttpAsyncRequestExecutor.pipelining(handler)) {
            request = state.getRequestQueue().poll();
            Asserts.notNull(request, "HTTP request");
        } else {
            request = state.getRequest();
            if (request == null) {
                throw new HttpException("Out of sequence response");
            }
        }
        HttpResponse response = conn.getHttpResponse();
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode < 100) {
            throw new ProtocolException("Invalid response: " + response.getStatusLine());
        }
        if (statusCode < 200) {
            if (statusCode != 100) {
                throw new ProtocolException("Unexpected response: " + response.getStatusLine());
            }
            if (state.getRequestState() == MessageState.ACK_EXPECTED) {
                int timeout = state.getTimeout();
                conn.setSocketTimeout(timeout);
                conn.requestOutput();
                state.setRequestState(MessageState.BODY_STREAM);
            }
            return;
        }
        state.setResponse(response);
        if (state.getRequestState() == MessageState.ACK_EXPECTED) {
            int timeout = state.getTimeout();
            conn.setSocketTimeout(timeout);
            conn.resetOutput();
            state.setRequestState(MessageState.COMPLETED);
        } else if (state.getRequestState() == MessageState.BODY_STREAM && statusCode >= 400) {
            conn.resetOutput();
            conn.suspendOutput();
            state.setRequestState(MessageState.COMPLETED);
            state.invalidate();
        }
        if (this.canResponseHaveBody(request, response)) {
            handler.responseReceived(response);
            state.setResponseState(MessageState.BODY_STREAM);
        } else {
            response.setEntity(null);
            handler.responseReceived(response);
            conn.resetInput();
            this.processResponse(conn, state, handler);
        }
    }

    @Override
    public void inputReady(NHttpClientConnection conn, ContentDecoder decoder) throws IOException, HttpException {
        State state = HttpAsyncRequestExecutor.getState(conn);
        Asserts.notNull(state, "Connection state");
        Asserts.check(state.getResponseState() == MessageState.BODY_STREAM, "Unexpected request state %s", (Object)state.getResponseState());
        HttpAsyncClientExchangeHandler handler = HttpAsyncRequestExecutor.getHandler(conn);
        Asserts.notNull(handler, "Client exchange handler");
        handler.consumeContent(decoder, conn);
        if (decoder.isCompleted()) {
            this.processResponse(conn, state, handler);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void endOfInput(NHttpClientConnection conn) throws IOException {
        HttpContext context;
        State state = HttpAsyncRequestExecutor.getState(conn);
        HttpContext httpContext = context = conn.getContext();
        synchronized (httpContext) {
            if (state != null) {
                HttpAsyncClientExchangeHandler handler;
                if (state.getRequestState().compareTo(MessageState.READY) != 0) {
                    state.invalidate();
                }
                if ((handler = HttpAsyncRequestExecutor.getHandler(conn)) != null) {
                    if (state.isValid()) {
                        handler.inputTerminated();
                    } else {
                        handler.failed(new ConnectionClosedException());
                    }
                }
            }
            if (conn.getSocketTimeout() <= 0) {
                conn.setSocketTimeout(1000);
            }
            conn.close();
        }
    }

    @Override
    public void timeout(NHttpClientConnection conn) throws IOException {
        State state = HttpAsyncRequestExecutor.getState(conn);
        if (state != null) {
            if (state.getRequestState() == MessageState.ACK_EXPECTED) {
                int timeout = state.getTimeout();
                conn.setSocketTimeout(timeout);
                conn.requestOutput();
                state.setRequestState(MessageState.BODY_STREAM);
                state.setTimeout(0);
                return;
            }
            state.invalidate();
            HttpAsyncClientExchangeHandler handler = HttpAsyncRequestExecutor.getHandler(conn);
            if (handler != null) {
                handler.failed(new SocketTimeoutException(String.format("%,d milliseconds timeout on connection %s", conn.getSocketTimeout(), conn)));
                handler.close();
            }
        }
        if (conn.getStatus() == 0) {
            conn.close();
            if (conn.getStatus() == 1) {
                conn.setSocketTimeout(250);
            }
        } else {
            conn.shutdown();
        }
    }

    protected void log(Exception ex) {
        this.exceptionLogger.log(ex);
    }

    private static State getState(NHttpConnection conn) {
        return (State)conn.getContext().getAttribute(HTTP_EXCHANGE_STATE);
    }

    private static HttpAsyncClientExchangeHandler getHandler(NHttpConnection conn) {
        return (HttpAsyncClientExchangeHandler)conn.getContext().getAttribute(HTTP_HANDLER);
    }

    private void shutdownConnection(NHttpConnection conn) {
        try {
            conn.shutdown();
        }
        catch (IOException ex) {
            this.log(ex);
        }
    }

    private void closeHandler(HttpAsyncClientExchangeHandler handler) {
        if (handler != null) {
            try {
                handler.close();
            }
            catch (IOException ioex) {
                this.log(ioex);
            }
        }
    }

    private void processResponse(NHttpClientConnection conn, State state, HttpAsyncClientExchangeHandler handler) throws IOException, HttpException {
        if (!state.isValid()) {
            conn.close();
        }
        handler.responseCompleted();
        if (!HttpAsyncRequestExecutor.pipelining(handler)) {
            state.setRequestState(MessageState.READY);
            state.setRequest(null);
        }
        state.setResponseState(MessageState.READY);
        state.setResponse(null);
        if (!handler.isDone() && conn.isOpen()) {
            conn.requestOutput();
        }
    }

    private boolean canResponseHaveBody(HttpRequest request, HttpResponse response) {
        String method = request.getRequestLine().getMethod();
        int status = response.getStatusLine().getStatusCode();
        if (method.equalsIgnoreCase("HEAD")) {
            return false;
        }
        if (method.equalsIgnoreCase("CONNECT") && status < 300) {
            return false;
        }
        return status >= 200 && status != 204 && status != 304 && status != 205;
    }

    static class State {
        private final Queue<HttpRequest> requestQueue = new ConcurrentLinkedQueue<HttpRequest>();
        private volatile MessageState requestState = MessageState.READY;
        private volatile MessageState responseState = MessageState.READY;
        private volatile HttpRequest request;
        private volatile HttpResponse response;
        private volatile boolean valid = true;
        private volatile int timeout;

        State() {
        }

        public MessageState getRequestState() {
            return this.requestState;
        }

        public void setRequestState(MessageState state) {
            this.requestState = state;
        }

        public MessageState getResponseState() {
            return this.responseState;
        }

        public void setResponseState(MessageState state) {
            this.responseState = state;
        }

        public HttpRequest getRequest() {
            return this.request;
        }

        public void setRequest(HttpRequest request) {
            this.request = request;
        }

        public HttpResponse getResponse() {
            return this.response;
        }

        public void setResponse(HttpResponse response) {
            this.response = response;
        }

        public Queue<HttpRequest> getRequestQueue() {
            return this.requestQueue;
        }

        public int getTimeout() {
            return this.timeout;
        }

        public void setTimeout(int timeout) {
            this.timeout = timeout;
        }

        public boolean isValid() {
            return this.valid;
        }

        public void invalidate() {
            this.valid = false;
        }

        public String toString() {
            StringBuilder buf = new StringBuilder();
            buf.append("request state: ");
            buf.append((Object)this.requestState);
            buf.append("; request: ");
            if (this.request != null) {
                buf.append(this.request.getRequestLine());
            }
            buf.append("; response state: ");
            buf.append((Object)this.responseState);
            buf.append("; response: ");
            if (this.response != null) {
                buf.append(this.response.getStatusLine());
            }
            buf.append("; valid: ");
            buf.append(this.valid);
            buf.append(";");
            return buf.toString();
        }
    }
}

