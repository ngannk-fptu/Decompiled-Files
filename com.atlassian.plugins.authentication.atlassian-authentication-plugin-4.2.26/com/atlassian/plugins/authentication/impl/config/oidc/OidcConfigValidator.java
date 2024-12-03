/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMultimap$Builder
 *  com.google.common.collect.ImmutableSetMultimap
 *  com.google.common.collect.ImmutableSetMultimap$Builder
 *  com.google.common.collect.Multimap
 *  javax.annotation.Nonnull
 *  javax.inject.Inject
 *  javax.inject.Named
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.authentication.impl.config.oidc;

import com.atlassian.plugins.authentication.api.config.SsoType;
import com.atlassian.plugins.authentication.api.config.ValidationError;
import com.atlassian.plugins.authentication.api.config.oidc.OidcConfig;
import com.atlassian.plugins.authentication.impl.config.AbstractIdpConfigValidator;
import com.atlassian.plugins.authentication.impl.config.ValidationContext;
import com.atlassian.plugins.authentication.impl.util.HttpsValidator;
import com.atlassian.plugins.authentication.impl.util.ValidationUtils;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;
import java.net.URL;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
public class OidcConfigValidator
extends AbstractIdpConfigValidator<OidcConfig> {
    private static final Logger log = LoggerFactory.getLogger(OidcConfigValidator.class);

    @Inject
    public OidcConfigValidator(HttpsValidator httpsValidator) {
        super(httpsValidator);
    }

    @Override
    protected SsoType getSsoType() {
        return SsoType.OIDC;
    }

    @Override
    protected Class<OidcConfig> getSsoClass() {
        return OidcConfig.class;
    }

    @Override
    protected void validate(@Nonnull ImmutableMultimap.Builder<String, ValidationError> errors, @Nonnull OidcConfig oidcConfig) {
        errors.putAll((Object)"issuer-url", this.validateRequiredField(oidcConfig.getIssuer()));
        errors.putAll((Object)"client-id", this.validateRequiredField(oidcConfig.getClientId()));
        errors.putAll((Object)"client-secret", this.validateRequiredField(oidcConfig.getClientSecret()));
        errors.putAll((Object)"authorization-endpoint", this.validateRequiredField(oidcConfig.getAuthorizationEndpoint()));
        errors.putAll((Object)"token-endpoint", this.validateRequiredField(oidcConfig.getTokenEndpoint()));
        errors.putAll((Object)"userinfo-endpoint", this.validateRequiredField(oidcConfig.getUserInfoEndpoint()));
        errors.putAll((Object)"issuer-url", this.validateIssuer(oidcConfig.getIssuer()));
        errors.putAll((Object)"authorization-endpoint", this.validateUrl(oidcConfig.getAuthorizationEndpoint()));
        errors.putAll((Object)"token-endpoint", this.validateUrl(oidcConfig.getTokenEndpoint()));
        errors.putAll((Object)"userinfo-endpoint", this.validateUrl(oidcConfig.getUserInfoEndpoint()));
        errors.putAll((Object)"additional-scopes", this.validateAdditionalScopes(oidcConfig.getAdditionalScopes()));
        errors.putAll((Object)"username-claim", this.validateMappingExpression(oidcConfig.getUsernameClaim()));
        errors.putAll(this.validateJitFields(oidcConfig.getJustInTimeConfig()));
    }

    @Override
    protected Multimap<String, ValidationError> validateInContext(OidcConfig oidcConfig, ValidationContext context) {
        ImmutableSetMultimap.Builder errors = ImmutableSetMultimap.builder();
        if (context == ValidationContext.OIDC_DISCOVERY) {
            errors.putAll((Object)"issuer-url", this.validateRequiredField(oidcConfig.getIssuer()));
            errors.putAll((Object)"issuer-url", this.validateIssuer(oidcConfig.getIssuer()));
            errors.putAll((Object)"client-id", this.validateRequiredField(oidcConfig.getClientId()));
            errors.putAll((Object)"client-secret", this.validateRequiredField(oidcConfig.getClientSecret()));
            return errors.build();
        }
        throw new IllegalArgumentException("Validation in context " + (Object)((Object)context) + " is not supported for OIDC configuration");
    }

    private Iterable<ValidationError> validateIssuer(String issuerUrl) {
        Iterable<ValidationError> urlValidation = this.validateUrl(issuerUrl);
        if (urlValidation.iterator().hasNext()) {
            return urlValidation;
        }
        URL url = ValidationUtils.convertToUrl(issuerUrl);
        if (url != null && (!"http".equalsIgnoreCase(url.getProtocol()) && !"https".equalsIgnoreCase(url.getProtocol()) || url.getQuery() != null)) {
            log.error("Invalid issuer, specified protocol: {}, query path: {}", (Object)url.getProtocol(), (Object)url.getQuery());
            return ERROR_INCORRECT;
        }
        return NO_ERRORS;
    }

    private Iterable<ValidationError> validateAdditionalScopes(Iterable<String> values) {
        for (String value : values) {
            if (!StringUtils.isEmpty((CharSequence)value) && StringUtils.isAsciiPrintable((CharSequence)value) && !StringUtils.containsWhitespace((CharSequence)value)) continue;
            return ERROR_INCORRECT;
        }
        return NO_ERRORS;
    }
}

