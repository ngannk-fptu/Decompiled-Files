/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.StringUtils
 */
package org.springframework.http;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

public class CacheControl {
    @Nullable
    private Duration maxAge;
    private boolean noCache = false;
    private boolean noStore = false;
    private boolean mustRevalidate = false;
    private boolean noTransform = false;
    private boolean cachePublic = false;
    private boolean cachePrivate = false;
    private boolean proxyRevalidate = false;
    @Nullable
    private Duration staleWhileRevalidate;
    @Nullable
    private Duration staleIfError;
    @Nullable
    private Duration sMaxAge;

    protected CacheControl() {
    }

    public static CacheControl empty() {
        return new CacheControl();
    }

    public static CacheControl maxAge(long maxAge, TimeUnit unit) {
        return CacheControl.maxAge(Duration.ofSeconds(unit.toSeconds(maxAge)));
    }

    public static CacheControl maxAge(Duration maxAge) {
        CacheControl cc = new CacheControl();
        cc.maxAge = maxAge;
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
        return this.sMaxAge(Duration.ofSeconds(unit.toSeconds(sMaxAge)));
    }

    public CacheControl sMaxAge(Duration sMaxAge) {
        this.sMaxAge = sMaxAge;
        return this;
    }

    public CacheControl staleWhileRevalidate(long staleWhileRevalidate, TimeUnit unit) {
        return this.staleWhileRevalidate(Duration.ofSeconds(unit.toSeconds(staleWhileRevalidate)));
    }

    public CacheControl staleWhileRevalidate(Duration staleWhileRevalidate) {
        this.staleWhileRevalidate = staleWhileRevalidate;
        return this;
    }

    public CacheControl staleIfError(long staleIfError, TimeUnit unit) {
        return this.staleIfError(Duration.ofSeconds(unit.toSeconds(staleIfError)));
    }

    public CacheControl staleIfError(Duration staleIfError) {
        this.staleIfError = staleIfError;
        return this;
    }

    @Nullable
    public String getHeaderValue() {
        String headerValue = this.toHeaderValue();
        return StringUtils.hasText((String)headerValue) ? headerValue : null;
    }

    private String toHeaderValue() {
        StringBuilder headerValue = new StringBuilder();
        if (this.maxAge != null) {
            this.appendDirective(headerValue, "max-age=" + this.maxAge.getSeconds());
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
        if (this.sMaxAge != null) {
            this.appendDirective(headerValue, "s-maxage=" + this.sMaxAge.getSeconds());
        }
        if (this.staleIfError != null) {
            this.appendDirective(headerValue, "stale-if-error=" + this.staleIfError.getSeconds());
        }
        if (this.staleWhileRevalidate != null) {
            this.appendDirective(headerValue, "stale-while-revalidate=" + this.staleWhileRevalidate.getSeconds());
        }
        return headerValue.toString();
    }

    private void appendDirective(StringBuilder builder, String value) {
        if (builder.length() > 0) {
            builder.append(", ");
        }
        builder.append(value);
    }

    public String toString() {
        return "CacheControl [" + this.toHeaderValue() + "]";
    }
}

