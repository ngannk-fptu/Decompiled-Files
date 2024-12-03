/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.util.pattern;

import org.springframework.web.util.pattern.InternalPathPatternParser;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PatternParseException;

public class PathPatternParser {
    private boolean matchOptionalTrailingSeparator = true;
    private boolean caseSensitive = true;

    public void setMatchOptionalTrailingSeparator(boolean matchOptionalTrailingSeparator) {
        this.matchOptionalTrailingSeparator = matchOptionalTrailingSeparator;
    }

    public boolean isMatchOptionalTrailingSeparator() {
        return this.matchOptionalTrailingSeparator;
    }

    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    public boolean isCaseSensitive() {
        return this.caseSensitive;
    }

    char getSeparator() {
        return '/';
    }

    public PathPattern parse(String pathPattern) throws PatternParseException {
        return new InternalPathPatternParser(this).parse(pathPattern);
    }
}

