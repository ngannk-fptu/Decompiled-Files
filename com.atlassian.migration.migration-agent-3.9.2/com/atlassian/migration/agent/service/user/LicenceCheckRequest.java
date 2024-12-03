/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.domain.Product
 *  com.google.common.collect.ImmutableList
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service.user;

import com.atlassian.cmpt.domain.Product;
import com.google.common.collect.ImmutableList;
import java.util.List;
import lombok.Generated;

public class LicenceCheckRequest {
    private String fileId;
    private String migrationType = "S2C_MIGRATION";
    private List<String> products = ImmutableList.of((Object)Product.CONFLUENCE.getKey());

    public LicenceCheckRequest(String fileId) {
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
    public List<String> getProducts() {
        return this.products;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof LicenceCheckRequest)) {
            return false;
        }
        LicenceCheckRequest other = (LicenceCheckRequest)o;
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
        if (this$migrationType == null ? other$migrationType != null : !this$migrationType.equals(other$migrationType)) {
            return false;
        }
        List<String> this$products = this.getProducts();
        List<String> other$products = other.getProducts();
        return !(this$products == null ? other$products != null : !((Object)this$products).equals(other$products));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof LicenceCheckRequest;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $fileId = this.getFileId();
        result = result * 59 + ($fileId == null ? 43 : $fileId.hashCode());
        String $migrationType = this.getMigrationType();
        result = result * 59 + ($migrationType == null ? 43 : $migrationType.hashCode());
        List<String> $products = this.getProducts();
        result = result * 59 + ($products == null ? 43 : ((Object)$products).hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "LicenceCheckRequest(fileId=" + this.getFileId() + ", migrationType=" + this.getMigrationType() + ", products=" + this.getProducts() + ")";
    }
}

