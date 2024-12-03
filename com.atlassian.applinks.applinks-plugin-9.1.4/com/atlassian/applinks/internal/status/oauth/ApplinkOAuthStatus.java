/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Objects
 *  javax.annotation.Nonnull
 */
package com.atlassian.applinks.internal.status.oauth;

import com.atlassian.applinks.internal.common.status.oauth.OAuthConfig;
import java.util.Objects;
import javax.annotation.Nonnull;

public final class ApplinkOAuthStatus {
    public static final ApplinkOAuthStatus DEFAULT = new ApplinkOAuthStatus(OAuthConfig.createDefaultOAuthConfig(), OAuthConfig.createDefaultOAuthConfig());
    public static final ApplinkOAuthStatus IMPERSONATION = new ApplinkOAuthStatus(OAuthConfig.createOAuthWithImpersonationConfig(), OAuthConfig.createOAuthWithImpersonationConfig());
    public static final ApplinkOAuthStatus OFF = new ApplinkOAuthStatus(OAuthConfig.createDisabledConfig(), OAuthConfig.createDisabledConfig());
    private final OAuthConfig incoming;
    private final OAuthConfig outgoing;

    public ApplinkOAuthStatus(@Nonnull OAuthConfig incoming, @Nonnull OAuthConfig outgoing) {
        this.incoming = Objects.requireNonNull(incoming, "incoming");
        this.outgoing = Objects.requireNonNull(outgoing, "outgoing");
    }

    @Nonnull
    public OAuthConfig getIncoming() {
        return this.incoming;
    }

    @Nonnull
    public OAuthConfig getOutgoing() {
        return this.outgoing;
    }

    public boolean matches(@Nonnull ApplinkOAuthStatus other) {
        Objects.requireNonNull(other, "other");
        return this.incoming.equals(other.getOutgoing()) && this.outgoing.equals(other.getIncoming());
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ApplinkOAuthStatus status = (ApplinkOAuthStatus)o;
        return com.google.common.base.Objects.equal((Object)this.incoming, (Object)status.incoming) && com.google.common.base.Objects.equal((Object)this.outgoing, (Object)status.outgoing);
    }

    public int hashCode() {
        return com.google.common.base.Objects.hashCode((Object[])new Object[]{this.incoming, this.outgoing});
    }
}

