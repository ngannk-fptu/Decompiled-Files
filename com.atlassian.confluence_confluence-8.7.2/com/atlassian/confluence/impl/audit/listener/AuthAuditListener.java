/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditService
 *  com.atlassian.audit.entity.AuditAttribute
 *  com.atlassian.audit.entity.AuditEvent
 *  com.atlassian.audit.entity.AuditEvent$Builder
 *  com.atlassian.audit.entity.AuditType
 *  com.atlassian.audit.entity.CoverageArea
 *  com.atlassian.audit.entity.CoverageLevel
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.impl.audit.listener;

import com.atlassian.audit.api.AuditService;
import com.atlassian.audit.entity.AuditAttribute;
import com.atlassian.audit.entity.AuditEvent;
import com.atlassian.audit.entity.AuditType;
import com.atlassian.audit.entity.CoverageArea;
import com.atlassian.audit.entity.CoverageLevel;
import com.atlassian.confluence.audit.AuditingContext;
import com.atlassian.confluence.audit.StandardAuditResourceTypes;
import com.atlassian.confluence.event.events.internal.auth.SudoAuthFailEvent;
import com.atlassian.confluence.event.events.internal.auth.SudoAuthSuccessEvent;
import com.atlassian.confluence.event.events.internal.auth.SudoLogoutEvent;
import com.atlassian.confluence.event.events.security.ForgotPasswordEvent;
import com.atlassian.confluence.event.events.security.ForgotPasswordUnknownUserEvent;
import com.atlassian.confluence.event.events.security.LoginDetails;
import com.atlassian.confluence.event.events.security.LoginEvent;
import com.atlassian.confluence.event.events.security.LoginFailedEvent;
import com.atlassian.confluence.event.events.security.LogoutEvent;
import com.atlassian.confluence.event.events.security.RpcAuthFailedEvent;
import com.atlassian.confluence.event.events.security.RpcAuthenticatedEvent;
import com.atlassian.confluence.event.events.security.SecurityEvent;
import com.atlassian.confluence.impl.audit.AuditCategories;
import com.atlassian.confluence.impl.audit.AuditHelper;
import com.atlassian.confluence.impl.audit.handler.AuditHandlerService;
import com.atlassian.confluence.impl.audit.listener.AbstractAuditListener;
import com.atlassian.event.api.EventListener;
import com.atlassian.user.User;

