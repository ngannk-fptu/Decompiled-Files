/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.atlassian.user.User
 *  com.atlassian.velocity.htmlsafe.HtmlFragment
 */
package com.atlassian.confluence.admin.actions;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.impl.security.AdminOnly;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.util.HTMLPairType;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.atlassian.user.User;
import com.atlassian.velocity.htmlsafe.HtmlFragment;
import java.util.ArrayList;
import java.util.List;

@Deprecated
@WebSudoRequired
@AdminOnly
public class AttachmentStorageSetupAction
extends ConfluenceActionSupport {
    protected List attachmentStorageTypes;
    protected String attachmentStorageType;
    protected String originalAttachmentStorageType;

    @Override
    public String doDefault() throws Exception {
        this.originalAttachmentStorageType = this.attachmentStorageType = this.getAttachmentDataStoreOrDefault();
        return super.doDefault();
    }

    public String execute() throws Exception {
        if (this.hasErrors()) {
            return "error";
        }
        if (this.attachmentStorageType != null && !this.getAttachmentDataStoreOrDefault().equals(this.attachmentStorageType)) {
            return "migrate";
        }
        return "success";
    }

    private String getAttachmentDataStoreOrDefault() {
        String setting = this.settingsManager.getGlobalSettings().getAttachmentDataStore();
        if (setting == null) {
            return "file.system.based.attachments.storage";
        }
        return setting;
    }

    public String getLocalAttachmentDirectory() {
        return this.getBootstrapManager().getString("attachments.dir");
    }

    public boolean isFileSystemConfigured() {
        return "file.system.based.attachments.storage".equals(this.attachmentStorageType);
    }

    public boolean isDatabaseConfigured() {
        return "database.based.attachments.storage".equals(this.attachmentStorageType);
    }

    public List getAttachmentStorageTypes() {
        if (this.attachmentStorageTypes == null) {
            this.attachmentStorageTypes = new ArrayList();
            this.attachmentStorageTypes.add(new HTMLPairType(new HtmlFragment((Object)this.getText("file.system.based.attachments.storage")), new HtmlFragment((Object)"file.system.based.attachments.storage")));
            this.attachmentStorageTypes.add(new HTMLPairType(new HtmlFragment((Object)this.getText("database.based.attachments.storage")), new HtmlFragment((Object)"database.based.attachments.storage")));
        }
        return this.attachmentStorageTypes;
    }

    public void setAttachmentStorageTypes(List attachmentStorageTypes) {
        this.attachmentStorageTypes = attachmentStorageTypes;
    }

    public String getAttachmentStorageType() {
        return this.attachmentStorageType;
    }

    public void setAttachmentStorageType(String attachmentStorageType) {
        this.attachmentStorageType = attachmentStorageType;
    }

    public String getOriginalAttachmentStorageType() {
        return this.originalAttachmentStorageType;
    }

    @Override
    public boolean isPermitted() {
        return this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.ADMINISTER, PermissionManager.TARGET_SYSTEM);
    }
}

