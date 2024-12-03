/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.security.delegate;

import com.atlassian.confluence.content.ContentType;
import com.atlassian.confluence.content.ContentTypeManager;
import com.atlassian.confluence.content.CustomContentEntityObject;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.ContentPermissionManager;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.Draft;
import com.atlassian.confluence.security.DefaultPermissionManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionDelegate;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.delegate.AbstractPermissionsDelegate;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.PersonalInformation;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.user.User;
import java.util.List;

public class AttachmentPermissionsDelegate
extends AbstractPermissionsDelegate<Attachment> {
    private PermissionManager permissionManager;
    private ContentPermissionManager contentPermissionManager;
    private SpaceManager spaceManager;
    private ContentTypeManager contentTypeManager;
    private AttachmentManager attachmentManager;

    @Override
    public boolean canView(User user, Attachment target) {
        return this.permissionManager.hasPermissionNoExemptions(user, Permission.VIEW, target.getContainer());
    }

    @Override
    public boolean canEdit(User user, Attachment target) {
        return this.permissionManager.hasPermissionNoExemptions(user, Permission.EDIT, target.getContainer());
    }

    @Override
    public boolean canSetPermissions(User user, Attachment target) {
        throw new IllegalStateException("Permission-setting privileges do not apply to comments");
    }

    @Override
    public boolean canRemove(User user, Attachment target) {
        ContentEntityObject container = target.getContainer();
        if (container instanceof PersonalInformation) {
            return user != null && ((PersonalInformation)container).getUsername().equals(user.getName());
        }
        if (container instanceof SpaceContentEntityObject) {
            return (this.hasSpaceLevelPermission("REMOVEATTACHMENT", user, target) || this.canRemoveOwn(target, user)) && this.contentPermissionManager.hasContentLevelPermission(user, "Edit", container) && this.permissionManager.hasPermissionNoExemptions(user, Permission.VIEW, container);
        }
        if (container instanceof Draft) {
            Space space = this.getSpaceManager().getSpace(((Draft)container).getDraftSpaceKey());
            if (space == null) {
                throw new IllegalStateException("Space does not exist");
            }
            return (this.spacePermissionManager.hasPermissionNoExemptions("REMOVEATTACHMENT", space, user) || this.canRemoveOwn(target, user)) && this.permissionManager.hasPermissionNoExemptions(user, Permission.VIEW, container);
        }
        if (container == null) {
            throw new IllegalStateException("Did not expect to find a top level attachment");
        }
        throw new IllegalStateException("Did not expect to find an attachment inside a " + container.getClass());
    }

    private boolean canRemoveOwn(Attachment attachment, User user) {
        Attachment earliestAttachmentVersion = this.getEarliestVersion(attachment);
        boolean isCreator = earliestAttachmentVersion.getCreator() != null && user != null && user.getName().equals(earliestAttachmentVersion.getCreator().getName());
        return isCreator && this.spacePermissionManager.hasPermissionNoExemptions("REMOVEOWNCONTENT", this.getSpaceFrom(attachment), user);
    }

    private Attachment getEarliestVersion(Attachment attachment) {
        List<Attachment> attachmentHistories = this.attachmentManager.getAllVersions(attachment);
        Attachment earliest = attachment;
        for (Attachment attachmentHistory : attachmentHistories) {
            if (earliest.getVersion() <= attachmentHistory.getVersion()) continue;
            earliest = attachmentHistory;
        }
        return earliest;
    }

    @Override
    public boolean canExport(User user, Attachment target) {
        throw new IllegalStateException("Export privileges do not apply to attachments");
    }

    @Override
    public boolean canAdminister(User user, Attachment target) {
        throw new IllegalStateException("Administration privileges do not apply to attachments");
    }

    @Override
    public boolean canCreate(User user, Object container) {
        if (container instanceof Attachment) {
            throw new IllegalStateException("Attachments can not be created inside " + container.getClass());
        }
        if (container instanceof CustomContentEntityObject) {
            ContentType contentType = this.contentTypeManager.getContentType(((CustomContentEntityObject)container).getPluginModuleKey());
            PermissionDelegate containerPermissionDelegate = contentType.getPermissionDelegate();
            if (containerPermissionDelegate == null) {
                throw new IllegalStateException("Error in computing canCreate() for " + container + ". No PermissionDelegate has been defined for " + contentType);
            }
            return containerPermissionDelegate.canCreate(user, container) && containerPermissionDelegate.canCreateInTarget(user, Attachment.class);
        }
        if (container instanceof SpaceContentEntityObject) {
            return this.permissionManager.hasPermissionNoExemptions(user, Permission.VIEW, container) && this.contentPermissionManager.hasContentLevelPermission(user, "Edit", (SpaceContentEntityObject)container) && this.spacePermissionManager.hasPermissionNoExemptions("CREATEATTACHMENT", ((SpaceContentEntityObject)container).getSpace(), user);
        }
        if (container instanceof Draft) {
            Draft draft = (Draft)container;
            if (!draft.isPersistent()) {
                return false;
            }
            String spaceKey = draft.getDraftSpaceKey();
            Space space = this.getSpaceManager().getSpace(spaceKey);
            if (space == null) {
                throw new IllegalStateException("Space does not exist: " + spaceKey);
            }
            return this.permissionManager.hasPermissionNoExemptions(user, Permission.VIEW, container) && this.spacePermissionManager.hasPermissionNoExemptions("CREATEATTACHMENT", space, user);
        }
        if (container instanceof Space) {
            return this.permissionManager.hasPermissionNoExemptions(user, Permission.ADMINISTER, container) && this.spacePermissionManager.hasPermissionNoExemptions("CREATEATTACHMENT", (Space)container, user);
        }
        if (container instanceof Comment) {
            return false;
        }
        throw new IllegalStateException("Attachments can not be created inside " + container.getClass());
    }

    public void setSpaceManager(SpaceManager spaceManager) {
        this.spaceManager = spaceManager;
    }

    @Deprecated
    public void setPermissionManagerTarget(DefaultPermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }

    public void setPermissionManager(PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }

    public void setContentPermissionManager(ContentPermissionManager contentPermissionManager) {
        this.contentPermissionManager = contentPermissionManager;
    }

    @Override
    protected Space getSpaceFrom(Object target) {
        ContentEntityObject entity = ((Attachment)target).getContainer();
        return entity instanceof SpaceContentEntityObject ? ((SpaceContentEntityObject)entity).getSpace() : null;
    }

    public SpaceManager getSpaceManager() {
        if (this.spaceManager == null) {
            this.spaceManager = (SpaceManager)ContainerManager.getComponent((String)"spaceManager");
        }
        return this.spaceManager;
    }

    public void setContentTypeManager(ContentTypeManager contentTypeManager) {
        this.contentTypeManager = contentTypeManager;
    }

    public void setAttachmentManager(AttachmentManager attachmentManager) {
        this.attachmentManager = attachmentManager;
    }
}

