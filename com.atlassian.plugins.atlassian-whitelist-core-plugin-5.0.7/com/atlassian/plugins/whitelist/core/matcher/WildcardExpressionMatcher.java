/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.atlassian.plugins.whitelist.core.matcher;

import com.atlassian.plugins.whitelist.core.matcher.MatcherUtils;
import com.google.common.base.Preconditions;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class WildcardExpressionMatcher
implements Predicate<URI> {
    private static final String[] RULE_ESCAPE_CHARACTERS = new String[]{".", "?", "+", "|", "[", "]"};
    private static final String WILDCARD_CHARACTER_PATTERN = ".*";
    private final Pattern pattern;

    public WildcardExpressionMatcher(String expression) {
        this.pattern = Pattern.compile(WildcardExpressionMatcher.createRegex((String)Preconditions.checkNotNull((Object)expression, (Object)"expression")), 2);
    }

    @Override
    public boolean test(URI uri) {
        try {
            URI normalizedUri = MatcherUtils.normalizeUri(uri);
            return this.pattern.matcher(normalizedUri.toString()).matches();
        }
        catch (URISyntaxException e) {
            return false;
        }
    }

    private static String createRegex(String rule) {
        String regex = rule;
        for (String escapeChar : RULE_ESCAPE_CHARACTERS) {
            regex = regex.replaceAll("\\" + escapeChar, "\\\\" + escapeChar);
        }
        return regex.replaceAll("\\*", WILDCARD_CHARACTER_PATTERN);
    }
}

