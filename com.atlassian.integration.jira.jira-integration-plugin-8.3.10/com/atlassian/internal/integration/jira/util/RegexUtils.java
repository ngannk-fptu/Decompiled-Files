/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.collect.ArrayListMultimap
 *  com.google.common.collect.Multimap
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.internal.integration.jira.util;

import com.google.common.base.Function;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RegexUtils {
    public static Multimap<String, MatchResult> findMatches(@Nonnull CharSequence input, @Nonnull Pattern includePattern, @Nullable Pattern excludePattern) {
        boolean hasExclusion;
        ArrayListMultimap matches = ArrayListMultimap.create();
        Matcher matcher = includePattern.matcher(input);
        Matcher excluder = excludePattern != null ? excludePattern.matcher(input) : null;
        boolean hasMatch = matcher.find();
        boolean bl = hasExclusion = excluder != null && excluder.find();
        while (hasMatch) {
            if (hasExclusion) {
                if (matcher.start() >= excluder.end()) {
                    hasExclusion = excluder.find();
                    continue;
                }
                if (matcher.end() > excluder.start()) {
                    hasMatch = matcher.find();
                    continue;
                }
            }
            matches.put((Object)matcher.group(1), (Object)matcher.toMatchResult());
            hasMatch = matcher.find();
        }
        return matches;
    }

    public static Function<MatchResult, String> group(final int n) {
        return new Function<MatchResult, String>(){

            public String apply(MatchResult matchResult) {
                return matchResult.group(n);
            }
        };
    }
}

