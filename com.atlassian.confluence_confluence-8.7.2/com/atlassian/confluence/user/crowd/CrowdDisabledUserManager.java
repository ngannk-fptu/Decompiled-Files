/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.CrowdService
 *  com.atlassian.crowd.embedded.api.User
 *  com.atlassian.crowd.exception.InvalidUserException
 *  com.atlassian.crowd.exception.OperationNotPermittedException
 *  com.atlassian.crowd.exception.UserNotFoundException
 *  com.atlassian.crowd.model.user.UserTemplate
 *  com.atlassian.user.User
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.user.crowd;

import com.atlassian.confluence.user.DisabledUserManager;
import com.atlassian.confluence.user.crowd.CrowdUserConversionHelper;
import com.atlassian.crowd.embedded.api.CrowdService;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.crowd.exception.InvalidUserException;
import com.atlassian.crowd.exception.OperationNotPermittedException;
import com.atlassian.crowd.exception.UserNotFoundException;
import com.atlassian.crowd.model.user.UserTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CrowdDisabledUserManager
implements DisabledUserManager {
    private static final Logger log = LoggerFactory.getLogger(CrowdDisabledUserManager.class);
    private final CrowdService crowdService;

    public CrowdDisabledUserManager(CrowdService crowdService) {
        this.crowdService = crowdService;
    }

    @Override
    public boolean isDisabled(User user) {
        return user != null && !user.isActive();
    }

    @Override
    public boolean isDisabled(com.atlassian.user.User user) {
        User crowdUser = new CrowdUserConversionHelper(this.crowdService).toCrowdUser(user);
        return this.isDisabled(crowdUser);
    }

    @Override
    public boolean isDisabled(String username) {
        if (username == null) {
            return false;
        }
        User user = this.crowdService.getUser(username);
        if (user == null) {
            return true;
        }
        return this.isDisabled(user);
    }

    @Override
    public void disableUser(User user) throws UserNotFoundException {
        if (!user.isActive()) {
            return;
        }
        UserTemplate crowdUser = new UserTemplate(user);
        crowdUser.setActive(false);
        try {
            this.crowdService.updateUser((User)crowdUser);
        }
        catch (InvalidUserException e) {
            throw new RuntimeException(e);
        }
        catch (OperationNotPermittedException e) {
            log.error("Could not disable user", (Throwable)e);
        }
    }

    @Override
    public void enableUser(User user) throws UserNotFoundException {
        if (user.isActive()) {
            return;
        }
        UserTemplate crowdUser = new UserTemplate(user);
        crowdUser.setActive(true);
        try {
            this.crowdService.updateUser((User)crowdUser);
        }
        catch (InvalidUserException e) {
            throw new RuntimeException(e);
        }
        catch (OperationNotPermittedException e) {
            log.error("Could not enable user", (Throwable)e);
        }
    }
}

