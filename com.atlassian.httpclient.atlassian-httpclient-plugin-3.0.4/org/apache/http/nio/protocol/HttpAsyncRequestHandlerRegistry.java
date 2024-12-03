/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.nio.protocol;

import java.util.Map;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.nio.protocol.HttpAsyncRequestHandler;
import org.apache.http.nio.protocol.HttpAsyncRequestHandlerResolver;
import org.apache.http.protocol.UriPatternMatcher;

@Deprecated
@Contract(threading=ThreadingBehavior.SAFE)
public class HttpAsyncRequestHandlerRegistry
implements HttpAsyncRequestHandlerResolver {
    private final UriPatternMatcher<HttpAsyncRequestHandler<?>> matcher = new UriPatternMatcher();

    public void register(String pattern, HttpAsyncRequestHandler<?> handler) {
        this.matcher.register(pattern, handler);
    }

    public void unregister(String pattern) {
        this.matcher.unregister(pattern);
    }

    public void setHandlers(Map<String, HttpAsyncRequestHandler<?>> map) {
        this.matcher.setObjects(map);
    }

    public Map<String, HttpAsyncRequestHandler<?>> getHandlers() {
        return this.matcher.getObjects();
    }

    @Override
    public HttpAsyncRequestHandler<?> lookup(String requestURI) {
        return this.matcher.lookup(requestURI);
    }
}

