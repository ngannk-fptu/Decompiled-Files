/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model;

import com.amazonaws.services.s3.internal.S3RequesterChargedResult;
import com.amazonaws.services.s3.internal.S3RestoreOutputPathResult;
import java.io.Serializable;

public class RestoreObjectResult
implements Serializable,
S3RequesterChargedResult,
S3RestoreOutputPathResult {
    private boolean isRequesterCharged;
    private String restoreOutputPath;

    @Override
    public boolean isRequesterCharged() {
        return this.isRequesterCharged;
    }

    @Override
    public void setRequesterCharged(boolean isRequesterCharged) {
        this.isRequesterCharged = isRequesterCharged;
    }

    @Override
    public String getRestoreOutputPath() {
        return this.restoreOutputPath;
    }

    @Override
    public void setRestoreOutputPath(String restoreOutputPath) {
        this.restoreOutputPath = restoreOutputPath;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (this.getRestoreOutputPath() != null) {
            sb.append("restoreOutputPath: ").append(this.getRestoreOutputPath()).append(",");
        }
        sb.append("isRequestCharged: ").append(this.isRequesterCharged());
        sb.append("}");
        return sb.toString();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof RestoreObjectResult)) {
            return false;
        }
        RestoreObjectResult other = (RestoreObjectResult)obj;
        if (other.getRestoreOutputPath() == null ^ this.getRestoreOutputPath() == null) {
            return false;
        }
        if (other.getRestoreOutputPath() != null && !other.getRestoreOutputPath().equals(this.getRestoreOutputPath())) {
            return false;
        }
        return other.isRequesterCharged() == this.isRequesterCharged();
    }

    public int hashCode() {
        int prime = 31;
        int hashCode = 1;
        hashCode = 31 * hashCode + (this.getRestoreOutputPath() == null ? 0 : this.getRestoreOutputPath().hashCode());
        return hashCode;
    }
}

