/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.UrlMode
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.plugins.navlink.util.url;

import com.atlassian.plugins.navlink.util.url.BaseUrl;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;
import java.net.URI;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;

public class UrlFactory {
    private final ApplicationProperties applicationProperties;

    public UrlFactory(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    public static String toAbsoluteUrl(@Nullable String baseUrl, @Nullable String initialUrl) {
        if (baseUrl == null || initialUrl == null) {
            return null;
        }
        String baseUrlWithoutTrailingSlash = StringUtils.stripEnd((String)baseUrl, (String)"/");
        if (!initialUrl.startsWith("http://") && !initialUrl.startsWith("https://")) {
            return baseUrlWithoutTrailingSlash + initialUrl;
        }
        return initialUrl;
    }

    public String toAbsoluteUrl(@Nullable String initialUrl) {
        return UrlFactory.toAbsoluteUrl(this.applicationProperties.getBaseUrl(UrlMode.CANONICAL), initialUrl);
    }

    @Nonnull
    public BaseUrl getCanonicalBaseUrl() {
        return new BaseUrl(this.applicationProperties.getBaseUrl(UrlMode.CANONICAL));
    }

    public String toRelativeUrlWithContextPath(String relativeUrl) {
        if (relativeUrl == null) {
            return null;
        }
        if (relativeUrl.startsWith("http://") || relativeUrl.startsWith("https://")) {
            return relativeUrl;
        }
        String baseUrl = this.getCanonicalBaseUrl().getBaseUrl();
        URI uri = URI.create(baseUrl);
        return StringUtils.stripEnd((String)uri.getPath(), (String)"/") + "/" + StringUtils.stripStart((String)relativeUrl, (String)"/");
    }
}

