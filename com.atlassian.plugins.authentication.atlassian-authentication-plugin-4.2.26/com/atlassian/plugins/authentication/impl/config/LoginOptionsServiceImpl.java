/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.UrlMode
 *  javax.inject.Inject
 *  javax.inject.Named
 */
package com.atlassian.plugins.authentication.impl.config;

import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugins.authentication.api.config.IdpConfig;
import com.atlassian.plugins.authentication.api.config.IdpConfigService;
import com.atlassian.plugins.authentication.api.config.IdpLoginOption;
import com.atlassian.plugins.authentication.api.config.IdpSearchParameters;
import com.atlassian.plugins.authentication.api.config.LoginFormLoginOption;
import com.atlassian.plugins.authentication.api.config.LoginGatewayType;
import com.atlassian.plugins.authentication.api.config.LoginOption;
import com.atlassian.plugins.authentication.api.config.LoginOptionsService;
import com.atlassian.plugins.authentication.api.config.SsoConfig;
import com.atlassian.plugins.authentication.api.config.SsoConfigService;
import com.atlassian.plugins.authentication.impl.util.ApplicationStateValidator;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.inject.Inject;
import javax.inject.Named;

@Named
@ExportAsService(value={LoginOptionsService.class})
public class LoginOptionsServiceImpl
implements LoginOptionsService {
    private final SsoConfigService ssoConfigService;
    private final IdpConfigService idpConfigService;
    private final ApplicationProperties applicationProperties;
    private ApplicationStateValidator applicationStateValidator;

    @Inject
    public LoginOptionsServiceImpl(SsoConfigService ssoConfigService, IdpConfigService idpConfigService, ApplicationProperties applicationProperties, ApplicationStateValidator applicationStateValidator) {
        this.ssoConfigService = ssoConfigService;
        this.idpConfigService = idpConfigService;
        this.applicationProperties = applicationProperties;
        this.applicationStateValidator = applicationStateValidator;
    }

    @Override
    public List<LoginOption> getLoginOptions(boolean shouldFallbackOnAuthentication, LoginGatewayType loginGatewayType) {
        Stream<LoginOption> allLoginOptions;
        String loginLinkBaseUrl = this.applicationProperties.getBaseUrl(UrlMode.CANONICAL);
        Stream<LoginOption> idpLoginOptions = this.idpConfigService.getIdpConfigs(this.resolveSearchParameters(loginGatewayType)).stream().filter(this.applicationStateValidator::canProcessAuthenticationRequest).map(idpConfig -> new IdpLoginOption((IdpConfig)idpConfig, loginLinkBaseUrl + "/plugins/servlet/external-login"));
        SsoConfig ssoConfig = this.ssoConfigService.getSsoConfig();
        if (this.isShowLoginForm(ssoConfig, loginGatewayType) || ssoConfig.enableAuthenticationFallback() && shouldFallbackOnAuthentication) {
            Stream<LoginFormLoginOption> loginFormLoginOption = Stream.of(LoginFormLoginOption.INSTANCE);
            allLoginOptions = Stream.concat(loginFormLoginOption, idpLoginOptions);
        } else {
            allLoginOptions = idpLoginOptions;
        }
        return allLoginOptions.collect(Collectors.toList());
    }

    private IdpSearchParameters resolveSearchParameters(LoginGatewayType loginGatewayType) {
        switch (loginGatewayType) {
            case GLOBAL_LOGIN_GATEWAY: {
                return IdpSearchParameters.builder().setEnabledRestriction(true).build();
            }
            case JSM_LOGIN_GATEWAY: {
                return IdpSearchParameters.builder().setIncludeCustomerLoginsRestriction(true).build();
            }
        }
        throw new IllegalArgumentException("Unsupported LoginGatewayType provided: " + (Object)((Object)loginGatewayType));
    }

    private boolean isShowLoginForm(SsoConfig ssoConfig, LoginGatewayType loginGatewayType) {
        switch (loginGatewayType) {
            case GLOBAL_LOGIN_GATEWAY: {
                return ssoConfig.getShowLoginForm();
            }
            case JSM_LOGIN_GATEWAY: {
                return ssoConfig.getShowLoginFormForJsm();
            }
        }
        throw new IllegalArgumentException("Unsupported lookup mode provided: " + (Object)((Object)loginGatewayType));
    }
}

