/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  reactor.core.publisher.Mono
 */
package org.springframework.web.server.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.lang.Nullable;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

public class ResponseStatusExceptionHandler
implements WebExceptionHandler {
    private static final Log logger = LogFactory.getLog(ResponseStatusExceptionHandler.class);

    @Override
    public Mono<Void> handle(ServerWebExchange exchange2, Throwable ex) {
        HttpStatus status = this.resolveStatus(ex);
        if (status != null && exchange2.getResponse().setStatusCode(status)) {
            if (status.is5xxServerError()) {
                logger.error(this.buildMessage(exchange2.getRequest(), ex));
            } else if (status == HttpStatus.BAD_REQUEST) {
                logger.warn(this.buildMessage(exchange2.getRequest(), ex));
            } else {
                logger.trace(this.buildMessage(exchange2.getRequest(), ex));
            }
            return exchange2.getResponse().setComplete();
        }
        return Mono.error((Throwable)ex);
    }

    private String buildMessage(ServerHttpRequest request, Throwable ex) {
        return "Failed to handle request [" + (Object)((Object)request.getMethod()) + " " + request.getURI() + "]: " + ex.getMessage();
    }

    @Nullable
    private HttpStatus resolveStatus(Throwable ex) {
        Throwable cause;
        HttpStatus status = this.determineStatus(ex);
        if (status == null && (cause = ex.getCause()) != null) {
            status = this.resolveStatus(cause);
        }
        return status;
    }

    @Nullable
    protected HttpStatus determineStatus(Throwable ex) {
        if (ex instanceof ResponseStatusException) {
            return ((ResponseStatusException)ex).getStatus();
        }
        return null;
    }
}

