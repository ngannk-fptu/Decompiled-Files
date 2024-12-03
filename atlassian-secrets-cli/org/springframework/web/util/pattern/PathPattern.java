/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.util.pattern;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.server.PathContainer;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.util.pattern.CaptureTheRestPathElement;
import org.springframework.web.util.pattern.PathElement;
import org.springframework.web.util.pattern.PathPatternParser;
import org.springframework.web.util.pattern.SeparatorPathElement;
import org.springframework.web.util.pattern.WildcardPathElement;
import org.springframework.web.util.pattern.WildcardTheRestPathElement;

public class PathPattern
implements Comparable<PathPattern> {
    private static final PathContainer EMPTY_PATH = PathContainer.parsePath("");
    private final String patternString;
    private final PathPatternParser parser;
    private final char separator;
    private final boolean matchOptionalTrailingSeparator;
    private final boolean caseSensitive;
    @Nullable
    private final PathElement head;
    private int capturedVariableCount;
    private int normalizedLength;
    private boolean endsWithSeparatorWildcard = false;
    private int score;
    private boolean catchAll = false;
    public static final Comparator<PathPattern> SPECIFICITY_COMPARATOR = Comparator.nullsLast(Comparator.comparingInt(p -> p.isCatchAll() ? 1 : 0).thenComparingInt(p -> p.isCatchAll() ? PathPattern.scoreByNormalizedLength(p) : 0).thenComparingInt(PathPattern::getScore).thenComparingInt(PathPattern::scoreByNormalizedLength));

    PathPattern(String patternText, PathPatternParser parser, @Nullable PathElement head) {
        this.patternString = patternText;
        this.parser = parser;
        this.separator = parser.getSeparator();
        this.matchOptionalTrailingSeparator = parser.isMatchOptionalTrailingSeparator();
        this.caseSensitive = parser.isCaseSensitive();
        this.head = head;
        PathElement elem = head;
        while (elem != null) {
            this.capturedVariableCount += elem.getCaptureCount();
            this.normalizedLength += elem.getNormalizedLength();
            this.score += elem.getScore();
            if (elem instanceof CaptureTheRestPathElement || elem instanceof WildcardTheRestPathElement) {
                this.catchAll = true;
            }
            if (elem instanceof SeparatorPathElement && elem.next != null && elem.next instanceof WildcardPathElement && elem.next.next == null) {
                this.endsWithSeparatorWildcard = true;
            }
            elem = elem.next;
        }
    }

    public String getPatternString() {
        return this.patternString;
    }

    public boolean matches(PathContainer pathContainer) {
        if (this.head == null) {
            return !this.hasLength(pathContainer) || this.matchOptionalTrailingSeparator && this.pathContainerIsJustSeparator(pathContainer);
        }
        if (!this.hasLength(pathContainer)) {
            if (this.head instanceof WildcardTheRestPathElement || this.head instanceof CaptureTheRestPathElement) {
                pathContainer = EMPTY_PATH;
            } else {
                return false;
            }
        }
        MatchingContext matchingContext = new MatchingContext(pathContainer, false);
        return this.head.matches(0, matchingContext);
    }

    @Nullable
    public PathMatchInfo matchAndExtract(PathContainer pathContainer) {
        MatchingContext matchingContext;
        if (this.head == null) {
            return this.hasLength(pathContainer) && (!this.matchOptionalTrailingSeparator || !this.pathContainerIsJustSeparator(pathContainer)) ? null : PathMatchInfo.EMPTY;
        }
        if (!this.hasLength(pathContainer)) {
            if (this.head instanceof WildcardTheRestPathElement || this.head instanceof CaptureTheRestPathElement) {
                pathContainer = EMPTY_PATH;
            } else {
                return null;
            }
        }
        return this.head.matches(0, matchingContext = new MatchingContext(pathContainer, true)) ? matchingContext.getPathMatchResult() : null;
    }

    @Nullable
    public PathRemainingMatchInfo matchStartOfPath(PathContainer pathContainer) {
        if (this.head == null) {
            return new PathRemainingMatchInfo(pathContainer);
        }
        if (!this.hasLength(pathContainer)) {
            return null;
        }
        MatchingContext matchingContext = new MatchingContext(pathContainer, true);
        matchingContext.setMatchAllowExtraPath();
        boolean matches = this.head.matches(0, matchingContext);
        if (!matches) {
            return null;
        }
        PathRemainingMatchInfo info = matchingContext.remainingPathIndex == pathContainer.elements().size() ? new PathRemainingMatchInfo(EMPTY_PATH, matchingContext.getPathMatchResult()) : new PathRemainingMatchInfo(pathContainer.subPath(matchingContext.remainingPathIndex), matchingContext.getPathMatchResult());
        return info;
    }

    public PathContainer extractPathWithinPattern(PathContainer path) {
        int endIndex;
        List<PathContainer.Element> pathElements = path.elements();
        int pathElementsCount = pathElements.size();
        int startIndex = 0;
        PathElement elem = this.head;
        while (elem != null && elem.getWildcardCount() == 0 && elem.getCaptureCount() == 0) {
            elem = elem.next;
            ++startIndex;
        }
        if (elem == null) {
            return PathContainer.parsePath("");
        }
        while (startIndex < pathElementsCount && pathElements.get(startIndex) instanceof PathContainer.Separator) {
            ++startIndex;
        }
        for (endIndex = pathElements.size(); endIndex > 0 && pathElements.get(endIndex - 1) instanceof PathContainer.Separator; --endIndex) {
        }
        boolean multipleAdjacentSeparators = false;
        for (int i = startIndex; i < endIndex - 1; ++i) {
            if (!(pathElements.get(i) instanceof PathContainer.Separator) || !(pathElements.get(i + 1) instanceof PathContainer.Separator)) continue;
            multipleAdjacentSeparators = true;
            break;
        }
        PathContainer resultPath = null;
        if (multipleAdjacentSeparators) {
            StringBuilder buf = new StringBuilder();
            int i = startIndex;
            while (i < endIndex) {
                PathContainer.Element e = pathElements.get(i++);
                buf.append(e.value());
                if (!(e instanceof PathContainer.Separator)) continue;
                while (i < endIndex && pathElements.get(i) instanceof PathContainer.Separator) {
                    ++i;
                }
            }
            resultPath = PathContainer.parsePath(buf.toString());
        } else {
            resultPath = startIndex >= endIndex ? PathContainer.parsePath("") : path.subPath(startIndex, endIndex);
        }
        return resultPath;
    }

    @Override
    public int compareTo(@Nullable PathPattern otherPattern) {
        int result = SPECIFICITY_COMPARATOR.compare(this, otherPattern);
        return result == 0 && otherPattern != null ? this.patternString.compareTo(otherPattern.patternString) : result;
    }

    public PathPattern combine(PathPattern pattern2string) {
        boolean secondExtensionWild;
        if (!StringUtils.hasLength(this.patternString)) {
            if (!StringUtils.hasLength(pattern2string.patternString)) {
                return this.parser.parse("");
            }
            return pattern2string;
        }
        if (!StringUtils.hasLength(pattern2string.patternString)) {
            return this;
        }
        if (!this.patternString.equals(pattern2string.patternString) && this.capturedVariableCount == 0 && this.matches(PathContainer.parsePath(pattern2string.patternString))) {
            return pattern2string;
        }
        if (this.endsWithSeparatorWildcard) {
            return this.parser.parse(this.concat(this.patternString.substring(0, this.patternString.length() - 2), pattern2string.patternString));
        }
        int starDotPos1 = this.patternString.indexOf("*.");
        if (this.capturedVariableCount != 0 || starDotPos1 == -1 || this.separator == '.') {
            return this.parser.parse(this.concat(this.patternString, pattern2string.patternString));
        }
        String firstExtension = this.patternString.substring(starDotPos1 + 1);
        String p2string = pattern2string.patternString;
        int dotPos2 = p2string.indexOf(46);
        String file2 = dotPos2 == -1 ? p2string : p2string.substring(0, dotPos2);
        String secondExtension = dotPos2 == -1 ? "" : p2string.substring(dotPos2);
        boolean firstExtensionWild = firstExtension.equals(".*") || firstExtension.equals("");
        boolean bl = secondExtensionWild = secondExtension.equals(".*") || secondExtension.equals("");
        if (!firstExtensionWild && !secondExtensionWild) {
            throw new IllegalArgumentException("Cannot combine patterns: " + this.patternString + " and " + pattern2string);
        }
        return this.parser.parse(file2 + (firstExtensionWild ? secondExtension : firstExtension));
    }

    public boolean equals(Object other) {
        if (!(other instanceof PathPattern)) {
            return false;
        }
        PathPattern otherPattern = (PathPattern)other;
        return this.patternString.equals(otherPattern.getPatternString()) && this.separator == otherPattern.getSeparator() && this.caseSensitive == otherPattern.caseSensitive;
    }

    public int hashCode() {
        return (this.patternString.hashCode() + this.separator) * 17 + (this.caseSensitive ? 1 : 0);
    }

    public String toString() {
        return this.patternString;
    }

    int getScore() {
        return this.score;
    }

    boolean isCatchAll() {
        return this.catchAll;
    }

    int getNormalizedLength() {
        return this.normalizedLength;
    }

    char getSeparator() {
        return this.separator;
    }

    int getCapturedVariableCount() {
        return this.capturedVariableCount;
    }

    String toChainString() {
        StringBuilder buf = new StringBuilder();
        PathElement pe = this.head;
        while (pe != null) {
            buf.append(pe.toString()).append(" ");
            pe = pe.next;
        }
        return buf.toString().trim();
    }

    String computePatternString() {
        StringBuilder buf = new StringBuilder();
        PathElement pe = this.head;
        while (pe != null) {
            buf.append(pe.getChars());
            pe = pe.next;
        }
        return buf.toString();
    }

    @Nullable
    PathElement getHeadSection() {
        return this.head;
    }

    private String concat(String path1, String path2) {
        boolean path2StartsWithSeparator;
        boolean path1EndsWithSeparator = path1.charAt(path1.length() - 1) == this.separator;
        boolean bl = path2StartsWithSeparator = path2.charAt(0) == this.separator;
        if (path1EndsWithSeparator && path2StartsWithSeparator) {
            return path1 + path2.substring(1);
        }
        if (path1EndsWithSeparator || path2StartsWithSeparator) {
            return path1 + path2;
        }
        return path1 + this.separator + path2;
    }

    private boolean hasLength(@Nullable PathContainer container) {
        return container != null && container.elements().size() > 0;
    }

    private static int scoreByNormalizedLength(PathPattern pattern) {
        return -pattern.getNormalizedLength();
    }

    private boolean pathContainerIsJustSeparator(PathContainer pathContainer) {
        return pathContainer.value().length() == 1 && pathContainer.value().charAt(0) == this.separator;
    }

    class MatchingContext {
        final PathContainer candidate;
        final List<PathContainer.Element> pathElements;
        final int pathLength;
        @Nullable
        private Map<String, String> extractedUriVariables;
        @Nullable
        private Map<String, MultiValueMap<String, String>> extractedMatrixVariables;
        boolean extractingVariables;
        boolean determineRemainingPath = false;
        int remainingPathIndex;

        public MatchingContext(PathContainer pathContainer, boolean extractVariables) {
            this.candidate = pathContainer;
            this.pathElements = pathContainer.elements();
            this.pathLength = this.pathElements.size();
            this.extractingVariables = extractVariables;
        }

        public void setMatchAllowExtraPath() {
            this.determineRemainingPath = true;
        }

        public boolean isMatchOptionalTrailingSeparator() {
            return PathPattern.this.matchOptionalTrailingSeparator;
        }

        public void set(String key, String value, MultiValueMap<String, String> parameters) {
            if (this.extractedUriVariables == null) {
                this.extractedUriVariables = new HashMap<String, String>();
            }
            this.extractedUriVariables.put(key, value);
            if (!parameters.isEmpty()) {
                if (this.extractedMatrixVariables == null) {
                    this.extractedMatrixVariables = new HashMap<String, MultiValueMap<String, String>>();
                }
                this.extractedMatrixVariables.put(key, CollectionUtils.unmodifiableMultiValueMap(parameters));
            }
        }

        public PathMatchInfo getPathMatchResult() {
            if (this.extractedUriVariables == null) {
                return PathMatchInfo.EMPTY;
            }
            return new PathMatchInfo(this.extractedUriVariables, this.extractedMatrixVariables);
        }

        boolean isSeparator(int pathIndex) {
            return this.pathElements.get(pathIndex) instanceof PathContainer.Separator;
        }

        String pathElementValue(int pathIndex) {
            PathContainer.Element element;
            PathContainer.Element element2 = element = pathIndex < this.pathLength ? this.pathElements.get(pathIndex) : null;
            if (element instanceof PathContainer.PathSegment) {
                return ((PathContainer.PathSegment)element).valueToMatch();
            }
            return "";
        }
    }

    public static class PathRemainingMatchInfo {
        private final PathContainer pathRemaining;
        private final PathMatchInfo pathMatchInfo;

        PathRemainingMatchInfo(PathContainer pathRemaining) {
            this(pathRemaining, PathMatchInfo.EMPTY);
        }

        PathRemainingMatchInfo(PathContainer pathRemaining, PathMatchInfo pathMatchInfo) {
            this.pathRemaining = pathRemaining;
            this.pathMatchInfo = pathMatchInfo;
        }

        public PathContainer getPathRemaining() {
            return this.pathRemaining;
        }

        public Map<String, String> getUriVariables() {
            return this.pathMatchInfo.getUriVariables();
        }

        public Map<String, MultiValueMap<String, String>> getMatrixVariables() {
            return this.pathMatchInfo.getMatrixVariables();
        }
    }

    public static class PathMatchInfo {
        private static final PathMatchInfo EMPTY = new PathMatchInfo(Collections.emptyMap(), Collections.emptyMap());
        private final Map<String, String> uriVariables;
        private final Map<String, MultiValueMap<String, String>> matrixVariables;

        PathMatchInfo(Map<String, String> uriVars, @Nullable Map<String, MultiValueMap<String, String>> matrixVars) {
            this.uriVariables = Collections.unmodifiableMap(uriVars);
            this.matrixVariables = matrixVars != null ? Collections.unmodifiableMap(matrixVars) : Collections.emptyMap();
        }

        public Map<String, String> getUriVariables() {
            return this.uriVariables;
        }

        public Map<String, MultiValueMap<String, String>> getMatrixVariables() {
            return this.matrixVariables;
        }

        public String toString() {
            return "PathMatchInfo[uriVariables=" + this.uriVariables + ", matrixVariables=" + this.matrixVariables + "]";
        }
    }
}

