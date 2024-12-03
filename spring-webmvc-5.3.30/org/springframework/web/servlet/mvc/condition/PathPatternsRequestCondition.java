/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletRequest
 *  javax.servlet.http.HttpServletRequest
 *  org.springframework.http.server.PathContainer
 *  org.springframework.lang.Nullable
 *  org.springframework.util.StringUtils
 *  org.springframework.web.util.ServletRequestPathUtils
 *  org.springframework.web.util.pattern.PathPattern
 *  org.springframework.web.util.pattern.PathPatternParser
 */
package org.springframework.web.servlet.mvc.condition;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.server.PathContainer;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.condition.AbstractRequestCondition;
import org.springframework.web.util.ServletRequestPathUtils;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

public final class PathPatternsRequestCondition
extends AbstractRequestCondition<PathPatternsRequestCondition> {
    private static final SortedSet<PathPattern> EMPTY_PATH_PATTERN = new TreeSet<PathPattern>(Collections.singleton(new PathPatternParser().parse("")));
    private static final Set<String> EMPTY_PATH = Collections.singleton("");
    private final SortedSet<PathPattern> patterns;

    public PathPatternsRequestCondition() {
        this(EMPTY_PATH_PATTERN);
    }

    public PathPatternsRequestCondition(PathPatternParser parser, String ... patterns) {
        this(PathPatternsRequestCondition.parse(parser, patterns));
    }

    private static SortedSet<PathPattern> parse(PathPatternParser parser, String ... patterns) {
        if (patterns.length == 0 || patterns.length == 1 && !StringUtils.hasText((String)patterns[0])) {
            return EMPTY_PATH_PATTERN;
        }
        TreeSet<PathPattern> result = new TreeSet<PathPattern>();
        for (String pattern : patterns) {
            pattern = parser.initFullPathPattern(pattern);
            result.add(parser.parse(pattern));
        }
        return result;
    }

    private PathPatternsRequestCondition(SortedSet<PathPattern> patterns) {
        this.patterns = patterns;
    }

    public Set<PathPattern> getPatterns() {
        return this.patterns;
    }

    @Override
    protected Collection<PathPattern> getContent() {
        return this.patterns;
    }

    @Override
    protected String getToStringInfix() {
        return " || ";
    }

    public PathPattern getFirstPattern() {
        return this.patterns.first();
    }

    public boolean isEmptyPathMapping() {
        return this.patterns == EMPTY_PATH_PATTERN;
    }

    public Set<String> getDirectPaths() {
        if (this.isEmptyPathMapping()) {
            return EMPTY_PATH;
        }
        HashSet<String> result = Collections.emptySet();
        for (PathPattern pattern : this.patterns) {
            if (pattern.hasPatternSyntax()) continue;
            result = result.isEmpty() ? new HashSet<String>(1) : result;
            result.add(pattern.getPatternString());
        }
        return result;
    }

    public Set<String> getPatternValues() {
        return this.isEmptyPathMapping() ? EMPTY_PATH : this.getPatterns().stream().map(PathPattern::getPatternString).collect(Collectors.toSet());
    }

    @Override
    public PathPatternsRequestCondition combine(PathPatternsRequestCondition other) {
        if (this.isEmptyPathMapping() && other.isEmptyPathMapping()) {
            return this;
        }
        if (other.isEmptyPathMapping()) {
            return this;
        }
        if (this.isEmptyPathMapping()) {
            return other;
        }
        TreeSet<PathPattern> combined = new TreeSet<PathPattern>();
        for (PathPattern pattern1 : this.patterns) {
            for (PathPattern pattern2 : other.patterns) {
                combined.add(pattern1.combine(pattern2));
            }
        }
        return new PathPatternsRequestCondition(combined);
    }

    @Override
    @Nullable
    public PathPatternsRequestCondition getMatchingCondition(HttpServletRequest request) {
        PathContainer path = ServletRequestPathUtils.getParsedRequestPath((ServletRequest)request).pathWithinApplication();
        SortedSet<PathPattern> matches = this.getMatchingPatterns(path);
        return matches != null ? new PathPatternsRequestCondition(matches) : null;
    }

    @Nullable
    private SortedSet<PathPattern> getMatchingPatterns(PathContainer path) {
        TreeSet<PathPattern> result = null;
        for (PathPattern pattern : this.patterns) {
            if (!pattern.matches(path)) continue;
            result = result != null ? result : new TreeSet<PathPattern>();
            result.add(pattern);
        }
        return result;
    }

    @Override
    public int compareTo(PathPatternsRequestCondition other, HttpServletRequest request) {
        Iterator iterator = this.patterns.iterator();
        Iterator<PathPattern> iteratorOther = other.getPatterns().iterator();
        while (iterator.hasNext() && iteratorOther.hasNext()) {
            int result = PathPattern.SPECIFICITY_COMPARATOR.compare(iterator.next(), iteratorOther.next());
            if (result == 0) continue;
            return result;
        }
        if (iterator.hasNext()) {
            return -1;
        }
        if (iteratorOther.hasNext()) {
            return 1;
        }
        return 0;
    }
}

