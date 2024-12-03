/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletRequestWrapper
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.plugin.servlet.cache.model;

import com.atlassian.plugin.servlet.cache.model.CacheableResponse;
import com.atlassian.plugin.servlet.cache.model.ETagToken;
import com.google.common.annotations.VisibleForTesting;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import org.apache.commons.lang3.StringUtils;

public class CacheableRequest
extends HttpServletRequestWrapper {
    @VisibleForTesting
    static final long EMPTY_MODIFIED_SINCE_HEADER = -1L;
    @VisibleForTesting
    static final String PLUGIN_LAST_MODIFIED_DATE = "Plugin-Last-Modified-Date";
    private static final String DOUBLE_QUOTE = "\"";
    private static final String IF_MODIFIED_SINCE = "If-Modified-Since";
    private static final String IF_NONE_MATCH = "If-None-Match";
    private final HttpServletRequest request;

    public CacheableRequest(@Nonnull HttpServletRequest request) {
        super(request);
        this.request = request;
    }

    public boolean isCacheable(@Nonnull LocalDateTime pluginLastModifiedDate) {
        long ifModifiedSinceHeader = this.request.getDateHeader(IF_MODIFIED_SINCE);
        long pluginLastModifiedSeconds = pluginLastModifiedDate.toEpochSecond(ZoneOffset.UTC);
        return -1L != ifModifiedSinceHeader && ifModifiedSinceHeader >= pluginLastModifiedSeconds;
    }

    public boolean isCacheable(@Nonnull CacheableResponse response) {
        if (this.getIfNoneMatchHeader().isPresent()) {
            return response.toETagToken().map(ETagToken::getValue).equals(this.getIfNoneMatchHeader());
        }
        return Optional.ofNullable(this.request.getAttribute(PLUGIN_LAST_MODIFIED_DATE)).map(pluginLastModifiedDate -> (LocalDateTime)pluginLastModifiedDate).map(this::isCacheable).orElse(false);
    }

    @Nonnull
    public Optional<String> getIfNoneMatchHeader() {
        return Optional.ofNullable(this.request.getHeader(IF_NONE_MATCH)).map(ifNoneMatchHeader -> ifNoneMatchHeader.replace(DOUBLE_QUOTE, "")).filter(StringUtils::isNotEmpty);
    }

    public void setPluginLastModifiedDate(@Nullable LocalDateTime pluginLastModifiedDate) {
        Optional.ofNullable(pluginLastModifiedDate).ifPresent(lastModifiedDate -> this.getRequest().setAttribute(PLUGIN_LAST_MODIFIED_DATE, lastModifiedDate));
    }
}

