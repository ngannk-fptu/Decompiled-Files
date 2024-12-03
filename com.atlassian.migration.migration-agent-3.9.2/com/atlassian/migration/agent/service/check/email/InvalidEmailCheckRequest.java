/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service.check.email;

import lombok.Generated;

public class InvalidEmailCheckRequest {
    private String fileId;
    private String migrationType = "S2C_MIGRATION";

    public InvalidEmailCheckRequest(String fileId) {
        this.fileId = fileId;
    }

    @Generated
    public String getFileId() {
        return this.fileId;
    }

    @Generated
    public String getMigrationType() {
        return this.migrationType;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof InvalidEmailCheckRequest)) {
            return false;
        }
        InvalidEmailCheckRequest other = (InvalidEmailCheckRequest)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$fileId = this.getFileId();
        String other$fileId = other.getFileId();
        if (this$fileId == null ? other$fileId != null : !this$fileId.equals(other$fileId)) {
            return false;
        }
        String this$migrationType = this.getMigrationType();
        String other$migrationType = other.getMigrationType();
        return !(this$migrationType == null ? other$migrationType != null : !this$migrationType.equals(other$migrationType));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof InvalidEmailCheckRequest;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $fileId = this.getFileId();
        result = result * 59 + ($fileId == null ? 43 : $fileId.hashCode());
        String $migrationType = this.getMigrationType();
        result = result * 59 + ($migrationType == null ? 43 : $migrationType.hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "InvalidEmailCheckRequest(fileId=" + this.getFileId() + ", migrationType=" + this.getMigrationType() + ")";
    }
}

