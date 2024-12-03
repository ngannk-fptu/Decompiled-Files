/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.plugin.Plugin
 */
package com.atlassian.plugin.predicate;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.util.RegularExpressions;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ExperimentalApi
public class PluginKeyPatternsPredicate
implements Predicate<Plugin> {
    private final MatchType matchType;
    private final Pattern pattern;

    public PluginKeyPatternsPredicate(MatchType matchType, Collection<String> parts) {
        this.matchType = matchType;
        this.pattern = Pattern.compile(matchType.buildRegularExpression(parts));
    }

    @Override
    public boolean test(Plugin plugin) {
        return this.matchType.processMatcher(this.pattern.matcher(plugin.getKey()));
    }

    public static enum MatchType {
        MATCHES_ANY{

            @Override
            public String buildRegularExpression(Collection<String> parts) {
                return RegularExpressions.anyOf(parts);
            }

            @Override
            public boolean processMatcher(Matcher matcher) {
                return matcher.matches();
            }
        }
        ,
        MATCHES_NONE{

            @Override
            public String buildRegularExpression(Collection<String> parts) {
                return RegularExpressions.anyOf(parts);
            }

            @Override
            public boolean processMatcher(Matcher matcher) {
                return !matcher.matches();
            }
        };


        public abstract String buildRegularExpression(Collection<String> var1);

        public abstract boolean processMatcher(Matcher var1);
    }
}

