/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.nio.protocol;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.ExceptionLogger;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseFactory;
import org.apache.http.HttpVersion;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.ProtocolException;
import org.apache.http.UnsupportedHttpVersionException;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.concurrent.Cancellable;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.nio.ContentDecoder;
import org.apache.http.nio.ContentEncoder;
import org.apache.http.nio.NHttpConnection;
import org.apache.http.nio.NHttpServerConnection;
import org.apache.http.nio.NHttpServerEventHandler;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.nio.protocol.BasicAsyncResponseProducer;
import org.apache.http.nio.protocol.ErrorResponseProducer;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.nio.protocol.HttpAsyncExpectationVerifier;
import org.apache.http.nio.protocol.HttpAsyncRequestConsumer;
import org.apache.http.nio.protocol.HttpAsyncRequestHandler;
import org.apache.http.nio.protocol.HttpAsyncRequestHandlerMapper;
import org.apache.http.nio.protocol.HttpAsyncRequestHandlerResolver;
import org.apache.http.nio.protocol.HttpAsyncResponseProducer;
import org.apache.http.nio.protocol.MessageState;
import org.apache.http.nio.protocol.NullRequestHandler;
import org.apache.http.nio.reactor.SessionBufferStatus;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpCoreContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.util.Args;
import org.apache.http.util.Asserts;

