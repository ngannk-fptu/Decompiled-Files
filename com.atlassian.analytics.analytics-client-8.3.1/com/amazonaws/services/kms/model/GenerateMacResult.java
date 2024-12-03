/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model;

import com.amazonaws.AmazonWebServiceResult;
import com.amazonaws.ResponseMetadata;
import com.amazonaws.services.kms.model.MacAlgorithmSpec;
import java.io.Serializable;
import java.nio.ByteBuffer;

public class GenerateMacResult
extends AmazonWebServiceResult<ResponseMetadata>
implements Serializable,
Cloneable {
    private ByteBuffer mac;
    private String macAlgorithm;
    private String keyId;

    public void setMac(ByteBuffer mac) {
        this.mac = mac;
    }

    public ByteBuffer getMac() {
        return this.mac;
    }

    public GenerateMacResult withMac(ByteBuffer mac) {
        this.setMac(mac);
        return this;
    }

    public void setMacAlgorithm(String macAlgorithm) {
        this.macAlgorithm = macAlgorithm;
    }

    public String getMacAlgorithm() {
        return this.macAlgorithm;
    }

    public GenerateMacResult withMacAlgorithm(String macAlgorithm) {
        this.setMacAlgorithm(macAlgorithm);
        return this;
    }

    public GenerateMacResult withMacAlgorithm(MacAlgorithmSpec macAlgorithm) {
        this.macAlgorithm = macAlgorithm.toString();
        return this;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getKeyId() {
        return this.keyId;
    }

    public GenerateMacResult withKeyId(String keyId) {
        this.setKeyId(keyId);
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (this.getMac() != null) {
            sb.append("Mac: ").append(this.getMac()).append(",");
        }
        if (this.getMacAlgorithm() != null) {
            sb.append("MacAlgorithm: ").append(this.getMacAlgorithm()).append(",");
        }
        if (this.getKeyId() != null) {
            sb.append("KeyId: ").append(this.getKeyId());
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
        if (!(obj instanceof GenerateMacResult)) {
            return false;
        }
        GenerateMacResult other = (GenerateMacResult)obj;
        if (other.getMac() == null ^ this.getMac() == null) {
            return false;
        }
        if (other.getMac() != null && !other.getMac().equals(this.getMac())) {
            return false;
        }
        if (other.getMacAlgorithm() == null ^ this.getMacAlgorithm() == null) {
            return false;
        }
        if (other.getMacAlgorithm() != null && !other.getMacAlgorithm().equals(this.getMacAlgorithm())) {
            return false;
        }
        if (other.getKeyId() == null ^ this.getKeyId() == null) {
            return false;
        }
        return other.getKeyId() == null || other.getKeyId().equals(this.getKeyId());
    }

    public int hashCode() {
        int prime = 31;
        int hashCode = 1;
        hashCode = 31 * hashCode + (this.getMac() == null ? 0 : this.getMac().hashCode());
        hashCode = 31 * hashCode + (this.getMacAlgorithm() == null ? 0 : this.getMacAlgorithm().hashCode());
        hashCode = 31 * hashCode + (this.getKeyId() == null ? 0 : this.getKeyId().hashCode());
        return hashCode;
    }

    public GenerateMacResult clone() {
        try {
            return (GenerateMacResult)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new IllegalStateException("Got a CloneNotSupportedException from Object.clone() even though we're Cloneable!", e);
        }
    }
}

