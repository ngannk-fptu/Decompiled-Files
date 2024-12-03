/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.xwork.FileUploadUtils
 *  com.atlassian.xwork.FileUploadUtils$FileUploadException
 *  com.atlassian.xwork.FileUploadUtils$UploadedFile
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.spaces.actions;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.UploadedResource;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.FileUploadManager;
import com.atlassian.xwork.FileUploadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractLogoAction
extends ConfluenceActionSupport {
    private static final Logger log = LoggerFactory.getLogger(AbstractLogoAction.class);
    protected FileUploadManager fileUploadManager;
    protected AttachmentManager attachmentManager;

    public abstract String doDisableLogo();

    public abstract String doEnableLogo();

    public abstract boolean isLogoDisabled();

    protected abstract ContentEntityObject getContentToAttachLogoTo();

    public abstract String getActionDescriminator();

    protected abstract String getAttachmentName();

    public String doDelete() {
        this.attachmentManager.removeAttachmentFromServer(this.attachmentManager.getAttachment(this.getContentToAttachLogoTo(), this.getAttachmentName()));
        return "success";
    }

    public Attachment getLogo() {
        return this.attachmentManager.getAttachment(this.getContentToAttachLogoTo(), this.getAttachmentName());
    }

    public String doUpload() {
        try {
            FileUploadUtils.UploadedFile uploadedFile = FileUploadUtils.getSingleUploadedFile();
            if (uploadedFile == null) {
                throw new FileUploadUtils.FileUploadException();
            }
            UploadedResource resource = new UploadedResource(uploadedFile.getFile(), this.getAttachmentName(), uploadedFile.getContentType(), null);
            this.fileUploadManager.storeResource(resource, this.getContentToAttachLogoTo());
            return "success";
        }
        catch (FileUploadUtils.FileUploadException e) {
            String errorMsg = this.getText("could.not.locate.uploaded.logo");
            log.debug(errorMsg, (Throwable)e);
            this.addActionError(errorMsg);
            return "error";
        }
    }

    public void setFileUploadManager(FileUploadManager fileUploadManager) {
        this.fileUploadManager = fileUploadManager;
    }

    public void setAttachmentManager(AttachmentManager attachmentManager) {
        this.attachmentManager = attachmentManager;
    }
}

