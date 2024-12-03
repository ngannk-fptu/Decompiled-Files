/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.seraph.config.SecurityConfig
 *  com.atlassian.seraph.elevatedsecurity.ElevatedSecurityGuard
 *  com.atlassian.spring.container.ContainerManager
 *  com.octo.captcha.service.CaptchaServiceException
 *  javax.servlet.http.HttpServletRequest
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.security.seraph;

import com.atlassian.confluence.event.events.security.LoginDetails;
import com.atlassian.confluence.event.events.security.LoginFailedEvent;
import com.atlassian.confluence.security.CaptchaManager;
import com.atlassian.confluence.security.login.LoginManager;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.seraph.config.SecurityConfig;
import com.atlassian.seraph.elevatedsecurity.ElevatedSecurityGuard;
import com.atlassian.spring.container.ContainerManager;
import com.octo.captcha.service.CaptchaServiceException;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfluenceElevatedSecurityGuard
implements ElevatedSecurityGuard {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceElevatedSecurityGuard.class);
    public static final String ELEVATED_SECURITY_FAILURE = "ElevatedSecurityGuard_Failure";
    private LoginManager loginManager;
    private CaptchaManager captchaManager;
    private EventPublisher eventPublisher;

    public ConfluenceElevatedSecurityGuard() {
    }

    @Deprecated
    ConfluenceElevatedSecurityGuard(LoginManager loginManager, CaptchaManager captchaManager) {
        this.loginManager = loginManager;
        this.captchaManager = captchaManager;
    }

    public boolean performElevatedSecurityCheck(HttpServletRequest httpServletRequest, String userName) {
        if (!ContainerManager.isContainerSetup()) {
            return true;
        }
        CaptchaManager cm = this.getCaptchaManager();
        if (this.getLoginManager().requiresElevatedSecurityCheck(userName) && cm.isCaptchaAvailable()) {
            boolean isValid = false;
            try {
                String captchaId = httpServletRequest.getParameter("captchaId");
                String captchaResponse = httpServletRequest.getParameter("captchaResponse");
                isValid = cm.getImageCaptchaService().validateResponseForID(captchaId, (Object)captchaResponse);
            }
            catch (CaptchaServiceException captchaServiceException) {
                // empty catch block
            }
            if (!isValid) {
                httpServletRequest.setAttribute(ELEVATED_SECURITY_FAILURE, (Object)true);
                this.getEventPublisher().publish((Object)new LoginFailedEvent(this, userName, httpServletRequest.getSession().getId(), httpServletRequest.getRemoteAddr(), httpServletRequest.getRemoteHost(), new LoginDetails(LoginDetails.LoginSource.DIRECT, LoginDetails.CaptchaState.FAILED)));
                log.info("User '{}' didn't provide captcha required by elevated security check", (Object)userName);
            }
            return isValid;
        }
        return true;
    }

    public void onFailedLoginAttempt(HttpServletRequest httpServletRequest, String userName) {
        if (!ContainerManager.isContainerSetup()) {
            return;
        }
        this.getLoginManager().onFailedLoginAttempt(userName, httpServletRequest);
    }

    public void onSuccessfulLoginAttempt(HttpServletRequest httpServletRequest, String userName) {
        if (!ContainerManager.isContainerSetup()) {
            return;
        }
        this.getLoginManager().onSuccessfulLoginAttempt(userName, httpServletRequest);
    }

    public void init(Map<String, String> params, SecurityConfig config) {
    }

    private synchronized LoginManager getLoginManager() {
        if (null != this.loginManager) {
            return this.loginManager;
        }
        this.loginManager = (LoginManager)ContainerManager.getComponent((String)"loginManager");
        return this.loginManager;
    }

    private synchronized CaptchaManager getCaptchaManager() {
        if (null != this.captchaManager) {
            return this.captchaManager;
        }
        this.captchaManager = (CaptchaManager)ContainerManager.getComponent((String)"captchaManager");
        return this.captchaManager;
    }

    private synchronized EventPublisher getEventPublisher() {
        if (null != this.eventPublisher) {
            return this.eventPublisher;
        }
        this.eventPublisher = (EventPublisher)ContainerManager.getComponent((String)"eventPublisher");
        return this.eventPublisher;
    }
}

