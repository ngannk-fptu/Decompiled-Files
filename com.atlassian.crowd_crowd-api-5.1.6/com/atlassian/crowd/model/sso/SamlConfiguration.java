/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.model.sso;

import java.util.Objects;

public class SamlConfiguration {
    private final String issuer;
    private final String ssoUrl;

    public SamlConfiguration(String issuer, String ssoUrl) {
        this.issuer = issuer;
        this.ssoUrl = ssoUrl;
    }

    public String getIssuer() {
        return this.issuer;
    }

    public String getSsoUrl() {
        return this.ssoUrl;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        SamlConfiguration that = (SamlConfiguration)o;
        return Objects.equals(this.issuer, that.issuer) && Objects.equals(this.ssoUrl, that.ssoUrl);
    }

    public int hashCode() {
        return Objects.hash(this.issuer, this.ssoUrl);
    }
}

