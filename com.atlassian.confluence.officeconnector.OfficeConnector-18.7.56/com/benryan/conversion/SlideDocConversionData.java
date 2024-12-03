/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.Attachment
 */
package com.benryan.conversion;

import com.atlassian.confluence.pages.Attachment;
import java.io.Serializable;
import java.util.Date;

public class SlideDocConversionData
implements Serializable {
    private static final long serialVersionUID = 7580028546557056706L;
    private int numSlides;
    private final Date conversionDate;
    private final long key;
    private final String ceoName;
    private final String attachmentName;

    public SlideDocConversionData(Attachment attachment) {
        this(attachment.getId(), attachment.getContainer().getTitle(), attachment.getFileName());
    }

    public SlideDocConversionData(long key, String ceoName, String attachmentName) {
        this.key = key;
        this.conversionDate = new Date();
        this.ceoName = ceoName;
        this.attachmentName = attachmentName;
    }

    protected SlideDocConversionData(long key) {
        this(key, "", "");
    }

    public void setNumSlides(int numSlides) {
        this.numSlides = numSlides;
    }

    public int getNumSlides() {
        return this.numSlides;
    }

    public Date getQueueDate() {
        return new Date(this.conversionDate.getTime());
    }

    public long getKey() {
        return this.key;
    }

    public String getCeoName() {
        return this.ceoName;
    }

    public String getAttachmentName() {
        return this.attachmentName;
    }
}

