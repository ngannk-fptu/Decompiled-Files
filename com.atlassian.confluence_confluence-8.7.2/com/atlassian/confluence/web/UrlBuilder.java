/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  org.apache.commons.lang3.StringUtils
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.springframework.util.Assert
 */
package com.atlassian.confluence.web;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.util.HtmlUtil;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.util.Assert;

@ParametersAreNonnullByDefault
public final class UrlBuilder {
    private final StringBuilder url;
    private final String encoding;
    private String anchor;
    private boolean hasQueryString = false;

    public static @NonNull URL createURL(String urlString) {
        try {
            return new URL(urlString);
        }
        catch (MalformedURLException e) {
            throw new IllegalArgumentException("The provided string is not a valid URL: '" + urlString + "'", e);
        }
    }

    public UrlBuilder(String baseUrl) {
        this(baseUrl, "UTF-8");
    }

    public UrlBuilder(String baseUrl, String encoding) {
        int anchorPos;
        this.encoding = encoding;
        if (baseUrl.contains("?")) {
            this.hasQueryString = true;
        }
        if ((anchorPos = baseUrl.indexOf(35)) != -1) {
            if (anchorPos + 1 < baseUrl.length()) {
                this.anchor = baseUrl.substring(anchorPos + 1);
            }
            this.url = new StringBuilder(baseUrl.substring(0, anchorPos));
        } else {
            this.url = new StringBuilder(baseUrl);
        }
    }

    public UrlBuilder(String baseUrl, Charset encoding) {
        this(baseUrl, encoding.name());
    }

    public UrlBuilder add(String name, String value) throws IllegalArgumentException {
        Assert.notNull((Object)name, (String)"parameter name cannot be null");
        Assert.notNull((Object)value, (String)"parameter value cannot be null");
        this.url.append(this.hasQueryString ? (char)'&' : '?');
        this.hasQueryString = true;
        this.url.append(this.urlEncode(name)).append('=').append(this.urlEncode(value));
        return this;
    }

    public UrlBuilder add(String name, String[] values) {
        Assert.notNull((Object)name, (String)"parameter name cannot be null");
        Assert.notNull((Object)values, (String)"parameter values cannot be null");
        Assert.isTrue((values.length > 0 ? 1 : 0) != 0, (String)"parameter values cannot be empty");
        for (String value : values) {
            Assert.notNull((Object)value, (String)"individual values cannot be null");
        }
        for (String value : values) {
            this.add(name, value);
        }
        return this;
    }

    public UrlBuilder add(String name, long value) throws IllegalArgumentException {
        return this.add(name, String.valueOf(value));
    }

    public UrlBuilder add(String name, int value) throws IllegalArgumentException {
        return this.add(name, String.valueOf(value));
    }

    public UrlBuilder add(String name, boolean value) throws IllegalArgumentException {
        return this.add(name, String.valueOf(value));
    }

    public UrlBuilder addAnchor(String anchor) {
        this.anchor = anchor;
        return this;
    }

    public UrlBuilder addAll(Map<String, Object> params) {
        for (Map.Entry<String, Object> param : params.entrySet()) {
            if (param.getValue() == null) continue;
            this.add(param.getKey(), param.getValue().toString());
        }
        return this;
    }

    public String toString() {
        return this.toUrl();
    }

    public String toUrl() {
        return this.toUrl(true);
    }

    public String toUrl(boolean encodeAnchor) {
        Object out = this.url.toString();
        if (StringUtils.isNotBlank((CharSequence)this.anchor)) {
            out = (String)out + "#" + (encodeAnchor ? this.urlEncode(this.anchor) : this.anchor);
        }
        return out;
    }

    private String urlEncode(String value) {
        return HtmlUtil.urlEncode(value, this.encoding);
    }
}

