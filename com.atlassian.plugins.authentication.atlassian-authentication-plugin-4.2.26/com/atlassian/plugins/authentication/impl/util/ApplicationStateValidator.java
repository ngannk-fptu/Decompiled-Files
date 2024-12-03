/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterables
 *  javax.inject.Inject
 *  javax.inject.Named
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.authentication.impl.util;

import com.atlassian.plugins.authentication.api.config.IdpConfig;
import com.atlassian.plugins.authentication.api.config.SsoType;
import com.atlassian.plugins.authentication.api.config.saml.SamlConfig;
import com.atlassian.plugins.authentication.impl.config.InsecureUrlException;
import com.atlassian.plugins.authentication.impl.util.HttpsValidator;
import com.atlassian.plugins.authentication.impl.util.ProductLicenseDataProvider;
import com.atlassian.plugins.authentication.impl.web.AuthenticationHandlerNotConfiguredException;
import com.atlassian.plugins.authentication.impl.web.InvalidLicenseException;
import com.atlassian.plugins.authentication.impl.web.saml.provider.SamlResponse;
import com.google.common.collect.Iterables;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
public class ApplicationStateValidator {
    private static final Logger log = LoggerFactory.getLogger(ApplicationStateValidator.class);
    private final HttpsValidator httpsValidator;
    private final ProductLicenseDataProvider productLicenseDataProvider;

    @Inject
    public ApplicationStateValidator(HttpsValidator httpsValidator, ProductLicenseDataProvider productLicenseDataProvider) {
        this.httpsValidator = httpsValidator;
        this.productLicenseDataProvider = productLicenseDataProvider;
    }

    public boolean canProcessAuthenticationRequest(IdpConfig idpConfig) {
        try {
            this.checkCanProcessAuthenticationRequest(idpConfig);
            return true;
        }
        catch (InsecureUrlException | AuthenticationHandlerNotConfiguredException | InvalidLicenseException e) {
            log.debug("Authentication request cannot be processed", (Throwable)e);
            return false;
        }
    }

    public void checkCanProcessAuthenticationRequest(IdpConfig config) {
        this.checkSsoIsConfigured(config);
        this.checkSsoIsAllowed(config);
    }

    public void checkSsoIsAllowed(IdpConfig config) {
        boolean isGenericSaml;
        SsoType ssoType = config.getSsoType();
        boolean isOpenIdConnect = ssoType == SsoType.OIDC;
        boolean bl = isGenericSaml = ssoType == SsoType.SAML && ((SamlConfig)config).getIdpType() == SamlConfig.IdpType.GENERIC;
        if (isOpenIdConnect || isGenericSaml) {
            this.checkIsDataCenterProduct();
        }
        this.checkBaseUrlIsHttps();
    }

    private void checkBaseUrlIsHttps() {
        if (!this.httpsValidator.isBaseUrlSecure()) {
            throw new InsecureUrlException("base-url", "Base Url is not https");
        }
    }

    private void checkSsoIsConfigured(IdpConfig config) {
        if (config == null) {
            throw new AuthenticationHandlerNotConfiguredException("Invalid SSO configuration");
        }
    }

    private void checkIsDataCenterProduct() {
        if (!this.productLicenseDataProvider.isDataCenterProduct()) {
            throw new InvalidLicenseException("Current license is not data center");
        }
    }

    public void checkHasAppropriateLicenseForSamlResponse(SamlResponse samlResponse) {
        if (this.isResponseNotFromCrowd(samlResponse)) {
            this.checkIsDataCenterProduct();
        }
    }

    private boolean isResponseNotFromCrowd(SamlResponse samlResponse) {
        return Optional.ofNullable(samlResponse.getAttribute("atl.crowd.properties.remember_me")).map(Iterables::isEmpty).orElse(true);
    }
}

