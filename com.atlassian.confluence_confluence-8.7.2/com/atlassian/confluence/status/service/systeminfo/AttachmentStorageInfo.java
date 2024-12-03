/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.status.service.systeminfo;

import com.atlassian.confluence.status.service.systeminfo.AttachmentStorageType;

public class AttachmentStorageInfo {
    private final AttachmentStorageType attachmentStorageType;
    public static final String SYSTEM_PROPERTY_STORAGE_TYPE = "AttachmentStorageType";

    public AttachmentStorageInfo(AttachmentStorageType attachmentStorageType) {
        this.attachmentStorageType = attachmentStorageType;
    }

    public AttachmentStorageType getStorageType() {
        return this.attachmentStorageType;
    }
}

