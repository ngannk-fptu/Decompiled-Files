/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.CrowdService
 *  com.atlassian.crowd.embedded.api.Group
 *  com.atlassian.crowd.embedded.api.User
 *  com.atlassian.crowd.exception.OperationNotPermittedException
 *  com.atlassian.seraph.auth.AuthenticationContextAwareAuthenticator
 *  com.atlassian.seraph.auth.AuthenticatorException
 *  com.atlassian.spring.container.ContainerManager
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.transaction.PlatformTransactionManager
 *  org.springframework.transaction.TransactionStatus
 *  org.springframework.transaction.support.TransactionCallback
 *  org.springframework.transaction.support.TransactionCallbackWithoutResult
 *  org.springframework.transaction.support.TransactionTemplate
 */
package com.atlassian.confluence.user;

import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.user.ConfluenceAuthenticator;
import com.atlassian.crowd.embedded.api.CrowdService;
import com.atlassian.crowd.embedded.api.Group;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.crowd.exception.OperationNotPermittedException;
import com.atlassian.seraph.auth.AuthenticationContextAwareAuthenticator;
import com.atlassian.seraph.auth.AuthenticatorException;
import com.atlassian.spring.container.ContainerManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

@AuthenticationContextAwareAuthenticator
public class ConfluenceGroupJoiningAuthenticator
extends ConfluenceAuthenticator {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceGroupJoiningAuthenticator.class);

    @Override
    public boolean login(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, String username, String password, boolean setRememberMeCookie) throws AuthenticatorException {
        boolean loginSucceeded = super.login(httpServletRequest, httpServletResponse, username, password, setRememberMeCookie);
        if (loginSucceeded) {
            User user = ConfluenceGroupJoiningAuthenticator.getCrowdService().getUser(username);
            this.postLogin(user);
        }
        return loginSucceeded;
    }

    void postLogin(final User user) {
        new TransactionTemplate(ConfluenceGroupJoiningAuthenticator.getTransactionManager()).execute((TransactionCallback)new TransactionCallbackWithoutResult(){

            protected void doInTransactionWithoutResult(TransactionStatus status) {
                ConfluenceGroupJoiningAuthenticator.addUserToGroup(user, ConfluenceGroupJoiningAuthenticator.getSettingsManager().getGlobalSettings().getDefaultUsersGroup());
            }
        });
    }

    protected static boolean addUserToGroup(User user, String groupName) {
        CrowdService crowdService = ConfluenceGroupJoiningAuthenticator.getCrowdService();
        try {
            Group group = crowdService.getGroup(groupName);
            if (group == null) {
                log.error("Failed to add '{}' to group '{}' because the group could not be found.", (Object)user.getName(), (Object)groupName);
                return false;
            }
            if (crowdService.isUserMemberOfGroup(user, group)) {
                log.debug("User '{}' is already a member of group: {}", (Object)user.getName(), (Object)group.getName());
                return false;
            }
            log.debug("Adding user '{}' to group: {}", (Object)user.getName(), (Object)group.getName());
            crowdService.addUserToGroup(user, group);
            return true;
        }
        catch (OperationNotPermittedException e) {
            log.error("Failed to add '" + user.getName() + "' to '" + groupName + "'.", (Throwable)e);
            return false;
        }
    }

    protected static CrowdService getCrowdService() {
        return (CrowdService)ContainerManager.getComponent((String)"crowdService");
    }

    private static PlatformTransactionManager getTransactionManager() {
        return (PlatformTransactionManager)ContainerManager.getComponent((String)"transactionManager");
    }

    private static SettingsManager getSettingsManager() {
        return (SettingsManager)ContainerManager.getComponent((String)"settingsManager");
    }
}

