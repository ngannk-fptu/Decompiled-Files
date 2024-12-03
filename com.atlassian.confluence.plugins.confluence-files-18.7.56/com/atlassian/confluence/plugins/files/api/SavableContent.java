/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.files.api;

import java.io.InputStream;

public class SavableContent {
    private final String mimeType;
    private final long fileSize;
    private final InputStream contentStream;

    public SavableContent(String mimeType, long fileSize, InputStream contentStream) {
        this.mimeType = mimeType;
        this.fileSize = fileSize;
        this.contentStream = contentStream;
    }

    public long getFileSize() {
        return this.fileSize;
    }

    public String getMimeType() {
        return this.mimeType;
    }

    public InputStream getContentStream() {
        return this.contentStream;
    }
}

