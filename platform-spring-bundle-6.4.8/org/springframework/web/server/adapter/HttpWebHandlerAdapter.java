/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  reactor.core.publisher.Mono
 */
package org.springframework.web.server.adapter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.core.log.LogFormatUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.LoggingCodecSupport;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebHandler;
import org.springframework.web.server.adapter.DefaultServerWebExchange;
import org.springframework.web.server.adapter.ForwardedHeaderTransformer;
import org.springframework.web.server.handler.WebHandlerDecorator;
import org.springframework.web.server.i18n.AcceptHeaderLocaleContextResolver;
import org.springframework.web.server.i18n.LocaleContextResolver;
import org.springframework.web.server.session.DefaultWebSessionManager;
import org.springframework.web.server.session.WebSessionManager;
import reactor.core.publisher.Mono;

public class HttpWebHandlerAdapter
extends WebHandlerDecorator
implements HttpHandler {
    private static final String DISCONNECTED_CLIENT_LOG_CATEGORY = "org.springframework.web.server.DisconnectedClient";
    private static final Set<String> DISCONNECTED_CLIENT_EXCEPTIONS = new HashSet<String>(Arrays.asList("AbortedException", "ClientAbortException", "EOFException", "EofException"));
    private static final Log logger = LogFactory.getLog(HttpWebHandlerAdapter.class);
    private static final Log lostClientLogger = LogFactory.getLog((String)"org.springframework.web.server.DisconnectedClient");
    private WebSessionManager sessionManager = new DefaultWebSessionManager();
    @Nullable
    private ServerCodecConfigurer codecConfigurer;
    private LocaleContextResolver localeContextResolver = new AcceptHeaderLocaleContextResolver();
    @Nullable
    private ForwardedHeaderTransformer forwardedHeaderTransformer;
    @Nullable
    private ApplicationContext applicationContext;
    private boolean enableLoggingRequestDetails = false;

    public HttpWebHandlerAdapter(WebHandler delegate) {
        super(delegate);
    }

    public void setSessionManager(WebSessionManager sessionManager) {
        Assert.notNull((Object)sessionManager, "WebSessionManager must not be null");
        this.sessionManager = sessionManager;
    }

    public WebSessionManager getSessionManager() {
        return this.sessionManager;
    }

    public void setCodecConfigurer(ServerCodecConfigurer codecConfigurer) {
        Assert.notNull((Object)codecConfigurer, "ServerCodecConfigurer is required");
        this.codecConfigurer = codecConfigurer;
        this.enableLoggingRequestDetails = false;
        this.codecConfigurer.getReaders().stream().filter(LoggingCodecSupport.class::isInstance).forEach(reader -> {
            if (((LoggingCodecSupport)((Object)reader)).isEnableLoggingRequestDetails()) {
                this.enableLoggingRequestDetails = true;
            }
        });
    }

    public ServerCodecConfigurer getCodecConfigurer() {
        if (this.codecConfigurer == null) {
            this.setCodecConfigurer(ServerCodecConfigurer.create());
        }
        return this.codecConfigurer;
    }

    public void setLocaleContextResolver(LocaleContextResolver resolver) {
        Assert.notNull((Object)resolver, "LocaleContextResolver is required");
        this.localeContextResolver = resolver;
    }

    public LocaleContextResolver getLocaleContextResolver() {
        return this.localeContextResolver;
    }

    public void setForwardedHeaderTransformer(ForwardedHeaderTransformer transformer) {
        Assert.notNull((Object)transformer, "ForwardedHeaderTransformer is required");
        this.forwardedHeaderTransformer = transformer;
    }

    @Nullable
    public ForwardedHeaderTransformer getForwardedHeaderTransformer() {
        return this.forwardedHeaderTransformer;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Nullable
    public ApplicationContext getApplicationContext() {
        return this.applicationContext;
    }

    public void afterPropertiesSet() {
        if (logger.isDebugEnabled()) {
            String value = this.enableLoggingRequestDetails ? "shown which may lead to unsafe logging of potentially sensitive data" : "masked to prevent unsafe logging of potentially sensitive data";
            logger.debug((Object)("enableLoggingRequestDetails='" + this.enableLoggingRequestDetails + "': form data and headers will be " + value));
        }
    }

    @Override
    public Mono<Void> handle(ServerHttpRequest request, ServerHttpResponse response) {
        if (this.forwardedHeaderTransformer != null) {
            try {
                request = this.forwardedHeaderTransformer.apply(request);
            }
            catch (Throwable ex2) {
                if (logger.isDebugEnabled()) {
                    logger.debug((Object)("Failed to apply forwarded headers to " + this.formatRequest(request)), ex2);
                }
                response.setStatusCode(HttpStatus.BAD_REQUEST);
                return response.setComplete();
            }
        }
        ServerWebExchange exchange2 = this.createExchange(request, response);
        LogFormatUtils.traceDebug(logger, traceOn -> exchange2.getLogPrefix() + this.formatRequest(exchange2.getRequest()) + (traceOn != false ? ", headers=" + this.formatHeaders(exchange2.getRequest().getHeaders()) : ""));
        return this.getDelegate().handle(exchange2).doOnSuccess(aVoid -> this.logResponse(exchange2)).onErrorResume(ex -> this.handleUnresolvedError(exchange2, (Throwable)ex)).then(Mono.defer(response::setComplete));
    }

    protected ServerWebExchange createExchange(ServerHttpRequest request, ServerHttpResponse response) {
        return new DefaultServerWebExchange(request, response, this.sessionManager, this.getCodecConfigurer(), this.getLocaleContextResolver(), this.applicationContext);
    }

    protected String formatRequest(ServerHttpRequest request) {
        String rawQuery = request.getURI().getRawQuery();
        String query = StringUtils.hasText(rawQuery) ? "?" + rawQuery : "";
        return "HTTP " + (Object)((Object)request.getMethod()) + " \"" + request.getPath() + query + "\"";
    }

    private void logResponse(ServerWebExchange exchange2) {
        LogFormatUtils.traceDebug(logger, traceOn -> {
            HttpStatus status = exchange2.getResponse().getStatusCode();
            return exchange2.getLogPrefix() + "Completed " + (status != null ? status : "200 OK") + (traceOn != false ? ", headers=" + this.formatHeaders(exchange2.getResponse().getHeaders()) : "");
        });
    }

    private String formatHeaders(HttpHeaders responseHeaders) {
        return this.enableLoggingRequestDetails ? responseHeaders.toString() : (responseHeaders.isEmpty() ? "{}" : "{masked}");
    }

    private Mono<Void> handleUnresolvedError(ServerWebExchange exchange2, Throwable ex) {
        ServerHttpRequest request = exchange2.getRequest();
        ServerHttpResponse response = exchange2.getResponse();
        String logPrefix = exchange2.getLogPrefix();
        if (response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR)) {
            logger.error((Object)(logPrefix + "500 Server Error for " + this.formatRequest(request)), ex);
            return Mono.empty();
        }
        if (this.isDisconnectedClientError(ex)) {
            if (lostClientLogger.isTraceEnabled()) {
                lostClientLogger.trace((Object)(logPrefix + "Client went away"), ex);
            } else if (lostClientLogger.isDebugEnabled()) {
                lostClientLogger.debug((Object)(logPrefix + "Client went away: " + ex + " (stacktrace at TRACE level for '" + DISCONNECTED_CLIENT_LOG_CATEGORY + "')"));
            }
            return Mono.empty();
        }
        logger.error((Object)(logPrefix + "Error [" + ex + "] for " + this.formatRequest(request) + ", but ServerHttpResponse already committed (" + (Object)((Object)response.getStatusCode()) + ")"));
        return Mono.error((Throwable)ex);
    }

    private boolean isDisconnectedClientError(Throwable ex) {
        String text;
        String message = NestedExceptionUtils.getMostSpecificCause(ex).getMessage();
        if (message != null && ((text = message.toLowerCase()).contains("broken pipe") || text.contains("connection reset by peer"))) {
            return true;
        }
        return DISCONNECTED_CLIENT_EXCEPTIONS.contains(ex.getClass().getSimpleName());
    }
}

