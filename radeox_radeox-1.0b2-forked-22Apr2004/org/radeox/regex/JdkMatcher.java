/*
 * Decompiled with CFR 0.152.
 */
package org.radeox.regex;

import org.radeox.regex.JdkMatchResult;
import org.radeox.regex.JdkPattern;
import org.radeox.regex.Matcher;
import org.radeox.regex.Pattern;
import org.radeox.regex.Substitution;

public class JdkMatcher
extends Matcher {
    private JdkPattern pattern;
    private String input;
    private java.util.regex.Matcher internalMatcher;

    public String substitute(Substitution substitution) {
        JdkMatchResult matchResult = new JdkMatchResult(this.internalMatcher);
        StringBuffer buffer = new StringBuffer();
        while (this.internalMatcher.find()) {
            this.internalMatcher.appendReplacement(buffer, "");
            substitution.handleMatch(buffer, matchResult);
        }
        this.internalMatcher.appendTail(buffer);
        return buffer.toString();
    }

    public String substitute(String substitution) {
        return this.internalMatcher.replaceAll(substitution);
    }

    protected java.util.regex.Matcher getMatcher() {
        return this.internalMatcher;
    }

    public JdkMatcher(String input, Pattern pattern) {
        this.input = input;
        this.pattern = (JdkPattern)pattern;
        this.internalMatcher = this.pattern.getPattern().matcher(this.input);
    }

    public boolean contains() {
        this.internalMatcher.reset();
        return this.internalMatcher.find();
    }

    public boolean matches() {
        return this.internalMatcher.matches();
    }
}

