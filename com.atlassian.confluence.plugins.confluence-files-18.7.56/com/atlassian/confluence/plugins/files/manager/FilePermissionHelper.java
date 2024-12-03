/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.pages.Contained
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.security.SpacePermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.files.manager;

import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.Contained;
import com.atlassian.confluence.plugins.files.entities.ConfluenceFileEntity;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FilePermissionHelper {
    private PermissionManager permissionManager;
    private SpacePermissionManager spacePermissionManager;

    @Autowired
    public FilePermissionHelper(@ComponentImport PermissionManager permissionManager, @ComponentImport SpacePermissionManager spacePermissionManager) {
        this.permissionManager = permissionManager;
        this.spacePermissionManager = spacePermissionManager;
    }

    public boolean hasCreateAnnotationPermission(Contained contained) {
        return this.permissionManager.hasCreatePermission((User)AuthenticatedUserThreadLocal.get(), (Object)contained.getContainer(), Comment.class);
    }

    private boolean hasEditAnnotationPermission(boolean hasCreateAnnotationPermission, Attachment file) {
        if (AuthenticatedUserThreadLocal.isAnonymousUser()) {
            return false;
        }
        boolean isFileOwner = AuthenticatedUserThreadLocal.get().equals(file.getCreator());
        return hasCreateAnnotationPermission && (isFileOwner || this.hasAdminSpacePermission(file.getSpace()));
    }

    private boolean hasDeleteAnnotationsPermission(Attachment file) {
        if (AuthenticatedUserThreadLocal.isAnonymousUser()) {
            return this.hasDeleteAnnotationSpacePermission(file.getSpace());
        }
        return AuthenticatedUserThreadLocal.get().equals(file.getCreator()) || this.hasDeleteAnnotationSpacePermission(file.getSpace());
    }

    private boolean hasAdminSpacePermission(Space space) {
        return this.spacePermissionManager.hasPermission("SETSPACEPERMISSIONS", space, (User)AuthenticatedUserThreadLocal.get());
    }

    private boolean hasDeleteAnnotationSpacePermission(Space space) {
        return this.spacePermissionManager.hasPermission("REMOVECOMMENT", space, (User)AuthenticatedUserThreadLocal.get());
    }

    private boolean hasUploadNewVersionPermission(Attachment file) {
        return this.permissionManager.hasCreatePermission((User)AuthenticatedUserThreadLocal.get(), (Object)file.getContainer(), Attachment.class);
    }

    public boolean hasCommentEditPermission(Comment comment) {
        return this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.EDIT, (Object)comment);
    }

    public boolean hasCommentDeletePermission(Comment comment) {
        return this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.REMOVE, (Object)comment);
    }

    public void setupPermission(ConfluenceFileEntity fileEntity, Attachment file) {
        boolean hasCreatePermission = this.hasCreateAnnotationPermission((Contained)file);
        fileEntity.setHasDeletePermission(this.hasDeleteAnnotationsPermission(file));
        fileEntity.setHasEditPermission(this.hasEditAnnotationPermission(hasCreatePermission, file));
        fileEntity.setHasReplyPermission(hasCreatePermission);
        fileEntity.setHasResolvePermission(hasCreatePermission);
        fileEntity.setHasUploadAttachmentVersionPermission(this.hasUploadNewVersionPermission(file));
    }
}

