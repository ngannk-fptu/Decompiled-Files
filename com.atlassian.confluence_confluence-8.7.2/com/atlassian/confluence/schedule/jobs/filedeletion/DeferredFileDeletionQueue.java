/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  io.atlassian.fugue.Either
 *  net.jcip.annotations.ThreadSafe
 */
package com.atlassian.confluence.schedule.jobs.filedeletion;

import com.atlassian.confluence.impl.pages.attachments.filesystem.model.AttachmentRef;
import com.atlassian.confluence.schedule.jobs.filedeletion.DeferredFileDeletionJob;
import com.google.common.annotations.VisibleForTesting;
import io.atlassian.fugue.Either;
import java.io.File;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class DeferredFileDeletionQueue {
    private final ConcurrentLinkedQueue<DeferredFileDeletionJob.DeferredFileDeletionOperation> deletionQueue;

    public DeferredFileDeletionQueue() {
        this.deletionQueue = new ConcurrentLinkedQueue();
    }

    @VisibleForTesting
    DeferredFileDeletionQueue(ConcurrentLinkedQueue<DeferredFileDeletionJob.DeferredFileDeletionOperation> deletionQueue) {
        this.deletionQueue = deletionQueue;
    }

    public void offer(Either<AttachmentRef, AttachmentRef.Container> container, File file) {
        DeferredFileDeletionJob.DeferredFileDeletionOperation fileDeletionOperation = new DeferredFileDeletionJob.DeferredFileDeletionOperation(container, file, 5);
        this.deletionQueue.offer(fileDeletionOperation);
    }

    public void offer(File file) {
        DeferredFileDeletionJob.DeferredFileDeletionOperation fileDeletionOperation = new DeferredFileDeletionJob.DeferredFileDeletionOperation(null, file, 5);
        this.deletionQueue.offer(fileDeletionOperation);
    }

    DeferredFileDeletionJob.DeferredFileDeletionOperation peek() {
        return this.deletionQueue.peek();
    }

    DeferredFileDeletionJob.DeferredFileDeletionOperation poll() {
        return this.deletionQueue.poll();
    }

    void offer(DeferredFileDeletionJob.DeferredFileDeletionOperation fileDeletionOperation) {
        this.deletionQueue.offer(fileDeletionOperation);
    }
}

