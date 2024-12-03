/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.FilterConfig
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.seraph.filter;

import com.atlassian.seraph.config.SecurityConfigFactory;
import com.atlassian.seraph.filter.PasswordBasedLoginFilter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginFilter
extends PasswordBasedLoginFilter {
    private static final Logger log = LoggerFactory.getLogger(LoginFilter.class);
    private static final String ALLOW_URL_PARAMETER_LOGIN_PROPERTY = "atlassian.allow.insecure.url.parameter.login";
    private static final String ATLASSIAN_DEV_MODE_PROPERTY = "atlassian.dev.mode";
    private static final String ALLOW_URL_PARAMETER_VALUE_PARAMETER_NAME = "allowUrlParameterValue";
    private static final String DISABLE_LOGGING_DEPRECATION_URL_PARAMETER_VALUE_PARAMETER_NAME = "disableLoggingDeprecationUrlParameterValue";
    private static final String ENCODING = "UTF-8";
    private volatile boolean allowUrlParameterValue = false;
    private volatile boolean disableLoggingDeprecationUrlParameterValue = false;

    @Override
    public void init(FilterConfig config) {
        String deprecationValue;
        super.init(config);
        String configValue = config.getInitParameter(ALLOW_URL_PARAMETER_VALUE_PARAMETER_NAME);
        if (StringUtils.isNotBlank((CharSequence)configValue)) {
            this.setAllowUrlParameterValue(Boolean.parseBoolean(configValue));
        }
        if (StringUtils.isNotBlank((CharSequence)(deprecationValue = config.getInitParameter(DISABLE_LOGGING_DEPRECATION_URL_PARAMETER_VALUE_PARAMETER_NAME)))) {
            this.setDisableLoggingDeprecationUrlParameterValue(Boolean.parseBoolean(deprecationValue));
        }
    }

    public void setAllowUrlParameterValue(boolean allowUrlParameterValue) {
        this.allowUrlParameterValue = allowUrlParameterValue;
    }

    public void setDisableLoggingDeprecationUrlParameterValue(boolean disableLoggingDeprecationUrlParameterValue) {
        this.disableLoggingDeprecationUrlParameterValue = disableLoggingDeprecationUrlParameterValue;
    }

    @Override
    protected PasswordBasedLoginFilter.UserPasswordPair extractUserPasswordPair(HttpServletRequest request) {
        String username = request.getParameter("os_username");
        String password = request.getParameter("os_password");
        boolean persistentLogin = "true".equals(request.getParameter("os_cookie"));
        if (StringUtils.isNotEmpty((CharSequence)password) && (LoginFilter.hasOsPasswordQueryParam(request) || !LoginFilter.isLoginSubmitUrl(request))) {
            if (!this.shouldAllowUrlParameterValue()) {
                log.info("Not accepting an authentication attempt for user \"{}\", as authentication url parameter values are not being accepted.", (Object)username);
                return null;
            }
            if (!this.disableLoggingDeprecationUrlParameterValue) {
                log.info("User \"{}\" authenticated using {} as a query parameter, this means of authentication has been deprecated.", (Object)username, (Object)"os_password");
            }
        }
        return new PasswordBasedLoginFilter.UserPasswordPair(username, password, persistentLogin);
    }

    private boolean shouldAllowUrlParameterValue() {
        String allowInsecurePropertyValue = System.getProperty(ALLOW_URL_PARAMETER_LOGIN_PROPERTY);
        String devModePropertyValue = System.getProperty(ATLASSIAN_DEV_MODE_PROPERTY);
        return this.allowUrlParameterValue || StringUtils.equalsIgnoreCase((CharSequence)"true", (CharSequence)allowInsecurePropertyValue) || StringUtils.equalsIgnoreCase((CharSequence)"true", (CharSequence)devModePropertyValue);
    }

    private static boolean hasOsPasswordQueryParam(HttpServletRequest request) {
        if (request.getQueryString() == null) {
            return false;
        }
        String decodedQueryString = LoginFilter.decodeQueryString(request.getQueryString());
        return decodedQueryString.contains("os_password=");
    }

    private static boolean isLoginSubmitUrl(HttpServletRequest request) {
        List<String> allowedUrlList = SecurityConfigFactory.getInstance().getLoginSubmitURL();
        String servletPath = request.getServletPath();
        if (allowedUrlList.isEmpty()) {
            return true;
        }
        return allowedUrlList.contains(servletPath);
    }

    private static String decodeQueryString(String queryString) {
        try {
            return URLDecoder.decode(queryString, ENCODING);
        }
        catch (UnsupportedEncodingException e) {
            throw new AssertionError((Object)e);
        }
    }
}

