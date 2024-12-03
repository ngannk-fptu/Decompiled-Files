/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model;

import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.internal.SdkInternalList;
import com.amazonaws.services.kms.model.MacAlgorithmSpec;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.List;

public class GenerateMacRequest
extends AmazonWebServiceRequest
implements Serializable,
Cloneable {
    private ByteBuffer message;
    private String keyId;
    private String macAlgorithm;
    private SdkInternalList<String> grantTokens;

    public void setMessage(ByteBuffer message) {
        this.message = message;
    }

    public ByteBuffer getMessage() {
        return this.message;
    }

    public GenerateMacRequest withMessage(ByteBuffer message) {
        this.setMessage(message);
        return this;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getKeyId() {
        return this.keyId;
    }

    public GenerateMacRequest withKeyId(String keyId) {
        this.setKeyId(keyId);
        return this;
    }

    public void setMacAlgorithm(String macAlgorithm) {
        this.macAlgorithm = macAlgorithm;
    }

    public String getMacAlgorithm() {
        return this.macAlgorithm;
    }

    public GenerateMacRequest withMacAlgorithm(String macAlgorithm) {
        this.setMacAlgorithm(macAlgorithm);
        return this;
    }

    public GenerateMacRequest withMacAlgorithm(MacAlgorithmSpec macAlgorithm) {
        this.macAlgorithm = macAlgorithm.toString();
        return this;
    }

    public List<String> getGrantTokens() {
        if (this.grantTokens == null) {
            this.grantTokens = new SdkInternalList();
        }
        return this.grantTokens;
    }

    public void setGrantTokens(Collection<String> grantTokens) {
        if (grantTokens == null) {
            this.grantTokens = null;
            return;
        }
        this.grantTokens = new SdkInternalList<String>(grantTokens);
    }

    public GenerateMacRequest withGrantTokens(String ... grantTokens) {
        if (this.grantTokens == null) {
            this.setGrantTokens(new SdkInternalList<String>(grantTokens.length));
        }
        for (String ele : grantTokens) {
            this.grantTokens.add(ele);
        }
        return this;
    }

    public GenerateMacRequest withGrantTokens(Collection<String> grantTokens) {
        this.setGrantTokens(grantTokens);
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (this.getMessage() != null) {
            sb.append("Message: ").append("***Sensitive Data Redacted***").append(",");
        }
        if (this.getKeyId() != null) {
            sb.append("KeyId: ").append(this.getKeyId()).append(",");
        }
        if (this.getMacAlgorithm() != null) {
            sb.append("MacAlgorithm: ").append(this.getMacAlgorithm()).append(",");
        }
        if (this.getGrantTokens() != null) {
            sb.append("GrantTokens: ").append(this.getGrantTokens());
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
        if (!(obj instanceof GenerateMacRequest)) {
            return false;
        }
        GenerateMacRequest other = (GenerateMacRequest)obj;
        if (other.getMessage() == null ^ this.getMessage() == null) {
            return false;
        }
        if (other.getMessage() != null && !other.getMessage().equals(this.getMessage())) {
            return false;
        }
        if (other.getKeyId() == null ^ this.getKeyId() == null) {
            return false;
        }
        if (other.getKeyId() != null && !other.getKeyId().equals(this.getKeyId())) {
            return false;
        }
        if (other.getMacAlgorithm() == null ^ this.getMacAlgorithm() == null) {
            return false;
        }
        if (other.getMacAlgorithm() != null && !other.getMacAlgorithm().equals(this.getMacAlgorithm())) {
            return false;
        }
        if (other.getGrantTokens() == null ^ this.getGrantTokens() == null) {
            return false;
        }
        return other.getGrantTokens() == null || other.getGrantTokens().equals(this.getGrantTokens());
    }

    public int hashCode() {
        int prime = 31;
        int hashCode = 1;
        hashCode = 31 * hashCode + (this.getMessage() == null ? 0 : this.getMessage().hashCode());
        hashCode = 31 * hashCode + (this.getKeyId() == null ? 0 : this.getKeyId().hashCode());
        hashCode = 31 * hashCode + (this.getMacAlgorithm() == null ? 0 : this.getMacAlgorithm().hashCode());
        hashCode = 31 * hashCode + (this.getGrantTokens() == null ? 0 : this.getGrantTokens().hashCode());
        return hashCode;
    }

    @Override
    public GenerateMacRequest clone() {
        return (GenerateMacRequest)super.clone();
    }
}

