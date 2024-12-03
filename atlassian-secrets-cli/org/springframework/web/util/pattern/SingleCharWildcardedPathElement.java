/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.util.pattern;

import org.springframework.http.server.PathContainer;
import org.springframework.web.util.pattern.PathElement;
import org.springframework.web.util.pattern.PathPattern;

class SingleCharWildcardedPathElement
extends PathElement {
    private final char[] text;
    private final int len;
    private final int questionMarkCount;
    private final boolean caseSensitive;

    public SingleCharWildcardedPathElement(int pos, char[] literalText, int questionMarkCount, boolean caseSensitive, char separator) {
        super(pos, separator);
        this.len = literalText.length;
        this.questionMarkCount = questionMarkCount;
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
        char ch;
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
        char[] data = ((PathContainer.PathSegment)element).valueToMatchAsChars();
        if (this.caseSensitive) {
            for (i = 0; i < this.len; ++i) {
                ch = this.text[i];
                if (ch == '?' || ch == data[i]) continue;
                return false;
            }
        } else {
            for (i = 0; i < this.len; ++i) {
                ch = this.text[i];
                if (ch == '?' || ch == Character.toLowerCase(data[i])) continue;
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
    public int getWildcardCount() {
        return this.questionMarkCount;
    }

    @Override
    public int getNormalizedLength() {
        return this.len;
    }

    public String toString() {
        return "SingleCharWildcarded(" + String.valueOf(this.text) + ")";
    }

    @Override
    public char[] getChars() {
        return this.text;
    }
}

