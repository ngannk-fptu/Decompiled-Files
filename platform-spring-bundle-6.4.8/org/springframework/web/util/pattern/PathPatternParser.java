/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.util.pattern;

import org.springframework.http.server.PathContainer;
import org.springframework.util.StringUtils;
import org.springframework.web.util.pattern.InternalPathPatternParser;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PatternParseException;

public class PathPatternParser {
    private boolean matchOptionalTrailingSeparator = true;
    private boolean caseSensitive = true;
    private PathContainer.Options pathOptions = PathContainer.Options.HTTP_PATH;
    public static final PathPatternParser defaultInstance = new PathPatternParser(){

        @Override
        public void setMatchOptionalTrailingSeparator(boolean matchOptionalTrailingSeparator) {
            this.raiseError();
        }

        @Override
        public void setCaseSensitive(boolean caseSensitive) {
            this.raiseError();
        }

        @Override
        public void setPathOptions(PathContainer.Options pathOptions) {
            this.raiseError();
        }

        private void raiseError() {
            throw new UnsupportedOperationException("This is a read-only, shared instance that cannot be modified");
        }
    };

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

    public void setPathOptions(PathContainer.Options pathOptions) {
        this.pathOptions = pathOptions;
    }

    public PathContainer.Options getPathOptions() {
        return this.pathOptions;
    }

    public String initFullPathPattern(String pattern) {
        return StringUtils.hasLength(pattern) && !pattern.startsWith("/") ? "/" + pattern : pattern;
    }

    public PathPattern parse(String pathPattern) throws PatternParseException {
        return new InternalPathPatternParser(this).parse(pathPattern);
    }
}

