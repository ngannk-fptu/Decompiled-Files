/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Strings
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.plugins.authentication.impl.web.saml.provider;

import com.atlassian.plugins.authentication.impl.web.AuthenticationRequest;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import java.util.Objects;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class SamlRequest
implements AuthenticationRequest {
    private final String id;
    private final String loginRequestUrl;
    private final String relayState;

    public SamlRequest(String id, String loginRequestUrl, String relayState) {
        this.id = (String)Preconditions.checkNotNull((Object)Strings.emptyToNull((String)id));
        this.loginRequestUrl = loginRequestUrl;
        this.relayState = relayState;
    }

    public String getId() {
        return this.id;
    }

    @Override
    public String getLoginRequestUrl() {
        return this.loginRequestUrl;
    }

    public String getRelayState() {
        return this.relayState;
    }

    @Override
    public String getSessionDataKey() {
        return this.getRelayState();
    }

    @Override
    public String getPublicId() {
        return this.getRelayState();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        SamlRequest that = (SamlRequest)o;
        return Objects.equals(this.id, that.id) && Objects.equals(this.loginRequestUrl, that.loginRequestUrl) && Objects.equals(this.relayState, that.relayState);
    }

    public int hashCode() {
        return Objects.hash(this.id, this.loginRequestUrl, this.relayState);
    }

    public String toString() {
        return new ToStringBuilder((Object)this).append("id", (Object)this.id).append("loginRequestUrl", (Object)this.loginRequestUrl).append("relayState", (Object)this.relayState).toString();
    }
}

