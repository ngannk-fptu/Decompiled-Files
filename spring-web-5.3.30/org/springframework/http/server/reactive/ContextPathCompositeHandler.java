/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 *  reactor.core.publisher.Mono
 */
package org.springframework.http.server.reactive;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;

public class ContextPathCompositeHandler
implements HttpHandler {
    private final Map<String, HttpHandler> handlerMap;

    public ContextPathCompositeHandler(Map<String, ? extends HttpHandler> handlerMap) {
        Assert.notEmpty(handlerMap, (String)"Handler map must not be empty");
        this.handlerMap = ContextPathCompositeHandler.initHandlers(handlerMap);
    }

    private static Map<String, HttpHandler> initHandlers(Map<String, ? extends HttpHandler> map) {
        map.keySet().forEach(ContextPathCompositeHandler::assertValidContextPath);
        return new LinkedHashMap<String, HttpHandler>(map);
    }

    private static void assertValidContextPath(String contextPath) {
        Assert.hasText((String)contextPath, (String)"Context path must not be empty");
        if (contextPath.equals("/")) {
            return;
        }
        Assert.isTrue((boolean)contextPath.startsWith("/"), (String)"Context path must begin with '/'");
        Assert.isTrue((!contextPath.endsWith("/") ? 1 : 0) != 0, (String)"Context path must not end with '/'");
    }

    @Override
    public Mono<Void> handle(ServerHttpRequest request, ServerHttpResponse response) {
        String path = request.getPath().pathWithinApplication().value();
        return this.handlerMap.entrySet().stream().filter(entry -> path.startsWith((String)entry.getKey())).findFirst().map(entry -> {
            String contextPath = request.getPath().contextPath().value() + (String)entry.getKey();
            ServerHttpRequest newRequest = request.mutate().contextPath(contextPath).build();
            return ((HttpHandler)entry.getValue()).handle(newRequest, response);
        }).orElseGet(() -> {
            response.setStatusCode(HttpStatus.NOT_FOUND);
            return response.setComplete();
        });
    }
}

