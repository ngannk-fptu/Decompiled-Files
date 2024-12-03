/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 *  com.google.common.annotations.VisibleForTesting
 *  io.atlassian.fugue.Either
 *  org.apache.commons.io.FileUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.schedule.jobs.filedeletion;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.impl.pages.attachments.filesystem.AttachmentDataFileSystemV003;
import com.atlassian.confluence.impl.pages.attachments.filesystem.model.AttachmentRef;
import com.atlassian.confluence.internal.pages.AttachmentManagerInternal;
import com.atlassian.confluence.internal.pages.PageManagerInternal;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.schedule.jobs.filedeletion.DeferredFileDeletionQueue;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import com.google.common.annotations.VisibleForTesting;
import io.atlassian.fugue.Either;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeferredFileDeletionJob
implements JobRunner {
    static final int NUM_RETRIES = 5;
    private static final Logger log = LoggerFactory.getLogger(DeferredFileDeletionJob.class);
    private final FileDeletion deleteFile;
    private final DeferredFileDeletionQueue deletionQueue;
    private final PageManagerInternal pageManager;
    private final AttachmentManagerInternal attachmentManager;
    private final AttachmentDataFileSystemV003 attachmentDataFileSystemV003;

    public DeferredFileDeletionJob(PageManagerInternal pageManager, AttachmentManagerInternal attachmentManager, DeferredFileDeletionQueue deletionQueue, AttachmentDataFileSystemV003 attachmentDataFileSystemV003) {
        this(pageManager, attachmentManager, deletionQueue, attachmentDataFileSystemV003, FileUtils::forceDelete);
    }

    @Internal
    @VisibleForTesting
    DeferredFileDeletionJob(PageManagerInternal pageManager, AttachmentManagerInternal attachmentManager, DeferredFileDeletionQueue deletionQueue, AttachmentDataFileSystemV003 attachmentDataFileSystemV003, FileDeletion deleteFile) {
        this.pageManager = pageManager;
        this.attachmentManager = attachmentManager;
        this.deletionQueue = deletionQueue;
        this.attachmentDataFileSystemV003 = attachmentDataFileSystemV003;
        this.deleteFile = deleteFile;
    }

    public @Nullable JobRunnerResponse runJob(JobRunnerRequest request) {
        DeferredFileDeletionOperation operation;
        ArrayList<DeferredFileDeletionOperation> filesToRetry = new ArrayList<DeferredFileDeletionOperation>();
        int numFailedToRetry = 0;
        while (this.deletionQueue.peek() != null && (operation = this.deletionQueue.poll()) != null) {
            File file = operation.getFile();
            if (!file.exists()) {
                log.debug("File/directory {} was already deleted. Skipping it.", (Object)file);
                continue;
            }
            try {
                Either<AttachmentRef, AttachmentRef.Container> container = operation.getContainer();
                if (container != null) {
                    File[] currentContainerFile = new File[]{null};
                    if (container.isLeft()) {
                        Attachment attachment = this.attachmentManager.getAttachment(((AttachmentRef)container.left().get()).getId());
                        Optional.ofNullable(attachment).ifPresent(exitingAttachment -> {
                            File attachmentFile = this.attachmentDataFileSystemV003.containerDirectoryForAttachmentVersions(attachment.getId(), attachment.getContainer().getId(), Optional.of(attachment.getSpace().getId()));
                            currentContainerFile[0] = attachmentFile.getParentFile();
                        });
                    } else {
                        AbstractPage contentEntityObject = this.pageManager.getAbstractPage(((AttachmentRef.Container)container.right().get()).getId());
                        Optional.ofNullable(contentEntityObject).ifPresent(exisintCEO -> {
                            File attachmentFile = this.attachmentDataFileSystemV003.containerDirectoryForAttachmentVersions(-1L, contentEntityObject.getId(), Optional.of(contentEntityObject.getSpace().getId()));
                            currentContainerFile[0] = attachmentFile.getParentFile();
                        });
                    }
                    String currentContainerFilePath = currentContainerFile[0] == null ? "" : currentContainerFile[0].getAbsolutePath();
                    String deleteFilePath = file.getAbsolutePath();
                    if (StringUtils.isNotEmpty((CharSequence)currentContainerFilePath) && deleteFilePath.contains(currentContainerFilePath)) {
                        log.warn("The file [{}] is being used will skip deleting it", (Object)deleteFilePath);
                        continue;
                    }
                }
                this.deleteFile.delete(file);
                log.debug("File/directory {} successfully deleted.", (Object)file);
            }
            catch (IOException e) {
                DeferredFileDeletionOperation newFileDeletionOperation = operation.decrementRetries();
                int remainingRetries = newFileDeletionOperation.getRemainingRetries();
                if (remainingRetries > 0) {
                    log.warn("File/directory {} could not be deleted. {} retries remaining.", (Object)file, (Object)remainingRetries);
                    filesToRetry.add(newFileDeletionOperation);
                    continue;
                }
                log.error("File/directory {} could not be deleted even after {} reties", (Object)file, (Object)5);
                ++numFailedToRetry;
            }
        }
        int numFilesToRetry = filesToRetry.size();
        filesToRetry.forEach(this.deletionQueue::offer);
        if (numFailedToRetry == 0) {
            return numFilesToRetry == 0 ? null : JobRunnerResponse.success((String)("filesToRetry: " + numFilesToRetry));
        }
        return numFilesToRetry == 0 ? JobRunnerResponse.failed((String)("failedToRetry: " + numFailedToRetry)) : JobRunnerResponse.failed((String)("failedToRetry: " + numFailedToRetry + ", filesToRetry: " + numFilesToRetry));
    }

    static class DeferredFileDeletionOperation {
        private Either<AttachmentRef, AttachmentRef.Container> container;
        private final File file;
        private final int numRetries;

        public DeferredFileDeletionOperation(@Nullable Either<AttachmentRef, AttachmentRef.Container> container, File file, int numRetries) {
            this.container = container;
            this.file = file;
            this.numRetries = numRetries;
        }

        public int getRemainingRetries() {
            return this.numRetries;
        }

        public File getFile() {
            return this.file;
        }

        public Either<AttachmentRef, AttachmentRef.Container> getContainer() {
            return this.container;
        }

        public DeferredFileDeletionOperation decrementRetries() {
            return new DeferredFileDeletionOperation(this.container, this.file, this.numRetries - 1);
        }

        public int hashCode() {
            return Objects.hash(this.numRetries, this.file);
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof DeferredFileDeletionOperation)) {
                return false;
            }
            DeferredFileDeletionOperation that = (DeferredFileDeletionOperation)obj;
            return this.numRetries == that.numRetries && Objects.equals(this.file, that.file);
        }
    }

    @VisibleForTesting
    @Internal
    static interface FileDeletion {
        public void delete(File var1) throws IOException;
    }
}

