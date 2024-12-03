/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.UrlMode
 *  com.google.common.base.Preconditions
 */
package com.atlassian.plugins.whitelist.core.matcher;

import com.atlassian.plugins.whitelist.core.matcher.MatcherUtils;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;
import com.google.common.base.Preconditions;
import java.net.URI;
import java.util.function.Predicate;

public class SelfUrlMatcher
implements Predicate<URI> {
    private final ApplicationProperties applicationProperties;

    public SelfUrlMatcher(ApplicationProperties applicationProperties) {
        this.applicationProperties = (ApplicationProperties)Preconditions.checkNotNull((Object)applicationProperties, (Object)"applicationProperties");
    }

    @Override
    public boolean test(URI uri) {
        return MatcherUtils.compare(this.applicationProperties.getBaseUrl(UrlMode.CANONICAL), uri);
    }
}

