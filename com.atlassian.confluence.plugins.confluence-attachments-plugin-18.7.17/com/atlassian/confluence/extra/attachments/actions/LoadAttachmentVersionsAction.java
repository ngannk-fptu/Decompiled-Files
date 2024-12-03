/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ConfluenceActionSupport
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.pages.AttachmentManager
 *  com.atlassian.confluence.plugin.descriptor.web.DefaultWebInterfaceContext
 *  com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 */
package com.atlassian.confluence.extra.attachments.actions;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.plugin.descriptor.web.DefaultWebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.security.Permission;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import java.util.ArrayList;
import java.util.List;

public class LoadAttachmentVersionsAction
extends ConfluenceActionSupport {
    @ComponentImport
    private AttachmentManager attachmentManager;
    private long attachmentId;
    private List<Attachment> allVersions;
    private Attachment currentVersion;

    public void setAttachmentManager(AttachmentManager attachmentManager) {
        this.attachmentManager = attachmentManager;
    }

    public boolean isPermitted() {
        Attachment attachment = this.attachmentManager.getAttachment(this.attachmentId);
        return this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.VIEW, (Object)attachment);
    }

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() {
        Attachment attachment = this.attachmentManager.getAttachment(this.attachmentId);
        if (attachment != null) {
            this.setCurrentVersion(attachment);
            ArrayList<Attachment> allVersions = new ArrayList<Attachment>(this.getAllVersions(attachment));
            this.setAllVersions(allVersions);
        }
        return "success";
    }

    public WebInterfaceContext getWebInterfaceContext() {
        DefaultWebInterfaceContext webInterfaceContext = DefaultWebInterfaceContext.copyOf((WebInterfaceContext)super.getWebInterfaceContext());
        webInterfaceContext.setAttachment(this.currentVersion);
        webInterfaceContext.setPage(this.getPage());
        return webInterfaceContext;
    }

    private List<Attachment> getAllVersions(Attachment attachment) {
        return this.attachmentManager.getAllVersions(attachment);
    }

    public long getAttachmentId() {
        return this.attachmentId;
    }

    public void setAttachmentId(long attachmentId) {
        this.attachmentId = attachmentId;
    }

    public AbstractPage getPage() {
        return (AbstractPage)this.getCurrentVersion().getContainer();
    }

    public List<Attachment> getAllVersions() {
        return this.allVersions;
    }

    public void setAllVersions(List<Attachment> allVersions) {
        this.allVersions = allVersions;
    }

    public Attachment getCurrentVersion() {
        return this.currentVersion;
    }

    public void setCurrentVersion(Attachment currentVersion) {
        this.currentVersion = currentVersion;
    }
}

