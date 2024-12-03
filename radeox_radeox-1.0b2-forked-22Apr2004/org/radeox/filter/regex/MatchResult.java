/*
 * Decompiled with CFR 0.152.
 */
package org.radeox.filter.regex;

import java.util.regex.Matcher;

public class MatchResult {
    private Matcher matcher;

    public MatchResult(Matcher matcher) {
        this.matcher = matcher;
    }

    public int groups() {
        return this.matcher.groupCount();
    }

    public String group(int i) {
        return this.matcher.group(i);
    }

    public int beginOffset(int i) {
        return this.matcher.start(i);
    }

    public int endOffset(int i) {
        return this.matcher.end(i);
    }
}

