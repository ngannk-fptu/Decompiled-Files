/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.util.pattern;

import org.springframework.web.util.pattern.PathElement;
import org.springframework.web.util.pattern.PathPattern;

class SeparatorPathElement
extends PathElement {
    SeparatorPathElement(int pos, char separator) {
        super(pos, separator);
    }

    @Override
    public boolean matches(int pathIndex, PathPattern.MatchingContext matchingContext) {
        if (pathIndex < matchingContext.pathLength && matchingContext.isSeparator(pathIndex)) {
            if (this.isNoMorePattern()) {
                if (matchingContext.determineRemainingPath) {
                    matchingContext.remainingPathIndex = pathIndex + 1;
                    return true;
                }
                return pathIndex + 1 == matchingContext.pathLength;
            }
            return this.next != null && this.next.matches(++pathIndex, matchingContext);
        }
        return false;
    }

    @Override
    public int getNormalizedLength() {
        return 1;
    }

    @Override
    public char[] getChars() {
        return new char[]{this.separator};
    }

    @Override
    public boolean isLiteral() {
        return true;
    }

    public String toString() {
        return "Separator(" + this.separator + ")";
    }
}

