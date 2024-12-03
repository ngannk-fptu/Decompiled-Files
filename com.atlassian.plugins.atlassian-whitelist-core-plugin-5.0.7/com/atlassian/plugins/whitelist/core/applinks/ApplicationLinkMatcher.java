/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ReadOnlyApplicationLink
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Predicate
 */
package com.atlassian.plugins.whitelist.core.applinks;

import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import com.atlassian.plugins.whitelist.core.matcher.MatcherUtils;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import java.net.URI;

public class ApplicationLinkMatcher
implements Predicate<URI> {
    private URI applinkRpcUrl;

    public ApplicationLinkMatcher(ReadOnlyApplicationLink applicationLink) {
        this.applinkRpcUrl = ((ReadOnlyApplicationLink)Preconditions.checkNotNull((Object)applicationLink, (Object)"applicationLink")).getRpcUrl();
    }

    public boolean apply(URI uri) {
        return MatcherUtils.compare(this.applinkRpcUrl, uri);
    }
}

