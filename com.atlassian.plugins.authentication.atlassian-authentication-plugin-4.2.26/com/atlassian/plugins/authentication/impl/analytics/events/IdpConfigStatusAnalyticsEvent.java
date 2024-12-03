/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.authentication.impl.analytics.events;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.plugins.authentication.api.config.IdpConfig;
import com.atlassian.plugins.authentication.api.config.oidc.OidcConfig;
import com.atlassian.plugins.authentication.api.config.saml.SamlConfig;
import com.atlassian.plugins.authentication.impl.analytics.events.AnalyticsEvent;
import java.net.URI;
import java.net.URISyntaxException;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IdpConfigStatusAnalyticsEvent
implements AnalyticsEvent {
    private static final Logger log = LoggerFactory.getLogger(IdpConfigStatusAnalyticsEvent.class);
    private final IdpConfig idpConfig;

    public IdpConfigStatusAnalyticsEvent(@Nonnull IdpConfig idpConfig) {
        this.idpConfig = idpConfig;
    }

    @Override
    @EventName
    public String getEventName() {
        return String.format("plugins.authentication.status.%s.idp.%s", this.getSsoTypeEventString(), this.getIdpProviderEventString());
    }

    public boolean isEnabled() {
        return this.idpConfig.isEnabled();
    }

    public boolean isJsmEnabled() {
        return this.idpConfig.isIncludeCustomerLogins();
    }

    public boolean isJitEnabled() {
        return this.idpConfig.getJustInTimeConfig().isEnabled().orElse(false);
    }

    public long getIdpConfigId() {
        return this.idpConfig.getId();
    }

    private String getSsoTypeEventString() {
        switch (this.idpConfig.getSsoType()) {
            case SAML: {
                return "saml";
            }
            case OIDC: {
                return "oidc";
            }
        }
        throw new IllegalStateException("Unknown SSO type: " + (Object)((Object)this.idpConfig.getSsoType()));
    }

    private String getIdpProviderEventString() {
        switch (this.idpConfig.getSsoType()) {
            case SAML: {
                SamlConfig samlConfig = (SamlConfig)this.idpConfig;
                return samlConfig.getInferredIdpType() == SamlConfig.IdpType.CROWD ? "crowd" : this.getIdpProviderEventStringFromUrl(samlConfig.getSsoUrl());
            }
            case OIDC: {
                OidcConfig oidcConfig = (OidcConfig)this.idpConfig;
                return this.getIdpProviderEventStringFromUrl(oidcConfig.getIssuer());
            }
        }
        throw new IllegalStateException("Unknown SSO type: " + (Object)((Object)this.idpConfig.getSsoType()));
    }

    private String getIdpProviderEventStringFromUrl(String url) {
        try {
            URI uri = new URI(url);
            if (uri.getHost().endsWith("onelogin.com")) {
                return "onelogin";
            }
            if (uri.getHost().endsWith("okta.com") || uri.getHost().endsWith("oktapreview.com")) {
                return "okta";
            }
            if (uri.getHost().endsWith("pingidentity.com")) {
                return "ping";
            }
            if (uri.getHost().endsWith("microsoft.com") || uri.getHost().endsWith("azure.com") || uri.getHost().endsWith("windows.net")) {
                return "azure";
            }
            if (uri.getHost().endsWith("google.com")) {
                return "google";
            }
            if (uri.getPath().startsWith("/adfs")) {
                return "adfs";
            }
        }
        catch (URISyntaxException e) {
            log.warn("Failed parsing SSO URL");
        }
        return "other";
    }
}

