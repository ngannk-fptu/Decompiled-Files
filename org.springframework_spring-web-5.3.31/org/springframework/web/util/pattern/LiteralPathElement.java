/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.util.pattern;

import org.springframework.http.server.PathContainer;
import org.springframework.web.util.pattern.PathElement;
import org.springframework.web.util.pattern.PathPattern;

class LiteralPathElement
extends PathElement {
    private final char[] text;
    private final int len;
    private final boolean caseSensitive;

    public LiteralPathElement(int pos, char[] literalText, boolean caseSensitive, char separator) {
        super(pos, separator);
        this.len = literalText.length;
        this.caseSensitive = caseSensitive;
        if (caseSensitive) {
            this.text = literalText;
        } else {
            this.text = new char[literalText.length];
            for (int i = 0; i < this.len; ++i) {
                this.text[i] = Character.toLowerCase(literalText[i]);
            }
        }
    }

    @Override
    public boolean matches(int pathIndex, PathPattern.MatchingContext matchingContext) {
        int i;
        if (pathIndex >= matchingContext.pathLength) {
            return false;
        }
        PathContainer.Element element = matchingContext.pathElements.get(pathIndex);
        if (!(element instanceof PathContainer.PathSegment)) {
            return false;
        }
        String value = ((PathContainer.PathSegment)element).valueToMatch();
        if (value.length() != this.len) {
            return false;
        }
        if (this.caseSensitive) {
            for (i = 0; i < this.len; ++i) {
                if (value.charAt(i) == this.text[i]) continue;
                return false;
            }
        } else {
            for (i = 0; i < this.len; ++i) {
                if (Character.toLowerCase(value.charAt(i)) == this.text[i]) continue;
                return false;
            }
        }
        ++pathIndex;
        if (this.isNoMorePattern()) {
            if (matchingContext.determineRemainingPath) {
                matchingContext.remainingPathIndex = pathIndex;
                return true;
            }
            if (pathIndex == matchingContext.pathLength) {
                return true;
            }
            return matchingContext.isMatchOptionalTrailingSeparator() && pathIndex + 1 == matchingContext.pathLength && matchingContext.isSeparator(pathIndex);
        }
        return this.next != null && this.next.matches(pathIndex, matchingContext);
    }

    @Override
    public int getNormalizedLength() {
        return this.len;
    }

    @Override
    public char[] getChars() {
        return this.text;
    }

    @Override
    public boolean isLiteral() {
        return true;
    }

    public String toString() {
        return "Literal(" + String.valueOf(this.text) + ")";
    }
}

