/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.xwork.FileUploadUtils
 *  com.atlassian.xwork.interceptors.XsrfTokenInterceptor
 *  com.atlassian.xwork.interceptors.XsrfTokenInterceptor$SecurityLevel
 *  com.opensymphony.xwork2.Action
 *  com.opensymphony.xwork2.ActionInvocation
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.struts2.ServletActionContext
 *  org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.xwork;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.I18NSupport;
import com.atlassian.xwork.FileUploadUtils;
import com.atlassian.xwork.interceptors.XsrfTokenInterceptor;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionInvocation;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfluenceXsrfTokenInterceptor
extends XsrfTokenInterceptor {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceXsrfTokenInterceptor.class);
    private static final String SRC_MAIL_RECIPIENT = "src.mail.recipient";
    private static final String JWT_TOKEN_PARAM = "jwt";
    private static XsrfTokenInterceptor.SecurityLevel configuredSecurityLevel = ConfluenceXsrfTokenInterceptor.getConfiguredSecurityLevel();

    public String intercept(ActionInvocation actionInvocation) throws Exception {
        if (this.isInspectionDisabledForRequest(actionInvocation)) {
            return actionInvocation.invoke();
        }
        return super.intercept(actionInvocation);
    }

    private boolean isInspectionDisabledForRequest(ActionInvocation actionInvocation) {
        if (!GeneralUtil.isSetupComplete()) {
            return false;
        }
        String actionName = actionInvocation.getProxy().getActionName();
        if (ServletActionContext.getRequest().getAttribute("Plugin-Key") != null && StringUtils.isNotEmpty((CharSequence)ServletActionContext.getRequest().getParameter(JWT_TOKEN_PARAM))) {
            String userKey = ServletActionContext.getRequest().getParameter(SRC_MAIL_RECIPIENT);
            if (StringUtils.isEmpty((CharSequence)userKey)) {
                return false;
            }
            ConfluenceUser user = AuthenticatedUserThreadLocal.get();
            return user != null && user.getKey().getStringValue().equals(userKey);
        }
        if ("doattachfile".equals(actionName)) {
            MultiPartRequestWrapper multiPartRequest = FileUploadUtils.unwrapMultiPartRequest((HttpServletRequest)ServletActionContext.getRequest());
            return multiPartRequest != null && multiPartRequest.hasErrors();
        }
        return false;
    }

    protected String internationaliseErrorMessage(Action action, String key) {
        if (action instanceof ConfluenceActionSupport) {
            ConfluenceActionSupport confluenceActionSupport = (ConfluenceActionSupport)action;
            return confluenceActionSupport.getText(key);
        }
        return I18NSupport.getText(key);
    }

    protected XsrfTokenInterceptor.SecurityLevel getSecurityLevel() {
        return configuredSecurityLevel;
    }

    private static XsrfTokenInterceptor.SecurityLevel getConfiguredSecurityLevel() {
        String securityLevelProp = System.getProperty("confluence.xwork.xsrf.securitylevel");
        if (securityLevelProp != null) {
            try {
                return XsrfTokenInterceptor.SecurityLevel.valueOf((String)securityLevelProp);
            }
            catch (IllegalArgumentException e) {
                log.error("Invalid security level. Using DEFAULT", (Throwable)e);
            }
        }
        return XsrfTokenInterceptor.SecurityLevel.OPT_IN;
    }
}

