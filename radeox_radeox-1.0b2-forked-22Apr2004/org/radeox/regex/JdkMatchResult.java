/*
 * Decompiled with CFR 0.152.
 */
package org.radeox.regex;

import org.radeox.regex.JdkMatcher;
import org.radeox.regex.MatchResult;
import org.radeox.regex.Matcher;

public class JdkMatchResult
extends MatchResult {
    private java.util.regex.Matcher matcher;

    public JdkMatchResult(java.util.regex.Matcher matcher) {
        this.matcher = matcher;
    }

    public JdkMatchResult(Matcher matcher) {
        this.matcher = ((JdkMatcher)matcher).getMatcher();
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

