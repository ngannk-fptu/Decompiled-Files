/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.struts2.interceptor.ServletRequestAware
 */
package com.atlassian.confluence.pages.actions;

import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.actions.AbstractPageAwareAction;
import com.atlassian.confluence.pages.actions.beans.AttachmentBean;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts2.interceptor.ServletRequestAware;

public abstract class AbstractRemoveAttachmentAction
extends AbstractPageAwareAction
implements ServletRequestAware {
    protected AttachmentManager attachmentManager;
    protected AttachmentBean attachmentBean = new AttachmentBean();
    protected Attachment attachment;
    protected HttpServletRequest request;

    public void setServletRequest(HttpServletRequest request) {
        this.request = request;
    }

    public void setAttachmentManager(AttachmentManager attachmentManager) {
        this.attachmentManager = attachmentManager;
    }

    public void setAttachmentBean(AttachmentBean attachmentBean) {
        this.attachmentBean = attachmentBean;
    }

    public void setFileName(String fileName) {
        this.attachmentBean.setFileName(fileName);
    }

    public String getFileName() {
        return this.attachmentBean.getFileName();
    }

    public int getVersion() {
        return this.attachmentBean.getVersion();
    }

    public Attachment getAttachment() {
        if (this.attachment == null) {
            this.attachment = this.attachmentBean.retrieveMatchingAttachment(this.getPage(), this.attachmentManager);
        }
        return this.attachment;
    }

    protected String localiseActionName(String actionI18NKey) {
        return super.getText(actionI18NKey);
    }
}

