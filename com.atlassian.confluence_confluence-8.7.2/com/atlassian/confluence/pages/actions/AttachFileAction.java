/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 *  com.atlassian.xwork.FileUploadUtils
 *  com.atlassian.xwork.RequireSecurityToken
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper
 *  org.apache.struts2.interceptor.ServletRequestAware
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.pages.actions;

import com.atlassian.confluence.core.AttachmentResource;
import com.atlassian.confluence.core.Beanable;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.FileUploadManager;
import com.atlassian.confluence.pages.actions.AttachmentUploadRequest;
import com.atlassian.confluence.util.StrutsUtil;
import com.atlassian.user.User;
import com.atlassian.xwork.FileUploadUtils;
import com.atlassian.xwork.RequireSecurityToken;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AttachFileAction
extends ConfluenceActionSupport
implements ServletRequestAware,
Beanable {
    private static final Logger log = LoggerFactory.getLogger(AttachFileAction.class);
    private AttachmentManager attachmentManager;
    private FileUploadManager fileUploadManager;
    private ContentEntityManager contentEntityManager;
    private long contentId;
    private HttpServletRequest request;
    private final List<Attachment> attachmentsAdded = new ArrayList<Attachment>();
    private String responseFormat = "json";

    @Override
    public void validate() {
        MultiPartRequestWrapper multiPartRequest = FileUploadUtils.unwrapMultiPartRequest((HttpServletRequest)this.request);
        if (multiPartRequest == null) {
            this.addActionError(this.getText("attachment.upload.error.invalid.request"));
            return;
        }
        if (multiPartRequest.hasErrors()) {
            this.setActionErrors(StrutsUtil.localizeMultipartErrorMessages(multiPartRequest));
            return;
        }
        if (!this.permissionManager.hasCreatePermission((User)this.getAuthenticatedUser(), (Object)this.getContentEntityObject(), Attachment.class)) {
            this.addActionError(this.getText("attachment.upload.not.permitted"));
            return;
        }
        AttachmentUploadRequest uploadRequest = new AttachmentUploadRequest(multiPartRequest);
        if (uploadRequest.getResourceCount() > this.getMaxAttachments()) {
            this.addActionError("error.max.attachments.upload", this.getMaxAttachments());
        }
        for (AttachmentResource file : uploadRequest.getResources()) {
            if (StringUtils.isBlank((CharSequence)file.getFilename())) {
                this.addActionError("fileName.required");
            }
            if (StringUtils.length((CharSequence)file.getComment()) <= 255) continue;
            this.addActionError("attachment.comment.too.long", file.getFilename());
        }
    }

    @RequireSecurityToken(value=true)
    public String execute() {
        AttachmentUploadRequest uploadRequest = new AttachmentUploadRequest(Objects.requireNonNull(FileUploadUtils.unwrapMultiPartRequest((HttpServletRequest)this.request)));
        ContentEntityObject content = this.getContentEntityObject();
        List<AttachmentResource> resources = uploadRequest.getResources();
        for (AttachmentResource resource : resources) {
            log.debug("Uploaded file '{}' will be attached to '{}'", (Object)resource, (Object)content);
            this.fileUploadManager.storeResource(resource, content);
            this.attachmentsAdded.add(this.attachmentManager.getAttachment(content, resource.getFilename()));
        }
        return this.responseFormat;
    }

    @Override
    public Object getBean() {
        return Map.of("attachmentsAdded", this.attachmentsAdded);
    }

    private int getMaxAttachments() {
        return this.settingsManager.getGlobalSettings().getMaxAttachmentsInUI();
    }

    @Override
    public boolean isPermitted() {
        return super.isPermitted();
    }

    private ContentEntityObject getContentEntityObject() {
        return this.contentEntityManager.getById(this.contentId);
    }

    public void setContentId(long contentId) {
        this.contentId = contentId;
    }

    public void setServletRequest(HttpServletRequest request) {
        this.request = request;
    }

    public void setAttachmentManager(AttachmentManager attachmentManager) {
        this.attachmentManager = attachmentManager;
    }

    public void setFileUploadManager(FileUploadManager fileUploadManager) {
        this.fileUploadManager = fileUploadManager;
    }

    public void setContentEntityManager(ContentEntityManager contentEntityManager) {
        this.contentEntityManager = contentEntityManager;
    }

    public void setResponseFormat(String responseFormat) {
        this.responseFormat = responseFormat;
    }
}

