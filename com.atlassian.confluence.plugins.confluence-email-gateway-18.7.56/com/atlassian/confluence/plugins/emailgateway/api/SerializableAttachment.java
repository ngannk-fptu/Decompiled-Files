/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  com.atlassian.mail.MailUtils$Attachment
 */
package com.atlassian.confluence.plugins.emailgateway.api;

import com.atlassian.annotations.PublicApi;
import com.atlassian.mail.MailUtils;
import java.io.Serializable;

@PublicApi
public class SerializableAttachment
implements Serializable {
    private String filename;
    private String contentType;
    private byte[] contents;

    public SerializableAttachment() {
    }

    public SerializableAttachment(MailUtils.Attachment attachment) {
        this.filename = attachment.getFilename();
        this.contentType = attachment.getContentType();
        this.contents = attachment.getContents();
    }

    public SerializableAttachment(byte[] contents, String contentType, String filename) {
        this.contents = (byte[])contents.clone();
        this.contentType = contentType;
        this.filename = filename;
    }

    public String getFilename() {
        return this.filename;
    }

    public String getContentType() {
        return this.contentType;
    }

    public byte[] getContents() {
        return this.contents;
    }
}

