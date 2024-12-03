/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.nio.protocol;

import org.apache.http.HttpRequest;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.nio.protocol.HttpAsyncRequestHandler;
import org.apache.http.nio.protocol.HttpAsyncRequestHandlerMapper;
import org.apache.http.protocol.UriPatternMatcher;
import org.apache.http.util.Args;

@Contract(threading=ThreadingBehavior.SAFE)
public class UriHttpAsyncRequestHandlerMapper
implements HttpAsyncRequestHandlerMapper {
    private final UriPatternMatcher<HttpAsyncRequestHandler<?>> matcher;

    protected UriHttpAsyncRequestHandlerMapper(UriPatternMatcher<HttpAsyncRequestHandler<?>> matcher) {
        this.matcher = Args.notNull(matcher, "Pattern matcher");
    }

    public UriPatternMatcher<HttpAsyncRequestHandler<?>> getUriPatternMatcher() {
        return this.matcher;
    }

    public UriHttpAsyncRequestHandlerMapper() {
        this(new UriPatternMatcher());
    }

    public void register(String pattern, HttpAsyncRequestHandler<?> handler) {
        this.matcher.register(pattern, handler);
    }

    public void unregister(String pattern) {
        this.matcher.unregister(pattern);
    }

    protected String getRequestPath(HttpRequest request) {
        String uriPath = request.getRequestLine().getUri();
        int index = uriPath.indexOf(63);
        if (index != -1) {
            uriPath = uriPath.substring(0, index);
        } else {
            index = uriPath.indexOf(35);
            if (index != -1) {
                uriPath = uriPath.substring(0, index);
            }
        }
        return uriPath;
    }

    public String toString() {
        return this.getClass().getName() + " [matcher=" + this.matcher + "]";
    }

    @Override
    public HttpAsyncRequestHandler<?> lookup(HttpRequest request) {
        return this.matcher.lookup(this.getRequestPath(request));
    }
}

