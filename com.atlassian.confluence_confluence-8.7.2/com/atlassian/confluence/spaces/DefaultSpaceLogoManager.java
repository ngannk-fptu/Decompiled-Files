/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.webresource.UrlMode
 *  com.atlassian.plugin.webresource.WebResourceUrlProvider
 *  com.atlassian.user.User
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.spaces;

import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceLogoManager;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserProfilePictureAccessor;
import com.atlassian.confluence.user.actions.ProfilePictureInfo;
import com.atlassian.plugin.webresource.UrlMode;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import com.atlassian.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultSpaceLogoManager
implements SpaceLogoManager {
    private static final Logger log = LoggerFactory.getLogger(DefaultSpaceLogoManager.class);
    private final UserProfilePictureAccessor userProfilePictureAccessor;
    private final PermissionManager permissionManager;
    private final SpaceManager spaceManager;
    private final WebResourceUrlProvider webResourceUrlProvider;

    public DefaultSpaceLogoManager(UserProfilePictureAccessor userProfilePictureAccessor, PermissionManager permissionManager, SpaceManager spaceManager, WebResourceUrlProvider webResourceUrlProvider) {
        this.userProfilePictureAccessor = userProfilePictureAccessor;
        this.permissionManager = permissionManager;
        this.spaceManager = spaceManager;
        this.webResourceUrlProvider = webResourceUrlProvider;
    }

    @Override
    public String getLogoDownloadPath(Space space, User viewingUser) {
        if (space.isPersonal()) {
            ConfluenceUser owner = space.getCreator();
            if (this.permissionManager.hasPermission(viewingUser, Permission.VIEW, owner)) {
                ProfilePictureInfo profilePic = this.userProfilePictureAccessor.getUserProfilePicture(owner);
                if (profilePic.isExternal()) {
                    log.warn("getDownloadPath method is deprecated and is not supposed to be used with external avatars. Please use getLogoUriReference instead. Falling back to default avatar. Real avatar url is [{}]", (Object)profilePic.getUriReference());
                    return "/images/icons/profilepics/default.svg";
                }
                return profilePic.getDownloadPath();
            }
            return "/images/icons/profilepics/anonymous.svg";
        }
        return this.spaceManager.getLogoForSpace(space.getKey()).getDownloadPath();
    }

    @Override
    public String getLogoUriReference(Space space, User viewingUser) {
        if (space.isPersonal()) {
            ConfluenceUser owner = space.getCreator();
            if (this.permissionManager.hasPermission(viewingUser, Permission.VIEW, owner)) {
                ProfilePictureInfo profilePic = this.userProfilePictureAccessor.getUserProfilePicture(owner);
                return profilePic.getUriReference();
            }
            return this.webResourceUrlProvider.getBaseUrl(UrlMode.RELATIVE) + "/images/icons/profilepics/anonymous.svg";
        }
        return this.webResourceUrlProvider.getBaseUrl(UrlMode.RELATIVE) + this.spaceManager.getLogoForSpace(space.getKey()).getDownloadPath();
    }
}

