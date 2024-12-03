/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.audit.file;

import com.atlassian.audit.rest.model.AuditRetentionFileConfigJson;
import java.util.Objects;

public class AuditRetentionFileConfig {
    private final int maxFileCount;
    private final long maxFileSizeB;

    public AuditRetentionFileConfig(int maxFileCount, long maxFileSize) {
        this.maxFileCount = maxFileCount;
        this.maxFileSizeB = maxFileSize * 1024L * 1024L;
    }

    public int getMaxFileCount() {
        return this.maxFileCount;
    }

    public long getMaxFileSizeB() {
        return this.maxFileSizeB;
    }

    public AuditRetentionFileConfigJson toJson() {
        return new AuditRetentionFileConfigJson(this.getMaxFileCount());
    }

    public static AuditRetentionFileConfig fromJson(AuditRetentionFileConfigJson configJson, long maxFileSize) {
        return new AuditRetentionFileConfig(configJson.getMaxFileCount(), maxFileSize);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AuditRetentionFileConfig that = (AuditRetentionFileConfig)o;
        return this.maxFileCount == that.maxFileCount;
    }

    public int hashCode() {
        return Objects.hash(this.maxFileCount);
    }
}

