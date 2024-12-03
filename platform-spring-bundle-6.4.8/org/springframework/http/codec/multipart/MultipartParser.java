/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.reactivestreams.Subscription
 *  reactor.core.CoreSubscriber
 *  reactor.core.publisher.BaseSubscriber
 *  reactor.core.publisher.Flux
 *  reactor.core.publisher.FluxSink
 *  reactor.util.context.Context
 *  reactor.util.context.ContextView
 */
package org.springframework.http.codec.multipart;

import java.nio.charset.Charset;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.reactivestreams.Subscription;
import org.springframework.core.codec.DecodingException;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferLimitException;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.codec.multipart.MultipartUtils;
import org.springframework.lang.Nullable;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.util.context.Context;
import reactor.util.context.ContextView;

final class MultipartParser
extends BaseSubscriber<DataBuffer> {
    private static final byte CR = 13;
    private static final byte LF = 10;
    private static final byte[] CR_LF = new byte[]{13, 10};
    private static final byte HYPHEN = 45;
    private static final byte[] TWO_HYPHENS = new byte[]{45, 45};
    private static final String HEADER_ENTRY_SEPARATOR = "\\r\\n";
    private static final Log logger = LogFactory.getLog(MultipartParser.class);
    private final AtomicReference<State> state;
    private final FluxSink<Token> sink;
    private final byte[] boundary;
    private final int maxHeadersSize;
    private final AtomicBoolean requestOutstanding = new AtomicBoolean();
    private final Charset headersCharset;

    private MultipartParser(FluxSink<Token> sink, byte[] boundary, int maxHeadersSize, Charset headersCharset) {
        this.sink = sink;
        this.boundary = boundary;
        this.maxHeadersSize = maxHeadersSize;
        this.headersCharset = headersCharset;
        this.state = new AtomicReference<PreambleState>(new PreambleState());
    }

    public static Flux<Token> parse(Flux<DataBuffer> buffers, byte[] boundary, int maxHeadersSize, Charset headersCharset) {
        return Flux.create(sink -> {
            MultipartParser parser = new MultipartParser((FluxSink<Token>)sink, boundary, maxHeadersSize, headersCharset);
            sink.onCancel(parser::onSinkCancel);
            sink.onRequest(n -> parser.requestBuffer());
            buffers.subscribe((CoreSubscriber)parser);
        });
    }

    public Context currentContext() {
        return Context.of((ContextView)this.sink.contextView());
    }

    protected void hookOnSubscribe(Subscription subscription) {
        this.requestBuffer();
    }

    protected void hookOnNext(DataBuffer value) {
        this.requestOutstanding.set(false);
        this.state.get().onNext(value);
    }

    protected void hookOnComplete() {
        this.state.get().onComplete();
    }

    protected void hookOnError(Throwable throwable) {
        State oldState = this.state.getAndSet(DisposedState.INSTANCE);
        oldState.dispose();
        this.sink.error(throwable);
    }

    private void onSinkCancel() {
        State oldState = this.state.getAndSet(DisposedState.INSTANCE);
        oldState.dispose();
        this.cancel();
    }

    boolean changeState(State oldState, State newState, @Nullable DataBuffer remainder) {
        if (this.state.compareAndSet(oldState, newState)) {
            if (logger.isTraceEnabled()) {
                logger.trace((Object)("Changed state: " + oldState + " -> " + newState));
            }
            oldState.dispose();
            if (remainder != null) {
                if (remainder.readableByteCount() > 0) {
                    newState.onNext(remainder);
                } else {
                    DataBufferUtils.release(remainder);
                    this.requestBuffer();
                }
            }
            return true;
        }
        DataBufferUtils.release(remainder);
        return false;
    }

    void emitHeaders(HttpHeaders headers) {
        if (logger.isTraceEnabled()) {
            logger.trace((Object)("Emitting headers: " + headers));
        }
        this.sink.next((Object)new HeadersToken(headers));
    }

    void emitBody(DataBuffer buffer) {
        if (logger.isTraceEnabled()) {
            logger.trace((Object)("Emitting body: " + buffer));
        }
        this.sink.next((Object)new BodyToken(buffer));
    }

    void emitError(Throwable t) {
        this.cancel();
        this.sink.error(t);
    }

    void emitComplete() {
        this.cancel();
        this.sink.complete();
    }

    private void requestBuffer() {
        if (this.upstream() != null && !this.sink.isCancelled() && this.sink.requestedFromDownstream() > 0L && this.requestOutstanding.compareAndSet(false, true)) {
            this.request(1L);
        }
    }

    private static final class DisposedState
    implements State {
        public static final DisposedState INSTANCE = new DisposedState();

        private DisposedState() {
        }

        @Override
        public void onNext(DataBuffer buf) {
            DataBufferUtils.release(buf);
        }

        @Override
        public void onComplete() {
        }

        public String toString() {
            return "DISPOSED";
        }
    }

    private final class BodyState
    implements State {
        private final DataBufferUtils.Matcher boundary;
        private final int boundaryLength;
        private final Deque<DataBuffer> queue = new ConcurrentLinkedDeque<DataBuffer>();

        public BodyState() {
            byte[] delimiter = MultipartUtils.concat(CR_LF, TWO_HYPHENS, MultipartParser.this.boundary);
            this.boundary = DataBufferUtils.matcher(delimiter);
            this.boundaryLength = delimiter.length;
        }

        @Override
        public void onNext(DataBuffer buffer) {
            int endIdx = this.boundary.match(buffer);
            if (endIdx != -1) {
                int len;
                if (logger.isTraceEnabled()) {
                    logger.trace((Object)("Boundary found @" + endIdx + " in " + buffer));
                }
                if ((len = endIdx - buffer.readPosition() - this.boundaryLength + 1) > 0) {
                    DataBuffer body2 = buffer.retainedSlice(buffer.readPosition(), len);
                    this.enqueue(body2);
                    this.flush();
                } else if (len < 0) {
                    DataBuffer prev;
                    while ((prev = this.queue.pollLast()) != null) {
                        int prevLen = prev.readableByteCount() + len;
                        if (prevLen > 0) {
                            DataBuffer body3 = prev.retainedSlice(prev.readPosition(), prevLen);
                            DataBufferUtils.release(prev);
                            this.enqueue(body3);
                            this.flush();
                            break;
                        }
                        DataBufferUtils.release(prev);
                        len += prev.readableByteCount();
                    }
                } else {
                    this.flush();
                }
                DataBuffer remainder = MultipartUtils.sliceFrom(buffer, endIdx);
                DataBufferUtils.release(buffer);
                MultipartParser.this.changeState(this, new HeadersState(), remainder);
            } else {
                this.enqueue(buffer);
                MultipartParser.this.requestBuffer();
            }
        }

        private void enqueue(DataBuffer buf) {
            this.queue.add(buf);
            int len = 0;
            ArrayDeque<DataBuffer> emit = new ArrayDeque<DataBuffer>();
            Iterator<DataBuffer> iterator = this.queue.descendingIterator();
            while (iterator.hasNext()) {
                DataBuffer previous = iterator.next();
                if (len > this.boundaryLength) {
                    emit.addFirst(previous);
                    iterator.remove();
                }
                len += previous.readableByteCount();
            }
            emit.forEach(MultipartParser.this::emitBody);
        }

        private void flush() {
            this.queue.forEach(MultipartParser.this::emitBody);
            this.queue.clear();
        }

        @Override
        public void onComplete() {
            if (MultipartParser.this.changeState(this, DisposedState.INSTANCE, null)) {
                MultipartParser.this.emitError(new DecodingException("Could not find end of body"));
            }
        }

        @Override
        public void dispose() {
            this.queue.forEach(DataBufferUtils::release);
            this.queue.clear();
        }

        public String toString() {
            return "BODY";
        }
    }

    private final class HeadersState
    implements State {
        private final DataBufferUtils.Matcher endHeaders = DataBufferUtils.matcher(MultipartUtils.concat(MultipartParser.access$500(), MultipartParser.access$500()));
        private final AtomicInteger byteCount = new AtomicInteger();
        private final List<DataBuffer> buffers = new ArrayList<DataBuffer>();

        private HeadersState() {
        }

        @Override
        public void onNext(DataBuffer buf) {
            if (this.isLastBoundary(buf)) {
                if (logger.isTraceEnabled()) {
                    logger.trace((Object)("Last boundary found in " + buf));
                }
                if (MultipartParser.this.changeState(this, DisposedState.INSTANCE, buf)) {
                    MultipartParser.this.emitComplete();
                }
                return;
            }
            int endIdx = this.endHeaders.match(buf);
            if (endIdx != -1) {
                long count;
                if (logger.isTraceEnabled()) {
                    logger.trace((Object)("End of headers found @" + endIdx + " in " + buf));
                }
                if (this.belowMaxHeaderSize(count = (long)this.byteCount.addAndGet(endIdx))) {
                    DataBuffer headerBuf = MultipartUtils.sliceTo(buf, endIdx);
                    this.buffers.add(headerBuf);
                    DataBuffer bodyBuf = MultipartUtils.sliceFrom(buf, endIdx);
                    DataBufferUtils.release(buf);
                    MultipartParser.this.emitHeaders(this.parseHeaders());
                    MultipartParser.this.changeState(this, new BodyState(), bodyBuf);
                }
            } else {
                long count = this.byteCount.addAndGet(buf.readableByteCount());
                if (this.belowMaxHeaderSize(count)) {
                    this.buffers.add(buf);
                    MultipartParser.this.requestBuffer();
                }
            }
        }

        private boolean isLastBoundary(DataBuffer buf) {
            return this.buffers.isEmpty() && buf.readableByteCount() >= 2 && buf.getByte(0) == 45 && buf.getByte(1) == 45 || this.buffers.size() == 1 && this.buffers.get(0).readableByteCount() == 1 && this.buffers.get(0).getByte(0) == 45 && buf.readableByteCount() >= 1 && buf.getByte(0) == 45;
        }

        private boolean belowMaxHeaderSize(long count) {
            if (count <= (long)MultipartParser.this.maxHeadersSize) {
                return true;
            }
            MultipartParser.this.emitError(new DataBufferLimitException("Part headers exceeded the memory usage limit of " + MultipartParser.this.maxHeadersSize + " bytes"));
            return false;
        }

        private HttpHeaders parseHeaders() {
            if (this.buffers.isEmpty()) {
                return HttpHeaders.EMPTY;
            }
            DataBuffer joined = this.buffers.get(0).factory().join(this.buffers);
            this.buffers.clear();
            String string = joined.toString(MultipartParser.this.headersCharset);
            DataBufferUtils.release(joined);
            String[] lines = string.split(MultipartParser.HEADER_ENTRY_SEPARATOR);
            HttpHeaders result = new HttpHeaders();
            for (String line : lines) {
                int idx = line.indexOf(58);
                if (idx == -1) continue;
                String name = line.substring(0, idx);
                String value = line.substring(idx + 1);
                while (value.startsWith(" ")) {
                    value = value.substring(1);
                }
                result.add(name, value);
            }
            return result;
        }

        @Override
        public void onComplete() {
            if (MultipartParser.this.changeState(this, DisposedState.INSTANCE, null)) {
                MultipartParser.this.emitError(new DecodingException("Could not find end of headers"));
            }
        }

        @Override
        public void dispose() {
            this.buffers.forEach(DataBufferUtils::release);
        }

        public String toString() {
            return "HEADERS";
        }
    }

    private final class PreambleState
    implements State {
        private final DataBufferUtils.Matcher firstBoundary;

        public PreambleState() {
            this.firstBoundary = DataBufferUtils.matcher(MultipartUtils.concat(TWO_HYPHENS, MultipartParser.this.boundary));
        }

        @Override
        public void onNext(DataBuffer buf) {
            int endIdx = this.firstBoundary.match(buf);
            if (endIdx != -1) {
                if (logger.isTraceEnabled()) {
                    logger.trace((Object)("First boundary found @" + endIdx + " in " + buf));
                }
                DataBuffer headersBuf = MultipartUtils.sliceFrom(buf, endIdx);
                DataBufferUtils.release(buf);
                MultipartParser.this.changeState(this, new HeadersState(), headersBuf);
            } else {
                DataBufferUtils.release(buf);
                MultipartParser.this.requestBuffer();
            }
        }

        @Override
        public void onComplete() {
            if (MultipartParser.this.changeState(this, DisposedState.INSTANCE, null)) {
                MultipartParser.this.emitError(new DecodingException("Could not find first boundary"));
            }
        }

        public String toString() {
            return "PREAMBLE";
        }
    }

    private static interface State {
        public void onNext(DataBuffer var1);

        public void onComplete();

        default public void dispose() {
        }
    }

    public static final class BodyToken
    extends Token {
        private final DataBuffer buffer;

        public BodyToken(DataBuffer buffer) {
            this.buffer = buffer;
        }

        @Override
        public HttpHeaders headers() {
            throw new IllegalStateException();
        }

        @Override
        public DataBuffer buffer() {
            return this.buffer;
        }
    }

    public static final class HeadersToken
    extends Token {
        private final HttpHeaders headers;

        public HeadersToken(HttpHeaders headers) {
            this.headers = headers;
        }

        @Override
        public HttpHeaders headers() {
            return this.headers;
        }

        @Override
        public DataBuffer buffer() {
            throw new IllegalStateException();
        }
    }

    public static abstract class Token {
        public abstract HttpHeaders headers();

        public abstract DataBuffer buffer();
    }
}

