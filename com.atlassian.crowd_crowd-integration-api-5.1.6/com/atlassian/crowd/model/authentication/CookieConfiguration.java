/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.model.authentication;

import java.util.Objects;

public class CookieConfiguration {
    public static String DEFAULT_COOKIE_TOKEN_KEY = "crowd.token_key";
    private final String domain;
    private final boolean secure;
    private final String name;

    public CookieConfiguration(String domain, boolean secure, String name) {
        this.domain = domain;
        this.secure = secure;
        this.name = name;
    }

    public String getDomain() {
        return this.domain;
    }

    public boolean isSecure() {
        return this.secure;
    }

    public String getName() {
        return this.name;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("CookieConfiguration{");
        sb.append("domain='").append(this.domain).append('\'');
        sb.append(", secure=").append(this.secure);
        sb.append(", name='").append(this.name).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        CookieConfiguration that = (CookieConfiguration)o;
        return this.secure == that.secure && Objects.equals(this.domain, that.domain) && Objects.equals(this.name, that.name);
    }

    public int hashCode() {
        return Objects.hash(this.domain, this.secure, this.name);
    }
}

