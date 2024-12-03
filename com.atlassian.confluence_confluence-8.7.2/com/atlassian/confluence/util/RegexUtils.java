/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtils {
    public static Collection<String> getMatches(String pattern, String text) {
        Pattern p = Pattern.compile(pattern);
        return RegexUtils.getMatches(p, text);
    }

    public static Collection<String> getMatches(Pattern p, String text) {
        Matcher m = p.matcher(text);
        ArrayList<String> result = new ArrayList<String>();
        while (m.find()) {
            result.add(m.group(0));
        }
        return result;
    }

    public static String quoteReplacement(String replacementText) {
        return replacementText.replaceAll("\\\\", "\\\\\\\\").replaceAll("\\$", "\\\\\\$");
    }
}

