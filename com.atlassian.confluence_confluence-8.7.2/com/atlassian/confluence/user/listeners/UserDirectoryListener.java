/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.CrowdDirectoryService
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.api.User
 *  com.atlassian.crowd.embedded.api.UserComparator
 *  com.atlassian.crowd.embedded.atlassianuser.EmbeddedCrowdUser
 *  com.atlassian.crowd.embedded.impl.IdentifierUtils
 *  com.atlassian.crowd.model.user.User
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.seraph.spi.rememberme.RememberMeTokenDao
 *  com.atlassian.user.EntityException
 *  com.atlassian.user.User
 *  com.atlassian.user.UserManager
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.confluence.user.listeners;

import com.atlassian.confluence.event.events.user.DirectoryUserRenamedEvent;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.ConfluenceUserImpl;
import com.atlassian.confluence.user.PersonalInformation;
import com.atlassian.confluence.user.PersonalInformationManager;
import com.atlassian.confluence.user.persistence.dao.ConfluenceUserDao;
import com.atlassian.crowd.embedded.api.CrowdDirectoryService;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.crowd.embedded.api.UserComparator;
import com.atlassian.crowd.embedded.atlassianuser.EmbeddedCrowdUser;
import com.atlassian.crowd.embedded.impl.IdentifierUtils;
import com.atlassian.event.api.EventListener;
import com.atlassian.seraph.spi.rememberme.RememberMeTokenDao;
import com.atlassian.user.EntityException;
import com.atlassian.user.UserManager;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Qualifier;

public class UserDirectoryListener {
    private final ConfluenceUserDao confluenceUserDao;
    private final RememberMeTokenDao rememberMeTokenDao;
    private final PersonalInformationManager personalInformationManager;
    private final UserManager backingUserManager;
    private final CrowdDirectoryService crowdDirectoryService;

    public UserDirectoryListener(ConfluenceUserDao confluenceUserDao, RememberMeTokenDao rememberMeTokenDao, @Qualifier(value="personalInformationManager") PersonalInformationManager personalInformationManager, @Qualifier(value="backingUserManager") UserManager backingUserManager, CrowdDirectoryService crowdDirectoryService) {
        this.confluenceUserDao = confluenceUserDao;
        this.rememberMeTokenDao = rememberMeTokenDao;
        this.personalInformationManager = personalInformationManager;
        this.backingUserManager = backingUserManager;
        this.crowdDirectoryService = crowdDirectoryService;
    }

    @EventListener
    public void onUserRenamedEvent(DirectoryUserRenamedEvent event) {
        boolean shadowedUserRenamedToShadowedUser;
        com.atlassian.crowd.model.user.User renamedUser = event.getUser();
        String oldUsername = event.getOldUsername();
        String newUsername = renamedUser.getName();
        EmbeddedCrowdUser userWithOldUsername = this.findBackingUser(oldUsername);
        EmbeddedCrowdUser userWithNewUsername = this.findBackingUser(newUsername);
        boolean caseOnlyRename = IdentifierUtils.equalsInLowerCase((String)oldUsername, (String)newUsername);
        boolean wasVisible = userWithOldUsername == null || this.isInHigherOrEqualDirectory((User)renamedUser, (User)userWithOldUsername);
        boolean isVisible = UserComparator.equal((User)userWithNewUsername, (User)renamedUser);
        boolean visibleUserRenamedToVisibleUser = wasVisible && isVisible;
        boolean visibleUserRenamedToShadowedUser = wasVisible && !isVisible;
        boolean shadowedUserRenamedToVisibleUser = !wasVisible && isVisible;
        boolean bl = shadowedUserRenamedToShadowedUser = !wasVisible && !isVisible;
        if (visibleUserRenamedToVisibleUser || visibleUserRenamedToShadowedUser) {
            ConfluenceUser oldConfluenceUser = this.confluenceUserDao.findByUsername(oldUsername);
            PersonalInformation currentInfo = null;
            if (oldConfluenceUser != null) {
                currentInfo = this.personalInformationManager.getOrCreatePersonalInformation(oldConfluenceUser);
            }
            ConfluenceUser user = this.confluenceUserDao.rename(oldUsername, newUsername, isVisible);
            this.updatePersonalInformation(currentInfo, user);
            this.rememberMeTokenDao.removeAllForUser(oldUsername);
            this.rememberMeTokenDao.removeAllForUser(newUsername);
            if (!caseOnlyRename && userWithOldUsername != null) {
                this.confluenceUserDao.create(new ConfluenceUserImpl((com.atlassian.user.User)userWithOldUsername));
            }
        } else if (shadowedUserRenamedToVisibleUser) {
            this.confluenceUserDao.deactivateUser(newUsername);
            ConfluenceUserImpl confluenceUser = new ConfluenceUserImpl((com.atlassian.user.User)userWithNewUsername);
            this.confluenceUserDao.create(confluenceUser);
            this.personalInformationManager.getOrCreatePersonalInformation(confluenceUser);
        } else if (!shadowedUserRenamedToShadowedUser) {
            throw new IllegalStateException();
        }
    }

    private EmbeddedCrowdUser findBackingUser(String username) {
        try {
            return (EmbeddedCrowdUser)this.backingUserManager.getUser(username);
        }
        catch (EntityException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isInHigherOrEqualDirectory(User user1, User user2) {
        for (Directory directory : this.crowdDirectoryService.findAllDirectories()) {
            if (directory.getId().longValue() == user1.getDirectoryId()) {
                return true;
            }
            if (directory.getId().longValue() != user2.getDirectoryId()) continue;
            return false;
        }
        return false;
    }

    private void updatePersonalInformation(@Nullable PersonalInformation currentInfo, ConfluenceUser user) {
        if (currentInfo == null) {
            this.personalInformationManager.getOrCreatePersonalInformation(user);
            return;
        }
        PersonalInformation oldInfo = (PersonalInformation)currentInfo.clone();
        currentInfo.setUser(user);
        this.personalInformationManager.savePersonalInformation(currentInfo, oldInfo);
    }
}

