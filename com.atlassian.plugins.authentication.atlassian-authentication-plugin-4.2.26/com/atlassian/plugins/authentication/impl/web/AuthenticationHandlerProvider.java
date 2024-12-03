/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.inject.Inject
 *  javax.inject.Named
 */
package com.atlassian.plugins.authentication.impl.web;

import com.atlassian.plugins.authentication.api.config.SsoType;
import com.atlassian.plugins.authentication.impl.web.AuthenticationHandler;
import com.atlassian.plugins.authentication.impl.web.AuthenticationHandlerNotConfiguredException;
import com.atlassian.plugins.authentication.impl.web.oidc.OidcAuthenticationHandler;
import com.atlassian.plugins.authentication.impl.web.saml.SamlAuthenticationHandler;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;

@Named
public class AuthenticationHandlerProvider {
    private final SamlAuthenticationHandler samlAuthenticationHandler;
    private final OidcAuthenticationHandler oidcAuthenticationHandler;

    @Inject
    public AuthenticationHandlerProvider(SamlAuthenticationHandler samlAuthenticationHandler, OidcAuthenticationHandler oidcAuthenticationHandler) {
        this.samlAuthenticationHandler = samlAuthenticationHandler;
        this.oidcAuthenticationHandler = oidcAuthenticationHandler;
    }

    @Nonnull
    public AuthenticationHandler getAuthenticationHandler(@Nonnull SsoType ssoType) {
        switch (ssoType) {
            case SAML: {
                return this.samlAuthenticationHandler;
            }
            case OIDC: {
                return this.oidcAuthenticationHandler;
            }
        }
        throw new AuthenticationHandlerNotConfiguredException("Can't provide authentication handler for SSO type: " + (Object)((Object)ssoType));
    }
}

