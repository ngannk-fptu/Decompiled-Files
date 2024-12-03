/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  reactor.core.publisher.Mono
 */
package org.springframework.web.server.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import org.springframework.web.server.WebHandler;
import org.springframework.web.server.handler.WebHandlerDecorator;
import reactor.core.publisher.Mono;

public class ExceptionHandlingWebHandler
extends WebHandlerDecorator {
    private final List<WebExceptionHandler> exceptionHandlers;

    public ExceptionHandlingWebHandler(WebHandler delegate, List<WebExceptionHandler> handlers) {
        super(delegate);
        this.exceptionHandlers = Collections.unmodifiableList(new ArrayList<WebExceptionHandler>(handlers));
    }

    public List<WebExceptionHandler> getExceptionHandlers() {
        return this.exceptionHandlers;
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange2) {
        Mono completion;
        try {
            completion = super.handle(exchange2);
        }
        catch (Throwable ex2) {
            completion = Mono.error((Throwable)ex2);
        }
        for (WebExceptionHandler handler : this.exceptionHandlers) {
            completion = completion.onErrorResume(ex -> handler.handle(exchange2, (Throwable)ex));
        }
        return completion;
    }
}

