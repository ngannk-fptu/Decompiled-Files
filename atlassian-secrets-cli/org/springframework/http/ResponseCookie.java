/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.http;

import java.time.Duration;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

public final class ResponseCookie
extends HttpCookie {
    private final Duration maxAge;
    @Nullable
    private final String domain;
    @Nullable
    private final String path;
    private final boolean secure;
    private final boolean httpOnly;

    private ResponseCookie(String name, String value, Duration maxAge, @Nullable String domain, @Nullable String path, boolean secure, boolean httpOnly) {
        super(name, value);
        Assert.notNull((Object)maxAge, "Max age must not be null");
        this.maxAge = maxAge;
        this.domain = domain;
        this.path = path;
        this.secure = secure;
        this.httpOnly = httpOnly;
    }

    public Duration getMaxAge() {
        return this.maxAge;
    }

    @Nullable
    public String getDomain() {
        return this.domain;
    }

    @Nullable
    public String getPath() {
        return this.path;
    }

    public boolean isSecure() {
        return this.secure;
    }

    public boolean isHttpOnly() {
        return this.httpOnly;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ResponseCookie)) {
            return false;
        }
        ResponseCookie otherCookie = (ResponseCookie)other;
        return this.getName().equalsIgnoreCase(otherCookie.getName()) && ObjectUtils.nullSafeEquals(this.path, otherCookie.getPath()) && ObjectUtils.nullSafeEquals(this.domain, otherCookie.getDomain());
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + ObjectUtils.nullSafeHashCode(this.domain);
        result = 31 * result + ObjectUtils.nullSafeHashCode(this.path);
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getName()).append('=').append(this.getValue());
        if (StringUtils.hasText(this.getPath())) {
            sb.append("; Path=").append(this.getPath());
        }
        if (StringUtils.hasText(this.domain)) {
            sb.append("; Domain=").append(this.domain);
        }
        if (!this.maxAge.isNegative()) {
            sb.append("; Max-Age=").append(this.maxAge.getSeconds());
            sb.append("; Expires=");
            long millis = this.maxAge.getSeconds() > 0L ? System.currentTimeMillis() + this.maxAge.toMillis() : 0L;
            sb.append(HttpHeaders.formatDate(millis));
        }
        if (this.secure) {
            sb.append("; Secure");
        }
        if (this.httpOnly) {
            sb.append("; HttpOnly");
        }
        return sb.toString();
    }

    public static ResponseCookieBuilder from(final String name, final String value) {
        return new ResponseCookieBuilder(){
            private Duration maxAge = Duration.ofSeconds(-1L);
            @Nullable
            private String domain;
            @Nullable
            private String path;
            private boolean secure;
            private boolean httpOnly;

            @Override
            public ResponseCookieBuilder maxAge(Duration maxAge) {
                this.maxAge = maxAge;
                return this;
            }

            @Override
            public ResponseCookieBuilder maxAge(long maxAgeSeconds) {
                this.maxAge = maxAgeSeconds >= 0L ? Duration.ofSeconds(maxAgeSeconds) : Duration.ofSeconds(-1L);
                return this;
            }

            @Override
            public ResponseCookieBuilder domain(String domain) {
                this.domain = domain;
                return this;
            }

            @Override
            public ResponseCookieBuilder path(String path) {
                this.path = path;
                return this;
            }

            @Override
            public ResponseCookieBuilder secure(boolean secure) {
                this.secure = secure;
                return this;
            }

            @Override
            public ResponseCookieBuilder httpOnly(boolean httpOnly) {
                this.httpOnly = httpOnly;
                return this;
            }

            @Override
            public ResponseCookie build() {
                return new ResponseCookie(name, value, this.maxAge, this.domain, this.path, this.secure, this.httpOnly);
            }
        };
    }

    public static interface ResponseCookieBuilder {
        public ResponseCookieBuilder maxAge(Duration var1);

        public ResponseCookieBuilder maxAge(long var1);

        public ResponseCookieBuilder path(String var1);

        public ResponseCookieBuilder domain(String var1);

        public ResponseCookieBuilder secure(boolean var1);

        public ResponseCookieBuilder httpOnly(boolean var1);

        public ResponseCookie build();
    }
}

