/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.internal.index.attachment;

import java.util.Optional;

public enum AttachmentStatus {
    CONTENT_EXTRACTED(0),
    EXTRACTION_ERROR(1);

    private final int priority;

    private AttachmentStatus(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return this.priority;
    }

    public static Optional<AttachmentStatus> ofNullable(String name) {
        try {
            if (name == null) {
                return Optional.empty();
            }
            return Optional.of(AttachmentStatus.valueOf(name));
        }
        catch (Exception e) {
            return Optional.empty();
        }
    }
}

