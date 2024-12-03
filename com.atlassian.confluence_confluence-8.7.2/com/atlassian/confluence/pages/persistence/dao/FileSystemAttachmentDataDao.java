/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.util.FileUtils
 *  org.apache.commons.io.FileUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.pages.persistence.dao;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentDataNotFoundException;
import com.atlassian.confluence.pages.AttachmentDataStorageType;
import com.atlassian.confluence.pages.persistence.dao.AttachmentDataStream;
import com.atlassian.confluence.pages.persistence.dao.AttachmentDataStreamType;
import com.atlassian.confluence.pages.persistence.dao.FileLocationResolver;
import com.atlassian.confluence.pages.persistence.dao.LegacyAttachmentDataDaoSupport;
import com.atlassian.confluence.pages.persistence.dao.NonTransactionalAttachmentDataDao;
import com.atlassian.confluence.pages.persistence.dao.filesystem.FileSystemAttachmentDataUtil;
import com.atlassian.confluence.util.io.ConfluenceFileUtils;
import com.atlassian.confluence.web.rangerequest.RangeRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class FileSystemAttachmentDataDao
implements NonTransactionalAttachmentDataDao {
    private static final Logger log = LoggerFactory.getLogger(FileSystemAttachmentDataDao.class);
    protected FileLocationResolver attachmentsDirResolver;
    private static final String ATTACHMENT_BACKUP_DATE_FORMAT = "yyyyMMddHHmmss";
    static final String TEMP_FILE_PREFIX = "data";
    public static final FileSystemAttachmentNamingStrategy NAMING_STRATEGY_ID = new AttachmentIdNamingStrategy();
    public static final FileSystemAttachmentNamingStrategy NAMING_STRATEGY_FILE_NAME = new AttachmentFileNameNamingStrategy();
    private FileSystemAttachmentNamingStrategy namingStrategy = NAMING_STRATEGY_ID;

    public FileSystemAttachmentNamingStrategy getNamingStrategy() {
        return this.namingStrategy;
    }

    public void setNamingStrategy(FileSystemAttachmentNamingStrategy namingStrategy) {
        this.namingStrategy = namingStrategy;
    }

    protected File getConfluenceAttachmentDirectory() {
        return this.attachmentsDirResolver.getFileLocation();
    }

    protected File getLatestAttachmentFile(Attachment attachment) {
        File attachmentbase = this.getDirectoryForAttachment(attachment.getContainer(), attachment);
        if (attachmentbase.isDirectory()) {
            attachmentbase = new File(attachmentbase, this.toFileName(attachment));
        }
        return attachmentbase;
    }

    @Override
    public InputStream getDataForAttachment(Attachment attachment) throws AttachmentDataNotFoundException {
        if (attachment == null) {
            throw new IllegalArgumentException("The attachment cannot be null.");
        }
        try {
            return new FileInputStream(this.getLatestAttachmentFile(attachment));
        }
        catch (IOException ex) {
            throw new AttachmentDataNotFoundException("Problem while getting attachment stream (" + attachment + ") from file system", ex);
        }
    }

    @Override
    public void removeDataForAttachment(Attachment attachment, ContentEntityObject originalContent) {
        this.checkAttachmentAndOriginalContentArgumentsNotNull(attachment, originalContent);
        if (!attachment.isLatestVersion()) {
            throw new IllegalArgumentException("Attachment must be latest version");
        }
        File attachmentFolder = this.getDirectoryForAttachment(originalContent, attachment);
        if (!attachmentFolder.exists()) {
            log.error("Could not find attachment folder to remove at [{}] for title [{}].", (Object)attachmentFolder.getAbsolutePath(), (Object)originalContent);
            return;
        }
        if (!com.atlassian.core.util.FileUtils.deleteDir((File)attachmentFolder)) {
            log.error("Error removing the attachment directory with path [{}].", (Object)attachmentFolder.getAbsolutePath());
            return;
        }
        this.cleanupEmptyAncestors(attachmentFolder);
    }

    @Override
    public void removeDataForAttachmentVersion(Attachment attachment, ContentEntityObject originalContent) {
        this.checkAttachmentAndOriginalContentArgumentsNotNull(attachment, originalContent);
        File attachmentFolder = this.getDirectoryForAttachment(originalContent, (Attachment)attachment.getLatestVersion());
        if (!attachmentFolder.exists()) {
            log.error("Could not find attachment folder at [{}] in order to remove the file representing [{}].", new Object[]{attachmentFolder.getAbsolutePath(), attachment});
            return;
        }
        String attachmentFileName = this.toFileName(attachment);
        for (File attachmentFileCandidate : attachmentFolder.listFiles()) {
            if (!attachmentFileName.equals(attachmentFileCandidate.getName())) continue;
            if (attachmentFileCandidate.delete()) {
                this.cleanupEmptyAncestors(attachmentFolder);
            } else {
                log.error("Could not delete file [{}] representing [{}].", new Object[]{attachmentFileCandidate.getAbsolutePath(), attachment});
            }
            return;
        }
        log.error("Could not find file representing [{}] in attachment folder [{}] in order to delete it.", new Object[]{attachment, attachmentFolder.getAbsolutePath()});
    }

    @Override
    public void removeDataForAttachmentVersion(Attachment attachment, ContentEntityObject originalContent, AttachmentDataStreamType dataStreamType) {
        if (dataStreamType == AttachmentDataStreamType.EXTRACTED_TEXT) {
            return;
        }
        this.removeDataForAttachmentVersion(attachment, originalContent);
    }

    @Override
    public void moveDataForAttachmentVersion(Attachment sourceAttachmentVersion, Attachment targetAttachmentVersion) {
        File targetAttachmentDirectory;
        File sourceAttachmentDirectory = this.getDirectoryForAttachment(sourceAttachmentVersion.getContainer(), (Attachment)sourceAttachmentVersion.getLatestVersion());
        if (!sourceAttachmentDirectory.equals(targetAttachmentDirectory = this.getDirectoryForAttachment(targetAttachmentVersion.getContainer(), (Attachment)targetAttachmentVersion.getLatestVersion()))) {
            throw new IllegalArgumentException(String.format("Expected source [%s] and target [%s] to resolve to the same attachment directory, but source resolved to [%s] and target resolved to [%s].", sourceAttachmentVersion, targetAttachmentVersion, sourceAttachmentDirectory, targetAttachmentDirectory));
        }
        File sourceAttachmentVersionFile = new File(sourceAttachmentDirectory, this.toFileName(sourceAttachmentVersion));
        File targetAttachmentVersionFile = new File(targetAttachmentDirectory, this.toFileName(targetAttachmentVersion));
        try {
            FileUtils.forceDelete((File)targetAttachmentVersionFile);
        }
        catch (FileNotFoundException e) {
            log.warn("File [{}] did not exist, will continue despite that inconsistency since it was about to get replaced by [{}].", (Object)targetAttachmentVersionFile, (Object)sourceAttachmentVersionFile);
        }
        catch (IOException e) {
            throw new IllegalStateException(String.format("Could delete file [%s] in order to prepare moving (rename or copy) [%s] to it, see cause.", targetAttachmentVersionFile, sourceAttachmentVersion), e);
        }
        try {
            FileUtils.moveFile((File)sourceAttachmentVersionFile, (File)targetAttachmentVersionFile);
        }
        catch (IOException e) {
            throw new IllegalStateException(String.format("Could not move (rename or copy) file [%s] to [%s], see cause.", sourceAttachmentVersion, targetAttachmentVersionFile), e);
        }
    }

    @Override
    public void saveDataForAttachment(Attachment attachment, InputStream data) {
        this.saveDataForAttachment(attachment, data, false);
    }

    public FileLocationResolver getAttachmentsDirResolver() {
        return this.attachmentsDirResolver;
    }

    protected void saveDataForAttachment(Attachment attachment, InputStream data, boolean overwrite) {
        if (attachment == null) {
            throw new IllegalArgumentException("The attachment cannot be null");
        }
        if (data == null) {
            throw new IllegalArgumentException("The data to be written cannot be null.");
        }
        File parentFolder = this.getDirectoryForAttachment(attachment.getContainer(), attachment);
        File destFile = new File(parentFolder, this.toFileName(attachment));
        if (destFile.exists() && !overwrite) {
            return;
        }
        this.writeStreamToFile(data, destFile, attachment.getFileSize());
    }

    void writeStreamToFile(InputStream data, File destFile, long expectedAttachmentSize) {
        FileSystemAttachmentDataUtil.writeStreamToFile(data, destFile, expectedAttachmentSize);
    }

    @Override
    public void saveDataForAttachmentVersion(Attachment attachment, Attachment previousVersion, InputStream data) {
        this.saveDataForAttachment(attachment, data);
    }

    @Override
    public void replaceDataForAttachment(Attachment attachment, InputStream data) {
        this.saveDataForAttachment(attachment, data, true);
    }

    @Override
    public boolean isAttachmentPresent(Attachment attachment) {
        File file = this.getDirectoryForAttachment(attachment.getContainer(), attachment);
        return file.exists();
    }

    @Override
    public void moveAttachment(Attachment attachment, Attachment oldAttachment, ContentEntityObject newContent) {
        if (attachment == null) {
            throw new IllegalArgumentException("The attachment cannot be null.");
        }
        if (oldAttachment == null) {
            throw new IllegalArgumentException("The old attachment cannot be null.");
        }
        if (newContent == null) {
            throw new IllegalArgumentException("The new content object of the attachment cannot be null.");
        }
        File originalAttachmentFolder = this.getDirectoryForAttachment(oldAttachment.getContainer(), oldAttachment);
        File newAttachmentFolder = this.getDirectoryForAttachment(newContent, attachment);
        try {
            ConfluenceFileUtils.moveDir(originalAttachmentFolder, newAttachmentFolder);
        }
        catch (IOException e) {
            throw new RuntimeException("Unable to move attachment (" + attachment.toString() + ") from [" + originalAttachmentFolder + "] to [" + newAttachmentFolder + "] - check folder permissions.", e);
        }
    }

    protected File getDirectoryForAttachment(ContentEntityObject content, Attachment attachment) {
        return new File(this.getDirectoryForContent(content), this.namingStrategy.getAttachmentFileName(attachment));
    }

    protected File getDirectoryForContent(ContentEntityObject content) {
        return new File(this.getConfluenceAttachmentDirectory(), String.valueOf(content.getId()));
    }

    @Override
    public void prepareForMigrationTo() {
        File attachmentsDirFile = this.getConfluenceAttachmentDirectory();
        if (!attachmentsDirFile.exists()) {
            log.info("Creating attachments directory on the filesystem at '" + attachmentsDirFile.getAbsolutePath() + "'");
            attachmentsDirFile.mkdirs();
            return;
        }
        File[] fileList = attachmentsDirFile.listFiles();
        File attachmentsBackupDir = this.createAttachmentsBackupDirectory(attachmentsDirFile, 10);
        for (File file : fileList) {
            File newLocation = new File(attachmentsBackupDir, file.getName());
            try {
                ConfluenceFileUtils.moveDir(file, newLocation);
            }
            catch (Exception e) {
                log.warn("Error while moving " + file + " to " + newLocation, (Throwable)e);
            }
        }
    }

    private File createAttachmentsBackupDirectory(File attachmentsDirFile, int maxTries) {
        String isoFormatDate = new SimpleDateFormat(ATTACHMENT_BACKUP_DATE_FORMAT).format(new Date());
        Object suffix = "";
        for (int tries = 1; tries <= maxTries; ++tries) {
            File attachmentsBackupDir = new File(attachmentsDirFile, "attachment-backup-" + isoFormatDate + (String)suffix);
            if (attachmentsBackupDir.mkdirs()) {
                return attachmentsBackupDir;
            }
            suffix = "-" + tries;
        }
        throw new RuntimeException("Unable to create backup directory in " + attachmentsDirFile + " for old attachments after " + maxTries + " attempts");
    }

    @Override
    public void afterMigrationFrom() {
    }

    public void setAttachmentsDirResolver(FileLocationResolver attachmentsDirResolver) {
        this.attachmentsDirResolver = attachmentsDirResolver;
    }

    @Override
    public AttachmentDataStream getDataForAttachment(Attachment attachment, AttachmentDataStreamType dataStreamType) throws AttachmentDataNotFoundException {
        return this.legacyDaoSupport().getAttachmentDataStream(attachment, dataStreamType);
    }

    @Override
    public AttachmentDataStream getDataForAttachment(Attachment attachment, AttachmentDataStreamType dataStreamType, Optional<RangeRequest> range) throws AttachmentDataNotFoundException {
        if (range != null && range.isPresent()) {
            throw new UnsupportedOperationException("Deprecated implementation. Use filesystem/FileSystemAttachmentDataDao instead.");
        }
        return this.getDataForAttachment(attachment, dataStreamType);
    }

    @Override
    public void saveDataForAttachment(Attachment attachment, AttachmentDataStream dataStream) {
        this.legacyDaoSupport().saveDataForAttachment(attachment, dataStream);
    }

    @Override
    public void saveDataForAttachmentVersion(Attachment attachment, Attachment previousVersion, AttachmentDataStream dataStream) {
        this.legacyDaoSupport().saveDataForAttachmentVersion(attachment, previousVersion, dataStream);
    }

    @Override
    public void replaceDataForAttachment(Attachment attachment, AttachmentDataStream dataStream) {
        this.legacyDaoSupport().replaceDataForAttachment(attachment, dataStream);
    }

    private LegacyAttachmentDataDaoSupport legacyDaoSupport() {
        return new LegacyAttachmentDataDaoSupport(this);
    }

    private void checkAttachmentAndOriginalContentArgumentsNotNull(Attachment attachment, ContentEntityObject originalContent) {
        if (attachment == null) {
            throw new IllegalArgumentException("The attachment cannot be null.");
        }
        if (originalContent == null) {
            throw new IllegalArgumentException("The content cannot be null.");
        }
    }

    private void cleanupEmptyAncestors(File attachmentFolder) {
        FileSystemAttachmentDataUtil.cleanupEmptyAncestors(attachmentFolder, null);
    }

    private String toFileName(Attachment attachment) {
        return Integer.toString(attachment.getVersion());
    }

    @Override
    public AttachmentDataStorageType getStorageType() {
        return AttachmentDataStorageType.FILE_SYSTEM;
    }

    private static class AttachmentFileNameNamingStrategy
    implements FileSystemAttachmentNamingStrategy {
        private AttachmentFileNameNamingStrategy() {
        }

        @Override
        public String getAttachmentFileName(Attachment attachment) {
            return attachment.getFileName();
        }
    }

    private static class AttachmentIdNamingStrategy
    implements FileSystemAttachmentNamingStrategy {
        private AttachmentIdNamingStrategy() {
        }

        @Override
        public String getAttachmentFileName(Attachment attachment) {
            return String.valueOf(((Attachment)attachment.getLatestVersion()).getId());
        }
    }

    public static interface FileSystemAttachmentNamingStrategy {
        public String getAttachmentFileName(Attachment var1);
    }
}

