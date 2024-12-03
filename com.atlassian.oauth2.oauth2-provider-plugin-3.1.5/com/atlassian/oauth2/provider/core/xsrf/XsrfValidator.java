/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth2.provider.api.xsrf.OAuth2XsrfTokenGenerator
 *  com.atlassian.oauth2.provider.api.xsrf.exeption.XsrfSessionException
 *  com.atlassian.sal.api.message.I18nResolver
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.oauth2.provider.core.xsrf;

import com.atlassian.oauth2.provider.api.xsrf.OAuth2XsrfTokenGenerator;
import com.atlassian.oauth2.provider.api.xsrf.exeption.XsrfSessionException;
import com.atlassian.oauth2.provider.core.xsrf.XsrfTokenValidationException;
import com.atlassian.sal.api.message.I18nResolver;
import javax.servlet.http.HttpServletRequest;

public class XsrfValidator {
    private final I18nResolver i18nResolver;
    private final OAuth2XsrfTokenGenerator oAuth2XsrfTokenGenerator;

    public XsrfValidator(OAuth2XsrfTokenGenerator oAuth2XsrfTokenGenerator, I18nResolver i18nResolver) {
        this.oAuth2XsrfTokenGenerator = oAuth2XsrfTokenGenerator;
        this.i18nResolver = i18nResolver;
    }

    public void validateXsrf(HttpServletRequest httpServletRequest) throws XsrfTokenValidationException {
        try {
            this.oAuth2XsrfTokenGenerator.validateSession(httpServletRequest);
            if (!this.oAuth2XsrfTokenGenerator.validateToken(httpServletRequest)) {
                throw new XsrfTokenValidationException(this.i18nResolver.getText("oauth2.xsrf.failure"));
            }
        }
        catch (XsrfSessionException xsrfSessionException) {
            throw new XsrfTokenValidationException(this.i18nResolver.getText("oauth2.xsrf.invalid.session"));
        }
    }
}

