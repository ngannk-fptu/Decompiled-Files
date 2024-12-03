/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.applinks.internal.rest.model.migration;

import com.atlassian.applinks.internal.migration.AuthenticationConfig;
import com.atlassian.applinks.internal.rest.model.ApplinksRestRepresentation;
import io.swagger.annotations.ApiModel;
import java.util.Objects;
import javax.annotation.Nonnull;

@ApiModel
public class RestAuthenticationConfig
extends ApplinksRestRepresentation {
    public static final String OAUTH = "oauth";
    public static final String BASIC = "basic";
    public static final String TRUSTED = "trusted";
    private boolean oauth;
    private boolean basic;
    private boolean trusted;

    public RestAuthenticationConfig() {
    }

    public RestAuthenticationConfig(@Nonnull AuthenticationConfig config) {
        Objects.requireNonNull(config, "authenticationConfig");
        this.oauth = config.isOAuthConfigured();
        this.basic = config.isBasicConfigured();
        this.trusted = config.isTrustedConfigured();
    }
}

