/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.util.pattern;

import org.springframework.web.util.pattern.PathElement;
import org.springframework.web.util.pattern.PathPattern;

class WildcardTheRestPathElement
extends PathElement {
    WildcardTheRestPathElement(int pos, char separator) {
        super(pos, separator);
    }

    @Override
    public boolean matches(int pathIndex, PathPattern.MatchingContext matchingContext) {
        if (pathIndex < matchingContext.pathLength && !matchingContext.isSeparator(pathIndex)) {
            return false;
        }
        if (matchingContext.determineRemainingPath) {
            matchingContext.remainingPathIndex = matchingContext.pathLength;
        }
        return true;
    }

    @Override
    public int getNormalizedLength() {
        return 1;
    }

    @Override
    public char[] getChars() {
        return (this.separator + "**").toCharArray();
    }

    @Override
    public int getWildcardCount() {
        return 1;
    }

    public String toString() {
        return "WildcardTheRest(" + this.separator + "**)";
    }
}

