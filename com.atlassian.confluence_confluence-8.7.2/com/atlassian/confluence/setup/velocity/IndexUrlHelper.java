/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Stopwatch
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.setup.velocity;

import com.atlassian.confluence.core.ContextPathHolder;
import com.atlassian.confluence.impl.homepage.HomepageService;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.google.common.base.Stopwatch;
import java.net.URI;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IndexUrlHelper {
    private static final Logger log = LoggerFactory.getLogger(IndexUrlHelper.class);
    private final HomepageService homepageService;
    private final ContextPathHolder contextPathHolder;

    public IndexUrlHelper(HomepageService homepageService, ContextPathHolder contextPathHolder) {
        this.homepageService = Objects.requireNonNull(homepageService);
        this.contextPathHolder = Objects.requireNonNull(contextPathHolder);
    }

    public @NonNull String getIndexUrl() {
        Stopwatch stopwatch = Stopwatch.createStarted();
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        URI relativeDeepLinkUri = this.homepageService.getHomepage(user).getDeepLinkUri();
        String indexUrl = this.contextPathHolder.getContextPath() + relativeDeepLinkUri.toString();
        log.debug("Generated index URL [{}] for user [{}] in {}ms", new Object[]{indexUrl, user, stopwatch.elapsed(TimeUnit.MILLISECONDS)});
        return indexUrl;
    }
}

