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

public class RegularExpressionMatcher
implements Predicate<URI> {
    private final Pattern pattern;

    public RegularExpressionMatcher(String expression) {
        this.pattern = Pattern.compile((String)Preconditions.checkNotNull((Object)expression, (Object)"expression"));
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
}

