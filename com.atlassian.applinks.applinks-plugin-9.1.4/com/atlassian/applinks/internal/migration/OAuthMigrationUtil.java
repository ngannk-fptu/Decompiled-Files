/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.applinks.internal.migration;

import com.atlassian.applinks.internal.common.status.oauth.OAuthConfig;
import com.atlassian.applinks.internal.migration.AuthenticationStatus;
import javax.annotation.Nonnull;

public final class OAuthMigrationUtil {
    private OAuthMigrationUtil() {
        throw new UnsupportedOperationException("Do not instantiate.");
    }

    public static boolean isOAuthConfigured(@Nonnull AuthenticationStatus status) {
        OAuthConfig outgoingOAuth = status.outgoing().getOAuthConfig();
        OAuthConfig incomingOAuth = status.incoming().getOAuthConfig();
        boolean hasIncomingLegacyConfig = status.incoming().hasLegacy();
        boolean hasOutgoingLegacyConfig = status.outgoing().hasLegacy();
        boolean atLeastOneConnectionHasOAuth = incomingOAuth.isEnabled() || outgoingOAuth.isEnabled();
        boolean incomingOAuthConfigured = incomingOAuth.isEnabled() || OAuthMigrationUtil.isConnectionDisabled(incomingOAuth, hasIncomingLegacyConfig);
        boolean outgoingOAuthConfigured = outgoingOAuth.isEnabled() || OAuthMigrationUtil.isConnectionDisabled(outgoingOAuth, hasOutgoingLegacyConfig);
        return incomingOAuthConfigured && outgoingOAuthConfigured && atLeastOneConnectionHasOAuth;
    }

    private static boolean isConnectionDisabled(OAuthConfig oauthConfig, boolean hasLegacyConfig) {
        return !oauthConfig.isEnabled() && !hasLegacyConfig;
    }
}

