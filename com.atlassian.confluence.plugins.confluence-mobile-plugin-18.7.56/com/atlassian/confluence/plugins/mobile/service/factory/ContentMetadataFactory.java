/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bonnie.Searchable
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.like.LikeManager
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.security.SpacePermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.mobile.service.factory;

import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.like.LikeManager;
import com.atlassian.confluence.plugins.mobile.dto.LocationDto;
import com.atlassian.confluence.plugins.mobile.dto.metadata.ContentMetadataDto;
import com.atlassian.confluence.plugins.mobile.dto.metadata.CurrentUserMetadataDto;
import com.atlassian.confluence.plugins.mobile.dto.metadata.CurrentUserPermissionDto;
import com.atlassian.confluence.plugins.mobile.dto.metadata.LikeMetadataDto;
import com.atlassian.confluence.plugins.mobile.helper.ContentHelper;
import com.atlassian.confluence.plugins.mobile.model.Context;
import com.atlassian.confluence.plugins.mobile.service.factory.ContextServiceFactory;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ContentMetadataFactory {
    private final LikeManager likeManager;
    private final PermissionManager permissionManager;
    private final SpacePermissionManager spacePermissionManager;
    private final SpaceManager spaceManager;
    private final ContextServiceFactory contextServiceFactory;

    @Autowired
    public ContentMetadataFactory(@ComponentImport LikeManager likeManager, @ComponentImport PermissionManager permissionManager, @ComponentImport SpacePermissionManager spacePermissionManager, @ComponentImport SpaceManager spaceManager, ContextServiceFactory contextServiceFactory) {
        this.likeManager = likeManager;
        this.permissionManager = permissionManager;
        this.spacePermissionManager = spacePermissionManager;
        this.spaceManager = spaceManager;
        this.contextServiceFactory = contextServiceFactory;
    }

    public ContentMetadataDto buildMetadata(ContentEntityObject content) {
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        CurrentUserMetadataDto.Builder currentUserMetadata = new CurrentUserMetadataDto.Builder();
        currentUserMetadata.liked(this.likeManager.hasLike(content, (User)AuthenticatedUserThreadLocal.get()));
        currentUserMetadata.saved(ContentHelper.isSaved(content.getLabels()));
        currentUserMetadata.permission(new CurrentUserPermissionDto(this.permissionManager.hasPermissionNoExemptions((User)currentUser, Permission.EDIT, (Object)content), this.permissionManager.hasPermissionNoExemptions((User)currentUser, Permission.REMOVE, (Object)content), this.permissionManager.hasPermissionNoExemptions((User)currentUser, Permission.SET_PERMISSIONS, (Object)content)));
        LikeMetadataDto likes = new LikeMetadataDto();
        likes.setCount(this.likeManager.countLikes((Searchable)content));
        return ContentMetadataDto.builder().currentUser(currentUserMetadata.build()).likes(likes).build();
    }

    public ContentMetadataDto buildMetadata(Context context) {
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        LocationDto locationDto = this.contextServiceFactory.getContextService(context.getType()).getPageCreateLocation(context);
        Space space = this.spaceManager.getSpace(locationDto.getSpace().getKey());
        CurrentUserMetadataDto currentUserMetadata = CurrentUserMetadataDto.builder().permission(new CurrentUserPermissionDto(false, false, this.spacePermissionManager.hasPermissionNoExemptions("SETPAGEPERMISSIONS", space, (User)currentUser))).build();
        return ContentMetadataDto.builder().currentUser(currentUserMetadata).location(locationDto).build();
    }
}

