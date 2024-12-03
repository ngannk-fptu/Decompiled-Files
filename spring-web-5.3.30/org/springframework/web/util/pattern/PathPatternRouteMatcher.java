/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.RouteMatcher
 *  org.springframework.util.RouteMatcher$Route
 */
package org.springframework.web.util.pattern;

import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.http.server.PathContainer;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.RouteMatcher;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

public class PathPatternRouteMatcher
implements RouteMatcher {
    private final PathPatternParser parser;
    private final Map<String, PathPattern> pathPatternCache = new ConcurrentHashMap<String, PathPattern>();

    public PathPatternRouteMatcher() {
        this.parser = new PathPatternParser();
        this.parser.setPathOptions(PathContainer.Options.MESSAGE_ROUTE);
        this.parser.setMatchOptionalTrailingSeparator(false);
    }

    public PathPatternRouteMatcher(PathPatternParser parser) {
        Assert.notNull((Object)parser, (String)"PathPatternParser must not be null");
        this.parser = parser;
    }

    public RouteMatcher.Route parseRoute(String routeValue) {
        return new PathContainerRoute(PathContainer.parsePath(routeValue, this.parser.getPathOptions()));
    }

    public boolean isPattern(String route) {
        return this.getPathPattern(route).hasPatternSyntax();
    }

    public String combine(String pattern1, String pattern2) {
        return this.getPathPattern(pattern1).combine(this.getPathPattern(pattern2)).getPatternString();
    }

    public boolean match(String pattern, RouteMatcher.Route route) {
        return this.getPathPattern(pattern).matches(this.getPathContainer(route));
    }

    @Nullable
    public Map<String, String> matchAndExtract(String pattern, RouteMatcher.Route route) {
        PathPattern.PathMatchInfo info = this.getPathPattern(pattern).matchAndExtract(this.getPathContainer(route));
        return info != null ? info.getUriVariables() : null;
    }

    public Comparator<String> getPatternComparator(RouteMatcher.Route route) {
        return Comparator.comparing(this::getPathPattern);
    }

    private PathPattern getPathPattern(String pattern) {
        return this.pathPatternCache.computeIfAbsent(pattern, this.parser::parse);
    }

    private PathContainer getPathContainer(RouteMatcher.Route route) {
        Assert.isInstanceOf(PathContainerRoute.class, (Object)route);
        return ((PathContainerRoute)route).pathContainer;
    }

    private static class PathContainerRoute
    implements RouteMatcher.Route {
        private final PathContainer pathContainer;

        PathContainerRoute(PathContainer pathContainer) {
            this.pathContainer = pathContainer;
        }

        public String value() {
            return this.pathContainer.value();
        }

        public String toString() {
            return this.value();
        }
    }
}

