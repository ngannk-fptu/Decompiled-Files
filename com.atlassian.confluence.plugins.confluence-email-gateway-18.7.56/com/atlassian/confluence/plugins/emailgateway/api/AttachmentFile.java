/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 */
package com.atlassian.confluence.plugins.emailgateway.api;

import com.atlassian.annotations.PublicApi;

@PublicApi
public class AttachmentFile {
    private final String fileName;
    private final String extension;
    private final String contentType;

    public AttachmentFile(String fileName, String extension, String contentType) {
        this.fileName = fileName;
        this.extension = extension;
        this.contentType = contentType;
    }

    public String getFileName() {
        return this.fileName;
    }

    public String getExtension() {
        return this.extension;
    }

    public String getContentType() {
        return this.contentType;
    }
}

