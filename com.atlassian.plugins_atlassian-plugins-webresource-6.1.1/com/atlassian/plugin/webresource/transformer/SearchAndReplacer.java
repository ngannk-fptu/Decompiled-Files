/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.webresource.transformer;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Deprecated
public class SearchAndReplacer {
    private final Pattern pattern;
    private final Function<Matcher, CharSequence> replacer;

    SearchAndReplacer(Pattern pattern, Function<Matcher, CharSequence> replacer) {
        this.pattern = pattern;
        this.replacer = replacer;
    }

    public static SearchAndReplacer create(Pattern pattern, Function<Matcher, CharSequence> replacer) {
        return new SearchAndReplacer(pattern, replacer);
    }

    public CharSequence replaceAll(CharSequence input) {
        Matcher matcher = this.pattern.matcher(input);
        StringBuffer output = new StringBuffer();
        while (matcher.find()) {
            CharSequence sequence = this.replacer.apply(matcher);
            matcher.appendReplacement(output, "");
            output.append(sequence);
        }
        matcher.appendTail(output);
        return output;
    }
}

