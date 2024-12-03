/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.service.catalogue.model;

import lombok.Generated;
import org.codehaus.jackson.annotate.JsonProperty;

public class MigrationCatalogueStorageFile {
    @JsonProperty
    private String fileId;
    @JsonProperty
    private String name;
    @JsonProperty
    private long size;

    public MigrationCatalogueStorageFile() {
    }

    @Generated
    public String getFileId() {
        return this.fileId;
    }

    @Generated
    public String getName() {
        return this.name;
    }

    @Generated
    public long getSize() {
        return this.size;
    }

    @Generated
    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    @Generated
    public void setName(String name) {
        this.name = name;
    }

    @Generated
    public void setSize(long size) {
        this.size = size;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof MigrationCatalogueStorageFile)) {
            return false;
        }
        MigrationCatalogueStorageFile other = (MigrationCatalogueStorageFile)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$fileId = this.getFileId();
        String other$fileId = other.getFileId();
        if (this$fileId == null ? other$fileId != null : !this$fileId.equals(other$fileId)) {
            return false;
        }
        String this$name = this.getName();
        String other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) {
            return false;
        }
        return this.getSize() == other.getSize();
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof MigrationCatalogueStorageFile;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $fileId = this.getFileId();
        result = result * 59 + ($fileId == null ? 43 : $fileId.hashCode());
        String $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        long $size = this.getSize();
        result = result * 59 + (int)($size >>> 32 ^ $size);
        return result;
    }

    @Generated
    public String toString() {
        return "MigrationCatalogueStorageFile(fileId=" + this.getFileId() + ", name=" + this.getName() + ", size=" + this.getSize() + ")";
    }

    @Generated
    public MigrationCatalogueStorageFile(String fileId, String name, long size) {
        this.fileId = fileId;
        this.name = name;
        this.size = size;
    }
}

