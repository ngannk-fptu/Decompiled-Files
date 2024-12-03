/*
 * Decompiled with CFR 0.152.
 */
package org.tuckey.web.filters.urlrewrite.utils;

import org.tuckey.web.filters.urlrewrite.utils.StringMatchingMatcher;
import org.tuckey.web.filters.urlrewrite.utils.StringMatchingPattern;
import org.tuckey.web.filters.urlrewrite.utils.WildcardHelper;
import org.tuckey.web.filters.urlrewrite.utils.WildcardMatcher;

public class WildcardPattern
implements StringMatchingPattern {
    WildcardHelper wh = new WildcardHelper();
    private String patternStr;

    public WildcardPattern(String patternStr) {
        this.patternStr = patternStr;
    }

    public StringMatchingMatcher matcher(String matchStr) {
        return new WildcardMatcher(this.wh, this.patternStr, matchStr);
    }
}

