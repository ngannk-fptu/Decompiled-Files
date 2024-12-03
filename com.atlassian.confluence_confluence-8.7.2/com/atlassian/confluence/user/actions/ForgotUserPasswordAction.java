/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.velocity.htmlsafe.HtmlSafe
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.mail.server.MailServerManager
 *  com.atlassian.user.EntityException
 *  com.atlassian.user.User
 *  com.atlassian.user.search.query.EmailTermQuery
 *  com.atlassian.user.search.query.Query
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  com.atlassian.xwork.RequireSecurityToken
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.struts2.ServletActionContext
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.user.actions;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.event.events.security.ForgotPasswordEvent;
import com.atlassian.confluence.event.events.security.ForgotPasswordUnknownUserEvent;
import com.atlassian.confluence.impl.ratelimiter.ActionRateLimiter;
import com.atlassian.confluence.security.CaptchaAware;
import com.atlassian.confluence.security.CaptchaManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserVerificationTokenManager;
import com.atlassian.confluence.user.UserVerificationTokenType;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.confluence.velocity.htmlsafe.HtmlSafe;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.mail.server.MailServerManager;
import com.atlassian.user.EntityException;
import com.atlassian.user.User;
import com.atlassian.user.search.query.EmailTermQuery;
import com.atlassian.user.search.query.Query;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import com.atlassian.xwork.RequireSecurityToken;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ForgotUserPasswordAction
extends ConfluenceActionSupport
implements CaptchaAware {
    private static final Logger log = LoggerFactory.getLogger(ForgotUserPasswordAction.class);
    private String usernameOrEmail;
    private MailServerManager mailServerManager;
    private String token;
    private UserVerificationTokenManager userVerificationTokenManager;
    private EventPublisher eventPublisher;
    private CaptchaManager captchaManager;
    private ActionRateLimiter actionRateLimiterForgetuserpassword;

    @Override
    @PermittedMethods(value={HttpMethod.GET, HttpMethod.POST})
    public String doDefault() {
        return "input";
    }

    @Override
    public void validate() {
        super.validate();
        if (!this.isMailServerConfigured()) {
            this.addActionError("forgot.pass.nomailserver", this.getGlobalSettings().getSiteTitle());
        }
        if (StringUtils.isBlank((CharSequence)this.getUsernameOrEmail())) {
            this.addFieldError("usernameOrEmail", this.getText("error.username.or.email.required.field"));
        }
    }

    @RequireSecurityToken(value=true)
    public String execute() throws Exception {
        String isReqAllowed = this.actionRateLimiterForgetuserpassword.isRequestAllowed(this.usernameOrEmail, "doforgotuserpassword");
        if (!isReqAllowed.equals("success")) {
            ServletActionContext.getResponse().setStatus(429);
            return isReqAllowed;
        }
        ConfluenceUser user = this.getUser();
        if (user == null) {
            ForgotPasswordUnknownUserEvent forgotPasswordUnknownUserEvent = new ForgotPasswordUnknownUserEvent(this, this.usernameOrEmail);
            this.eventPublisher.publish((Object)forgotPasswordUnknownUserEvent);
            return "success";
        }
        this.token = this.userVerificationTokenManager.generateAndSaveToken(user.getName(), UserVerificationTokenType.PASSWORD_RESET);
        ForgotPasswordEvent event = new ForgotPasswordEvent(user, this.getChangePasswordLink(), this.getChangePasswordRequestLink());
        this.eventPublisher.publish((Object)event);
        return "success";
    }

    @HtmlSafe
    public String getChangePasswordLink() {
        return this.getBaseUrl() + "resetuserpassword.action?username=" + HtmlUtil.urlEncode(this.getUser().getName()) + "&token=" + this.token;
    }

    public String getChangePasswordRequestLink() {
        return this.getBaseUrl() + "forgotuserpassword.action";
    }

    private String getBaseUrl() {
        Object baseUrl = GeneralUtil.getGlobalSettings().getBaseUrl();
        if (!((String)baseUrl).endsWith("/")) {
            baseUrl = (String)baseUrl + "/";
        }
        return baseUrl;
    }

    public ConfluenceUser getUser() {
        ConfluenceUser user = this.userAccessor.getUserByName(this.usernameOrEmail);
        if (user == null) {
            try {
                List<User> users = this.userAccessor.findUsersAsList((Query<User>)new EmailTermQuery(this.usernameOrEmail));
                if (users != null && !users.isEmpty()) {
                    user = this.getUserByName(users.get(0).getName());
                }
            }
            catch (EntityException e) {
                log.error("Unable to find users", (Throwable)e);
            }
        }
        return user;
    }

    public String format(String format, Object ... args) {
        return String.format(format, args);
    }

    public boolean isMailServerConfigured() {
        return this.mailServerManager.isDefaultSMTPMailServerDefined();
    }

    @Override
    public boolean isPermitted() {
        return true;
    }

    public String getUsernameOrEmail() {
        return this.usernameOrEmail;
    }

    public void setUsernameOrEmail(String usernameOrEmail) {
        this.usernameOrEmail = usernameOrEmail;
    }

    public void setMailServerManager(MailServerManager mailServerManager) {
        this.mailServerManager = mailServerManager;
    }

    public void setUserVerificationTokenManager(UserVerificationTokenManager userVerificationTokenManager) {
        this.userVerificationTokenManager = userVerificationTokenManager;
    }

    public void setEventPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public CaptchaManager getCaptchaManager() {
        return this.captchaManager;
    }

    public void setCaptchaManager(CaptchaManager captchaManager) {
        this.captchaManager = captchaManager;
    }

    @Override
    public boolean mustValidateCaptcha() {
        return true;
    }

    public void setActionRateLimiterForgetuserpassword(ActionRateLimiter actionRateLimiterForgetuserpassword) {
        this.actionRateLimiterForgetuserpassword = actionRateLimiterForgetuserpassword;
    }
}

