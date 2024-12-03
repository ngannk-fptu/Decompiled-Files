/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.http.server.PathContainer
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.PathMatcher
 *  org.springframework.web.util.pattern.PathPattern
 */
package org.springframework.web.servlet.handler;

import java.util.Map;
import org.springframework.http.server.PathContainer;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.PathMatcher;
import org.springframework.web.util.pattern.PathPattern;

public class RequestMatchResult {
    @Nullable
    private final PathPattern pathPattern;
    @Nullable
    private final PathContainer lookupPathContainer;
    @Nullable
    private final String pattern;
    @Nullable
    private final String lookupPath;
    @Nullable
    private final PathMatcher pathMatcher;

    public RequestMatchResult(PathPattern pathPattern, PathContainer lookupPath) {
        Assert.notNull((Object)pathPattern, (String)"PathPattern is required");
        Assert.notNull((Object)lookupPath, (String)"PathContainer is required");
        this.pattern = null;
        this.lookupPath = null;
        this.pathMatcher = null;
        this.pathPattern = pathPattern;
        this.lookupPathContainer = lookupPath;
    }

    public RequestMatchResult(String pattern, String lookupPath, PathMatcher pathMatcher) {
        Assert.hasText((String)pattern, (String)"'matchingPattern' is required");
        Assert.hasText((String)lookupPath, (String)"'lookupPath' is required");
        Assert.notNull((Object)pathMatcher, (String)"PathMatcher is required");
        this.pattern = pattern;
        this.lookupPath = lookupPath;
        this.pathMatcher = pathMatcher;
        this.pathPattern = null;
        this.lookupPathContainer = null;
    }

    public Map<String, String> extractUriTemplateVariables() {
        return this.pathPattern != null ? this.pathPattern.matchAndExtract(this.lookupPathContainer).getUriVariables() : this.pathMatcher.extractUriTemplateVariables(this.pattern, this.lookupPath);
    }
}