public class AuthAuditListener
extends AbstractAuditListener {
    private static final String SUDO_AUTH_SUCCESSFUL = AuditHelper.buildSummaryTextKey("sudo.auth.successful");
    private static final String SUDO_AUTH_FAILED = AuditHelper.buildSummaryTextKey("sudo.auth.failed");
    private static final String SUDO_LOGOUT = AuditHelper.buildSummaryTextKey("sudo.logout");
    private static final String USER_LOGOUT = AuditHelper.buildSummaryTextKey("user.logout");
    private static final String FORGOT_PASSWORD = AuditHelper.buildSummaryTextKey("forgot.password");
    private static final String FORGOT_PASSWORD_UNKNOWN_USER = AuditHelper.buildSummaryTextKey("forgot.password.unknown");
    private static final String LOGIN_SUCCESS = AuditHelper.buildSummaryTextKey("login.success");
    private static final String LOGIN_FAILED = AuditHelper.buildSummaryTextKey("login.failed");
    private static final String LOGIN_SOURCE = AuditHelper.buildExtraAttribute("login.source");
    private static final String CAPTCHA_ATTRIBUTE = AuditHelper.buildExtraAttribute("captcha");
    private static final String DIRECT_LOGIN = LoginDetails.LoginSource.DIRECT.name().toLowerCase();

    public AuthAuditListener(AuditHandlerService auditHandlerService, AuditService service, AuditHelper auditHelper, StandardAuditResourceTypes resourceTypes, AuditingContext auditingContext) {
        super(auditHandlerService, service, auditHelper, resourceTypes, auditingContext);
    }

    @EventListener
    public void sudoAuthSuccessEvent(SudoAuthSuccessEvent event) {
        this.save(() -> AuditEvent.builder((AuditType)this.buildAuditType(SUDO_AUTH_SUCCESSFUL, CoverageLevel.ADVANCED)).build());
    }

    @EventListener
    public void sudoAuthFailEvent(SudoAuthFailEvent event) {
        this.save(() -> AuditEvent.builder((AuditType)this.buildAuditType(SUDO_AUTH_FAILED, CoverageLevel.ADVANCED)).build());
    }

    @EventListener
    public void sudoLogoutEvent(SudoLogoutEvent event) {
        this.save(() -> AuditEvent.builder((AuditType)this.buildAuditType(SUDO_LOGOUT, CoverageLevel.ADVANCED)).build());
    }

    @EventListener
    public void logoutEvent(LogoutEvent event) {
        if (!event.getExplicitLogout()) {
            return;
        }
        this.save(() -> AuditEvent.builder((AuditType)AuditType.fromI18nKeys((CoverageArea)CoverageArea.SECURITY, (CoverageLevel)CoverageLevel.FULL, (String)AuditCategories.AUTH, (String)USER_LOGOUT).build()).affectedObject(this.buildResource(this.auditHelper.fetchUserFullName(event.getUsername()), this.resourceTypes.user(), this.auditHelper.fetchUserKey(event.getUsername()))).build());
    }

    @EventListener
    public void forgotPasswordEvent(ForgotPasswordEvent event) {
        this.save(() -> AuditEvent.builder((AuditType)AuditType.fromI18nKeys((CoverageArea)CoverageArea.SECURITY, (CoverageLevel)CoverageLevel.ADVANCED, (String)AuditCategories.USER_MANAGEMENT, (String)FORGOT_PASSWORD).build()).affectedObject(this.buildResource(event.getUser().getFullName(), this.resourceTypes.user(), this.auditHelper.fetchUserKey(event.getUser()))).build());
    }

    @EventListener
    public void forgotPasswordUnknownUserEvent(ForgotPasswordUnknownUserEvent event) {
        this.save(() -> AuditEvent.builder((AuditType)AuditType.fromI18nKeys((CoverageArea)CoverageArea.SECURITY, (CoverageLevel)CoverageLevel.ADVANCED, (String)AuditCategories.USER_MANAGEMENT, (String)FORGOT_PASSWORD_UNKNOWN_USER).build()).affectedObject(this.buildResourceWithoutId(event.getUsernameOrEmail(), this.resourceTypes.user())).build());
    }

    @EventListener
    public void loginEvent(LoginEvent event) {
        this.save(() -> this.buildLoginAuditEvent(LOGIN_SUCCESS, event, event.getLoginDetails()));
    }

    @EventListener
    public void loginFailedEvent(LoginFailedEvent event) {
        this.save(() -> this.buildLoginAuditEvent(LOGIN_FAILED, event, event.getLoginDetails()));
    }

    @EventListener
    public void rpcAuthenticatedEvent(RpcAuthenticatedEvent event) {
        User user = event.getUser();
        if (user == null) {
            return;
        }
        this.save(() -> AuditEvent.builder((AuditType)this.buildAuditType(LOGIN_SUCCESS, CoverageLevel.FULL)).affectedObject(this.buildResource(user.getFullName(), this.resourceTypes.user(), this.auditHelper.fetchUserKey(user.getName()))).extraAttribute(AuditAttribute.fromI18nKeys((String)LOGIN_SOURCE, (String)DIRECT_LOGIN).build()).build());
    }

    @EventListener
    public void rpcAuthFailedEvent(RpcAuthFailedEvent event) {
        this.save(() -> {
            String userKey = this.auditHelper.fetchUserKey(event.getUsername());
            return AuditEvent.builder((AuditType)this.buildAuditType(LOGIN_FAILED, CoverageLevel.FULL)).affectedObject(userKey == null ? this.buildResourceWithoutId(event.getUsername(), this.resourceTypes.user()) : this.buildResource(this.auditHelper.fetchUserFullName(event.getUsername()), this.resourceTypes.user(), userKey)).extraAttribute(AuditAttribute.fromI18nKeys((String)LOGIN_SOURCE, (String)DIRECT_LOGIN).build()).build();
        });
    }

    private AuditEvent buildLoginAuditEvent(String summaryKey, SecurityEvent event, LoginDetails loginDetails) {
        String userKey = this.auditHelper.fetchUserKey(event.getUsername());
        AuditEvent.Builder builder = AuditEvent.builder((AuditType)this.buildAuditType(summaryKey, CoverageLevel.FULL)).affectedObject(userKey == null ? this.buildResourceWithoutId(event.getUsername(), this.resourceTypes.user()) : this.buildResource(this.auditHelper.fetchUserFullName(event.getUsername()), this.resourceTypes.user(), userKey)).extraAttribute(AuditAttribute.fromI18nKeys((String)LOGIN_SOURCE, (String)loginDetails.getLoginSource().name().toLowerCase()).build());
        if (loginDetails.getCaptchaState() != LoginDetails.CaptchaState.NOT_SHOWN) {
            builder.extraAttribute(AuditAttribute.fromI18nKeys((String)CAPTCHA_ATTRIBUTE, (String)loginDetails.getCaptchaState().name().toLowerCase()).build());
        }
        return builder.build();
    }

    private AuditType buildAuditType(String summaryKey, CoverageLevel coverageLevel) {
        return AuditType.fromI18nKeys((CoverageArea)CoverageArea.SECURITY, (CoverageLevel)coverageLevel, (String)AuditCategories.AUTH, (String)summaryKey).build();
    }
}

