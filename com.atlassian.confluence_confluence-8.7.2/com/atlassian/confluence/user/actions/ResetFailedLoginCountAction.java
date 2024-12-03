/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.accessmode.ReadOnlyAccessAllowed
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.user.actions;

import com.atlassian.confluence.api.service.accessmode.ReadOnlyAccessAllowed;
import com.atlassian.confluence.security.login.LoginManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.actions.AbstractUsersAction;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ReadOnlyAccessAllowed
public class ResetFailedLoginCountAction
extends AbstractUsersAction {
    private static final Logger log = LoggerFactory.getLogger(ResetFailedLoginCountAction.class);
    private LoginManager loginManager;

    @Override
    public void setLoginManager(LoginManager loginManager) {
        this.loginManager = loginManager;
    }

    @PermittedMethods(value={HttpMethod.GET, HttpMethod.POST})
    public String execute() throws Exception {
        ConfluenceUser user = this.getUser();
        if (null != user) {
            this.loginManager.resetFailedLoginCount(user);
            log.info("Reset failed login count for {}", (Object)user);
        }
        return "success";
    }
}

