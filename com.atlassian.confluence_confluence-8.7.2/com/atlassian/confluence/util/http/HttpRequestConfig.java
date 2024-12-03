/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.util.http;

import com.atlassian.confluence.util.http.Authenticator;
import java.io.Serializable;
import java.util.regex.Pattern;

@Deprecated(forRemoval=true)
public class HttpRequestConfig
implements Serializable {
    private static final long serialVersionUID = 6758243406205197667L;
    private String urlPattern;
    private boolean isRegex;
    private Authenticator authenticator;
    private int maxDownloadSize;
    private int maxCacheAge;
    private transient Pattern urlRegexPattern;

    public HttpRequestConfig() {
    }

    public HttpRequestConfig(int maxDownloadSize, int maxCacheAge) {
        this.maxDownloadSize = maxDownloadSize;
        this.maxCacheAge = maxCacheAge;
    }

    public boolean matches(String url) {
        if (!this.isRegex()) {
            return url.toLowerCase().startsWith(this.urlPattern.toLowerCase());
        }
        return this.getUrlRegexPattern().matcher(url).matches();
    }

    private Pattern getUrlRegexPattern() {
        if (this.urlRegexPattern == null) {
            this.urlRegexPattern = Pattern.compile(this.urlPattern, 2);
        }
        return this.urlRegexPattern;
    }

    public String getUrlPattern() {
        return this.urlPattern;
    }

    public void setUrlPattern(String urlPattern) {
        this.urlPattern = urlPattern;
    }

    public boolean isRegex() {
        return this.isRegex;
    }

    public void setRegex(boolean regex) {
        this.isRegex = regex;
    }

    public Authenticator getAuthenticator() {
        return this.authenticator;
    }

    public void setAuthenticator(Authenticator authenticator) {
        this.authenticator = authenticator;
    }

    public int getMaxDownloadSize() {
        return this.maxDownloadSize;
    }

    public void setMaxDownloadSize(int maxDownloadSize) {
        this.maxDownloadSize = maxDownloadSize;
    }

    public int getMaxCacheAge() {
        return this.maxCacheAge;
    }

    public void setMaxCacheAge(int maxCacheAge) {
        this.maxCacheAge = maxCacheAge;
    }
}

