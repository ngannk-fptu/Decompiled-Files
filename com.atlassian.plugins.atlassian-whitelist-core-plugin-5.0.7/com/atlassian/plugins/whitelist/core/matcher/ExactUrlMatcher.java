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

public class ExactUrlMatcher
implements Predicate<URI> {
    private final URI expression;

    public ExactUrlMatcher(String expression) {
        this.expression = MatcherUtils.normalizeUriUnchecked(URI.create((String)Preconditions.checkNotNull((Object)expression, (Object)"expression")));
    }

    @Override
    public boolean test(URI uri) {
        try {
            URI normalizedUri = MatcherUtils.normalizeUri(uri);
            return this.expression.equals(normalizedUri);
        }
        catch (URISyntaxException e) {
            return false;
        }
    }
}

