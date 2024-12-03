/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.inject.Inject
 *  javax.inject.Named
 */
package com.atlassian.plugins.authentication.impl.config;

import com.atlassian.plugins.authentication.api.config.SsoType;
import com.atlassian.plugins.authentication.impl.config.IdpConfigValidator;
import com.atlassian.plugins.authentication.impl.config.oidc.OidcConfigValidator;
import com.atlassian.plugins.authentication.impl.config.saml.SamlConfigValidator;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;

@Named
public class IdpConfigValidatorProvider {
    private final SamlConfigValidator samlConfigValidator;
    private final OidcConfigValidator oidcConfigValidator;

    @Inject
    public IdpConfigValidatorProvider(SamlConfigValidator samlConfigValidator, OidcConfigValidator oidcConfigValidator) {
        this.samlConfigValidator = samlConfigValidator;
        this.oidcConfigValidator = oidcConfigValidator;
    }

    @Nonnull
    public Optional<IdpConfigValidator> getValidator(@Nonnull SsoType ssoType) {
        switch (ssoType) {
            case SAML: {
                return Optional.of(this.samlConfigValidator);
            }
            case OIDC: {
                return Optional.of(this.oidcConfigValidator);
            }
        }
        return Optional.empty();
    }

    @Nonnull
    public IdpConfigValidator getValidatorUnchecked(@Nonnull SsoType ssoType) {
        return this.getValidator(ssoType).orElseThrow(() -> new IllegalArgumentException("Could not obtain a validator for SSO type: " + (Object)((Object)ssoType)));
    }
}

