/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.google.common.base.Preconditions
 */
package com.atlassian.plugins.authentication.impl.web;

import com.atlassian.plugins.authentication.impl.web.AuthenticationRequest;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.net.URI;
import java.util.Objects;
import java.util.Optional;

public class SessionData
implements Serializable {
    private final AuthenticationRequest authenticationRequest;
    private final URI targetUrl;
    private final long idpConfigId;

    public SessionData(AuthenticationRequest authenticationRequest, URI targetUrl, long idpConfigId) {
        this.authenticationRequest = (AuthenticationRequest)Preconditions.checkNotNull((Object)authenticationRequest);
        this.targetUrl = targetUrl;
        this.idpConfigId = idpConfigId;
    }

    public AuthenticationRequest getAuthenticationRequest() {
        return this.authenticationRequest;
    }

    public Optional<URI> getTargetUrl() {
        return Optional.ofNullable(this.targetUrl);
    }

    public long getIdpConfigId() {
        return this.idpConfigId;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        SessionData that = (SessionData)o;
        return this.idpConfigId == that.idpConfigId && Objects.equals(this.authenticationRequest, that.authenticationRequest) && Objects.equals(this.targetUrl, that.targetUrl);
    }

    public int hashCode() {
        return Objects.hash(this.authenticationRequest, this.targetUrl, this.idpConfigId);
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("authenticationRequest", (Object)this.authenticationRequest).add("targetUrl", (Object)this.targetUrl).add("idpConfigId", this.idpConfigId).toString();
    }
}

