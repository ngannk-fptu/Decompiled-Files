/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.io.InputStreamSource
 */
package com.atlassian.confluence.importexport.resource;

import com.atlassian.confluence.importexport.resource.DownloadResourceReader;
import com.atlassian.confluence.pages.Attachment;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import org.springframework.core.io.InputStreamSource;

public class ThumbnailDownloadResourceReader
implements DownloadResourceReader {
    private final Attachment attachment;
    private final File thumbnailFile;
    private final InputStreamSource inputStreamSource;

    public ThumbnailDownloadResourceReader(Attachment attachment, File thumbnailFile, InputStreamSource inputStreamSource) {
        this.attachment = attachment;
        this.thumbnailFile = thumbnailFile;
        this.inputStreamSource = inputStreamSource;
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
        return this.thumbnailFile.length();
    }

    @Override
    public Date getLastModificationDate() {
        if (!this.thumbnailFile.exists()) {
            return new Date();
        }
        return new Date(this.thumbnailFile.lastModified());
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

