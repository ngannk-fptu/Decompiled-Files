/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  com.atlassian.xwork.RequireSecurityToken
 */
package com.atlassian.confluence.pages.actions;

import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.actions.AbstractRemoveAttachmentAction;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.user.User;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import com.atlassian.xwork.RequireSecurityToken;

public class RemoveAttachedFileVersionAction
extends AbstractRemoveAttachmentAction {
    @PermittedMethods(value={HttpMethod.GET})
    public String execute() {
        return "success";
    }

    @PermittedMethods(value={HttpMethod.POST})
    @RequireSecurityToken(value=true)
    public String doRemove() {
        Attachment attachment = this.getAttachment();
        this.attachmentManager.removeAttachmentVersionFromServer(attachment);
        return "success";
    }

    @Override
    protected String localiseActionName(String actionI18NKey) {
        return super.localiseActionName("remove.attachment.version.confirmation.title");
    }

    public void setVersion(int version) {
        this.attachmentBean.setVersion(version);
    }

    @Override
    public int getVersion() {
        return this.attachmentBean.getVersion();
    }

    @Override
    public boolean isPermitted() {
        Space space = this.getSpace();
        if (this.accessModeService.isReadOnlyAccessModeEnabled()) {
            return false;
        }
        if (space == null) {
            return false;
        }
        return this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.ADMINISTER, space);
    }
}

