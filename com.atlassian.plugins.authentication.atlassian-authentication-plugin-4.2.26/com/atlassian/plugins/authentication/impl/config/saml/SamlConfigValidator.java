/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  com.google.common.collect.ImmutableMultimap$Builder
 *  com.google.common.collect.Multimap
 *  javax.annotation.Nonnull
 *  javax.inject.Inject
 *  javax.inject.Named
 */
package com.atlassian.plugins.authentication.impl.config.saml;

import com.atlassian.plugins.authentication.api.config.SsoType;
import com.atlassian.plugins.authentication.api.config.ValidationError;
import com.atlassian.plugins.authentication.api.config.saml.SamlConfig;
import com.atlassian.plugins.authentication.impl.config.AbstractIdpConfigValidator;
import com.atlassian.plugins.authentication.impl.config.ValidationContext;
import com.atlassian.plugins.authentication.impl.util.HttpsValidator;
import com.atlassian.plugins.authentication.impl.util.ValidationUtils;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;

@Named
public class SamlConfigValidator
extends AbstractIdpConfigValidator<SamlConfig> {
    @Inject
    public SamlConfigValidator(HttpsValidator httpsValidator) {
        super(httpsValidator);
    }

    @Override
    protected SsoType getSsoType() {
        return SsoType.SAML;
    }

    @Override
    protected Class<SamlConfig> getSsoClass() {
        return SamlConfig.class;
    }

    @Override
    protected void validate(@Nonnull ImmutableMultimap.Builder<String, ValidationError> errors, @Nonnull SamlConfig samlConfig) {
        String ssoUrlField = samlConfig.getIdpType() == SamlConfig.IdpType.CROWD ? "crowd-url" : "sso-url";
        String ssoIssuerField = samlConfig.getIdpType() == SamlConfig.IdpType.CROWD ? "crowd-url" : "sso-issuer";
        errors.putAll((Object)ssoUrlField, this.validateRequiredField(samlConfig.getSsoUrl()));
        errors.putAll((Object)ssoIssuerField, this.validateRequiredField(samlConfig.getIssuer()));
        errors.putAll((Object)"certificate", this.validateRequiredField(samlConfig.getCertificate()));
        errors.putAll((Object)"idp-type", this.validateIdpType(samlConfig.getIdpType(), samlConfig.getInferredIdpType()));
        errors.putAll((Object)ssoUrlField, this.validateUrl(samlConfig.getSsoUrl()));
        errors.putAll((Object)"certificate", this.validateCertificate(samlConfig.getCertificate()));
        if (samlConfig.getIdpType().equals((Object)SamlConfig.IdpType.GENERIC)) {
            errors.putAll((Object)"username-attribute", this.validateMappingExpression(samlConfig.getUsernameAttribute()));
            errors.putAll(this.validateJitFields(samlConfig.getJustInTimeConfig()));
        }
    }

    @Override
    protected Multimap<String, ValidationError> validateInContext(SamlConfig config, ValidationContext context) {
        throw new IllegalArgumentException("Validation in context " + (Object)((Object)context) + " is not supported for SAML configuration");
    }

    private Iterable<? extends ValidationError> validateIdpType(SamlConfig.IdpType actualIdpType, SamlConfig.IdpType inferredIdpType) {
        if (inferredIdpType == actualIdpType) {
            return NO_ERRORS;
        }
        if (actualIdpType == SamlConfig.IdpType.GENERIC && inferredIdpType == SamlConfig.IdpType.CROWD) {
            return NO_ERRORS;
        }
        return ERROR_INCORRECT;
    }

    private Iterable<ValidationError> validateCertificate(String certificateString) {
        if (!Strings.isNullOrEmpty((String)certificateString)) {
            try {
                ValidationUtils.convertToCertificate(certificateString);
            }
            catch (Exception e) {
                return ERROR_INCORRECT;
            }
        }
        return NO_ERRORS;
    }
}

