/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.dc.filestore.api.compat.FilesystemPath
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.util.concurrent.ThreadFactories
 *  com.google.common.annotations.VisibleForTesting
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.upgrade;

import com.atlassian.confluence.impl.pages.attachments.filesystem.AttachmentDataFile;
import com.atlassian.confluence.impl.pages.attachments.filesystem.AttachmentDataFileSystem;
import com.atlassian.confluence.impl.pages.attachments.filesystem.AttachmentDataStorageLocationResolver;
import com.atlassian.confluence.pages.persistence.dao.AttachmentDataStreamType;
import com.atlassian.confluence.pages.persistence.dao.filesystem.FileSystemAttachmentDataUtil;
import com.atlassian.confluence.upgrade.AttachmentMigrationAnalyticsEvent;
import com.atlassian.confluence.upgrade.AttachmentMigratorToV4Reporter;
import com.atlassian.confluence.util.longrunning.ConfluenceAbstractLongRunningTask;
import com.atlassian.dc.filestore.api.compat.FilesystemPath;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.util.concurrent.ThreadFactories;
import com.google.common.annotations.VisibleForTesting;
import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AttachmentMigratorToV4
extends ConfluenceAbstractLongRunningTask {
    private static final Logger logger = LoggerFactory.getLogger(AttachmentMigratorToV4.class);
    public static final String CONFLUENCE_ATTACHMENTS_VER004_MIGRATION_NUM_OF_THREADS = "confluence.attachments-ver004-migration-num-of-threads";
    public static final String CONFLUENCE_ATTACHMENTS_VER004_LEVEL_FOR_NEW_THREAD = "confluence.attachments-ver004-level-for-new-thread";
    private final AttachmentDataFileSystem attachmentDataFileSystemV4;
    private final AttachmentMigratorToV4Reporter reporter;
    private final AttachmentDataStorageLocationResolver v3LocationResolver;
    private final AtomicBoolean stopMigration = new AtomicBoolean(false);
    private final AtomicLong migrationCount = new AtomicLong(0L);
    private int levelForNewThread;
    private Map<String, Boolean> lockedAttachmentIds = new ConcurrentHashMap<String, Boolean>();
    private EventPublisher eventPublisher;
    private AtomicInteger numDuplicates;
    private AtomicInteger numFailedToMigrate;
    private AtomicInteger numCores;
    private AtomicBoolean migrationIsRunning;

    public AttachmentMigratorToV4(AttachmentDataStorageLocationResolver v3LocationResolver, AttachmentDataFileSystem attachmentDataFileSystemV4, EventPublisher eventPublisher) throws IOException {
        this(v3LocationResolver, attachmentDataFileSystemV4, eventPublisher, new AttachmentMigratorToV4Reporter(new AttachmentMigratorToV4Reporter.OutputStreamWriterFactory()));
    }

    @VisibleForTesting
    public AttachmentMigratorToV4(AttachmentDataStorageLocationResolver locationResolver, AttachmentDataFileSystem attachmentDataFileSystemV4, EventPublisher eventPublisher, AttachmentMigratorToV4Reporter reporter) {
        this.v3LocationResolver = locationResolver;
        this.attachmentDataFileSystemV4 = attachmentDataFileSystemV4;
        this.reporter = reporter;
        this.eventPublisher = eventPublisher;
        this.numCores = new AtomicInteger(0);
        this.numFailedToMigrate = new AtomicInteger(0);
        this.numDuplicates = new AtomicInteger(0);
        this.migrationIsRunning = new AtomicBoolean(false);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public void migrate() {
        if (this.migrationIsRunning.getAndSet(true)) {
            log.info("A Migration is already in progress. Only one migration can run at a time");
            return;
        }
        this.cleanAnalytics();
        ExecutorService executorService = this.initExecutor();
        try {
            FilesystemPath sourcePath = this.v3LocationResolver.getFileLocation();
            this.reporter.initFile((FilesystemPath)sourcePath.getParent().orElseThrow());
            this.reporter.writeMessage("Attachments migration from v3 to v4 started");
            this.migrateAttachments(sourcePath, 0, executorService);
        }
        catch (Exception e) {
            logger.error("Exception while running attachments migration to V4. Please restart Confluence to retry. Error: {}", (Object)e.getMessage());
        }
        finally {
            executorService.shutdown();
            try {
                while (!executorService.awaitTermination(60L, TimeUnit.SECONDS)) {
                }
                if (this.stopMigration.get()) {
                    this.notifyInterrupted(new InterruptedException("Confluence shutdown"));
                } else {
                    this.notifyCompleted();
                }
            }
            catch (InterruptedException e) {
                this.notifyInterrupted(e);
                Thread.currentThread().interrupt();
            }
        }
        this.migrationIsRunning.set(false);
        this.reporter.close();
    }

    private void cleanAnalytics() {
        this.numDuplicates.set(0);
        this.numCores.set(0);
        this.numFailedToMigrate.set(0);
    }

    private void notifyInterrupted(InterruptedException e) {
        String interruptedMsg = String.format("Attachments migration from v3 to v4 task interrupted. Msg: %s", e.getMessage());
        logger.info(interruptedMsg);
        this.eventPublisher.publish((Object)new AttachmentMigrationAnalyticsEvent(this.numDuplicates.get(), this.numFailedToMigrate.get(), this.numCores.get(), "interrupted"));
        this.reporter.writeMessage(interruptedMsg);
        this.reportStatistics();
    }

    private void notifyCompleted() {
        String message;
        File ver003Dir = this.v3LocationResolver.getFileLocation().asJavaFile();
        if (ver003Dir.exists()) {
            message = String.format("Attachments migration from V3 to V4 completed with warnings. Some files failed to migrate. Please review all remaining files in %s directory and move them to the appropriate place. You can check the report file %s for details. This message will keep showing up until all files are removed from ver003 directory.", ver003Dir.getAbsolutePath(), "v3-to-v4-report.log");
            logger.warn(message);
            this.eventPublisher.publish((Object)new AttachmentMigrationAnalyticsEvent(this.numDuplicates.get(), this.numFailedToMigrate.get(), this.numCores.get(), "completedWithWarnings"));
        } else {
            message = "Attachments migration from V3 to V4 completed successfully.";
            logger.info(message);
            this.eventPublisher.publish((Object)new AttachmentMigrationAnalyticsEvent(this.numDuplicates.get(), this.numFailedToMigrate.get(), this.numCores.get(), "completed"));
        }
        this.reporter.writeMessage(message);
        this.reportStatistics();
    }

    private void reportStatistics() {
        String fileMigratedMsg = String.format("Files migrated: %d", this.migrationCount.get());
        logger.info(fileMigratedMsg);
        this.reporter.writeMessage(fileMigratedMsg);
        this.stopTimer();
        String timeElapsedMsg = String.format("Time elapsed: %d ms", this.getElapsedTime());
        logger.info(timeElapsedMsg);
        this.reporter.writeMessage(timeElapsedMsg);
    }

    public void stopMigration() {
        this.stopMigration.set(true);
    }

    private ExecutorService initExecutor() {
        this.migrationCount.set(0L);
        this.numCores.set(Math.max(1, Integer.getInteger(CONFLUENCE_ATTACHMENTS_VER004_MIGRATION_NUM_OF_THREADS, Runtime.getRuntime().availableProcessors() / 2)));
        this.levelForNewThread = Integer.getInteger(CONFLUENCE_ATTACHMENTS_VER004_LEVEL_FOR_NEW_THREAD, 4);
        logger.info("Running thread pool of size: {}. You can set the number of threads via '{}' system variable", (Object)this.numCores.get(), (Object)CONFLUENCE_ATTACHMENTS_VER004_MIGRATION_NUM_OF_THREADS);
        return Executors.newFixedThreadPool(this.numCores.get(), ThreadFactories.namedThreadFactory((String)"attachments-migration-job"));
    }

    private void migrateAttachments(FilesystemPath sourcePath, int levelDepth, ExecutorService executorService) throws InterruptedException {
        if (this.stopMigration.get()) {
            return;
        }
        logger.debug("Start processing a directory {}", (Object)sourcePath);
        String[] listOfFiles = sourcePath.asJavaFile().list();
        if (listOfFiles == null) {
            logger.warn("List returned null for directory {}", (Object)sourcePath);
            return;
        }
        logger.debug("Found {} entries in a directory {}", (Object)listOfFiles.length, (Object)sourcePath);
        File leafToCleanUp = null;
        for (String fileName : listOfFiles) {
            FilesystemPath sourceFilePath = sourcePath.path(new String[]{fileName});
            if (fileName.equals(".DS_Store")) {
                leafToCleanUp = this.deleteDSStoreFile(sourceFilePath);
                continue;
            }
            if (sourceFilePath.asJavaFile().isDirectory()) {
                if (levelDepth == this.levelForNewThread) {
                    executorService.submit(() -> this.migrateAttachmentsInNewThread(sourceFilePath, levelDepth + 1, executorService));
                    continue;
                }
                this.migrateAttachments(sourceFilePath, levelDepth + 1, executorService);
                continue;
            }
            leafToCleanUp = this.moveAttachmentToV4(sourceFilePath, fileName, leafToCleanUp);
        }
        if (listOfFiles.length == 0) {
            leafToCleanUp = sourcePath.path(new String[]{"leaf"}).asJavaFile();
        }
        this.cleanUpV3Directory(leafToCleanUp);
    }

    private File deleteDSStoreFile(FilesystemPath sourceFilePath) {
        File leafToCleanUp = sourceFilePath.asJavaFile();
        try {
            sourceFilePath.deleteFile();
        }
        catch (Exception e) {
            logger.debug("Failed to delete file {}. Msg: {}", (Object)sourceFilePath.asJavaFile().getAbsolutePath(), (Object)e.getMessage());
            this.reporter.reportFailedFile(sourceFilePath.asJavaFile().getAbsolutePath(), "Failed to delete");
        }
        return leafToCleanUp;
    }

    private void migrateAttachmentsInNewThread(FilesystemPath sourcePath, int levelDepth, ExecutorService executorService) {
        try {
            this.migrateAttachments(sourcePath, levelDepth, executorService);
        }
        catch (InterruptedException e) {
            logger.info("Attachments migration to v4 task interrupted. Msg: {}", (Object)e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private File moveAttachmentToV4(FilesystemPath sourceFilePath, String fileName, File leafToCleanup) throws InterruptedException {
        logger.debug("Found a V3 attachment {}", (Object)sourceFilePath.asJavaFile().getAbsolutePath());
        String attachmentId = sourceFilePath.asJavaFile().getParentFile().getName();
        try {
            FilesystemPath targetPath;
            this.waitForDuplicateAttachments(attachmentId);
            AttachmentDataStreamType type = AttachmentDataStreamType.RAW_BINARY;
            String version = sourceFilePath.asJavaFile().getName();
            LinkedList<StandardCopyOption> copyOptions = new LinkedList<StandardCopyOption>();
            if (version.indexOf(".extracted_text") > 0) {
                version = fileName.substring(0, fileName.lastIndexOf(".extracted_text"));
                type = AttachmentDataStreamType.EXTRACTED_TEXT;
                copyOptions.add(StandardCopyOption.REPLACE_EXISTING);
            }
            if ((targetPath = this.calculateTargetFilePath(fileName, sourceFilePath, attachmentId, type, version)) == null) {
                logger.debug("No need to migrate file {}. Deleting it.", (Object)sourceFilePath.asJavaFile().getAbsolutePath());
                Files.delete(sourceFilePath.asJavaPath());
                File file = sourceFilePath.asJavaFile();
                return file;
            }
            Path sourceAbsolutePath = sourceFilePath.asJavaPath().toAbsolutePath();
            Path targetAbsolutePath = targetPath.asJavaPath().toAbsolutePath();
            logger.debug("Moving file from {} to {}", (Object)sourceAbsolutePath, (Object)targetAbsolutePath);
            Files.move(sourceAbsolutePath, targetAbsolutePath, (CopyOption[])copyOptions.toArray(CopyOption[]::new));
            logger.debug("Moved file from {} to {}", (Object)sourceAbsolutePath, (Object)targetAbsolutePath);
            long migratedFiles = this.migrationCount.incrementAndGet();
            if (migratedFiles % 50000L == 0L) {
                logger.info("Attachments migration from V3 to V4 progressed. Moved {} attachments since the last restart.", (Object)migratedFiles);
            }
            File file = sourceFilePath.asJavaFile();
            return file;
        }
        catch (IOException | NumberFormatException e) {
            logger.debug("Failed to migrate a V3 attachment: {}. Msg: {}", (Object)sourceFilePath.asJavaPath().toAbsolutePath(), (Object)e.getMessage());
            this.reporter.reportFailedFile(sourceFilePath.asJavaFile().getAbsolutePath(), "Failed to migrate. Msg: " + e.getMessage());
            this.numFailedToMigrate.getAndIncrement();
        }
        finally {
            this.lockedAttachmentIds.remove(attachmentId);
        }
        return leafToCleanup;
    }

    private void waitForDuplicateAttachments(String attachmentId) throws InterruptedException {
        while (this.lockedAttachmentIds.putIfAbsent(attachmentId, true) != null) {
            logger.debug("Waiting for another attachment with id {}", (Object)attachmentId);
            Thread.sleep(500L);
        }
    }

    private void cleanUpV3Directory(File leafToCleanUp) {
        File v3Dir = this.v3LocationResolver.getFileLocation().asJavaFile();
        if (leafToCleanUp != null) {
            boolean SUPPRESS_LOGGING = true;
            FileSystemAttachmentDataUtil.cleanupEmptyAncestors(leafToCleanUp, v3Dir.getParentFile(), true);
        }
    }

    private @Nullable FilesystemPath calculateTargetFilePath(String fileName, FilesystemPath sourceFilePath, String attachmentId, AttachmentDataStreamType type, String version) throws NumberFormatException, IOException {
        AttachmentDataFile<FilesystemPath> targetAttachmentFile = this.attachmentDataFileSystemV4.getAttachmentDataFile(Long.parseLong(attachmentId), null, null, Integer.parseInt(version), type);
        FilesystemPath targetPath = targetAttachmentFile.getFilePath();
        FilesystemPath parentTargetDir = targetPath.getParent().orElse(targetPath);
        String targetFileName = targetPath.getLeafName().orElse(fileName);
        this.createDirectory(parentTargetDir.asJavaFile());
        int duplicateNumber = 0;
        while (targetPath.asJavaFile().exists()) {
            if (type == AttachmentDataStreamType.EXTRACTED_TEXT) {
                FileTime targetLastModifiedTime;
                FileTime sourceLastModifiedTime = Files.readAttributes(sourceFilePath.asJavaPath(), BasicFileAttributes.class, new LinkOption[0]).lastModifiedTime();
                if (sourceLastModifiedTime.compareTo(targetLastModifiedTime = Files.readAttributes(targetPath.asJavaPath(), BasicFileAttributes.class, new LinkOption[0]).lastModifiedTime()) > 0) {
                    logger.debug("Found a duplicate extracted_text file {}. Using the latest version {}.", (Object)sourceFilePath.asJavaFile().getAbsolutePath(), (Object)targetPath.asJavaFile().getAbsolutePath());
                    return targetPath;
                }
                logger.debug(".extracted_text file {} is older than the file in v4 storage {}. Deleting the file", (Object)sourceFilePath.asJavaFile().getAbsolutePath(), (Object)targetPath.asJavaFile().getAbsolutePath());
                return null;
            }
            targetPath = parentTargetDir.path(new String[]{targetFileName + ".duplicate." + ++duplicateNumber});
        }
        if (duplicateNumber > 0) {
            this.numDuplicates.getAndAdd(1);
            logger.debug("Found a duplicate V3 attachment with id {} ({}). Saving it in V4 directory as {}", new Object[]{attachmentId, sourceFilePath.asJavaFile().getAbsolutePath(), targetPath.asJavaFile().getAbsolutePath()});
            this.reporter.reportFailedFile(sourceFilePath.asJavaFile().getAbsolutePath(), "Duplicate file saved as " + targetPath.asJavaFile().getAbsolutePath());
        }
        return targetPath;
    }

    private void createDirectory(File file) throws IOException {
        if (file.exists()) {
            if (!file.isDirectory()) {
                throw new IOException(file.getAbsolutePath() + " already exists but it is not a directory.");
            }
        } else if (!file.mkdirs()) {
            logger.warn("Failed to create a directory {}. It might already exist.", (Object)file.getAbsolutePath());
        }
    }

    @Override
    protected void runInternal() {
        this.migrate();
    }

    public String getName() {
        return null;
    }
}

