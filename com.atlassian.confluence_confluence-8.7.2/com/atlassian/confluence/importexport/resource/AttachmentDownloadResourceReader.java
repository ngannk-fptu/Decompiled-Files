/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.io.InputStreamSource
 */
package com.atlassian.confluence.importexport.resource;

import com.atlassian.confluence.importexport.resource.DownloadResourceReader;
import com.atlassian.confluence.pages.Attachment;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Objects;
import org.springframework.core.io.InputStreamSource;

public class AttachmentDownloadResourceReader
implements DownloadResourceReader {
    private final Attachment attachment;
    private final InputStreamSource inputStreamSource;

    public AttachmentDownloadResourceReader(Attachment attachment, InputStreamSource inputStreamSource) {
        this.attachment = Objects.requireNonNull(attachment);
        this.inputStreamSource = Objects.requireNonNull(inputStreamSource);
    }

    @Override
    public String getName() {
        return this.attachment.getFileName();
    }

    @Override
    public String getContentType() {
        return this.attachment.getMediaType();
    }

    @Override
    public long getContentLength() {
        return this.attachment.getFileSize();
    }

    @Override
    public Date getLastModificationDate() {
        return this.attachment.getLastModificationDate();
    }

    @Override
    public InputStream getStreamForReading() {
        try {
            return this.inputStreamSource.getInputStream();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

