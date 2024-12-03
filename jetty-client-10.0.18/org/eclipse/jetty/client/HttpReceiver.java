/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.http.HttpField
 *  org.eclipse.jetty.http.HttpFields
 *  org.eclipse.jetty.http.HttpHeader
 *  org.eclipse.jetty.http.HttpMethod
 *  org.eclipse.jetty.http.HttpStatus
 *  org.eclipse.jetty.http.QuotedCSV
 *  org.eclipse.jetty.util.BufferUtil
 *  org.eclipse.jetty.util.Callback
 *  org.eclipse.jetty.util.MathUtils
 *  org.eclipse.jetty.util.component.Destroyable
 *  org.eclipse.jetty.util.thread.AutoLock
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.client;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.LongConsumer;
import java.util.function.LongUnaryOperator;
import org.eclipse.jetty.client.ContentDecoder;
import org.eclipse.jetty.client.HttpChannel;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.HttpConversation;
import org.eclipse.jetty.client.HttpDestination;
import org.eclipse.jetty.client.HttpExchange;
import org.eclipse.jetty.client.HttpResponse;
import org.eclipse.jetty.client.ProtocolHandler;
import org.eclipse.jetty.client.ResponseNotifier;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.client.api.Result;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.http.QuotedCSV;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.MathUtils;
import org.eclipse.jetty.util.component.Destroyable;
import org.eclipse.jetty.util.thread.AutoLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class HttpReceiver {
    private static final Logger LOG = LoggerFactory.getLogger(HttpReceiver.class);
    private final AutoLock lock = new AutoLock();
    private final AtomicReference<ResponseState> responseState = new AtomicReference<ResponseState>(ResponseState.IDLE);
    private final ContentListeners contentListeners = new ContentListeners();
    private final HttpChannel channel;
    private Decoder decoder;
    private Throwable failure;
    private long demand;
    private boolean stalled;

    protected HttpReceiver(HttpChannel channel) {
        this.channel = channel;
    }

    protected HttpChannel getHttpChannel() {
        return this.channel;
    }

    void demand(long n) {
        if (n <= 0L) {
            throw new IllegalArgumentException("Invalid demand " + n);
        }
        boolean resume = false;
        try (AutoLock l = this.lock.lock();){
            this.demand = MathUtils.cappedAdd((long)this.demand, (long)n);
            if (this.stalled) {
                this.stalled = false;
                resume = true;
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("Response demand={}/{}, resume={}", new Object[]{n, this.demand, resume});
            }
        }
        if (resume) {
            if (this.decoder != null) {
                this.decoder.resume();
            } else {
                this.receive();
            }
        }
    }

    protected long demand() {
        return this.demand(LongUnaryOperator.identity());
    }

    private long demand(LongUnaryOperator operator) {
        try (AutoLock l = this.lock.lock();){
            long l2 = this.demand = operator.applyAsLong(this.demand);
            return l2;
        }
    }

    protected boolean hasDemandOrStall() {
        try (AutoLock l = this.lock.lock();){
            this.stalled = this.demand <= 0L;
            boolean bl = !this.stalled;
            return bl;
        }
    }

    protected HttpExchange getHttpExchange() {
        return this.channel.getHttpExchange();
    }

    protected HttpDestination getHttpDestination() {
        return this.channel.getHttpDestination();
    }

    public boolean isFailed() {
        return this.responseState.get() == ResponseState.FAILURE;
    }

    protected void receive() {
    }

    protected boolean responseBegin(HttpExchange exchange) {
        if (!this.updateResponseState(ResponseState.IDLE, ResponseState.TRANSIENT)) {
            return false;
        }
        HttpConversation conversation = exchange.getConversation();
        HttpResponse response = exchange.getResponse();
        HttpDestination destination = this.getHttpDestination();
        HttpClient client = destination.getHttpClient();
        ProtocolHandler protocolHandler = client.findProtocolHandler(exchange.getRequest(), response);
        Response.Listener handlerListener = null;
        if (protocolHandler != null) {
            handlerListener = protocolHandler.getResponseListener();
            if (LOG.isDebugEnabled()) {
                LOG.debug("Response {} found protocol handler {}", (Object)response, (Object)protocolHandler);
            }
        }
        exchange.getConversation().updateResponseListeners(handlerListener);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Response begin {}", (Object)response);
        }
        ResponseNotifier notifier = destination.getResponseNotifier();
        notifier.notifyBegin(conversation.getResponseListeners(), (Response)response);
        if (this.updateResponseState(ResponseState.TRANSIENT, ResponseState.BEGIN)) {
            return true;
        }
        this.dispose();
        this.terminateResponse(exchange);
        return false;
    }

    protected boolean responseHeader(HttpExchange exchange, HttpField field) {
        if (!this.updateResponseState(ResponseState.BEGIN, ResponseState.HEADER, ResponseState.TRANSIENT)) {
            return false;
        }
        HttpResponse response = exchange.getResponse();
        ResponseNotifier notifier = this.getHttpDestination().getResponseNotifier();
        boolean process = notifier.notifyHeader(exchange.getConversation().getResponseListeners(), (Response)response, field);
        if (process) {
            response.addHeader(field);
            HttpHeader fieldHeader = field.getHeader();
            if (fieldHeader != null) {
                switch (fieldHeader) {
                    case SET_COOKIE: 
                    case SET_COOKIE2: {
                        URI uri = exchange.getRequest().getURI();
                        if (uri == null) break;
                        this.storeCookie(uri, field);
                        break;
                    }
                }
            }
        }
        if (this.updateResponseState(ResponseState.TRANSIENT, ResponseState.HEADER)) {
            return true;
        }
        this.dispose();
        this.terminateResponse(exchange);
        return false;
    }

    protected void storeCookie(URI uri, HttpField field) {
        block3: {
            try {
                String value = field.getValue();
                if (value != null) {
                    HashMap<String, List<String>> header = new HashMap<String, List<String>>(1);
                    header.put(field.getHeader().asString(), Collections.singletonList(value));
                    this.getHttpDestination().getHttpClient().getCookieManager().put(uri, header);
                }
            }
            catch (IOException x) {
                if (!LOG.isDebugEnabled()) break block3;
                LOG.debug("Unable to store cookies {} from {}", new Object[]{field, uri, x});
            }
        }
    }

    protected boolean responseHeaders(HttpExchange exchange) {
        String contentEncoding;
        if (!this.updateResponseState(ResponseState.BEGIN, ResponseState.HEADER, ResponseState.TRANSIENT)) {
            return false;
        }
        HttpResponse response = exchange.getResponse();
        HttpFields responseHeaders = response.getHeaders();
        if (LOG.isDebugEnabled()) {
            LOG.debug("Response headers {}{}{}", new Object[]{response, System.lineSeparator(), responseHeaders.toString().trim()});
        }
        if (!HttpMethod.HEAD.is(exchange.getRequest().getMethod()) && (contentEncoding = responseHeaders.get(HttpHeader.CONTENT_ENCODING)) != null) {
            int comma = contentEncoding.indexOf(",");
            if (comma > 0) {
                QuotedCSV parser = new QuotedCSV(false, new String[0]);
                parser.addValue(contentEncoding);
                List values = parser.getValues();
                contentEncoding = (String)values.get(values.size() - 1);
            }
            for (ContentDecoder.Factory factory : this.getHttpDestination().getHttpClient().getContentDecoderFactories()) {
                if (!factory.getEncoding().equalsIgnoreCase(contentEncoding)) continue;
                this.decoder = new Decoder(exchange, factory.newContentDecoder());
                break;
            }
        }
        ResponseNotifier notifier = this.getHttpDestination().getResponseNotifier();
        List<Response.ResponseListener> responseListeners = exchange.getConversation().getResponseListeners();
        notifier.notifyHeaders(responseListeners, (Response)response);
        this.contentListeners.reset(responseListeners);
        this.contentListeners.notifyBeforeContent(response);
        if (this.updateResponseState(ResponseState.TRANSIENT, ResponseState.HEADERS)) {
            boolean hasDemand = this.hasDemandOrStall();
            if (LOG.isDebugEnabled()) {
                LOG.debug("Response headers hasDemand={} {}", (Object)hasDemand, (Object)response);
            }
            return hasDemand;
        }
        this.dispose();
        this.terminateResponse(exchange);
        return false;
    }

    protected boolean responseContent(HttpExchange exchange, ByteBuffer buffer, Callback callback) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Response content {}{}{}", new Object[]{exchange.getResponse(), System.lineSeparator(), BufferUtil.toDetailString((ByteBuffer)buffer)});
        }
        if (this.demand() <= 0L) {
            callback.failed((Throwable)new IllegalStateException("No demand for response content"));
            return false;
        }
        if (this.decoder == null) {
            return this.plainResponseContent(exchange, buffer, callback);
        }
        return this.decodeResponseContent(buffer, callback);
    }

    private boolean plainResponseContent(HttpExchange exchange, ByteBuffer buffer, Callback callback) {
        if (!this.updateResponseState(ResponseState.HEADERS, ResponseState.CONTENT, ResponseState.TRANSIENT)) {
            callback.failed((Throwable)new IllegalStateException("Invalid response state " + this.responseState));
            return false;
        }
        HttpResponse response = exchange.getResponse();
        if (this.contentListeners.isEmpty()) {
            callback.succeeded();
        } else {
            this.contentListeners.notifyContent(response, buffer, callback);
        }
        if (this.updateResponseState(ResponseState.TRANSIENT, ResponseState.CONTENT)) {
            boolean hasDemand = this.hasDemandOrStall();
            if (LOG.isDebugEnabled()) {
                LOG.debug("Response content {}, hasDemand={}", (Object)response, (Object)hasDemand);
            }
            return hasDemand;
        }
        this.dispose();
        this.terminateResponse(exchange);
        return false;
    }

    private boolean decodeResponseContent(ByteBuffer buffer, Callback callback) {
        return this.decoder.decode(buffer, callback);
    }

    protected boolean responseSuccess(HttpExchange exchange) {
        if (!exchange.responseComplete(null)) {
            return false;
        }
        this.responseState.set(ResponseState.IDLE);
        this.reset();
        HttpResponse response = exchange.getResponse();
        if (LOG.isDebugEnabled()) {
            LOG.debug("Response success {}", (Object)response);
        }
        List<Response.ResponseListener> listeners = exchange.getConversation().getResponseListeners();
        ResponseNotifier notifier = this.getHttpDestination().getResponseNotifier();
        notifier.notifySuccess(listeners, (Response)response);
        if (HttpStatus.isInterim((int)exchange.getResponse().getStatus())) {
            return true;
        }
        this.terminateResponse(exchange);
        return true;
    }

    protected boolean responseFailure(Throwable failure) {
        HttpExchange exchange = this.getHttpExchange();
        if (exchange == null) {
            return false;
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Response failure {}", (Object)exchange.getResponse(), (Object)failure);
        }
        if (exchange.responseComplete(failure)) {
            return this.abort(exchange, failure);
        }
        return false;
    }

    private void terminateResponse(HttpExchange exchange) {
        Result result = exchange.terminateResponse();
        this.terminateResponse(exchange, result);
    }

    private void terminateResponse(HttpExchange exchange, Result result) {
        HttpResponse response = exchange.getResponse();
        if (LOG.isDebugEnabled()) {
            LOG.debug("Response complete {}, result: {}", (Object)response, (Object)result);
        }
        if (result != null) {
            result = this.channel.exchangeTerminating(exchange, result);
            boolean ordered = this.getHttpDestination().getHttpClient().isStrictEventOrdering();
            if (!ordered) {
                this.channel.exchangeTerminated(exchange, result);
            }
            List<Response.ResponseListener> listeners = exchange.getConversation().getResponseListeners();
            if (LOG.isDebugEnabled()) {
                LOG.debug("Request/Response {}: {}, notifying {}", new Object[]{this.failure == null ? "succeeded" : "failed", result, listeners});
            }
            ResponseNotifier notifier = this.getHttpDestination().getResponseNotifier();
            notifier.notifyComplete(listeners, result);
            if (ordered) {
                this.channel.exchangeTerminated(exchange, result);
            }
        }
    }

    protected void reset() {
        this.cleanup();
    }

    protected void dispose() {
        assert (this.responseState.get() != ResponseState.TRANSIENT);
        this.cleanup();
    }

    private void cleanup() {
        this.contentListeners.clear();
        if (this.decoder != null) {
            this.decoder.destroy();
        }
        this.decoder = null;
        this.demand = 0L;
        this.stalled = false;
    }

    public boolean abort(HttpExchange exchange, Throwable failure) {
        ResponseState current;
        do {
            if ((current = this.responseState.get()) != ResponseState.FAILURE) continue;
            return false;
        } while (!this.updateResponseState(current, ResponseState.FAILURE));
        boolean terminate = current != ResponseState.TRANSIENT;
        this.failure = failure;
        if (terminate) {
            this.dispose();
        }
        HttpResponse response = exchange.getResponse();
        if (LOG.isDebugEnabled()) {
            LOG.debug("Response abort {} {} on {}: {}", new Object[]{response, exchange, this.getHttpChannel(), failure});
        }
        List<Response.ResponseListener> listeners = exchange.getConversation().getResponseListeners();
        ResponseNotifier notifier = this.getHttpDestination().getResponseNotifier();
        notifier.notifyFailure(listeners, (Response)response, failure);
        if (terminate) {
            this.terminateResponse(exchange);
            return true;
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Concurrent failure: response termination skipped, performed by helpers");
        }
        return false;
    }

    private boolean updateResponseState(ResponseState from1, ResponseState from2, ResponseState to) {
        ResponseState current;
        while ((current = this.responseState.get()) == from1 || current == from2) {
            if (!this.updateResponseState(current, to)) continue;
            return true;
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("State update failed: [{},{}] -> {}: {}", new Object[]{from1, from2, to, current});
        }
        return false;
    }

    private boolean updateResponseState(ResponseState from, ResponseState to) {
        ResponseState current;
        while ((current = this.responseState.get()) == from) {
            if (!this.responseState.compareAndSet(current, to)) continue;
            return true;
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("State update failed: {} -> {}: {}", new Object[]{from, to, current});
        }
        return false;
    }

    public String toString() {
        return String.format("%s@%x(rsp=%s,failure=%s)", this.getClass().getSimpleName(), this.hashCode(), this.responseState, this.failure);
    }

    private static enum ResponseState {
        TRANSIENT,
        IDLE,
        BEGIN,
        HEADER,
        HEADERS,
        CONTENT,
        FAILURE;

    }

    private class ContentListeners {
        private final Map<Object, Long> demands = new ConcurrentHashMap<Object, Long>();
        private final LongConsumer demand = HttpReceiver.this::demand;
        private final List<Response.DemandedContentListener> listeners = new ArrayList<Response.DemandedContentListener>(2);

        private ContentListeners() {
        }

        private void clear() {
            this.demands.clear();
            this.listeners.clear();
        }

        private void reset(List<Response.ResponseListener> responseListeners) {
            this.clear();
            for (Response.ResponseListener listener : responseListeners) {
                if (!(listener instanceof Response.DemandedContentListener)) continue;
                this.listeners.add((Response.DemandedContentListener)listener);
            }
        }

        private boolean isEmpty() {
            return this.listeners.isEmpty();
        }

        private void notifyBeforeContent(HttpResponse response) {
            if (this.isEmpty()) {
                this.demand.accept(1L);
            } else {
                ResponseNotifier notifier = HttpReceiver.this.getHttpDestination().getResponseNotifier();
                notifier.notifyBeforeContent(response, this::demand, this.listeners);
            }
        }

        private void notifyContent(HttpResponse response, ByteBuffer buffer, Callback callback) {
            HttpReceiver.this.demand(d -> d - 1L);
            ResponseNotifier notifier = HttpReceiver.this.getHttpDestination().getResponseNotifier();
            notifier.notifyContent(response, this::demand, buffer, callback, this.listeners);
        }

        private void demand(Object context, long value) {
            if (this.listeners.size() > 1) {
                this.accept(context, value);
            } else {
                this.demand.accept(value);
            }
        }

        private void accept(Object context, long value) {
            this.demands.merge(context, value, MathUtils::cappedAdd);
            if (this.demands.size() == this.listeners.size()) {
                long minDemand = Long.MAX_VALUE;
                for (Long demand : this.demands.values()) {
                    if (demand >= minDemand) continue;
                    minDemand = demand;
                }
                if (minDemand > 0L) {
                    Iterator<Map.Entry<Object, Long>> iterator = this.demands.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry<Object, Long> entry = iterator.next();
                        long newValue = entry.getValue() - minDemand;
                        if (newValue == 0L) {
                            iterator.remove();
                            continue;
                        }
                        entry.setValue(newValue);
                    }
                    this.demand.accept(minDemand);
                }
            }
        }
    }

    private class Decoder
    implements Destroyable {
        private final HttpExchange exchange;
        private final ContentDecoder decoder;
        private ByteBuffer encoded;
        private Callback callback;

        private Decoder(HttpExchange exchange, ContentDecoder decoder) {
            this.exchange = exchange;
            this.decoder = Objects.requireNonNull(decoder);
            decoder.beforeDecoding(exchange);
        }

        private boolean decode(ByteBuffer encoded, Callback callback) {
            boolean needInput;
            this.encoded = encoded;
            this.callback = callback;
            HttpResponse response = this.exchange.getResponse();
            if (LOG.isDebugEnabled()) {
                LOG.debug("Response content decoding {} with {}{}{}", new Object[]{response, this.decoder, System.lineSeparator(), BufferUtil.toDetailString((ByteBuffer)encoded)});
            }
            if (!(needInput = this.decode())) {
                return false;
            }
            boolean hasDemand = HttpReceiver.this.hasDemandOrStall();
            if (LOG.isDebugEnabled()) {
                LOG.debug("Response content decoded, hasDemand={} {}", (Object)hasDemand, (Object)response);
            }
            return hasDemand;
        }

        private boolean decode() {
            block4: {
                boolean hasDemand;
                do {
                    if (!HttpReceiver.this.updateResponseState(ResponseState.HEADERS, ResponseState.CONTENT, ResponseState.TRANSIENT)) {
                        this.callback.failed((Throwable)new IllegalStateException("Invalid response state " + HttpReceiver.this.responseState));
                        return false;
                    }
                    DecodeResult result = this.decodeChunk();
                    if (!HttpReceiver.this.updateResponseState(ResponseState.TRANSIENT, ResponseState.CONTENT)) break block4;
                    if (result == DecodeResult.NEED_INPUT) {
                        return true;
                    }
                    if (result == DecodeResult.ABORT) {
                        return false;
                    }
                    hasDemand = HttpReceiver.this.hasDemandOrStall();
                    if (!LOG.isDebugEnabled()) continue;
                    LOG.debug("Response content decoded chunk, hasDemand={} {}", (Object)hasDemand, (Object)this.exchange.getResponse());
                } while (hasDemand);
                return false;
            }
            HttpReceiver.this.dispose();
            HttpReceiver.this.terminateResponse(this.exchange);
            return false;
        }

        private DecodeResult decodeChunk() {
            try {
                ByteBuffer buffer;
                while (!(buffer = this.decoder.decode(this.encoded)).hasRemaining()) {
                    if (this.encoded.hasRemaining()) continue;
                    this.callback.succeeded();
                    this.encoded = null;
                    this.callback = null;
                    return DecodeResult.NEED_INPUT;
                }
                ByteBuffer decoded = buffer;
                HttpResponse response = this.exchange.getResponse();
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Response content decoded chunk {}{}{}", new Object[]{response, System.lineSeparator(), BufferUtil.toDetailString((ByteBuffer)decoded)});
                }
                HttpReceiver.this.contentListeners.notifyContent(response, decoded, Callback.from(() -> this.decoder.release(decoded), arg_0 -> ((Callback)this.callback).failed(arg_0)));
                return DecodeResult.DECODE;
            }
            catch (Throwable x) {
                this.callback.failed(x);
                return DecodeResult.ABORT;
            }
        }

        private void resume() {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Response content resume decoding {} with {}", (Object)this.exchange.getResponse(), (Object)this.decoder);
            }
            if (this.callback == null) {
                HttpReceiver.this.receive();
                return;
            }
            boolean needInput = this.decode();
            if (needInput) {
                HttpReceiver.this.receive();
            }
        }

        public void destroy() {
            this.decoder.afterDecoding(this.exchange);
            if (this.decoder instanceof Destroyable) {
                ((Destroyable)this.decoder).destroy();
            }
        }
    }

    private static enum DecodeResult {
        DECODE,
        NEED_INPUT,
        ABORT;

    }
}

