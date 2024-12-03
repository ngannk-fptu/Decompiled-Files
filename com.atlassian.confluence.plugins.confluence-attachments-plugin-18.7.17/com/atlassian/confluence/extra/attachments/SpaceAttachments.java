/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.Attachment
 */
package com.atlassian.confluence.extra.attachments;

import com.atlassian.confluence.pages.Attachment;
import java.util.List;

public class SpaceAttachments {
    private List<Attachment> attachmentList;
    private int totalPage;
    private int totalAttachments;

    public void setAttachmentList(List<Attachment> attachmentList) {
        this.attachmentList = attachmentList;
    }

    public List<Attachment> getAttachmentList() {
        return this.attachmentList;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getTotalPage() {
        return this.totalPage;
    }

    public void setTotalAttachments(int totalAttachments) {
        this.totalAttachments = totalAttachments;
    }

    public int getTotalAttachments() {
        return this.totalAttachments;
    }
}

