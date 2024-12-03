/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  reactor.core.publisher.Mono
 */
package org.springframework.web.server.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.log.LogFormatUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

public class ResponseStatusExceptionHandler
implements WebExceptionHandler {
    private static final Log logger = LogFactory.getLog(ResponseStatusExceptionHandler.class);
    @Nullable
    private Log warnLogger;

    public void setWarnLogCategory(String loggerName) {
        this.warnLogger = LogFactory.getLog((String)loggerName);
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange2, Throwable ex) {
        if (!this.updateResponse(exchange2.getResponse(), ex)) {
            return Mono.error((Throwable)ex);
        }
        String logPrefix = exchange2.getLogPrefix();
        if (this.warnLogger != null && this.warnLogger.isWarnEnabled()) {
            this.warnLogger.warn((Object)(logPrefix + this.formatError(ex, exchange2.getRequest())));
        } else if (logger.isDebugEnabled()) {
            logger.debug((Object)(logPrefix + this.formatError(ex, exchange2.getRequest())));
        }
        return exchange2.getResponse().setComplete();
    }

    private String formatError(Throwable ex, ServerHttpRequest request) {
        String className = ex.getClass().getSimpleName();
        String message = LogFormatUtils.formatValue(ex.getMessage(), -1, true);
        String path = request.getURI().getRawPath();
        return "Resolved [" + className + ": " + message + "] for HTTP " + (Object)((Object)request.getMethod()) + " " + path;
    }

    private boolean updateResponse(ServerHttpResponse response, Throwable ex) {
        int code;
        boolean result = false;
        HttpStatus httpStatus = this.determineStatus(ex);
        int n = code = httpStatus != null ? httpStatus.value() : this.determineRawStatusCode(ex);
        if (code != -1) {
            if (response.setRawStatusCode(code)) {
                if (ex instanceof ResponseStatusException) {
                    ((ResponseStatusException)ex).getResponseHeaders().forEach((name, values) -> values.forEach(value -> response.getHeaders().add((String)name, (String)value)));
                }
                result = true;
            }
        } else {
            Throwable cause = ex.getCause();
            if (cause != null) {
                result = this.updateResponse(response, cause);
            }
        }
        return result;
    }

    @Nullable
    @Deprecated
    protected HttpStatus determineStatus(Throwable ex) {
        return null;
    }

    protected int determineRawStatusCode(Throwable ex) {
        if (ex instanceof ResponseStatusException) {
            return ((ResponseStatusException)ex).getRawStatusCode();
        }
        return -1;
    }
}

