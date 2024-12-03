/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.math.IntMath
 *  org.springframework.beans.factory.DisposableBean
 */
package com.atlassian.confluence.internal.index.attachment;

import com.atlassian.confluence.index.attachment.AttachmentTextExtraction;
import com.atlassian.confluence.index.attachment.AttachmentTextExtractionService;
import com.atlassian.confluence.search.ReIndexOption;
import com.google.common.math.IntMath;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiFunction;
import org.springframework.beans.factory.DisposableBean;

public class LocalAttachmentTextExtractionService
implements AttachmentTextExtractionService,
DisposableBean {
    private static final int NUMBER_OF_WORKERS = Integer.getInteger("attachment.text.extraction.workers", ReIndexOption.ATTACHMENT_ONLY.getThreadCount());
    private final ExecutorService[] executorServices;
    private final BiFunction<Long, Integer, AttachmentTextExtraction> textExtractionFunction;

    public LocalAttachmentTextExtractionService(BiFunction<Long, Integer, AttachmentTextExtraction> textExtractionFunction) {
        this.textExtractionFunction = Objects.requireNonNull(textExtractionFunction);
        this.executorServices = new ExecutorService[NUMBER_OF_WORKERS];
        for (int i = 0; i < NUMBER_OF_WORKERS; ++i) {
            String workerName = String.format("attachment-text-extraction-worker-%d", i);
            this.executorServices[i] = Executors.newSingleThreadExecutor(runnable -> new Thread(runnable, workerName));
        }
    }

    @Override
    public CompletionStage<AttachmentTextExtraction> submit(long attachmentId, int version) {
        return CompletableFuture.supplyAsync(() -> this.textExtractionFunction.apply(attachmentId, version), this.executorServices[IntMath.mod((int)Long.hashCode(attachmentId), (int)NUMBER_OF_WORKERS)]);
    }

    public void destroy() throws Exception {
        for (int i = 0; i < NUMBER_OF_WORKERS; ++i) {
            this.executorServices[i].shutdown();
        }
    }
}

