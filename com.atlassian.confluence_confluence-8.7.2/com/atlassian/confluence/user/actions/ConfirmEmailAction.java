/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.CrowdService
 *  com.atlassian.crowd.embedded.api.User
 *  com.atlassian.user.User
 *  com.atlassian.user.impl.DefaultUser
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.confluence.user.actions;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.event.Evented;
import com.atlassian.confluence.event.events.user.ConfirmEmailAddressEvent;
import com.atlassian.confluence.event.events.user.UserSignupEvent;
import com.atlassian.confluence.license.exception.LicenseUserLimitExceededException;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.SignupManager;
import com.atlassian.confluence.util.SeraphUtils;
import com.atlassian.crowd.embedded.api.CrowdService;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.user.impl.DefaultUser;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import javax.servlet.http.HttpServletRequest;

public class ConfirmEmailAction
extends ConfluenceActionSupport
implements Evented<UserSignupEvent> {
    private SignupManager signupManager;
    private CrowdService crowdService;
    private String token;
    private String username;

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() throws Exception {
        DefaultUser atlUser;
        User user = this.crowdService.getUser(this.username);
        if (user != null && this.signupManager.isTokenForUserValid((com.atlassian.user.User)(atlUser = new DefaultUser(user.getName(), user.getDisplayName(), user.getEmailAddress())), this.token)) {
            try {
                this.signupManager.enableConfirmedUser((com.atlassian.user.User)atlUser);
            }
            catch (LicenseUserLimitExceededException e) {
                this.addActionError(this.getText("not.licensed"), this.getContactAdminUrl());
                return "error";
            }
            return "success";
        }
        this.addActionError(this.getText("easyuser.invalid.token.for.given.username"));
        return "error";
    }

    private String[] getContactAdminUrl() {
        return new String[]{this.getBootstrapStatusProvider().getWebAppContextPath() + "/administrators.action"};
    }

    public String getLinkLoginURL(HttpServletRequest request) {
        return SeraphUtils.getLinkLoginURL(request, this.username);
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setSignupManager(SignupManager signupManager) {
        this.signupManager = signupManager;
    }

    @Override
    public boolean isPermitted() {
        return true;
    }

    public void setCrowdService(CrowdService crowdService) {
        this.crowdService = crowdService;
    }

    @Override
    public UserSignupEvent getEventToPublish(String result) {
        if ("success".equals(result)) {
            ConfluenceUser user = this.userAccessor.getUserByName(this.username);
            return new ConfirmEmailAddressEvent(this, user);
        }
        return null;
    }
}

