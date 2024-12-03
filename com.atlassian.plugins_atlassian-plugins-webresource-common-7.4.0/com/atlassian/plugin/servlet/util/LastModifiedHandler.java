/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.BooleanUtils
 */
package com.atlassian.plugin.servlet.util;

import com.atlassian.plugin.servlet.cache.model.CacheableRequest;
import com.atlassian.plugin.servlet.util.date.DateUtil;
import com.google.common.annotations.VisibleForTesting;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.BooleanUtils;

public class LastModifiedHandler {
    @VisibleForTesting
    static final String ATLASSIAN_DISABLE_CACHES_PROPERTY = "atlassian.disable.caches";
    private LocalDateTime lastModified;

    public LastModifiedHandler() {
        this(new Date());
    }

    public LastModifiedHandler(@Nullable LocalDateTime lastModifiedDate) {
        this.lastModified = lastModifiedDate;
    }

    public LastModifiedHandler(@Nullable Date lastModifiedDate) {
        this((LocalDateTime)DateUtil.localDateTimeOf(lastModifiedDate).orElse(null));
    }

    @Deprecated
    public static boolean checkRequest(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull Date lastModifiedDate) {
        LocalDateTime lastModified = DateUtil.defaultIfNull(lastModifiedDate, LocalDateTime.now());
        LastModifiedHandler.setCacheHeadersIfCacheable(request, response, lastModified);
        return LastModifiedHandler.isCacheableResponse(new CacheableRequest(request), lastModified);
    }

    private static boolean isCacheEnabled() {
        return Optional.ofNullable(System.getProperty(ATLASSIAN_DISABLE_CACHES_PROPERTY)).map(BooleanUtils::toBoolean).filter(Boolean.FALSE::equals).orElse(Boolean.TRUE);
    }

    private static boolean isCacheableResponse(CacheableRequest cachingInformation, LocalDateTime lastModifiedDate) {
        return LastModifiedHandler.isCacheEnabled() && Objects.nonNull(lastModifiedDate) && cachingInformation.isCacheable(lastModifiedDate) && !cachingInformation.getIfNoneMatchHeader().isPresent();
    }

    public static void setCacheHeadersIfCacheable(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull LocalDateTime lastModifiedDate) {
        CacheableRequest cachingInformation = new CacheableRequest(request);
        if (LastModifiedHandler.isCacheableResponse(cachingInformation, lastModifiedDate)) {
            response.setStatus(304);
        }
        if (Objects.nonNull(lastModifiedDate)) {
            CacheableRequest cacheableRequest = new CacheableRequest(request);
            cacheableRequest.setPluginLastModifiedDate(lastModifiedDate);
            response.setDateHeader("Last-Modified", lastModifiedDate.toEpochSecond(ZoneOffset.UTC));
        }
    }

    @Deprecated
    public boolean checkRequest(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response) {
        this.setCacheHeadersIfCacheable(request, response);
        return LastModifiedHandler.isCacheableResponse(new CacheableRequest(request), this.lastModified);
    }

    public boolean isNotCacheableResponse(@Nonnull HttpServletRequest request) {
        return !LastModifiedHandler.isCacheableResponse(new CacheableRequest(request), this.lastModified);
    }

    public void setCacheHeadersIfCacheable(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response) {
        LastModifiedHandler.setCacheHeadersIfCacheable(request, response, this.lastModified);
    }

    public void modified() {
        this.lastModified = LocalDateTime.now();
    }
}

