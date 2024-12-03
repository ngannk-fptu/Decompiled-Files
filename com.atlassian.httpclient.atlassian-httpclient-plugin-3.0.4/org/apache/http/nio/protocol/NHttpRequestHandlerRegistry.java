/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.nio.protocol;

import java.util.Map;
import org.apache.http.nio.protocol.NHttpRequestHandler;
import org.apache.http.nio.protocol.NHttpRequestHandlerResolver;
import org.apache.http.protocol.UriPatternMatcher;

@Deprecated
public class NHttpRequestHandlerRegistry
implements NHttpRequestHandlerResolver {
    private final UriPatternMatcher<NHttpRequestHandler> matcher = new UriPatternMatcher();

    public void register(String pattern, NHttpRequestHandler handler) {
        this.matcher.register(pattern, handler);
    }

    public void unregister(String pattern) {
        this.matcher.unregister(pattern);
    }

    public void setHandlers(Map<String, NHttpRequestHandler> map) {
        this.matcher.setObjects(map);
    }

    public Map<String, NHttpRequestHandler> getHandlers() {
        return this.matcher.getObjects();
    }

    @Override
    public NHttpRequestHandler lookup(String requestURI) {
        return this.matcher.lookup(requestURI);
    }
}

