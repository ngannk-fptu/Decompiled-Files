/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  javax.annotation.Nonnull
 */
package com.atlassian.jwt.internal.applinks;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.jwt.JwtIssuer;
import javax.annotation.Nonnull;

public class ApplicationLinkJwtIssuer
implements JwtIssuer {
    private final ApplicationLink applicationLink;

    public ApplicationLinkJwtIssuer(ApplicationLink applicationLink) {
        this.applicationLink = applicationLink;
    }

    @Override
    @Nonnull
    public String getName() {
        return this.getProperty("plugin-key");
    }

    @Override
    public String getSharedSecret() {
        return this.getProperty("atlassian.jwt.shared.secret");
    }

    private String getProperty(String propertyName) {
        Object property = this.applicationLink.getProperty(propertyName);
        return property instanceof String ? (String)property : null;
    }
}

