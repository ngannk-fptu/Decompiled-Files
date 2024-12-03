/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletRequest
 *  javax.servlet.http.HttpServletRequest
 *  org.springframework.http.server.PathContainer
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.web.util.ServletRequestPathUtils
 *  org.springframework.web.util.pattern.PathPattern
 *  org.springframework.web.util.pattern.PathPatternParser
 */
package org.springframework.web.servlet.handler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.server.PathContainer;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.handler.MatchableHandlerMapping;
import org.springframework.web.servlet.handler.RequestMatchResult;
import org.springframework.web.util.ServletRequestPathUtils;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

class PathPatternMatchableHandlerMapping
implements MatchableHandlerMapping {
    private static final int MAX_PATTERNS = 1024;
    private final MatchableHandlerMapping delegate;
    private final PathPatternParser parser;
    private final Map<String, PathPattern> pathPatternCache = new ConcurrentHashMap<String, PathPattern>();

    public PathPatternMatchableHandlerMapping(MatchableHandlerMapping delegate) {
        Assert.notNull((Object)delegate, (String)"HandlerMapping to delegate to is required.");
        Assert.notNull((Object)delegate.getPatternParser(), (String)"Expected HandlerMapping configured to use PatternParser.");
        this.delegate = delegate;
        this.parser = delegate.getPatternParser();
    }

    @Override
    @Nullable
    public RequestMatchResult match(HttpServletRequest request, String pattern) {
        PathContainer path;
        PathPattern pathPattern = this.pathPatternCache.computeIfAbsent(pattern, value -> {
            Assert.isTrue((this.pathPatternCache.size() < 1024 ? 1 : 0) != 0, (String)"Max size for pattern cache exceeded.");
            return this.parser.parse(pattern);
        });
        return pathPattern.matches(path = ServletRequestPathUtils.getParsedRequestPath((ServletRequest)request).pathWithinApplication()) ? new RequestMatchResult(pathPattern, path) : null;
    }

    @Override
    @Nullable
    public HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
        return this.delegate.getHandler(request);
    }
}

