/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.PasswordCredential
 *  com.atlassian.crowd.model.authentication.CookieConfiguration
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 *  javax.servlet.http.Cookie
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpSession
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.Validate
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.integration.http.util;

import com.atlassian.crowd.embedded.api.PasswordCredential;
import com.atlassian.crowd.integration.Constants;
import com.atlassian.crowd.integration.http.util.CrowdHttpTokenHelper;
import com.atlassian.crowd.integration.http.util.CrowdHttpValidationFactorExtractor;
import com.atlassian.crowd.model.authentication.CookieConfiguration;
import com.atlassian.crowd.model.authentication.UserAuthenticationContext;
import com.atlassian.crowd.model.authentication.ValidationFactor;
import com.atlassian.crowd.service.client.ClientProperties;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrowdHttpTokenHelperImpl
implements CrowdHttpTokenHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(CrowdHttpTokenHelperImpl.class);
    private final CrowdHttpValidationFactorExtractor validationFactorExtractor;

    private CrowdHttpTokenHelperImpl(CrowdHttpValidationFactorExtractor validationFactorExtractor) {
        this.validationFactorExtractor = validationFactorExtractor;
    }

    @Override
    public String getCrowdToken(HttpServletRequest request, String tokenName) {
        Validate.notNull((Object)request);
        Validate.notNull((Object)tokenName);
        LOGGER.debug("Checking for a SSO token that will need to be verified by Crowd.");
        String token = (String)request.getAttribute(Constants.COOKIE_TOKEN_KEY);
        if (token == null) {
            LOGGER.debug("No request attribute token could be found, now checking the browser submitted cookies.");
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                Optional<Cookie> cookie;
                if (LOGGER.isDebugEnabled()) {
                    for (Cookie cookie2 : cookies) {
                        LOGGER.debug("Cookie name/value: " + cookie2.getName() + " / " + cookie2.getValue());
                    }
                }
                if ((cookie = Arrays.stream(cookies).filter(CrowdHttpTokenHelperImpl.nonEmptyCookiesCalled(tokenName)).findFirst()).isPresent()) {
                    token = cookie.get().getValue();
                    LOGGER.debug("Accepting the SSO cookie value: {}", (Object)token);
                }
            }
        }
        if (LOGGER.isDebugEnabled()) {
            if (token == null) {
                LOGGER.debug("Unable to find a valid Crowd token.");
            } else {
                LOGGER.debug("Existing token value yet to be verified by Crowd: " + token);
            }
        }
        return token;
    }

    @Override
    public void removeCrowdToken(HttpServletRequest request, HttpServletResponse response, ClientProperties clientProperties, CookieConfiguration cookieConfig) {
        Validate.notNull((Object)request);
        Validate.notNull((Object)clientProperties);
        if (response != null) {
            Validate.notNull((Object)cookieConfig);
        }
        HttpSession session = request.getSession();
        session.removeAttribute(clientProperties.getSessionTokenKey());
        request.removeAttribute(Constants.COOKIE_TOKEN_KEY);
        if (response != null) {
            Cookie tokenCookie = this.buildCookie(null, clientProperties.getCookieTokenKey(cookieConfig.getName()), cookieConfig, clientProperties);
            tokenCookie.setMaxAge(0);
            tokenCookie.setHttpOnly(true);
            response.addCookie(tokenCookie);
        }
    }

    @Override
    public void setCrowdToken(HttpServletRequest request, HttpServletResponse response, String token, ClientProperties clientProperties, CookieConfiguration cookieConfig) {
        Validate.notNull((Object)request);
        Validate.notNull((Object)token);
        Validate.notNull((Object)clientProperties);
        if (response != null) {
            Validate.notNull((Object)cookieConfig);
        }
        HttpSession session = request.getSession();
        session.setAttribute(clientProperties.getSessionLastValidation(), (Object)new Date());
        request.setAttribute(Constants.COOKIE_TOKEN_KEY, (Object)token);
        if (response != null && request.getAttribute("com.atlassian.crowd.integration.http.HttpAuthenticator.REQUEST_SSO_COOKIE_COMMITTED") == null) {
            Cookie tokenCookie = this.buildCookie(token, clientProperties.getCookieTokenKey(cookieConfig.getName()), cookieConfig, clientProperties);
            tokenCookie.setHttpOnly(true);
            response.addCookie(tokenCookie);
            request.setAttribute("com.atlassian.crowd.integration.http.HttpAuthenticator.REQUEST_SSO_COOKIE_COMMITTED", (Object)Boolean.TRUE);
        }
    }

    @Override
    public UserAuthenticationContext getUserAuthenticationContext(HttpServletRequest request, String username, String password, ClientProperties clientProperties) {
        PasswordCredential credential = new PasswordCredential(password);
        UserAuthenticationContext userAuthenticationContext = new UserAuthenticationContext();
        userAuthenticationContext.setApplication(clientProperties.getApplicationName());
        userAuthenticationContext.setCredential(credential);
        userAuthenticationContext.setName(username);
        List<ValidationFactor> validationFactors = this.validationFactorExtractor.getValidationFactors(request);
        userAuthenticationContext.setValidationFactors(validationFactors.toArray(new ValidationFactor[0]));
        return userAuthenticationContext;
    }

    @Override
    public CrowdHttpValidationFactorExtractor getValidationFactorExtractor() {
        return this.validationFactorExtractor;
    }

    @SuppressFBWarnings(value={"INSECURE_COOKIE"}, justification="setSecure() called if configured")
    private Cookie buildCookie(String token, String tokenCookieKey, CookieConfiguration cookieConfig, ClientProperties clientProperties) {
        String domain = !StringUtils.isBlank((CharSequence)clientProperties.getSSOCookieDomainName()) ? clientProperties.getSSOCookieDomainName() : cookieConfig.getDomain();
        boolean isSecure = cookieConfig.isSecure();
        Cookie tokenCookie = new Cookie(tokenCookieKey, token);
        tokenCookie.setPath("/");
        if (StringUtils.isNotBlank((CharSequence)domain) && !"localhost".equals(domain)) {
            tokenCookie.setDomain(StringUtils.removeStart((String)domain, (String)"."));
        }
        tokenCookie.setSecure(isSecure);
        return tokenCookie;
    }

    public static CrowdHttpTokenHelper getInstance(CrowdHttpValidationFactorExtractor validationFactorExtractor) {
        return new CrowdHttpTokenHelperImpl(validationFactorExtractor);
    }

    private static Predicate<Cookie> nonEmptyCookiesCalled(String tokenName) {
        return cookie -> tokenName.equals(cookie.getName()) && cookie.getValue() != null;
    }
}

