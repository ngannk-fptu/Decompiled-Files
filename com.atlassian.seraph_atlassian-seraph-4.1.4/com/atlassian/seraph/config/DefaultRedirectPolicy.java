/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.lang3.StringEscapeUtils
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.seraph.config;

import com.atlassian.seraph.config.RedirectPolicy;
import com.atlassian.seraph.config.SecurityConfig;
import com.atlassian.seraph.util.RedirectUtils;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

public class DefaultRedirectPolicy
implements RedirectPolicy {
    private static final Pattern MORE_THAN_2_LEADING_SLASHES = Pattern.compile("^///+");
    private static final Pattern PROTOCOL_PATTERN = Pattern.compile("^.*:");
    private static final Pattern PROTOCOL_PATTERN_WITH_SLASHES = Pattern.compile("^.*://");
    private boolean allowAnyUrl = false;

    @Override
    public void init(Map<String, String> params, SecurityConfig config) {
        if (params == null) {
            throw new IllegalArgumentException("params is not allowed to be null");
        }
        this.allowAnyUrl = "true".equals(params.get("allow.any.redirect.url"));
    }

    public boolean isAllowAnyUrl() {
        return this.allowAnyUrl;
    }

    @Override
    public boolean allowedRedirectDestination(String redirectUrl, HttpServletRequest request) {
        URI uri;
        if (this.allowAnyUrl) {
            return true;
        }
        String foldedSlashes = this.foldLeadingSlashes(StringEscapeUtils.unescapeHtml4((String)redirectUrl), request.getScheme());
        try {
            uri = new URI(StringUtils.substringBefore((String)foldedSlashes, (String)"?"));
        }
        catch (IllegalArgumentException | URISyntaxException e) {
            return false;
        }
        return uri.getScheme() == null && uri.getHost() == null || RedirectUtils.sameContext(uri.toString(), request);
    }

    private String foldLeadingSlashes(String url, String scheme) {
        Matcher protocolMatcher = PROTOCOL_PATTERN.matcher(url);
        if (protocolMatcher.find()) {
            return protocolMatcher.group() + this.tryRemoveExcessSlashes(url.substring(protocolMatcher.end()), "");
        }
        return this.tryRemoveExcessSlashes(url, scheme);
    }

    private String tryRemoveExcessSlashes(String url, String scheme) {
        Matcher leadingSlashesMatcher = MORE_THAN_2_LEADING_SLASHES.matcher(url);
        if (leadingSlashesMatcher.find()) {
            String part = leadingSlashesMatcher.replaceFirst("//");
            return this.appendSchemeIfRequired(scheme, part);
        }
        return this.appendSchemeIfRequired(scheme, url);
    }

    private String appendSchemeIfRequired(String scheme, String part) {
        if (scheme.length() == 0 || StringUtils.isNotBlank((CharSequence)this.getScheme(part))) {
            return part;
        }
        if (part.startsWith(":")) {
            return scheme + part;
        }
        if (part.startsWith("//")) {
            return scheme + ":" + part;
        }
        return part;
    }

    private String getScheme(String url) {
        Matcher protocolMatcher = PROTOCOL_PATTERN_WITH_SLASHES.matcher(url);
        if (protocolMatcher.find()) {
            String protocolWithSlashes = protocolMatcher.group();
            return protocolWithSlashes.substring(0, protocolWithSlashes.length() - 3);
        }
        return null;
    }
}

