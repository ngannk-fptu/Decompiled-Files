/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  reactor.core.publisher.Flux
 *  reactor.core.publisher.Mono
 */
package org.springframework.http.server.reactive;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ChannelSendOperator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public abstract class AbstractServerHttpResponse
implements ServerHttpResponse {
    private final Log logger = LogFactory.getLog(this.getClass());
    private final DataBufferFactory dataBufferFactory;
    @Nullable
    private Integer statusCode;
    private final HttpHeaders headers;
    private final MultiValueMap<String, ResponseCookie> cookies;
    private final AtomicReference<State> state = new AtomicReference<State>(State.NEW);
    private final List<Supplier<? extends Mono<Void>>> commitActions = new ArrayList<Supplier<? extends Mono<Void>>>(4);

    public AbstractServerHttpResponse(DataBufferFactory dataBufferFactory) {
        Assert.notNull((Object)dataBufferFactory, "DataBufferFactory must not be null");
        this.dataBufferFactory = dataBufferFactory;
        this.headers = new HttpHeaders();
        this.cookies = new LinkedMultiValueMap<String, ResponseCookie>();
    }

    @Override
    public final DataBufferFactory bufferFactory() {
        return this.dataBufferFactory;
    }

    @Override
    public boolean setStatusCode(@Nullable HttpStatus statusCode) {
        if (this.state.get() == State.COMMITTED) {
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("HTTP response already committed. Status not set to " + (statusCode != null ? statusCode.toString() : "null"));
            }
            return false;
        }
        this.statusCode = statusCode != null ? Integer.valueOf(statusCode.value()) : null;
        return true;
    }

    @Override
    @Nullable
    public HttpStatus getStatusCode() {
        return this.statusCode != null ? HttpStatus.resolve(this.statusCode) : null;
    }

    public void setStatusCodeValue(@Nullable Integer statusCode) {
        this.statusCode = statusCode;
    }

    @Nullable
    public Integer getStatusCodeValue() {
        return this.statusCode;
    }

    @Override
    public HttpHeaders getHeaders() {
        return this.state.get() == State.COMMITTED ? HttpHeaders.readOnlyHttpHeaders(this.headers) : this.headers;
    }

    @Override
    public MultiValueMap<String, ResponseCookie> getCookies() {
        return this.state.get() == State.COMMITTED ? CollectionUtils.unmodifiableMultiValueMap(this.cookies) : this.cookies;
    }

    @Override
    public void addCookie(ResponseCookie cookie) {
        Assert.notNull((Object)cookie, "ResponseCookie must not be null");
        if (this.state.get() == State.COMMITTED) {
            throw new IllegalStateException("Can't add the cookie " + cookie + "because the HTTP response has already been committed");
        }
        this.getCookies().add(cookie.getName(), cookie);
    }

    public abstract <T> T getNativeResponse();

    @Override
    public void beforeCommit(Supplier<? extends Mono<Void>> action) {
        this.commitActions.add(action);
    }

    @Override
    public boolean isCommitted() {
        return this.state.get() != State.NEW;
    }

    @Override
    public final Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
        return new ChannelSendOperator<DataBuffer>(body, writePublisher -> this.doCommit(() -> this.writeWithInternal((Publisher<? extends DataBuffer>)writePublisher))).doOnError(t -> this.removeContentLength());
    }

    @Override
    public final Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body) {
        return new ChannelSendOperator<Publisher<? extends DataBuffer>>(body, writePublisher -> this.doCommit(() -> this.writeAndFlushWithInternal((Publisher<? extends Publisher<? extends DataBuffer>>)writePublisher))).doOnError(t -> this.removeContentLength());
    }

    private void removeContentLength() {
        if (!this.isCommitted()) {
            this.getHeaders().remove("Content-Length");
        }
    }

    @Override
    public Mono<Void> setComplete() {
        return !this.isCommitted() ? this.doCommit(null) : Mono.empty();
    }

    protected Mono<Void> doCommit() {
        return this.doCommit(null);
    }

    protected Mono<Void> doCommit(@Nullable Supplier<? extends Mono<Void>> writeAction) {
        if (!this.state.compareAndSet(State.NEW, State.COMMITTING)) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Skipping doCommit (response already committed).");
            }
            return Mono.empty();
        }
        this.commitActions.add(() -> Mono.fromRunnable(() -> {
            this.applyStatusCode();
            this.applyHeaders();
            this.applyCookies();
            this.state.set(State.COMMITTED);
        }));
        if (writeAction != null) {
            this.commitActions.add(writeAction);
        }
        List actions = this.commitActions.stream().map(Supplier::get).collect(Collectors.toList());
        return Flux.concat(actions).then();
    }

    protected abstract Mono<Void> writeWithInternal(Publisher<? extends DataBuffer> var1);

    protected abstract Mono<Void> writeAndFlushWithInternal(Publisher<? extends Publisher<? extends DataBuffer>> var1);

    protected abstract void applyStatusCode();

    protected abstract void applyHeaders();

    protected abstract void applyCookies();

    private static enum State {
        NEW,
        COMMITTING,
        COMMITTED;

    }
}

