/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model;

import com.amazonaws.AmazonWebServiceRequest;
import java.io.Serializable;

public class GenerateRandomRequest
extends AmazonWebServiceRequest
implements Serializable,
Cloneable {
    private Integer numberOfBytes;
    private String customKeyStoreId;

    public void setNumberOfBytes(Integer numberOfBytes) {
        this.numberOfBytes = numberOfBytes;
    }

    public Integer getNumberOfBytes() {
        return this.numberOfBytes;
    }

    public GenerateRandomRequest withNumberOfBytes(Integer numberOfBytes) {
        this.setNumberOfBytes(numberOfBytes);
        return this;
    }

    public void setCustomKeyStoreId(String customKeyStoreId) {
        this.customKeyStoreId = customKeyStoreId;
    }

    public String getCustomKeyStoreId() {
        return this.customKeyStoreId;
    }

    public GenerateRandomRequest withCustomKeyStoreId(String customKeyStoreId) {
        this.setCustomKeyStoreId(customKeyStoreId);
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (this.getNumberOfBytes() != null) {
            sb.append("NumberOfBytes: ").append(this.getNumberOfBytes()).append(",");
        }
        if (this.getCustomKeyStoreId() != null) {
            sb.append("CustomKeyStoreId: ").append(this.getCustomKeyStoreId());
        }
        sb.append("}");
        return sb.toString();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof GenerateRandomRequest)) {
            return false;
        }
        GenerateRandomRequest other = (GenerateRandomRequest)obj;
        if (other.getNumberOfBytes() == null ^ this.getNumberOfBytes() == null) {
            return false;
        }
        if (other.getNumberOfBytes() != null && !other.getNumberOfBytes().equals(this.getNumberOfBytes())) {
            return false;
        }
        if (other.getCustomKeyStoreId() == null ^ this.getCustomKeyStoreId() == null) {
            return false;
        }
        return other.getCustomKeyStoreId() == null || other.getCustomKeyStoreId().equals(this.getCustomKeyStoreId());
    }

    public int hashCode() {
        int prime = 31;
        int hashCode = 1;
        hashCode = 31 * hashCode + (this.getNumberOfBytes() == null ? 0 : this.getNumberOfBytes().hashCode());
        hashCode = 31 * hashCode + (this.getCustomKeyStoreId() == null ? 0 : this.getCustomKeyStoreId().hashCode());
        return hashCode;
    }

    @Override
    public GenerateRandomRequest clone() {
        return (GenerateRandomRequest)super.clone();
    }
}

