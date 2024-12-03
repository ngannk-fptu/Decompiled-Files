/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.applinks.internal.common.rest.model.status;

import com.atlassian.applinks.internal.common.status.oauth.OAuthConfig;
import com.atlassian.applinks.internal.rest.model.ApplinksRestRepresentation;
import com.atlassian.applinks.internal.rest.model.RestRepresentation;
import java.util.Objects;
import javax.annotation.Nonnull;

public class RestOAuthConfig
extends ApplinksRestRepresentation
implements RestRepresentation<OAuthConfig> {
    public static final String ENABLED = "enabled";
    public static final String TWO_LO_ENABLED = "twoLoEnabled";
    public static final String TWO_LO_IMPERSONATION_ENABLED = "twoLoImpersonationEnabled";
    private boolean enabled;
    private boolean twoLoEnabled;
    private boolean twoLoImpersonationEnabled;

    public RestOAuthConfig() {
    }

    public RestOAuthConfig(@Nonnull OAuthConfig oAuthConfig) {
        Objects.requireNonNull(oAuthConfig, "oAuthConfig");
        this.enabled = oAuthConfig.isEnabled();
        this.twoLoEnabled = oAuthConfig.isTwoLoEnabled();
        this.twoLoImpersonationEnabled = oAuthConfig.isTwoLoImpersonationEnabled();
    }

    @Override
    @Nonnull
    public OAuthConfig asDomain() {
        if (!this.enabled) {
            return OAuthConfig.createDisabledConfig();
        }
        if (this.twoLoImpersonationEnabled) {
            return OAuthConfig.createOAuthWithImpersonationConfig();
        }
        if (this.twoLoEnabled) {
            return OAuthConfig.createDefaultOAuthConfig();
        }
        return OAuthConfig.createThreeLoOnlyConfig();
    }
}

