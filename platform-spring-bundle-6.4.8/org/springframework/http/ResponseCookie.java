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
    @Nullable
    private final String sameSite;

    private ResponseCookie(String name, String value, Duration maxAge, @Nullable String domain, @Nullable String path, boolean secure, boolean httpOnly, @Nullable String sameSite) {
        super(name, value);
        Assert.notNull((Object)maxAge, "Max age must not be null");
        this.maxAge = maxAge;
        this.domain = domain;
        this.path = path;
        this.secure = secure;
        this.httpOnly = httpOnly;
        this.sameSite = sameSite;
        Rfc6265Utils.validateCookieName(name);
        Rfc6265Utils.validateCookieValue(value);
        Rfc6265Utils.validateDomain(domain);
        Rfc6265Utils.validatePath(path);
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

    @Nullable
    public String getSameSite() {
        return this.sameSite;
    }

    @Override
    public boolean equals(@Nullable Object other) {
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
        if (StringUtils.hasText(this.sameSite)) {
            sb.append("; SameSite=").append(this.sameSite);
        }
        return sb.toString();
    }

    public static ResponseCookieBuilder from(String name, String value) {
        return ResponseCookie.from(name, value, false);
    }

    public static ResponseCookieBuilder fromClientResponse(String name, String value) {
        return ResponseCookie.from(name, value, true);
    }

    private static ResponseCookieBuilder from(final String name, final String value, final boolean lenient) {
        return new ResponseCookieBuilder(){
            private Duration maxAge = Duration.ofSeconds(-1L);
            @Nullable
            private String domain;
            @Nullable
            private String path;
            private boolean secure;
            private boolean httpOnly;
            @Nullable
            private String sameSite;

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
            public ResponseCookieBuilder domain(@Nullable String domain) {
                this.domain = this.initDomain(domain);
                return this;
            }

            @Nullable
            private String initDomain(@Nullable String domain) {
                String str;
                if (lenient && StringUtils.hasLength(domain) && (str = domain.trim()).startsWith("\"") && str.endsWith("\"") && str.substring(1, str.length() - 1).trim().isEmpty()) {
                    return null;
                }
                return domain;
            }

            @Override
            public ResponseCookieBuilder path(@Nullable String path) {
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
            public ResponseCookieBuilder sameSite(@Nullable String sameSite) {
                this.sameSite = sameSite;
                return this;
            }

            @Override
            public ResponseCookie build() {
                return new ResponseCookie(name, value, this.maxAge, this.domain, this.path, this.secure, this.httpOnly, this.sameSite);
            }
        };
    }

    private static class Rfc6265Utils {
        private static final String SEPARATOR_CHARS = new String(new char[]{'(', ')', '<', '>', '@', ',', ';', ':', '\\', '\"', '/', '[', ']', '?', '=', '{', '}', ' '});
        private static final String DOMAIN_CHARS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ.-";

        private Rfc6265Utils() {
        }

        public static void validateCookieName(String name) {
            for (int i2 = 0; i2 < name.length(); ++i2) {
                char c = name.charAt(i2);
                if (c <= '\u001f' || c == '\u007f') {
                    throw new IllegalArgumentException(name + ": RFC2616 token cannot have control chars");
                }
                if (SEPARATOR_CHARS.indexOf(c) >= 0) {
                    throw new IllegalArgumentException(name + ": RFC2616 token cannot have separator chars such as '" + c + "'");
                }
                if (c < '\u0080') continue;
                throw new IllegalArgumentException(name + ": RFC2616 token can only have US-ASCII: 0x" + Integer.toHexString(c));
            }
        }

        public static void validateCookieValue(@Nullable String value) {
            if (value == null) {
                return;
            }
            int start = 0;
            int end = value.length();
            if (end > 1 && value.charAt(0) == '\"' && value.charAt(end - 1) == '\"') {
                start = 1;
                --end;
            }
            for (int i2 = start; i2 < end; ++i2) {
                char c = value.charAt(i2);
                if (c < '!' || c == '\"' || c == ',' || c == ';' || c == '\\' || c == '\u007f') {
                    throw new IllegalArgumentException("RFC2616 cookie value cannot have '" + c + "'");
                }
                if (c < '\u0080') continue;
                throw new IllegalArgumentException("RFC2616 cookie value can only have US-ASCII chars: 0x" + Integer.toHexString(c));
            }
        }

        public static void validateDomain(@Nullable String domain) {
            if (!StringUtils.hasLength(domain)) {
                return;
            }
            char char1 = domain.charAt(0);
            char charN = domain.charAt(domain.length() - 1);
            if (char1 == '-' || charN == '.' || charN == '-') {
                throw new IllegalArgumentException("Invalid first/last char in cookie domain: " + domain);
            }
            int c = -1;
            for (int i2 = 0; i2 < domain.length(); ++i2) {
                int p = c;
                c = domain.charAt(i2);
                if (DOMAIN_CHARS.indexOf(c) != -1 && (p != 46 || c != 46 && c != 45) && (p != 45 || c != 46)) continue;
                throw new IllegalArgumentException(domain + ": invalid cookie domain char '" + c + "'");
            }
        }

        public static void validatePath(@Nullable String path) {
            if (path == null) {
                return;
            }
            for (int i2 = 0; i2 < path.length(); ++i2) {
                char c = path.charAt(i2);
                if (c >= ' ' && c <= '~' && c != ';') continue;
                throw new IllegalArgumentException(path + ": Invalid cookie path char '" + c + "'");
            }
        }
    }

    public static interface ResponseCookieBuilder {
        public ResponseCookieBuilder maxAge(Duration var1);

        public ResponseCookieBuilder maxAge(long var1);

        public ResponseCookieBuilder path(@Nullable String var1);

        public ResponseCookieBuilder domain(@Nullable String var1);

        public ResponseCookieBuilder secure(boolean var1);

        public ResponseCookieBuilder httpOnly(boolean var1);

        public ResponseCookieBuilder sameSite(@Nullable String var1);

        public ResponseCookie build();
    }
}

