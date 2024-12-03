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
package com.atlassian.confluence.plugins.previews.jwt;

import com.atlassian.confluence.plugins.previews.jwt.ConfluencePreviewsJwtIssuer;
import com.atlassian.jwt.JwtIssuer;
import com.atlassian.jwt.JwtIssuerRegistry;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@ExportAsService(value={JwtIssuerRegistry.class})
@Component
public class ConfluencePreviewsJwtRegistry
implements JwtIssuerRegistry {
    private final ConfluencePreviewsJwtIssuer confluencePreviewsJwtIssuer;

    @Autowired
    public ConfluencePreviewsJwtRegistry(ConfluencePreviewsJwtIssuer issuer) {
        this.confluencePreviewsJwtIssuer = issuer;
    }

    @Nullable
    public JwtIssuer getIssuer(@Nonnull String issuer) {
        if (!Objects.equals(issuer, this.confluencePreviewsJwtIssuer.getName())) {
            return null;
        }
        return this.confluencePreviewsJwtIssuer.get();
    }
}

