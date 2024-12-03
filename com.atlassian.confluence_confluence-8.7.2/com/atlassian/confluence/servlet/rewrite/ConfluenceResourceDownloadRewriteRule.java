/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.StringUtils
 *  org.tuckey.web.filters.urlrewrite.extend.RewriteMatch
 *  org.tuckey.web.filters.urlrewrite.extend.RewriteRule
 */
package com.atlassian.confluence.servlet.rewrite;

import com.atlassian.confluence.servlet.rewrite.CachedRewriteMatch;
import com.atlassian.confluence.servlet.rewrite.DisableCacheRewriteMatch;
import com.atlassian.confluence.util.HtmlUtil;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.tuckey.web.filters.urlrewrite.extend.RewriteMatch;
import org.tuckey.web.filters.urlrewrite.extend.RewriteRule;

public class ConfluenceResourceDownloadRewriteRule
extends RewriteRule {
    private static final Pattern NO_CACHE_PATTERN = Pattern.compile("^/s/(.*)/NOCACHE(.*)/_/(.*)");
    private static final Pattern CACHE_PATTERN = Pattern.compile("^/s/(.*)/_/(.*)");
    private static final Pattern PATHS_DENIED = Pattern.compile("((?i)(/WEB-INF/)|(/META-INF/))");
    public static final String DOWNLOAD_IMAGES = "download/images";
    public static final String IMAGES = "images";

    public RewriteMatch matches(HttpServletRequest request, HttpServletResponse response) {
        String url;
        try {
            url = this.getNormalisedPathFrom(request);
        }
        catch (URISyntaxException invalidUriInRequest) {
            return null;
        }
        if (this.isPathDenied(url)) {
            return null;
        }
        Matcher noCacheMatcher = NO_CACHE_PATTERN.matcher(url);
        Matcher cacheMatcher = CACHE_PATTERN.matcher(url);
        if (noCacheMatcher.matches()) {
            String rewrittenContextUrl = "/" + this.rewritePathMappings(noCacheMatcher.group(3));
            String rewrittenUrl = request.getContextPath() + rewrittenContextUrl;
            return new DisableCacheRewriteMatch(rewrittenUrl, rewrittenContextUrl);
        }
        if (cacheMatcher.matches()) {
            String rewrittenContextUrl = "/" + this.rewritePathMappings(cacheMatcher.group(2));
            String rewrittenUrl = request.getContextPath() + rewrittenContextUrl;
            return new CachedRewriteMatch(rewrittenUrl, rewrittenContextUrl, cacheMatcher.group(1));
        }
        return null;
    }

    private String rewritePathMappings(String path) {
        if (StringUtils.startsWith((CharSequence)path, (CharSequence)DOWNLOAD_IMAGES)) {
            return StringUtils.replaceOnce((String)path, (String)DOWNLOAD_IMAGES, (String)IMAGES);
        }
        return path;
    }

    private String getNormalisedPathFrom(HttpServletRequest request) throws URISyntaxException {
        String encodedURL = this.stripContextFrom(request);
        String decodedUri = this.decodeURL(encodedURL);
        return new URI(decodedUri).normalize().toString();
    }

    private String decodeURL(String url) {
        String decodedUri = HtmlUtil.urlDecode(url);
        while (HtmlUtil.shouldUrlDecode(decodedUri)) {
            decodedUri = HtmlUtil.urlDecode(decodedUri);
        }
        return decodedUri;
    }

    private String stripContextFrom(HttpServletRequest request) {
        return request.getRequestURI().substring(request.getContextPath().length());
    }

    private boolean isPathDenied(String normalisedUrl) {
        return PATHS_DENIED.matcher(normalisedUrl).find();
    }
}

