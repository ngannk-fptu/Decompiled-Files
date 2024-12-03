/*
 * Decompiled with CFR 0.152.
 */
package org.tuckey.web.filters.urlrewrite.utils;

import java.util.regex.Matcher;
import org.tuckey.web.filters.urlrewrite.utils.StringMatchingMatcher;

public class RegexMatcher
implements StringMatchingMatcher {
    private Matcher matcher;
    private boolean found = false;

    public RegexMatcher(Matcher matcher) {
        this.matcher = matcher;
    }

    public boolean find() {
        this.found = this.matcher.find();
        return this.found;
    }

    public boolean isFound() {
        return this.found;
    }

    public void reset() {
        this.matcher.reset();
        this.found = false;
    }

    public String replaceAll(String replacement) {
        String replaced = this.matcher.replaceAll(replacement);
        this.reset();
        return replaced;
    }

    public int groupCount() {
        return this.matcher.groupCount();
    }

    public String group(int groupId) {
        return this.matcher.group(groupId);
    }

    public int end() {
        return this.matcher.end();
    }

    public int start() {
        return this.matcher.start();
    }

    public boolean isMultipleMatchingSupported() {
        return true;
    }
}

