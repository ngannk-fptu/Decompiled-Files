/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.pages.AttachmentDataNotFoundException
 *  com.atlassian.confluence.pages.persistence.dao.AttachmentDataDao
 *  com.atlassian.confluence.pages.persistence.dao.AttachmentDataStream
 *  com.atlassian.confluence.pages.persistence.dao.AttachmentDataStreamType
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.conversion.impl;

import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentDataNotFoundException;
import com.atlassian.confluence.pages.persistence.dao.AttachmentDataDao;
import com.atlassian.confluence.pages.persistence.dao.AttachmentDataStream;
import com.atlassian.confluence.pages.persistence.dao.AttachmentDataStreamType;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class AttachmentDataTempFile
implements AutoCloseable {
    private static final Logger log = LoggerFactory.getLogger(AttachmentDataTempFile.class);
    private final Path file;

    static AttachmentDataTempFile extract(Path tempDir, AttachmentDataDao attachmentDataDao, Attachment attachment, AttachmentDataStreamType dataStreamType) throws AttachmentDataNotFoundException, IOException {
        AttachmentDataStream dataStream = attachmentDataDao.getDataForAttachment(attachment, dataStreamType);
        Path tempDataFile = Files.createTempFile(Files.createDirectories(tempDir, new FileAttribute[0]), attachment.getFileName(), null, new FileAttribute[0]);
        log.debug("Extracting data for {} to temp file {} for sandbox conversion", (Object)attachment, (Object)tempDataFile);
        try (InputStream is = dataStream.getInputStream();){
            Files.copy(is, tempDataFile, StandardCopyOption.REPLACE_EXISTING);
        }
        return new AttachmentDataTempFile(tempDataFile);
    }

    private AttachmentDataTempFile(Path tempDataFile) {
        this.file = tempDataFile;
    }

    Path getFile() {
        return this.file;
    }

    private void dispose() {
        log.debug("Deleting temp data file {}", (Object)this.file);
        try {
            Files.delete(this.file);
        }
        catch (IOException e) {
            log.warn("Failed to delete temporary attachment conversion file {}", (Object)this.file, (Object)e);
        }
    }

    @Override
    public void close() {
        this.dispose();
    }
}

