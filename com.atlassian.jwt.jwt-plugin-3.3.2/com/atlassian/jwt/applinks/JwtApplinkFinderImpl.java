/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkService
 *  javax.annotation.Nonnull
 */
package com.atlassian.jwt.applinks;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkService;
import com.atlassian.jwt.applinks.JwtApplinkFinder;
import javax.annotation.Nonnull;

public class JwtApplinkFinderImpl
implements JwtApplinkFinder {
    private final ApplicationLinkService applicationLinkService;

    public JwtApplinkFinderImpl(ApplicationLinkService applicationLinkService) {
        this.applicationLinkService = applicationLinkService;
    }

    @Override
    public ApplicationLink find(@Nonnull String addOnId) {
        if (null == addOnId) {
            throw new NullPointerException("Add-on id cannot be null");
        }
        for (ApplicationLink appLink : this.applicationLinkService.getApplicationLinks()) {
            if (!addOnId.equals(appLink.getProperty("plugin-key")) || !"JWT".equals(appLink.getProperty("atlassian.auth.method"))) continue;
            return appLink;
        }
        return null;
    }
}

