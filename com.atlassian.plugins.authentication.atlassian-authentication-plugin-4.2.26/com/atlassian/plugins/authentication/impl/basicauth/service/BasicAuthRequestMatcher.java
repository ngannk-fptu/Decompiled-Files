/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.plugins.authentication.impl.basicauth.service;

import com.atlassian.plugins.authentication.impl.basicauth.BasicAuthConfig;
import com.atlassian.plugins.authentication.impl.basicauth.util.BasicAuthMatcherUtils;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;

public class BasicAuthRequestMatcher {
    private static final int CACHE_SIZE = Integer.getInteger("com.atlassian.plugins.authentication.basic.auth.filter.cache.size", 1000);
    private static final int CACHE_EXPIRY_SECONDS = Integer.getInteger("com.atlassian.plugins.authentication.basic.auth.filter.cache.expiry.seconds", 300);
    private final BasicAuthConfig config;
    private final Set<String> exactAllowedPaths = new HashSet<String>();
    private final Set<String> exactAllowedUsers = new HashSet<String>();
    private final List<String> allowedPathPatterns = new ArrayList<String>();
    private final List<String> allowedUserPatterns = new ArrayList<String>();
    private final Function<String, Boolean> allowedPathsCache;
    private final Function<String, Boolean> allowedUsersCache;

    public BasicAuthRequestMatcher(@Nonnull BasicAuthConfig config) {
        this.config = config;
        this.splitByMatchType(config.getAllowedPaths(), this.exactAllowedPaths, this.allowedPathPatterns);
        this.splitByMatchType(config.getAllowedUsers(), this.exactAllowedUsers, this.allowedUserPatterns);
        this.allowedPathsCache = config.getAllowedPaths().isEmpty() ? this.emptyCache() : this.buildCache(this::calculateIsPathAllowed);
        this.allowedUsersCache = config.getAllowedUsers().isEmpty() ? this.emptyCache() : this.buildCache(this::calculateIsUserAllowed);
    }

    private void splitByMatchType(@Nonnull Iterable<String> allowlist, @Nonnull Collection<String> exactMatchers, @Nonnull Collection<String> patternMatchers) {
        for (String allowlistEntry : allowlist) {
            if (StringUtils.containsAny((CharSequence)allowlistEntry, (char[])BasicAuthMatcherUtils.WILDCARD_CHARACTERS)) {
                patternMatchers.add(allowlistEntry);
                continue;
            }
            exactMatchers.add(allowlistEntry);
        }
    }

    @Nonnull
    private Function<String, Boolean> buildCache(@Nonnull Function<String, Boolean> loader) {
        LoadingCache cache = CacheBuilder.newBuilder().maximumSize((long)CACHE_SIZE).expireAfterAccess(Duration.ofSeconds(CACHE_EXPIRY_SECONDS)).build(CacheLoader.from(loader::apply));
        return arg_0 -> ((LoadingCache)cache).getUnchecked(arg_0);
    }

    @Nonnull
    private Function<String, Boolean> emptyCache() {
        return any -> false;
    }

    public BasicAuthConfig getConfig() {
        return this.config;
    }

    public boolean isBlockRequests() {
        return this.config.isBlockRequests();
    }

    public boolean isPathAllowed(@Nonnull String path) {
        return this.allowedPathsCache.apply(path);
    }

    public boolean isUserAllowed(@Nullable String user) {
        return user != null && this.allowedUsersCache.apply(user) != false;
    }

    @VisibleForTesting
    protected boolean calculateIsPathAllowed(@Nonnull String path) {
        String normalizedPath = BasicAuthMatcherUtils.normalizePath(path);
        return this.exactAllowedPaths.contains(normalizedPath) || this.allowedPathPatterns.stream().anyMatch(pattern -> BasicAuthMatcherUtils.wildcardMatch(normalizedPath, pattern));
    }

    @VisibleForTesting
    protected boolean calculateIsUserAllowed(@Nonnull String user) {
        return this.exactAllowedUsers.contains(user) || this.allowedUserPatterns.stream().anyMatch(pattern -> BasicAuthMatcherUtils.wildcardMatch(user, pattern));
    }
}

