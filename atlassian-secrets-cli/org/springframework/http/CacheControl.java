/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.http;

import java.util.concurrent.TimeUnit;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

public class CacheControl {
    private long maxAge = -1L;
    private boolean noCache = false;
    private boolean noStore = false;
    private boolean mustRevalidate = false;
    private boolean noTransform = false;
    private boolean cachePublic = false;
    private boolean cachePrivate = false;
    private boolean proxyRevalidate = false;
    private long staleWhileRevalidate = -1L;
    private long staleIfError = -1L;
    private long sMaxAge = -1L;

    protected CacheControl() {
    }

    public static CacheControl empty() {
        return new CacheControl();
    }

    public static CacheControl maxAge(long maxAge, TimeUnit unit) {
        CacheControl cc = new CacheControl();
        cc.maxAge = unit.toSeconds(maxAge);
        return cc;
    }

    public static CacheControl noCache() {
        CacheControl cc = new CacheControl();
        cc.noCache = true;
        return cc;
    }

    public static CacheControl noStore() {
        CacheControl cc = new CacheControl();
        cc.noStore = true;
        return cc;
    }

    public CacheControl mustRevalidate() {
        this.mustRevalidate = true;
        return this;
    }

    public CacheControl noTransform() {
        this.noTransform = true;
        return this;
    }

    public CacheControl cachePublic() {
        this.cachePublic = true;
        return this;
    }

    public CacheControl cachePrivate() {
        this.cachePrivate = true;
        return this;
    }

    public CacheControl proxyRevalidate() {
        this.proxyRevalidate = true;
        return this;
    }

    public CacheControl sMaxAge(long sMaxAge, TimeUnit unit) {
        this.sMaxAge = unit.toSeconds(sMaxAge);
        return this;
    }

    public CacheControl staleWhileRevalidate(long staleWhileRevalidate, TimeUnit unit) {
        this.staleWhileRevalidate = unit.toSeconds(staleWhileRevalidate);
        return this;
    }

    public CacheControl staleIfError(long staleIfError, TimeUnit unit) {
        this.staleIfError = unit.toSeconds(staleIfError);
        return this;
    }

    @Nullable
    public String getHeaderValue() {
        String valueString;
        StringBuilder headerValue = new StringBuilder();
        if (this.maxAge != -1L) {
            this.appendDirective(headerValue, "max-age=" + this.maxAge);
        }
        if (this.noCache) {
            this.appendDirective(headerValue, "no-cache");
        }
        if (this.noStore) {
            this.appendDirective(headerValue, "no-store");
        }
        if (this.mustRevalidate) {
            this.appendDirective(headerValue, "must-revalidate");
        }
        if (this.noTransform) {
            this.appendDirective(headerValue, "no-transform");
        }
        if (this.cachePublic) {
            this.appendDirective(headerValue, "public");
        }
        if (this.cachePrivate) {
            this.appendDirective(headerValue, "private");
        }
        if (this.proxyRevalidate) {
            this.appendDirective(headerValue, "proxy-revalidate");
        }
        if (this.sMaxAge != -1L) {
            this.appendDirective(headerValue, "s-maxage=" + this.sMaxAge);
        }
        if (this.staleIfError != -1L) {
            this.appendDirective(headerValue, "stale-if-error=" + this.staleIfError);
        }
        if (this.staleWhileRevalidate != -1L) {
            this.appendDirective(headerValue, "stale-while-revalidate=" + this.staleWhileRevalidate);
        }
        return StringUtils.hasText(valueString = headerValue.toString()) ? valueString : null;
    }

    private void appendDirective(StringBuilder builder, String value) {
        if (builder.length() > 0) {
            builder.append(", ");
        }
        builder.append(value);
    }
}

