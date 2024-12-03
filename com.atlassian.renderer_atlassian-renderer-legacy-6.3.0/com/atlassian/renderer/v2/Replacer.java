/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.v2;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Replacer {
    private final Pattern pattern;
    private final String replacement;
    private final String[] necessaryConstantParts;

    public Replacer(Pattern pattern, String replacement, String[] necessaryConstantParts) {
        this.pattern = pattern;
        this.replacement = replacement;
        this.necessaryConstantParts = necessaryConstantParts;
    }

    public String replaceAll(String str) {
        for (int i = 0; i < this.necessaryConstantParts.length; ++i) {
            if (str.indexOf(this.necessaryConstantParts[i]) != -1) continue;
            return str;
        }
        Matcher matcher = this.pattern.matcher(str);
        return matcher.replaceAll(this.replacement);
    }

    public String replace(String str) {
        Matcher matcher = this.pattern.matcher(str);
        return matcher.replaceAll(this.replacement);
    }
}

