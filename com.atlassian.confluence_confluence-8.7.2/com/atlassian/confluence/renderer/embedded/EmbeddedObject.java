/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.util.FileTypeUtil
 */
package com.atlassian.confluence.renderer.embedded;

import com.atlassian.confluence.pages.Attachment;
import com.atlassian.renderer.util.FileTypeUtil;
import java.util.Map;

public class EmbeddedObject {
    private final Attachment attachment;
    private final Map<String, Object> properties;
    private final String fileName;
    private String contentType;
    private String fileExtension;

    public EmbeddedObject(Attachment attachment, Map<String, Object> properties) {
        this.attachment = attachment;
        this.properties = properties;
        this.fileName = attachment.getFileName();
    }

    public Attachment getAttachment() {
        return this.attachment;
    }

    public Map<String, Object> getProperties() {
        return this.properties;
    }

    public String getContentType() {
        if (this.contentType == null) {
            this.contentType = FileTypeUtil.getContentType((String)this.fileName);
        }
        return this.contentType;
    }

    public String getFileExtension() {
        if (this.fileExtension == null) {
            this.fileExtension = this.fileName.substring(this.fileName.lastIndexOf("."), this.fileName.length());
        }
        return this.fileExtension;
    }
}

