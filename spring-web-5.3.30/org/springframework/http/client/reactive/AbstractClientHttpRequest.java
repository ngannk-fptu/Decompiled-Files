/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.reactivestreams.Publisher
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.CollectionUtils
 *  org.springframework.util.LinkedMultiValueMap
 *  org.springframework.util.MultiValueMap
 *  reactor.core.publisher.Flux
 *  reactor.core.publisher.Mono
 */
package org.springframework.http.client.reactive;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.reactivestreams.Publisher;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ClientHttpRequest;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public abstract class AbstractClientHttpRequest
implements ClientHttpRequest {
    private final HttpHeaders headers;
    private final MultiValueMap<String, HttpCookie> cookies;
    private final AtomicReference<State> state = new AtomicReference<State>(State.NEW);
    private final List<Supplier<? extends Publisher<Void>>> commitActions = new ArrayList<Supplier<? extends Publisher<Void>>>(4);
    @Nullable
    private HttpHeaders readOnlyHeaders;

    public AbstractClientHttpRequest() {
        this(new HttpHeaders());
    }

    public AbstractClientHttpRequest(HttpHeaders headers) {
        Assert.notNull((Object)headers, (String)"HttpHeaders must not be null");
        this.headers = headers;
        this.cookies = new LinkedMultiValueMap();
    }

    @Override
    public HttpHeaders getHeaders() {
        if (this.readOnlyHeaders != null) {
            return this.readOnlyHeaders;
        }
        if (State.COMMITTED.equals((Object)this.state.get())) {
            this.readOnlyHeaders = this.initReadOnlyHeaders();
            return this.readOnlyHeaders;
        }
        return this.headers;
    }

    protected HttpHeaders initReadOnlyHeaders() {
        return HttpHeaders.readOnlyHttpHeaders(this.headers);
    }

    @Override
    public MultiValueMap<String, HttpCookie> getCookies() {
        if (State.COMMITTED.equals((Object)this.state.get())) {
            return CollectionUtils.unmodifiableMultiValueMap(this.cookies);
        }
        return this.cookies;
    }

    @Override
    public void beforeCommit(Supplier<? extends Mono<Void>> action) {
        Assert.notNull(action, (String)"Action must not be null");
        this.commitActions.add(action);
    }

    @Override
    public boolean isCommitted() {
        return this.state.get() != State.NEW;
    }

    protected Mono<Void> doCommit() {
        return this.doCommit(null);
    }

    protected Mono<Void> doCommit(@Nullable Supplier<? extends Publisher<Void>> writeAction) {
        if (!this.state.compareAndSet(State.NEW, State.COMMITTING)) {
            return Mono.empty();
        }
        this.commitActions.add(() -> Mono.fromRunnable(() -> {
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

    protected abstract void applyHeaders();

    protected abstract void applyCookies();

    private static enum State {
        NEW,
        COMMITTING,
        COMMITTED;

    }
}

