/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.jwt.JwtIssuer
 *  com.atlassian.jwt.JwtIssuerRegistry
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.remotepageview.jwt;

import com.atlassian.confluence.plugins.remotepageview.jwt.RemotePageViewJwtIssuer;
import com.atlassian.jwt.JwtIssuer;
import com.atlassian.jwt.JwtIssuerRegistry;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ExportAsService(value={JwtIssuerRegistry.class})
public class RemotePageViewJwtRegistry
implements JwtIssuerRegistry {
    private final RemotePageViewJwtIssuer remotePageViewJwtIssuer;

    @Autowired
    public RemotePageViewJwtRegistry(RemotePageViewJwtIssuer remotePageViewJwtIssuer) {
        this.remotePageViewJwtIssuer = remotePageViewJwtIssuer;
    }

    @Nullable
    public JwtIssuer getIssuer(@Nonnull String issuer) {
        if (!Objects.equals(issuer, this.remotePageViewJwtIssuer.getName())) {
            return null;
        }
        return this.remotePageViewJwtIssuer.get();
    }
}

