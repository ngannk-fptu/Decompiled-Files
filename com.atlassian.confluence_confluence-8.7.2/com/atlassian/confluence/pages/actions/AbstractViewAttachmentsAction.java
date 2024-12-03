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
 */
package com.atlassian.confluence.pages.actions;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.FileUploadManager;
import com.atlassian.confluence.pages.actions.beans.AttachmentStorer;
import com.atlassian.confluence.pages.actions.beans.BootstrapAware;
import com.atlassian.confluence.pages.actions.beans.FileStorer;
import com.atlassian.confluence.util.AttachFileHelper;
import com.atlassian.confluence.util.AttachmentComparator;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.user.User;
import com.atlassian.xwork.FileUploadUtils;
import com.atlassian.xwork.RequireSecurityToken;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
import org.apache.struts2.interceptor.ServletRequestAware;

public abstract class AbstractViewAttachmentsAction
extends ConfluenceActionSupport
implements ServletRequestAware,
BootstrapAware {
    protected AttachmentManager attachmentManager;
    private FileUploadManager fileUploadManager;
    private HttpServletRequest request;
    protected List<String> highlight = new ArrayList<String>();
    private String sortBy = "date";
    protected FileStorer fileStorer;
    private AttachFileHelper attachFileHelper;
    private String labels = "";

    public void setServletRequest(HttpServletRequest httpServletRequest) {
        this.request = httpServletRequest;
    }

    private MultiPartRequestWrapper getMultiPartRequest() {
        return FileUploadUtils.unwrapMultiPartRequest((HttpServletRequest)this.request);
    }

    @Override
    public void bootstrap() {
        this.fileStorer = new FileStorer(this, this.getContentEntityObject());
    }

    protected void validateUpload() {
        MultiPartRequestWrapper multiPartRequest = this.getMultiPartRequest();
        this.attachFileHelper = new AttachFileHelper(multiPartRequest, this.getMaxAttachments());
        if (this.attachFileHelper.getUploadedFiles().size() > this.getMaxAttachments()) {
            this.addActionError("error.max.attachments.upload", this.getMaxAttachments());
        }
        this.attachFileHelper.validateAttachments();
        if (this.attachFileHelper.getErrors().isEmpty()) {
            this.fileStorer.processMultipartRequest(multiPartRequest);
        } else {
            this.setActionErrors(this.attachFileHelper.getErrors());
        }
    }

    @RequireSecurityToken(value=true)
    public String execute() throws Exception {
        if (!this.hasAttachFilePermissions()) {
            if (this.accessModeService.isReadOnlyAccessModeEnabled()) {
                this.addActionError(this.getText("read.only.mode.default.error.short.message"));
            }
            return "error";
        }
        if (this.getMultiPartRequest() == null) {
            return "input";
        }
        this.validateUpload();
        if (this.getActionErrors().size() > 0) {
            return "input";
        }
        AttachmentStorer storer = new AttachmentStorer(this.attachFileHelper);
        storer.setAttachmentManager(this.attachmentManager);
        storer.setFileUploadManager(this.fileUploadManager);
        List<Attachment> attachments = storer.attachFiles(this.getContentEntityObject());
        this.addLabelsToAttachments(attachments);
        this.highlight = storer.getGetFilenamesSuccessfullyAttached();
        if (this.getActionErrors().size() > 0) {
            return "input";
        }
        return "success";
    }

    private void addLabelsToAttachments(List<Attachment> attachments) {
        if (!StringUtils.isBlank((CharSequence)this.labels)) {
            List<String> labelStringList = Arrays.asList(this.labels.split(","));
            for (Attachment att : attachments) {
                for (Label label : this.labelManager.getLabels(labelStringList)) {
                    this.labelManager.addLabel(att, label);
                }
            }
        }
    }

    public boolean hasAttachFilePermissions() {
        return this.permissionManager.hasCreatePermission((User)this.getAuthenticatedUser(), (Object)this.getContentEntityObject(), Attachment.class);
    }

    public abstract ContentEntityObject getContentEntityObject();

    public void setAttachmentManager(AttachmentManager attachmentManager) {
        this.attachmentManager = attachmentManager;
    }

    public List<Attachment> getLatestVersionsOfAttachments() {
        List<Attachment> latestVersionsOfAttachments = this.attachmentManager.getLatestVersionsOfAttachments(this.getContentEntityObject());
        if (StringUtils.isNotEmpty((CharSequence)this.sortBy)) {
            boolean reverseMode = true;
            if ("name".equals(this.sortBy)) {
                reverseMode = false;
            }
            Collections.sort(latestVersionsOfAttachments, new AttachmentComparator(this.sortBy, reverseMode));
        }
        return latestVersionsOfAttachments;
    }

    public String[] getAttachmentDetails(Attachment attachment) {
        return new String[]{GeneralUtil.escapeXml(attachment.getFileName()), String.valueOf(attachment.getVersion())};
    }

    public long getUploadLimit() {
        return this.settingsManager.getGlobalSettings().getAttachmentMaxSize() / 1000000L;
    }

    public String getHighlightParameter() {
        StringBuilder highlightParameter = new StringBuilder();
        for (String highlightName : this.highlight) {
            highlightParameter.append("highlight=");
            highlightParameter.append(HtmlUtil.urlEncode(highlightName));
            highlightParameter.append("&");
        }
        return highlightParameter.toString();
    }

    public List<Attachment> getAllVersions(Attachment attachment) {
        return this.attachmentManager.getAllVersions(attachment);
    }

    public int getMaxAttachments() {
        return this.settingsManager.getGlobalSettings().getMaxAttachmentsInUI();
    }

    public void setFileUploadManager(FileUploadManager fileUploadManager) {
        this.fileUploadManager = fileUploadManager;
    }

    public List<String> getHighlight() {
        return this.highlight;
    }

    public void setHighlight(List<String> names) {
        this.highlight = names;
    }

    public String getSortBy() {
        return this.sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public void setLabels(String labels) {
        this.labels = labels;
    }

    public String getLabels() {
        return this.labels;
    }
}

