/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.xwork2.ActionSupport
 *  org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper
 */
package com.atlassian.confluence.pages.actions.beans;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.actions.beans.FileStorer;
import com.atlassian.confluence.pages.thumbnail.ThumbnailManager;
import com.atlassian.confluence.util.HtmlUtil;
import com.opensymphony.xwork2.ActionSupport;
import java.util.Collections;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;

public class ImageStorer
extends FileStorer {
    private final ThumbnailManager thumbnailManager;

    public ImageStorer(ActionSupport action, ContentEntityObject content, ThumbnailManager thumbnailManager) {
        super(action, content);
        this.thumbnailManager = thumbnailManager;
    }

    @Override
    public void processMultipartRequest(MultiPartRequestWrapper multiPartRequest) {
        super.processMultipartRequest(multiPartRequest);
        if (this.action.getActionErrors().isEmpty()) {
            Attachment attachment;
            if (!(this.fileName.toLowerCase().endsWith(".gif") || this.fileName.toLowerCase().endsWith(".png") || this.fileName.toLowerCase().endsWith(".jpg"))) {
                this.action.addActionError(this.action.getText("unsupported.file.error", Collections.singletonList(HtmlUtil.htmlEncode(this.fileName))));
            }
            if ((attachment = this.content.getAttachmentNamed(this.fileName)) != null && attachment.getFileName().equalsIgnoreCase(this.fileName)) {
                this.thumbnailManager.removeThumbnail(attachment);
            }
        }
        this.inited = true;
    }
}

