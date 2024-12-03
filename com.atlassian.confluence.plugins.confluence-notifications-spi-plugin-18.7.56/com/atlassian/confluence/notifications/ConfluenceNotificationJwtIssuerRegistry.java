/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.jwt.JwtIssuer
 *  com.atlassian.jwt.JwtIssuerRegistry
 *  javax.annotation.Nonnull
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.confluence.notifications;

import com.atlassian.confluence.notifications.ConfluenceNotificationJwtIssuer;
import com.atlassian.jwt.JwtIssuer;
import com.atlassian.jwt.JwtIssuerRegistry;
import javax.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;

public class ConfluenceNotificationJwtIssuerRegistry
implements JwtIssuerRegistry {
    private final ConfluenceNotificationJwtIssuer confluenceNotificationJwtIssuer;

    @Autowired
    public ConfluenceNotificationJwtIssuerRegistry(ConfluenceNotificationJwtIssuer confluenceNotificationJwtIssuer) {
        this.confluenceNotificationJwtIssuer = confluenceNotificationJwtIssuer;
    }

    public JwtIssuer getIssuer(@Nonnull String issuer) {
        if (issuer.equals(this.confluenceNotificationJwtIssuer.getName())) {
            return this.confluenceNotificationJwtIssuer;
        }
        return null;
    }
}

