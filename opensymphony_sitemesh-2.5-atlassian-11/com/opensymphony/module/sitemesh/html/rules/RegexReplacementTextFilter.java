/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.sitemesh.html.rules;

import com.opensymphony.module.sitemesh.html.TextFilter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexReplacementTextFilter
implements TextFilter {
    private final Pattern regex;
    private final String replacement;

    public RegexReplacementTextFilter(String regex, String replacement) {
        this.regex = Pattern.compile(regex);
        this.replacement = replacement;
    }

    public RegexReplacementTextFilter(Pattern regex, String replacement) {
        this.regex = regex;
        this.replacement = replacement;
    }

    public String filter(String text) {
        Matcher matcher = this.regex.matcher(text);
        return matcher.replaceAll(this.replacement);
    }
}

