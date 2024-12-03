/*
 * Decompiled with CFR 0.152.
 */
package org.tuckey.web.filters.urlrewrite.utils;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.tuckey.web.filters.urlrewrite.utils.RegexMatcher;
import org.tuckey.web.filters.urlrewrite.utils.StringMatchingMatcher;
import org.tuckey.web.filters.urlrewrite.utils.StringMatchingPattern;
import org.tuckey.web.filters.urlrewrite.utils.StringMatchingPatternSyntaxException;

public class RegexPattern
implements StringMatchingPattern {
    private Pattern pattern;

    public RegexPattern(String patternStr, boolean caseSensitive) throws StringMatchingPatternSyntaxException {
        try {
            this.pattern = caseSensitive ? Pattern.compile(patternStr) : Pattern.compile(patternStr, 2);
        }
        catch (PatternSyntaxException e) {
            throw new StringMatchingPatternSyntaxException(e);
        }
    }

    public StringMatchingMatcher matcher(String regex) {
        return new RegexMatcher(this.pattern.matcher(regex));
    }
}

