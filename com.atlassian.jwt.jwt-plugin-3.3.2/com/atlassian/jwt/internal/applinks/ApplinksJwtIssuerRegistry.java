/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  javax.annotation.Nonnull
 */
package com.atlassian.jwt.internal.applinks;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.jwt.JwtIssuerRegistry;
import com.atlassian.jwt.applinks.JwtApplinkFinder;
import com.atlassian.jwt.internal.applinks.ApplicationLinkJwtIssuer;
import com.atlassian.sal.api.features.DarkFeatureManager;
import javax.annotation.Nonnull;

public class ApplinksJwtIssuerRegistry
implements JwtIssuerRegistry {
    private final JwtApplinkFinder jwtApplinkFinder;
    private final DarkFeatureManager darkFeatureManager;
    private static final String CONNECT_PLUGIN_SETTINGS_FEATURE_FLAG = "connect.no-applinks";

    public ApplinksJwtIssuerRegistry(JwtApplinkFinder jwtApplinkFinder, DarkFeatureManager darkFeatureManager) {
        this.jwtApplinkFinder = jwtApplinkFinder;
        this.darkFeatureManager = darkFeatureManager;
    }

    @Override
    public ApplicationLinkJwtIssuer getIssuer(@Nonnull String issuer) {
        if (this.darkFeatureManager.isFeatureEnabledForAllUsers(CONNECT_PLUGIN_SETTINGS_FEATURE_FLAG)) {
            return null;
        }
        if (issuer == null) {
            return null;
        }
        ApplicationLink link = this.jwtApplinkFinder.find(issuer);
        return link == null ? null : new ApplicationLinkJwtIssuer(link);
    }
}

