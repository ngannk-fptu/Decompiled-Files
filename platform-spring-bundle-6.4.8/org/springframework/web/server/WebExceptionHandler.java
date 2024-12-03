/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  reactor.core.publisher.Mono
 */
package org.springframework.web.server;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public interface WebExceptionHandler {
    public Mono<Void> handle(ServerWebExchange var1, Throwable var2);
}

