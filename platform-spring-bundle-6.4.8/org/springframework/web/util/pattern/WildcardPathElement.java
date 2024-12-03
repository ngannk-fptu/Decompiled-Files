/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.util.pattern;

import org.springframework.http.server.PathContainer;
import org.springframework.web.util.pattern.PathElement;
import org.springframework.web.util.pattern.PathPattern;

class WildcardPathElement
extends PathElement {
    public WildcardPathElement(int pos, char separator) {
        super(pos, separator);
    }

    @Override
    public boolean matches(int pathIndex, PathPattern.MatchingContext matchingContext) {
        String segmentData = null;
        if (pathIndex < matchingContext.pathLength) {
            PathContainer.Element element = matchingContext.pathElements.get(pathIndex);
            if (!(element instanceof PathContainer.PathSegment)) {
                return false;
            }
            segmentData = ((PathContainer.PathSegment)element).valueToMatch();
            ++pathIndex;
        }
        if (this.isNoMorePattern()) {
            if (matchingContext.determineRemainingPath) {
                matchingContext.remainingPathIndex = pathIndex;
                return true;
            }
            if (pathIndex == matchingContext.pathLength) {
                return true;
            }
            return matchingContext.isMatchOptionalTrailingSeparator() && segmentData != null && segmentData.length() > 0 && pathIndex + 1 == matchingContext.pathLength && matchingContext.isSeparator(pathIndex);
        }
        if (segmentData == null || segmentData.length() == 0) {
            return false;
        }
        return this.next != null && this.next.matches(pathIndex, matchingContext);
    }

    @Override
    public int getNormalizedLength() {
        return 1;
    }

    @Override
    public char[] getChars() {
        return new char[]{'*'};
    }

    @Override
    public int getWildcardCount() {
        return 1;
    }

    @Override
    public int getScore() {
        return 100;
    }

    public String toString() {
        return "Wildcard(*)";
    }
}

