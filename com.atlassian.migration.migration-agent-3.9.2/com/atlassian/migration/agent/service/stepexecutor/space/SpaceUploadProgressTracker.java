/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.service.stepexecutor.space;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

public class SpaceUploadProgressTracker {
    private final AtomicLong uploadedBytes = new AtomicLong();
    private final Consumer<Long> onProgress;

    SpaceUploadProgressTracker(Consumer<Long> onProgress) {
        this.onProgress = onProgress;
    }

    void addUploadedBytes(long uploadedBytes) {
        this.uploadedBytes.addAndGet(uploadedBytes);
        this.onProgress.accept(uploadedBytes);
    }
}

