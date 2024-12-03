/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model;

import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.services.kms.model.AlgorithmSpec;
import com.amazonaws.services.kms.model.WrappingKeySpec;
import java.io.Serializable;

public class GetParametersForImportRequest
extends AmazonWebServiceRequest
implements Serializable,
Cloneable {
    private String keyId;
    private String wrappingAlgorithm;
    private String wrappingKeySpec;

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getKeyId() {
        return this.keyId;
    }

    public GetParametersForImportRequest withKeyId(String keyId) {
        this.setKeyId(keyId);
        return this;
    }

    public void setWrappingAlgorithm(String wrappingAlgorithm) {
        this.wrappingAlgorithm = wrappingAlgorithm;
    }

    public String getWrappingAlgorithm() {
        return this.wrappingAlgorithm;
    }

    public GetParametersForImportRequest withWrappingAlgorithm(String wrappingAlgorithm) {
        this.setWrappingAlgorithm(wrappingAlgorithm);
        return this;
    }

    public void setWrappingAlgorithm(AlgorithmSpec wrappingAlgorithm) {
        this.withWrappingAlgorithm(wrappingAlgorithm);
    }

    public GetParametersForImportRequest withWrappingAlgorithm(AlgorithmSpec wrappingAlgorithm) {
        this.wrappingAlgorithm = wrappingAlgorithm.toString();
        return this;
    }

    public void setWrappingKeySpec(String wrappingKeySpec) {
        this.wrappingKeySpec = wrappingKeySpec;
    }

    public String getWrappingKeySpec() {
        return this.wrappingKeySpec;
    }

    public GetParametersForImportRequest withWrappingKeySpec(String wrappingKeySpec) {
        this.setWrappingKeySpec(wrappingKeySpec);
        return this;
    }

    public void setWrappingKeySpec(WrappingKeySpec wrappingKeySpec) {
        this.withWrappingKeySpec(wrappingKeySpec);
    }

    public GetParametersForImportRequest withWrappingKeySpec(WrappingKeySpec wrappingKeySpec) {
        this.wrappingKeySpec = wrappingKeySpec.toString();
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (this.getKeyId() != null) {
            sb.append("KeyId: ").append(this.getKeyId()).append(",");
        }
        if (this.getWrappingAlgorithm() != null) {
            sb.append("WrappingAlgorithm: ").append(this.getWrappingAlgorithm()).append(",");
        }
        if (this.getWrappingKeySpec() != null) {
            sb.append("WrappingKeySpec: ").append(this.getWrappingKeySpec());
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
        if (!(obj instanceof GetParametersForImportRequest)) {
            return false;
        }
        GetParametersForImportRequest other = (GetParametersForImportRequest)obj;
        if (other.getKeyId() == null ^ this.getKeyId() == null) {
            return false;
        }
        if (other.getKeyId() != null && !other.getKeyId().equals(this.getKeyId())) {
            return false;
        }
        if (other.getWrappingAlgorithm() == null ^ this.getWrappingAlgorithm() == null) {
            return false;
        }
        if (other.getWrappingAlgorithm() != null && !other.getWrappingAlgorithm().equals(this.getWrappingAlgorithm())) {
            return false;
        }
        if (other.getWrappingKeySpec() == null ^ this.getWrappingKeySpec() == null) {
            return false;
        }
        return other.getWrappingKeySpec() == null || other.getWrappingKeySpec().equals(this.getWrappingKeySpec());
    }

    public int hashCode() {
        int prime = 31;
        int hashCode = 1;
        hashCode = 31 * hashCode + (this.getKeyId() == null ? 0 : this.getKeyId().hashCode());
        hashCode = 31 * hashCode + (this.getWrappingAlgorithm() == null ? 0 : this.getWrappingAlgorithm().hashCode());
        hashCode = 31 * hashCode + (this.getWrappingKeySpec() == null ? 0 : this.getWrappingKeySpec().hashCode());
        return hashCode;
    }

    @Override
    public GetParametersForImportRequest clone() {
        return (GetParametersForImportRequest)super.clone();
    }
}

