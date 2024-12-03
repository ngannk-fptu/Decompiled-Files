/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.xwork.FileUploadUtils$UploadedFile
 */
package com.atlassian.confluence.pages.actions.beans;

import com.atlassian.confluence.core.AttachmentResource;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.UploadedResource;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.FileUploadManager;
import com.atlassian.confluence.util.AttachFileHelper;
import com.atlassian.xwork.FileUploadUtils;
import java.util.ArrayList;
import java.util.List;

public class AttachmentStorer {
    private FileUploadManager fileUploadManager;
    private AttachmentManager attachmentManager;
    private List<String> getFilenamesSuccessfullyAttached = new ArrayList<String>();
    private AttachFileHelper attachFileHelper;

    public AttachmentStorer(AttachFileHelper aFHelper) {
        this.attachFileHelper = aFHelper;
    }

    public void setAttachmentManager(AttachmentManager attachmentManager) {
        this.attachmentManager = attachmentManager;
    }

    public void setFileUploadManager(FileUploadManager fileUploadManager) {
        this.fileUploadManager = fileUploadManager;
    }

    public List<Attachment> attachFiles(ContentEntityObject contentEntityObject) {
        ArrayList<Attachment> attachmentsAdded = new ArrayList<Attachment>();
        List<FileUploadUtils.UploadedFile> uploadedFiles = this.attachFileHelper.getUploadedFiles();
        ArrayList<AttachmentResource> uploadedResources = new ArrayList<AttachmentResource>();
        ArrayList<String> uploadedFilenames = new ArrayList<String>();
        for (FileUploadUtils.UploadedFile uploadedFile : uploadedFiles) {
            String uploadedFilename = uploadedFile.getFileName();
            uploadedFilenames.add(uploadedFilename);
            UploadedResource uploadedResource = new UploadedResource(uploadedFile, this.attachFileHelper.getCommentForFilename(uploadedFilename));
            uploadedResources.add(uploadedResource);
        }
        this.fileUploadManager.storeResources(uploadedResources, contentEntityObject);
        for (String uploadedFilename : uploadedFilenames) {
            Attachment attachment = this.attachmentManager.getAttachment(contentEntityObject, uploadedFilename);
            attachmentsAdded.add(attachment);
            this.getFilenamesSuccessfullyAttached.add(attachment.getFileName());
        }
        return attachmentsAdded;
    }

    public List<String> getGetFilenamesSuccessfullyAttached() {
        return this.getFilenamesSuccessfullyAttached;
    }
}

