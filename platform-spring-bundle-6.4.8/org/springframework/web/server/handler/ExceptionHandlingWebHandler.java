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
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.StringUtils;
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
        ArrayList<WebExceptionHandler> handlersToUse = new ArrayList<WebExceptionHandler>();
        handlersToUse.add(new CheckpointInsertingHandler());
        handlersToUse.addAll(handlers);
        this.exceptionHandlers = Collections.unmodifiableList(handlersToUse);
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

    private static class CheckpointInsertingHandler
    implements WebExceptionHandler {
        private CheckpointInsertingHandler() {
        }

        @Override
        public Mono<Void> handle(ServerWebExchange exchange2, Throwable ex) {
            ServerHttpRequest request = exchange2.getRequest();
            String rawQuery = request.getURI().getRawQuery();
            String query = StringUtils.hasText(rawQuery) ? "?" + rawQuery : "";
            HttpMethod httpMethod = request.getMethod();
            String description = "HTTP " + (Object)((Object)httpMethod) + " \"" + request.getPath() + query + "\"";
            return Mono.error((Throwable)ex).checkpoint(description + " [ExceptionHandlingWebHandler]");
        }
    }
}

