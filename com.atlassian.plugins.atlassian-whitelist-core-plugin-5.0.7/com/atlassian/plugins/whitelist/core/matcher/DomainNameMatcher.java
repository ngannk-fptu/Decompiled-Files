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

public class DomainNameMatcher
implements Predicate<URI> {
    private final String expression;

    public DomainNameMatcher(String expression) {
        this.expression = (String)Preconditions.checkNotNull((Object)expression, (Object)"expression");
    }

    @Override
    public boolean test(URI uri) {
        try {
            URI normalizedUri = MatcherUtils.normalizeUri(uri);
            return this.expression.equalsIgnoreCase(normalizedUri.getScheme() + "://" + normalizedUri.getAuthority());
        }
        catch (URISyntaxException e) {
            return false;
        }
    }
}

