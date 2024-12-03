/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Attributes
 *  com.atlassian.crowd.exception.DirectoryNotFoundException
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.atlassian.crowd.exception.UserNotFoundException
 *  com.atlassian.crowd.manager.application.ApplicationService
 *  com.atlassian.crowd.manager.directory.DirectoryManager
 *  com.atlassian.crowd.model.application.Application
 *  com.atlassian.crowd.model.user.User
 *  com.atlassian.crowd.model.user.UserWithAttributes
 *  com.atlassian.plugins.rest.common.Link
 *  com.google.common.base.Preconditions
 *  org.apache.commons.lang3.Validate
 */
package com.atlassian.crowd.plugin.rest.util;

import com.atlassian.crowd.embedded.api.Attributes;
import com.atlassian.crowd.exception.DirectoryNotFoundException;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.exception.UserNotFoundException;
import com.atlassian.crowd.manager.application.ApplicationService;
import com.atlassian.crowd.manager.directory.DirectoryManager;
import com.atlassian.crowd.model.application.Application;
import com.atlassian.crowd.model.user.User;
import com.atlassian.crowd.model.user.UserWithAttributes;
import com.atlassian.crowd.plugin.rest.entity.UserEntity;
import com.atlassian.crowd.plugin.rest.util.EntityTranslator;
import com.atlassian.crowd.plugin.rest.util.LinkUriHelper;
import com.atlassian.plugins.rest.common.Link;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.Validate;

public class UserEntityUtil {
    private UserEntityUtil() {
    }

    public static UserEntity expandUser(ApplicationService applicationService, Application application, UserEntity minimalUserEntity, boolean expandAttributes) throws UserNotFoundException {
        UserEntity expandedUser;
        Validate.notNull((Object)applicationService);
        Validate.notNull((Object)application);
        Validate.notNull((Object)minimalUserEntity);
        Validate.notNull((Object)minimalUserEntity.getName(), (String)"Minimal user entity must include a username", (Object[])new Object[0]);
        Validate.notNull((Object)minimalUserEntity.getLink(), (String)"Minimal user entity must include a link", (Object[])new Object[0]);
        String username = minimalUserEntity.getName();
        Link userLink = minimalUserEntity.getLink();
        if (expandAttributes) {
            UserWithAttributes user = applicationService.findUserWithAttributesByName(application, username);
            Link updatedLink = LinkUriHelper.updateUserLink(userLink, user.getName());
            expandedUser = EntityTranslator.toUserEntity((User)user, (Attributes)user, updatedLink);
        } else {
            User user = applicationService.findUserByName(application, username);
            Link updatedLink = LinkUriHelper.updateUserLink(userLink, user.getName());
            expandedUser = EntityTranslator.toUserEntity(user, updatedLink);
        }
        return expandedUser;
    }

    public static UserEntity translate(User user, Link oldLink) {
        Link updatedLink = LinkUriHelper.updateUserLink(oldLink, user.getName());
        return EntityTranslator.toUserEntity(user, updatedLink);
    }

    public static UserEntity translateWithAttributes(User user, Attributes attributes, Link oldLink) {
        Link updatedLink = LinkUriHelper.updateUserLink(oldLink, user.getName());
        return EntityTranslator.toUserEntity(user, attributes, updatedLink);
    }

    public static UserEntity expandUser(DirectoryManager directoryManager, long directoryId, UserEntity minimalUserEntity, boolean expandAttributes) throws UserNotFoundException, DirectoryNotFoundException, OperationFailedException {
        UserEntity expandedUser;
        Preconditions.checkNotNull((Object)directoryManager);
        Preconditions.checkNotNull((Object)directoryManager);
        Preconditions.checkArgument((directoryId > 0L ? 1 : 0) != 0, (String)"The directory id must be greater than 0, %d", (long)directoryId);
        Preconditions.checkNotNull((Object)minimalUserEntity);
        Preconditions.checkNotNull((Object)minimalUserEntity.getName(), (Object)"Minimal user entity must include a username");
        Preconditions.checkNotNull((Object)minimalUserEntity.getLink(), (Object)"Minimal user entity must include a link");
        String username = minimalUserEntity.getName();
        Link userLink = minimalUserEntity.getLink();
        if (expandAttributes) {
            UserWithAttributes user = directoryManager.findUserWithAttributesByName(directoryId, username);
            Link updatedLink = LinkUriHelper.updateUserLink(userLink, user.getName());
            expandedUser = EntityTranslator.toUserEntity((User)user, (Attributes)user, updatedLink);
        } else {
            User user = directoryManager.findUserByName(directoryId, username);
            Link updatedLink = LinkUriHelper.updateUserLink(userLink, user.getName());
            expandedUser = EntityTranslator.toUserEntity(user, updatedLink);
        }
        expandedUser.setDirectoryId(directoryId);
        expandedUser.setDirectoryName(directoryManager.findDirectoryById(directoryId).getName());
        return expandedUser;
    }
}

