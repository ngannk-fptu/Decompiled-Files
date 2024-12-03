/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.dc.filestore.api.FileStore$Path
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.pages.attachments.objectstorage;

import com.atlassian.confluence.pages.attachments.AttachmentDataStreamSizeMismatchException;
import com.atlassian.confluence.pages.persistence.dao.filesystem.AttachmentDataFileSystemException;
import com.atlassian.dc.filestore.api.FileStore;
import java.io.IOException;
import java.io.InputStream;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObjectStorageAttachmentDataUtil {
    private static final Logger log = LoggerFactory.getLogger(ObjectStorageAttachmentDataUtil.class);

    private ObjectStorageAttachmentDataUtil() {
    }

    public static void writeStreamToPath(InputStream data, FileStore.Path destFile, @Nullable Long expectedFileSize) {
        long byteCount = ObjectStorageAttachmentDataUtil.writeFile(destFile, data);
        if (expectedFileSize != null && byteCount != expectedFileSize) {
            log.error("The written attachment [{}] has a size [{}] that does not match expected size [{}]. Deleting attachment from S3 object storage.", new Object[]{destFile.getPathName(), byteCount, expectedFileSize});
            ObjectStorageAttachmentDataUtil.deleteFile(destFile);
            throw new AttachmentDataStreamSizeMismatchException(expectedFileSize, byteCount);
        }
    }

    private static long writeFile(FileStore.Path destFile, InputStream data) {
        try {
            log.info("Writing attachment [{}] to S3 object storage.", (Object)destFile.getPathName());
            if (destFile.fileExists()) {
                ObjectStorageAttachmentDataUtil.deleteFile(destFile);
            }
            destFile.fileWriter().write(data);
            return destFile.getFileSize().getBytes();
        }
        catch (IOException e) {
            log.error("Error writing attachment [{}] to S3 object storage.", (Object)destFile.getPathName(), (Object)e);
            throw new AttachmentDataFileSystemException("Error writing attachment to S3 object storage: " + destFile.getPathName(), e);
        }
    }

    private static void deleteFile(FileStore.Path destFile) {
        try {
            destFile.deleteFile();
        }
        catch (IOException e) {
            log.error("Error deleting attachment [{}] from S3 object storage.", (Object)destFile.getPathName(), (Object)e);
            throw new AttachmentDataFileSystemException("Error deleting attachment from S3 object storage: " + destFile.getPathName());
        }
    }
}

