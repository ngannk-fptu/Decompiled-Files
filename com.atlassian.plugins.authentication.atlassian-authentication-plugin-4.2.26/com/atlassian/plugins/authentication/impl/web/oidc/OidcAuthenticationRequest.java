/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.plugins.authentication.impl.web.oidc;

import com.atlassian.plugins.authentication.impl.web.AuthenticationRequest;
import java.util.Objects;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class OidcAuthenticationRequest
implements AuthenticationRequest {
    private final String state;
    private final String nonce;
    private final String publicId;
    private final String loginRequestUrl;

    public OidcAuthenticationRequest(String state, String nonce, String publicId, String loginRequestUrl) {
        this.state = state;
        this.nonce = nonce;
        this.publicId = publicId;
        this.loginRequestUrl = loginRequestUrl;
    }

    public String getState() {
        return this.state;
    }

    public String getNonce() {
        return this.nonce;
    }

    @Override
    public String getSessionDataKey() {
        return this.getState();
    }

    @Override
    public String getPublicId() {
        return this.publicId;
    }

    @Override
    public String getLoginRequestUrl() {
        return this.loginRequestUrl;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        OidcAuthenticationRequest request = (OidcAuthenticationRequest)o;
        return Objects.equals(this.state, request.state) && Objects.equals(this.nonce, request.nonce) && Objects.equals(this.publicId, request.publicId) && Objects.equals(this.loginRequestUrl, request.loginRequestUrl);
    }

    public int hashCode() {
        return Objects.hash(this.state, this.nonce, this.publicId, this.loginRequestUrl);
    }

    public String toString() {
        return new ToStringBuilder((Object)this).append("state", (Object)this.state).append("nonce", (Object)this.nonce).append("publicId", (Object)this.publicId).append("loginRequestUrl", (Object)this.loginRequestUrl).toString();
    }
}

