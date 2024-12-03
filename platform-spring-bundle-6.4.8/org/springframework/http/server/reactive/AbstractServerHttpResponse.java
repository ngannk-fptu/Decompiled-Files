/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.reactivestreams.Publisher
 *  reactor.core.publisher.Flux
 *  reactor.core.publisher.Mono
 */
package org.springframework.http.server.reactive;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.PooledDataBuffer;
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
    private final DataBufferFactory dataBufferFactory;
    @Nullable
    private Integer statusCode;
    private final HttpHeaders headers;
    private final MultiValueMap<String, ResponseCookie> cookies;
    private final AtomicReference<State> state = new AtomicReference<State>(State.NEW);
    private final List<Supplier<? extends Mono<Void>>> commitActions = new ArrayList<Supplier<? extends Mono<Void>>>(4);
    @Nullable
    private HttpHeaders readOnlyHeaders;

    public AbstractServerHttpResponse(DataBufferFactory dataBufferFactory) {
        this(dataBufferFactory, new HttpHeaders());
    }

    public AbstractServerHttpResponse(DataBufferFactory dataBufferFactory, HttpHeaders headers) {
        Assert.notNull((Object)dataBufferFactory, "DataBufferFactory must not be null");
        Assert.notNull((Object)headers, "HttpHeaders must not be null");
        this.dataBufferFactory = dataBufferFactory;
        this.headers = headers;
        this.cookies = new LinkedMultiValueMap<String, ResponseCookie>();
    }

    @Override
    public final DataBufferFactory bufferFactory() {
        return this.dataBufferFactory;
    }

    @Override
    public boolean setStatusCode(@Nullable HttpStatus status) {
        if (this.state.get() == State.COMMITTED) {
            return false;
        }
        this.statusCode = status != null ? Integer.valueOf(status.value()) : null;
        return true;
    }

    @Override
    @Nullable
    public HttpStatus getStatusCode() {
        return this.statusCode != null ? HttpStatus.resolve(this.statusCode) : null;
    }

    @Override
    public boolean setRawStatusCode(@Nullable Integer statusCode) {
        if (this.state.get() == State.COMMITTED) {
            return false;
        }
        this.statusCode = statusCode;
        return true;
    }

    @Override
    @Nullable
    public Integer getRawStatusCode() {
        return this.statusCode;
    }

    @Deprecated
    public void setStatusCodeValue(@Nullable Integer statusCode) {
        if (this.state.get() != State.COMMITTED) {
            this.statusCode = statusCode;
        }
    }

    @Nullable
    @Deprecated
    public Integer getStatusCodeValue() {
        return this.statusCode;
    }

    @Override
    public HttpHeaders getHeaders() {
        if (this.readOnlyHeaders != null) {
            return this.readOnlyHeaders;
        }
        if (this.state.get() == State.COMMITTED) {
            this.readOnlyHeaders = HttpHeaders.readOnlyHttpHeaders(this.headers);
            return this.readOnlyHeaders;
        }
        return this.headers;
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
        State state = this.state.get();
        return state != State.NEW && state != State.COMMIT_ACTION_FAILED;
    }

    @Override
    public final Mono<Void> writeWith(Publisher<? extends DataBuffer> body2) {
        if (body2 instanceof Mono) {
            return ((Mono)body2).flatMap(buffer -> {
                this.touchDataBuffer((DataBuffer)buffer);
                AtomicBoolean subscribed = new AtomicBoolean();
                return this.doCommit(() -> {
                    try {
                        return this.writeWithInternal((Publisher<? extends DataBuffer>)Mono.fromCallable(() -> buffer).doOnSubscribe(s -> subscribed.set(true)).doOnDiscard(PooledDataBuffer.class, DataBufferUtils::release));
                    }
                    catch (Throwable ex) {
                        return Mono.error((Throwable)ex);
                    }
                }).doOnError(ex -> DataBufferUtils.release(buffer)).doOnCancel(() -> {
                    if (!subscribed.get()) {
                        DataBufferUtils.release(buffer);
                    }
                });
            }).doOnError(t -> this.getHeaders().clearContentHeaders()).doOnDiscard(PooledDataBuffer.class, DataBufferUtils::release);
        }
        return new ChannelSendOperator<DataBuffer>(body2, inner -> this.doCommit(() -> this.writeWithInternal((Publisher<? extends DataBuffer>)inner))).doOnError(t -> this.getHeaders().clearContentHeaders());
    }

    @Override
    public final Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body2) {
        return new ChannelSendOperator<Publisher<? extends DataBuffer>>(body2, inner -> this.doCommit(() -> this.writeAndFlushWithInternal((Publisher<? extends Publisher<? extends DataBuffer>>)inner))).doOnError(t -> this.getHeaders().clearContentHeaders());
    }

    @Override
    public Mono<Void> setComplete() {
        return !this.isCommitted() ? this.doCommit(null) : Mono.empty();
    }

    protected Mono<Void> doCommit() {
        return this.doCommit(null);
    }

    protected Mono<Void> doCommit(@Nullable Supplier<? extends Mono<Void>> writeAction) {
        Flux allActions = Flux.empty();
        if (this.state.compareAndSet(State.NEW, State.COMMITTING)) {
            if (!this.commitActions.isEmpty()) {
                allActions = Flux.concat((Publisher)Flux.fromIterable(this.commitActions).map(Supplier::get)).doOnError(ex -> {
                    if (this.state.compareAndSet(State.COMMITTING, State.COMMIT_ACTION_FAILED)) {
                        this.getHeaders().clearContentHeaders();
                    }
                });
            }
        } else if (!this.state.compareAndSet(State.COMMIT_ACTION_FAILED, State.COMMITTING)) {
            return Mono.empty();
        }
        allActions = allActions.concatWith((Publisher)Mono.fromRunnable(() -> {
            this.applyStatusCode();
            this.applyHeaders();
            this.applyCookies();
            this.state.set(State.COMMITTED);
        }));
        if (writeAction != null) {
            allActions = allActions.concatWith((Publisher)writeAction.get());
        }
        return allActions.then();
    }

    protected abstract Mono<Void> writeWithInternal(Publisher<? extends DataBuffer> var1);

    protected abstract Mono<Void> writeAndFlushWithInternal(Publisher<? extends Publisher<? extends DataBuffer>> var1);

    protected abstract void applyStatusCode();

    protected abstract void applyHeaders();

    protected abstract void applyCookies();

    protected void touchDataBuffer(DataBuffer buffer) {
    }

    private static enum State {
        NEW,
        COMMITTING,
        COMMIT_ACTION_FAILED,
        COMMITTED;

    }
}

