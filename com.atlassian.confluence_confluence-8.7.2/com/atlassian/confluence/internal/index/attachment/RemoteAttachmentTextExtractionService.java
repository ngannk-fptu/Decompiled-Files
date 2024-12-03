/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.spring.container.ContainerManager
 *  com.google.common.annotations.VisibleForTesting
 */
package com.atlassian.confluence.internal.index.attachment;

import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.index.attachment.AttachmentTextExtraction;
import com.atlassian.confluence.index.attachment.AttachmentTextExtractionService;
import com.atlassian.spring.container.ContainerManager;
import com.google.common.annotations.VisibleForTesting;
import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionStage;

public class RemoteAttachmentTextExtractionService
implements AttachmentTextExtractionService {
    @VisibleForTesting
    static final String ATTACHMENT_EXTRACTION_SERVICE = "attachment-text-extraction-service";
    private final ClusterManager clusterManager;

    public RemoteAttachmentTextExtractionService(ClusterManager clusterManager) {
        this.clusterManager = Objects.requireNonNull(clusterManager);
    }

    @Override
    public CompletionStage<AttachmentTextExtraction> submit(long attachmentId, int version) {
        return this.clusterManager.submitToKeyOwner(new AttachmentTextExtractionTask(attachmentId, version), ATTACHMENT_EXTRACTION_SERVICE, attachmentId);
    }

    static class AttachmentTextExtractionTask
    implements Callable<AttachmentTextExtraction>,
    Serializable {
        private final long attachmentId;
        private final int version;

        AttachmentTextExtractionTask(long attachmentId, int version) {
            this.attachmentId = attachmentId;
            this.version = version;
        }

        @Override
        public AttachmentTextExtraction call() {
            AttachmentTextExtractionService localAttachmentTextExtractionService = (AttachmentTextExtractionService)ContainerManager.getInstance().getContainerContext().getComponent((Object)"localAttachmentTextExtractionService");
            CompletionStage<AttachmentTextExtraction> completionStage = localAttachmentTextExtractionService.submit(this.attachmentId, this.version);
            return completionStage.toCompletableFuture().join();
        }
    }
}

