/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.sal.api.web.context.HttpContext
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.audit.rest.v1;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.sal.api.web.context.HttpContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DelegatedViewTypeProvider {
    public static final String GLOBAL = "global";
    public static final String REFERER_HEADER = "referer";
    public static final Pattern SERVLET_AUDIT_DELEGATED_URL_PATTERN = Pattern.compile(".*[/]servlet[/]audit[/]resource[/](\\w+),.*");
    public static final Pattern SERVLET_AUDIT_GLOBAL_URL_PATTERN = Pattern.compile(".*[/]servlet[/]audit.*");

    @Nonnull
    public String getDelegatedViewType(@Nullable HttpContext httpContext) {
        if (httpContext == null || httpContext.getRequest() == null) {
            return "";
        }
        String refererHeader = httpContext.getRequest().getHeader(REFERER_HEADER);
        return this.getDelegatedViewTypeFromUrl(refererHeader);
    }

    @Nonnull
    @VisibleForTesting
    public String getDelegatedViewTypeFromUrl(@Nullable String url) {
        if (url != null) {
            Matcher matcher = SERVLET_AUDIT_DELEGATED_URL_PATTERN.matcher(url);
            if (matcher.matches()) {
                return matcher.group(1);
            }
            if (SERVLET_AUDIT_GLOBAL_URL_PATTERN.matcher(url).matches()) {
                return GLOBAL;
            }
        }
        return "";
    }
}

