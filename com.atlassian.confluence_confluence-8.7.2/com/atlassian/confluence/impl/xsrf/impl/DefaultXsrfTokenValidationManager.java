/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.security.XsrfProtectionExcluded
 *  com.atlassian.xwork.FileUploadUtils
 *  com.atlassian.xwork.RequireSecurityToken
 *  com.atlassian.xwork.XsrfTokenGenerator
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.xsrf.impl;

import com.atlassian.annotations.security.XsrfProtectionExcluded;
import com.atlassian.confluence.impl.xsrf.SecurityLevelConfig;
import com.atlassian.confluence.impl.xsrf.XsrfTokenValidationManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.xwork.FileUploadUtils;
import com.atlassian.xwork.RequireSecurityToken;
import com.atlassian.xwork.XsrfTokenGenerator;
import java.lang.reflect.Method;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultXsrfTokenValidationManager
implements XsrfTokenValidationManager {
    public static final String SRC_MAIL_RECIPIENT = "src.mail.recipient";
    public static final String JWT_TOKEN_PARAM = "jwt";
    public static final String HTTP_ADD_ON_ID_ATTRIBUTE_NAME = "Plugin-Key";
    public static final String X_ATLASSIAN_TOKEN = "X-Atlassian-Token";
    public static final String NO_CHECK_HEADER_VALUE = "no-check";
    public static final String REQUIRE_SECURITY_TOKEN = "RequireSecurityToken";
    public static final String ATL_TOKEN = "atl_token";
    private static final String ACTION_NAME = "doattachfile";
    private final Logger log = LoggerFactory.getLogger(DefaultXsrfTokenValidationManager.class);
    private final XsrfTokenGenerator tokenGenerator;

    public DefaultXsrfTokenValidationManager(XsrfTokenGenerator tokenGenerator) {
        this.tokenGenerator = tokenGenerator;
    }

    @Override
    public boolean isRequestExempt(String actionName, HttpServletRequest request) {
        return StringUtils.isEmpty((CharSequence)actionName) || this.isExemptedMailRequest(request) || this.isExemptedHeaderRequest(request) || this.isExemptedUploadRequest(actionName, request);
    }

    @Override
    public boolean isRequestValid(Class<?> actionClass, Method actionMethod, Map<String, String> actionParams, HttpServletRequest request) {
        String configTokenParam = actionParams.get(REQUIRE_SECURITY_TOKEN);
        RequireSecurityToken requireSecurityTokenAnnotation = actionMethod.getAnnotation(RequireSecurityToken.class);
        XsrfProtectionExcluded xsrfExcludeAnnotation = actionMethod.getAnnotation(XsrfProtectionExcluded.class);
        boolean isProtected = this.checkIfMethodRequiresProtection(configTokenParam, requireSecurityTokenAnnotation, xsrfExcludeAnnotation);
        String token = request.getParameter(ATL_TOKEN);
        boolean validToken = this.tokenGenerator.validateToken(request, token);
        if (isProtected && !validToken) {
            this.log.debug("invalid token provided for action class: {}, method: {}", (Object)actionClass.getSimpleName(), (Object)actionMethod.getName());
            return false;
        }
        return true;
    }

    private boolean checkIfMethodRequiresProtection(String configTokenParam, RequireSecurityToken requireSecurityTokenAnnotation, XsrfProtectionExcluded xsrfExcludeAnnotation) {
        if (xsrfExcludeAnnotation != null) {
            return false;
        }
        if (configTokenParam != null) {
            return Boolean.parseBoolean(configTokenParam);
        }
        return requireSecurityTokenAnnotation != null ? requireSecurityTokenAnnotation.value() : SecurityLevelConfig.getSecurityLevel().getDefaultProtection();
    }

    private boolean isExemptedMailRequest(HttpServletRequest request) {
        if (request.getAttribute(HTTP_ADD_ON_ID_ATTRIBUTE_NAME) != null && StringUtils.isNotEmpty((CharSequence)request.getParameter(JWT_TOKEN_PARAM))) {
            String userKey = request.getParameter(SRC_MAIL_RECIPIENT);
            if (StringUtils.isBlank((CharSequence)userKey)) {
                return false;
            }
            ConfluenceUser user = AuthenticatedUserThreadLocal.get();
            return user != null && user.getKey().getStringValue().equals(userKey);
        }
        return false;
    }

    private boolean isExemptedUploadRequest(String actionName, HttpServletRequest request) {
        if (ACTION_NAME.equalsIgnoreCase(actionName)) {
            MultiPartRequestWrapper multiPartRequest = FileUploadUtils.unwrapMultiPartRequest((HttpServletRequest)request);
            return multiPartRequest != null && multiPartRequest.hasErrors();
        }
        return false;
    }

    private boolean isExemptedHeaderRequest(HttpServletRequest request) {
        return NO_CHECK_HEADER_VALUE.equals(request.getHeader(X_ATLASSIAN_TOKEN));
    }
}

