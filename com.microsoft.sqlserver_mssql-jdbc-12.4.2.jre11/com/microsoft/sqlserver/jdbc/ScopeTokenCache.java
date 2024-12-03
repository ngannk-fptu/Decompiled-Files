/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.azure.core.credential.AccessToken
 *  com.azure.core.credential.TokenRequestContext
 *  reactor.core.publisher.FluxSink
 *  reactor.core.publisher.FluxSink$OverflowStrategy
 *  reactor.core.publisher.Mono
 *  reactor.core.publisher.ReplayProcessor
 */
package com.microsoft.sqlserver.jdbc;

import com.azure.core.credential.AccessToken;
import com.azure.core.credential.TokenRequestContext;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import reactor.core.publisher.ReplayProcessor;

class ScopeTokenCache {
    private final AtomicBoolean wip;
    private AccessToken cache;
    private final ReplayProcessor<AccessToken> emitterProcessor = ReplayProcessor.create((int)1);
    private final FluxSink<AccessToken> sink = this.emitterProcessor.sink(FluxSink.OverflowStrategy.BUFFER);
    private final Function<TokenRequestContext, Mono<AccessToken>> getNew;
    private TokenRequestContext request;

    ScopeTokenCache(Function<TokenRequestContext, Mono<AccessToken>> getNew) {
        this.wip = new AtomicBoolean(false);
        this.getNew = getNew;
    }

    void setRequest(TokenRequestContext request) {
        this.request = request;
    }

    Mono<AccessToken> getToken() {
        if (this.cache != null && !this.cache.isExpired()) {
            return Mono.just((Object)this.cache);
        }
        return Mono.defer(() -> {
            if (!this.wip.getAndSet(true)) {
                return this.getNew.apply(this.request).doOnNext(ac -> {
                    this.cache = ac;
                }).doOnNext(arg_0 -> this.sink.next(arg_0)).doOnError(arg_0 -> this.sink.error(arg_0)).doOnTerminate(() -> this.wip.set(false));
            }
            return this.emitterProcessor.next();
        });
    }
}