@Contract(threading=ThreadingBehavior.IMMUTABLE_CONDITIONAL)
public class HttpAsyncService
implements NHttpServerEventHandler {
    static final String HTTP_EXCHANGE_STATE = "http.nio.http-exchange-state";
    private final HttpProcessor httpProcessor;
    private final ConnectionReuseStrategy connectionStrategy;
    private final HttpResponseFactory responseFactory;
    private final HttpAsyncRequestHandlerMapper handlerMapper;
    private final HttpAsyncExpectationVerifier expectationVerifier;
    private final ExceptionLogger exceptionLogger;

    @Deprecated
    public HttpAsyncService(HttpProcessor httpProcessor, ConnectionReuseStrategy connStrategy, HttpResponseFactory responseFactory, HttpAsyncRequestHandlerResolver handlerResolver, HttpAsyncExpectationVerifier expectationVerifier, HttpParams params) {
        this(httpProcessor, connStrategy, responseFactory, new HttpAsyncRequestHandlerResolverAdapter(handlerResolver), expectationVerifier);
    }

    @Deprecated
    public HttpAsyncService(HttpProcessor httpProcessor, ConnectionReuseStrategy connStrategy, HttpAsyncRequestHandlerResolver handlerResolver, HttpParams params) {
        this(httpProcessor, connStrategy, DefaultHttpResponseFactory.INSTANCE, new HttpAsyncRequestHandlerResolverAdapter(handlerResolver), null);
    }

    public HttpAsyncService(HttpProcessor httpProcessor, ConnectionReuseStrategy connStrategy, HttpResponseFactory responseFactory, HttpAsyncRequestHandlerMapper handlerMapper, HttpAsyncExpectationVerifier expectationVerifier) {
        this(httpProcessor, connStrategy, responseFactory, handlerMapper, expectationVerifier, null);
    }

    public HttpAsyncService(HttpProcessor httpProcessor, ConnectionReuseStrategy connStrategy, HttpResponseFactory responseFactory, HttpAsyncRequestHandlerMapper handlerMapper, HttpAsyncExpectationVerifier expectationVerifier, ExceptionLogger exceptionLogger) {
        this.httpProcessor = Args.notNull(httpProcessor, "HTTP processor");
        this.connectionStrategy = connStrategy != null ? connStrategy : DefaultConnectionReuseStrategy.INSTANCE;
        this.responseFactory = responseFactory != null ? responseFactory : DefaultHttpResponseFactory.INSTANCE;
        this.handlerMapper = handlerMapper;
        this.expectationVerifier = expectationVerifier;
        this.exceptionLogger = exceptionLogger != null ? exceptionLogger : ExceptionLogger.NO_OP;
    }

    public HttpAsyncService(HttpProcessor httpProcessor, HttpAsyncRequestHandlerMapper handlerMapper) {
        this(httpProcessor, null, null, handlerMapper, null);
    }

    public HttpAsyncService(HttpProcessor httpProcessor, HttpAsyncRequestHandlerMapper handlerMapper, ExceptionLogger exceptionLogger) {
        this(httpProcessor, null, null, handlerMapper, null, exceptionLogger);
    }

    @Override
    public void connected(NHttpServerConnection conn) {
        State state = new State();
        conn.getContext().setAttribute(HTTP_EXCHANGE_STATE, state);
    }

    @Override
    public void closed(NHttpServerConnection conn) {
        State state = (State)conn.getContext().removeAttribute(HTTP_EXCHANGE_STATE);
        if (state != null) {
            state.setTerminated();
            this.closeHandlers(state);
            Cancellable cancellable = state.getCancellable();
            if (cancellable != null) {
                cancellable.cancel();
            }
        }
    }

    @Override
    public void exception(NHttpServerConnection conn, Exception cause) {
        this.log(cause);
        State state = this.getState(conn);
        if (state == null) {
            this.shutdownConnection(conn);
            return;
        }
        state.setTerminated();
        this.closeHandlers(state, cause);
        try {
            Queue<PipelineEntry> pipeline;
            PipelineEntry pipelineEntry;
            Incoming incoming;
            Cancellable cancellable = state.getCancellable();
            if (cancellable != null) {
                cancellable.cancel();
            }
            if (cause instanceof SocketException || cause.getClass() == IOException.class) {
                conn.shutdown();
                return;
            }
            if (cause instanceof SocketTimeoutException) {
                conn.close();
                return;
            }
            if (conn.isResponseSubmitted() || state.getResponseState().compareTo(MessageState.INIT) > 0) {
                conn.close();
                return;
            }
            HttpRequest request = conn.getHttpRequest();
            if (request == null && (incoming = state.getIncoming()) != null) {
                request = incoming.getRequest();
            }
            if (request == null && (pipelineEntry = (pipeline = state.getPipeline()).poll()) != null) {
                request = pipelineEntry.getRequest();
            }
            if (request != null) {
                conn.resetInput();
                HttpCoreContext context = HttpCoreContext.create();
                HttpAsyncResponseProducer responseProducer = this.handleException(cause, context);
                HttpResponse response = responseProducer.generateResponse();
                Outgoing outgoing = new Outgoing(request, response, responseProducer, context);
                state.setResponseState(MessageState.INIT);
                state.setOutgoing(outgoing);
                this.commitFinalResponse(conn, state);
                return;
            }
            conn.close();
        }
        catch (Exception ex) {
            this.shutdownConnection(conn);
            this.closeHandlers(state);
            if (ex instanceof RuntimeException) {
                throw (RuntimeException)ex;
            }
            this.log(ex);
        }
    }

    protected HttpResponse createHttpResponse(int status, HttpContext context) {
        return this.responseFactory.newHttpResponse(HttpVersion.HTTP_1_1, status, context);
    }

    @Override
    public void requestReceived(NHttpServerConnection conn) throws IOException, HttpException {
        State state = this.getState(conn);
        Asserts.notNull(state, "Connection state");
        Asserts.check(state.getRequestState() == MessageState.READY, "Unexpected request state %s", (Object)state.getRequestState());
        HttpRequest request = conn.getHttpRequest();
        BasicHttpContext context = new BasicHttpContext();
        context.setAttribute("http.request", request);
        context.setAttribute("http.connection", conn);
        this.httpProcessor.process(request, (HttpContext)context);
        HttpAsyncRequestHandler<Object> requestHandler = this.getRequestHandler(request);
        HttpAsyncRequestConsumer<Object> consumer = requestHandler.processRequest(request, context);
        consumer.requestReceived(request);
        Incoming incoming = new Incoming(request, requestHandler, consumer, context);
        state.setIncoming(incoming);
        if (request instanceof HttpEntityEnclosingRequest) {
            if (((HttpEntityEnclosingRequest)request).expectContinue() && state.getResponseState() == MessageState.READY && state.getPipeline().isEmpty() && (!(conn instanceof SessionBufferStatus) || !((SessionBufferStatus)((Object)conn)).hasBufferedInput())) {
                state.setRequestState(MessageState.ACK_EXPECTED);
                HttpResponse ack = this.createHttpResponse(100, context);
                if (this.expectationVerifier != null) {
                    conn.suspendInput();
                    conn.suspendOutput();
                    HttpAsyncExchangeImpl httpAsyncExchange = new HttpAsyncExchangeImpl(request, ack, state, conn, context);
                    this.expectationVerifier.verify(httpAsyncExchange, context);
                } else {
                    conn.submitResponse(ack);
                    state.setRequestState(MessageState.BODY_STREAM);
                }
            } else {
                state.setRequestState(MessageState.BODY_STREAM);
            }
        } else {
            this.completeRequest(incoming, conn, state);
        }
    }

    @Override
    public void inputReady(NHttpServerConnection conn, ContentDecoder decoder) throws IOException, HttpException {
        State state = this.getState(conn);
        Asserts.notNull(state, "Connection state");
        Asserts.check(state.getRequestState() == MessageState.BODY_STREAM, "Unexpected request state %s", (Object)state.getRequestState());
        Incoming incoming = state.getIncoming();
        Asserts.notNull(incoming, "Incoming request");
        HttpAsyncRequestConsumer<Object> consumer = incoming.getConsumer();
        consumer.consumeContent(decoder, conn);
        if (decoder.isCompleted()) {
            this.completeRequest(incoming, conn, state);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public void responseReady(NHttpServerConnection conn) throws IOException, HttpException {
        Outgoing outgoing;
        Object pipelineEntry;
        State state = this.getState(conn);
        Asserts.notNull(state, "Connection state");
        Asserts.check(state.getResponseState() == MessageState.READY || state.getResponseState() == MessageState.INIT, "Unexpected response state %s", (Object)state.getResponseState());
        if (state.getRequestState() == MessageState.ACK_EXPECTED) {
            Outgoing outgoing2;
            State state2 = state;
            synchronized (state2) {
                outgoing2 = state.getOutgoing();
                if (outgoing2 == null) {
                    conn.suspendOutput();
                    return;
                }
            }
            HttpResponse response = outgoing2.getResponse();
            int status = response.getStatusLine().getStatusCode();
            if (status == 100) {
                HttpContext context = outgoing2.getContext();
                HttpAsyncResponseProducer responseProducer = outgoing2.getProducer();
                try {
                    response.setEntity(null);
                    conn.requestInput();
                    state.setRequestState(MessageState.BODY_STREAM);
                    state.setOutgoing(null);
                    conn.submitResponse(response);
                    responseProducer.responseCompleted(context);
                    return;
                }
                finally {
                    responseProducer.close();
                }
            } else {
                if (status < 400) throw new HttpException("Invalid response: " + response.getStatusLine());
                conn.resetInput();
                state.setRequestState(MessageState.READY);
                this.commitFinalResponse(conn, state);
            }
            return;
        }
        if (state.getResponseState() == MessageState.READY) {
            Queue<PipelineEntry> pipeline = state.getPipeline();
            pipelineEntry = pipeline.poll();
            if (pipelineEntry == null) {
                conn.suspendOutput();
                return;
            }
            state.setResponseState(MessageState.INIT);
            Object result = ((PipelineEntry)pipelineEntry).getResult();
            HttpRequest request = ((PipelineEntry)pipelineEntry).getRequest();
            HttpContext context = ((PipelineEntry)pipelineEntry).getContext();
            HttpResponse response = this.createHttpResponse(200, context);
            HttpAsyncExchangeImpl httpExchange = new HttpAsyncExchangeImpl(request, response, state, conn, context);
            if (result != null) {
                HttpAsyncRequestHandler<Object> handler = ((PipelineEntry)pipelineEntry).getHandler();
                conn.suspendOutput();
                try {
                    handler.handle(result, httpExchange, context);
                }
                catch (RuntimeException ex) {
                    throw ex;
                }
                catch (Exception ex) {
                    if (!httpExchange.isCompleted()) {
                        httpExchange.submitResponse(this.handleException(ex, context));
                        return;
                    } else {
                        this.log(ex);
                        conn.close();
                    }
                    return;
                }
            } else {
                Exception exception = ((PipelineEntry)pipelineEntry).getException();
                HttpAsyncResponseProducer responseProducer = this.handleException(exception != null ? exception : new HttpException("Internal error processing request"), context);
                httpExchange.submitResponse(responseProducer);
            }
        }
        if (state.getResponseState() != MessageState.INIT) return;
        pipelineEntry = state;
        synchronized (pipelineEntry) {
            outgoing = state.getOutgoing();
            if (outgoing == null) {
                conn.suspendOutput();
                return;
            }
        }
        HttpResponse response = outgoing.getResponse();
        int status = response.getStatusLine().getStatusCode();
        if (status < 200) throw new HttpException("Invalid response: " + response.getStatusLine());
        this.commitFinalResponse(conn, state);
    }

    @Override
    public void outputReady(NHttpServerConnection conn, ContentEncoder encoder) throws HttpException, IOException {
        State state = this.getState(conn);
        Asserts.notNull(state, "Connection state");
        Asserts.check(state.getResponseState() == MessageState.BODY_STREAM, "Unexpected response state %s", (Object)state.getResponseState());
        Outgoing outgoing = state.getOutgoing();
        Asserts.notNull(outgoing, "Outgoing response");
        HttpAsyncResponseProducer responseProducer = outgoing.getProducer();
        responseProducer.produceContent(encoder, conn);
        if (encoder.isCompleted()) {
            this.completeResponse(outgoing, conn, state);
        }
    }

    @Override
    public void endOfInput(NHttpServerConnection conn) throws IOException {
        if (conn.getSocketTimeout() <= 0) {
            conn.setSocketTimeout(1000);
        }
        conn.close();
    }

    @Override
    public void timeout(NHttpServerConnection conn) throws IOException {
        State state = this.getState(conn);
        if (state != null) {
            this.closeHandlers(state, new SocketTimeoutException(String.format("%,d milliseconds timeout on connection %s", conn.getSocketTimeout(), conn)));
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

    private State getState(NHttpConnection conn) {
        return (State)conn.getContext().getAttribute(HTTP_EXCHANGE_STATE);
    }

    protected void log(Exception ex) {
        this.exceptionLogger.log(ex);
    }

    private void shutdownConnection(NHttpConnection conn) {
        try {
            conn.shutdown();
        }
        catch (IOException ex) {
            this.log(ex);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void closeHandlers(State state, Exception ex) {
        HttpAsyncResponseProducer producer;
        HttpAsyncRequestConsumer<Object> consumer;
        HttpAsyncRequestConsumer<Object> httpAsyncRequestConsumer = consumer = state.getIncoming() != null ? state.getIncoming().getConsumer() : null;
        if (consumer != null) {
            try {
                consumer.failed(ex);
            }
            finally {
                try {
                    consumer.close();
                }
                catch (IOException ioex) {
                    this.log(ioex);
                }
            }
        }
        HttpAsyncResponseProducer httpAsyncResponseProducer = producer = state.getOutgoing() != null ? state.getOutgoing().getProducer() : null;
        if (producer != null) {
            try {
                producer.failed(ex);
            }
            finally {
                try {
                    producer.close();
                }
                catch (IOException ioex) {
                    this.log(ioex);
                }
            }
        }
    }

    private void closeHandlers(State state) {
        HttpAsyncResponseProducer producer;
        HttpAsyncRequestConsumer<Object> consumer;
        HttpAsyncRequestConsumer<Object> httpAsyncRequestConsumer = consumer = state.getIncoming() != null ? state.getIncoming().getConsumer() : null;
        if (consumer != null) {
            try {
                consumer.close();
            }
            catch (IOException ioex) {
                this.log(ioex);
            }
        }
        HttpAsyncResponseProducer httpAsyncResponseProducer = producer = state.getOutgoing() != null ? state.getOutgoing().getProducer() : null;
        if (producer != null) {
            try {
                producer.close();
            }
            catch (IOException ioex) {
                this.log(ioex);
            }
        }
    }

    protected HttpAsyncResponseProducer handleException(Exception ex, HttpContext context) {
        String message = ex.getMessage();
        if (message == null) {
            message = ex.toString();
        }
        HttpResponse response = this.createHttpResponse(this.toStatusCode(ex, context), context);
        return new ErrorResponseProducer(response, new NStringEntity(message, ContentType.DEFAULT_TEXT), false);
    }

    protected int toStatusCode(Exception ex, HttpContext context) {
        int code = ex instanceof MethodNotSupportedException ? 501 : (ex instanceof UnsupportedHttpVersionException ? 505 : (ex instanceof ProtocolException ? 400 : 500));
        return code;
    }

    protected void handleAlreadySubmittedResponse(Cancellable cancellable, HttpContext context) {
        throw new IllegalStateException("Response already submitted");
    }

    protected void handleAlreadySubmittedResponse(HttpAsyncResponseProducer responseProducer, HttpContext context) {
        throw new IllegalStateException("Response already submitted");
    }

    private boolean canResponseHaveBody(HttpRequest request, HttpResponse response) {
        if (request != null && "HEAD".equalsIgnoreCase(request.getRequestLine().getMethod())) {
            return false;
        }
        int status = response.getStatusLine().getStatusCode();
        return status >= 200 && status != 204 && status != 304 && status != 205;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void completeRequest(Incoming incoming, NHttpServerConnection conn, State state) throws IOException {
        PipelineEntry pipelineEntry;
        state.setRequestState(MessageState.READY);
        state.setIncoming(null);
        HttpAsyncRequestConsumer<Object> consumer = incoming.getConsumer();
        try {
            HttpContext context = incoming.getContext();
            consumer.requestCompleted(context);
            pipelineEntry = new PipelineEntry(incoming.getRequest(), consumer.getResult(), consumer.getException(), incoming.getHandler(), context);
        }
        finally {
            consumer.close();
        }
        Queue<PipelineEntry> pipeline = state.getPipeline();
        pipeline.add(pipelineEntry);
        if (state.getResponseState() == MessageState.READY) {
            conn.requestOutput();
        }
    }

    private void commitFinalResponse(NHttpServerConnection conn, State state) throws IOException, HttpException {
        Outgoing outgoing = state.getOutgoing();
        Asserts.notNull(outgoing, "Outgoing response");
        HttpRequest request = outgoing.getRequest();
        HttpResponse response = outgoing.getResponse();
        HttpContext context = outgoing.getContext();
        context.setAttribute("http.response", response);
        this.httpProcessor.process(response, context);
        HttpEntity entity = response.getEntity();
        if (entity != null && !this.canResponseHaveBody(request, response)) {
            response.setEntity(null);
            entity = null;
        }
        conn.submitResponse(response);
        if (entity == null) {
            this.completeResponse(outgoing, conn, state);
        } else {
            state.setResponseState(MessageState.BODY_STREAM);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void completeResponse(Outgoing outgoing, NHttpServerConnection conn, State state) throws IOException {
        HttpContext context = outgoing.getContext();
        HttpResponse response = outgoing.getResponse();
        HttpAsyncResponseProducer responseProducer = outgoing.getProducer();
        try {
            responseProducer.responseCompleted(context);
            state.setOutgoing(null);
            state.setCancellable(null);
            state.setResponseState(MessageState.READY);
        }
        finally {
            responseProducer.close();
        }
        if (!this.connectionStrategy.keepAlive(response, context)) {
            conn.close();
        } else {
            conn.requestInput();
        }
    }

    private HttpAsyncRequestHandler<Object> getRequestHandler(HttpRequest request) {
        HttpAsyncRequestHandler<Object> handler = null;
        if (this.handlerMapper != null) {
            handler = this.handlerMapper.lookup(request);
        }
        if (handler == null) {
            handler = NullRequestHandler.INSTANCE;
        }
        return handler;
    }

    public HttpResponseFactory getResponseFactory() {
        return this.responseFactory;
    }

    public HttpProcessor getHttpProcessor() {
        return this.httpProcessor;
    }

    public ConnectionReuseStrategy getConnectionStrategy() {
        return this.connectionStrategy;
    }

    public HttpAsyncRequestHandlerMapper getHandlerMapper() {
        return this.handlerMapper;
    }

    public HttpAsyncExpectationVerifier getExpectationVerifier() {
        return this.expectationVerifier;
    }

    public ExceptionLogger getExceptionLogger() {
        return this.exceptionLogger;
    }

    @Deprecated
    private static class HttpAsyncRequestHandlerResolverAdapter
    implements HttpAsyncRequestHandlerMapper {
        private final HttpAsyncRequestHandlerResolver resolver;

        public HttpAsyncRequestHandlerResolverAdapter(HttpAsyncRequestHandlerResolver resolver) {
            this.resolver = resolver;
        }

        @Override
        public HttpAsyncRequestHandler<?> lookup(HttpRequest request) {
            return this.resolver.lookup(request.getRequestLine().getUri());
        }
    }

    class HttpAsyncExchangeImpl
    implements HttpAsyncExchange {
        private final AtomicBoolean completed = new AtomicBoolean();
        private final HttpRequest request;
        private final HttpResponse response;
        private final State state;
        private final NHttpServerConnection conn;
        private final HttpContext context;

        public HttpAsyncExchangeImpl(HttpRequest request, HttpResponse response, State state, NHttpServerConnection conn, HttpContext context) {
            this.request = request;
            this.response = response;
            this.state = state;
            this.conn = conn;
            this.context = context;
        }

        @Override
        public HttpRequest getRequest() {
            return this.request;
        }

        @Override
        public HttpResponse getResponse() {
            return this.response;
        }

        @Override
        public void setCallback(Cancellable cancellable) {
            if (this.completed.get()) {
                HttpAsyncService.this.handleAlreadySubmittedResponse(cancellable, this.context);
            } else if (this.state.isTerminated() && cancellable != null) {
                cancellable.cancel();
            } else {
                this.state.setCancellable(cancellable);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void submitResponse(HttpAsyncResponseProducer responseProducer) {
            Args.notNull(responseProducer, "Response producer");
            if (this.completed.getAndSet(true)) {
                HttpAsyncService.this.handleAlreadySubmittedResponse(responseProducer, this.context);
            } else {
                if (!this.state.isTerminated()) {
                    HttpResponse response = responseProducer.generateResponse();
                    Outgoing outgoing = new Outgoing(this.request, response, responseProducer, this.context);
                    State state = this.state;
                    synchronized (state) {
                        this.state.setOutgoing(outgoing);
                        this.state.setCancellable(null);
                        this.conn.requestOutput();
                    }
                }
                try {
                    responseProducer.close();
                }
                catch (IOException ex) {
                    HttpAsyncService.this.log(ex);
                }
            }
        }

        @Override
        public void submitResponse() {
            this.submitResponse(new BasicAsyncResponseProducer(this.response));
        }

        @Override
        public boolean isCompleted() {
            return this.completed.get();
        }

        @Override
        public void setTimeout(int timeout) {
            this.conn.setSocketTimeout(timeout);
        }

        @Override
        public int getTimeout() {
            return this.conn.getSocketTimeout();
        }
    }

    static class State {
        private final Queue<PipelineEntry> pipeline = new ConcurrentLinkedQueue<PipelineEntry>();
        private volatile boolean terminated;
        private volatile MessageState requestState = MessageState.READY;
        private volatile MessageState responseState = MessageState.READY;
        private volatile Incoming incoming;
        private volatile Outgoing outgoing;
        private volatile Cancellable cancellable;

        State() {
        }

        public boolean isTerminated() {
            return this.terminated;
        }

        public void setTerminated() {
            this.terminated = true;
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

        public Incoming getIncoming() {
            return this.incoming;
        }

        public void setIncoming(Incoming incoming) {
            this.incoming = incoming;
        }

        public Outgoing getOutgoing() {
            return this.outgoing;
        }

        public void setOutgoing(Outgoing outgoing) {
            this.outgoing = outgoing;
        }

        public Cancellable getCancellable() {
            return this.cancellable;
        }

        public void setCancellable(Cancellable cancellable) {
            this.cancellable = cancellable;
        }

        public Queue<PipelineEntry> getPipeline() {
            return this.pipeline;
        }

        public String toString() {
            StringBuilder buf = new StringBuilder();
            buf.append("[incoming ");
            buf.append((Object)this.requestState);
            if (this.incoming != null) {
                buf.append(" ");
                buf.append(this.incoming.getRequest().getRequestLine());
            }
            buf.append("; outgoing ");
            buf.append((Object)this.responseState);
            if (this.outgoing != null) {
                buf.append(" ");
                buf.append(this.outgoing.getResponse().getStatusLine());
            }
            buf.append("]");
            return buf.toString();
        }
    }

    static class PipelineEntry {
        private final HttpRequest request;
        private final Object result;
        private final Exception exception;
        private final HttpAsyncRequestHandler<Object> handler;
        private final HttpContext context;

        PipelineEntry(HttpRequest request, Object result, Exception exception, HttpAsyncRequestHandler<Object> handler, HttpContext context) {
            this.request = request;
            this.result = result;
            this.exception = exception;
            this.handler = handler;
            this.context = context;
        }

        public HttpRequest getRequest() {
            return this.request;
        }

        public Object getResult() {
            return this.result;
        }

        public Exception getException() {
            return this.exception;
        }

        public HttpAsyncRequestHandler<Object> getHandler() {
            return this.handler;
        }

        public HttpContext getContext() {
            return this.context;
        }
    }

    static class Outgoing {
        private final HttpRequest request;
        private final HttpResponse response;
        private final HttpAsyncResponseProducer producer;
        private final HttpContext context;

        Outgoing(HttpRequest request, HttpResponse response, HttpAsyncResponseProducer producer, HttpContext context) {
            this.request = request;
            this.response = response;
            this.producer = producer;
            this.context = context;
        }

        public HttpRequest getRequest() {
            return this.request;
        }

        public HttpResponse getResponse() {
            return this.response;
        }

        public HttpAsyncResponseProducer getProducer() {
            return this.producer;
        }

        public HttpContext getContext() {
            return this.context;
        }
    }

    static class Incoming {
        private final HttpRequest request;
        private final HttpAsyncRequestHandler<Object> handler;
        private final HttpAsyncRequestConsumer<Object> consumer;
        private final HttpContext context;

        Incoming(HttpRequest request, HttpAsyncRequestHandler<Object> handler, HttpAsyncRequestConsumer<Object> consumer, HttpContext context) {
            this.request = request;
            this.handler = handler;
            this.consumer = consumer;
            this.context = context;
        }

        public HttpRequest getRequest() {
            return this.request;
        }

        public HttpAsyncRequestHandler<Object> getHandler() {
            return this.handler;
        }

        public HttpAsyncRequestConsumer<Object> getConsumer() {
            return this.consumer;
        }

        public HttpContext getContext() {
            return this.context;
        }
    }
}

