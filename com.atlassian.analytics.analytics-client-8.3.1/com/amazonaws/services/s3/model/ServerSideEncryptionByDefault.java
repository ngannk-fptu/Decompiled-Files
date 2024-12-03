/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model;

import com.amazonaws.services.s3.model.SSEAlgorithm;
import java.io.Serializable;

public class ServerSideEncryptionByDefault
implements Serializable,
Cloneable {
    private String sseAlgorithm;
    private String kmsMasterKeyID;

    public String getSSEAlgorithm() {
        return this.sseAlgorithm;
    }

    public void setSSEAlgorithm(String sseAlgorithm) {
        this.sseAlgorithm = sseAlgorithm;
    }

    public ServerSideEncryptionByDefault withSSEAlgorithm(String sseAlgorithm) {
        this.setSSEAlgorithm(sseAlgorithm);
        return this;
    }

    public ServerSideEncryptionByDefault withSSEAlgorithm(SSEAlgorithm sseAlgorithm) {
        this.setSSEAlgorithm(sseAlgorithm == null ? null : sseAlgorithm.toString());
        return this;
    }

    public String getKMSMasterKeyID() {
        return this.kmsMasterKeyID;
    }

    public void setKMSMasterKeyID(String kmsMasterKeyID) {
        this.kmsMasterKeyID = kmsMasterKeyID;
    }

    public ServerSideEncryptionByDefault withKMSMasterKeyID(String kmsMasterKeyID) {
        this.setKMSMasterKeyID(kmsMasterKeyID);
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (this.getSSEAlgorithm() != null) {
            sb.append("SSEAlgorithm: ").append(this.getSSEAlgorithm()).append(",");
        }
        if (this.getKMSMasterKeyID() != null) {
            sb.append("KMSMasterKeyID: ").append(this.getKMSMasterKeyID()).append(",");
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
        if (!(obj instanceof ServerSideEncryptionByDefault)) {
            return false;
        }
        ServerSideEncryptionByDefault other = (ServerSideEncryptionByDefault)obj;
        if (other.getSSEAlgorithm() == null ^ this.getSSEAlgorithm() == null) {
            return false;
        }
        if (other.getSSEAlgorithm() != null && !other.getSSEAlgorithm().equals(this.getSSEAlgorithm())) {
            return false;
        }
        if (other.getKMSMasterKeyID() == null ^ this.getKMSMasterKeyID() == null) {
            return false;
        }
        return other.getKMSMasterKeyID() == null || other.getKMSMasterKeyID().equals(this.getKMSMasterKeyID());
    }

    public int hashCode() {
        int prime = 31;
        int hashCode = 1;
        hashCode = 31 * hashCode + (this.getSSEAlgorithm() == null ? 0 : this.getSSEAlgorithm().hashCode());
        hashCode = 31 * hashCode + (this.getKMSMasterKeyID() == null ? 0 : this.getKMSMasterKeyID().hashCode());
        return hashCode;
    }

    public ServerSideEncryptionByDefault clone() {
        try {
            return (ServerSideEncryptionByDefault)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new IllegalStateException("Got a CloneNotSupportedException from Object.clone() even though we're Cloneable!", e);
        }
    }
}

