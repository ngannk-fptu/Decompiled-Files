/*
 * Decompiled with CFR 0.152.
 */
package org.tuckey.web.filters.urlrewrite;

import org.tuckey.web.filters.urlrewrite.utils.StringMatchingMatcher;

public class ConditionMatch {
    private StringMatchingMatcher matcher;

    public StringMatchingMatcher getMatcher() {
        return this.matcher;
    }

    public void setMatcher(StringMatchingMatcher matcher) {
        this.matcher = matcher;
    }
}

