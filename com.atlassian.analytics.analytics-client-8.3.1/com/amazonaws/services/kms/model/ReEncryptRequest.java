/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model;

import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.internal.SdkInternalList;
import com.amazonaws.internal.SdkInternalMap;
import com.amazonaws.services.kms.model.EncryptionAlgorithmSpec;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ReEncryptRequest
extends AmazonWebServiceRequest
implements Serializable,
Cloneable {
    private ByteBuffer ciphertextBlob;
    private SdkInternalMap<String, String> sourceEncryptionContext;
    private String sourceKeyId;
    private String destinationKeyId;
    private SdkInternalMap<String, String> destinationEncryptionContext;
    private String sourceEncryptionAlgorithm;
    private String destinationEncryptionAlgorithm;
    private SdkInternalList<String> grantTokens;

    public void setCiphertextBlob(ByteBuffer ciphertextBlob) {
        this.ciphertextBlob = ciphertextBlob;
    }

    public ByteBuffer getCiphertextBlob() {
        return this.ciphertextBlob;
    }

    public ReEncryptRequest withCiphertextBlob(ByteBuffer ciphertextBlob) {
        this.setCiphertextBlob(ciphertextBlob);
        return this;
    }

    public Map<String, String> getSourceEncryptionContext() {
        if (this.sourceEncryptionContext == null) {
            this.sourceEncryptionContext = new SdkInternalMap();
        }
        return this.sourceEncryptionContext;
    }

    public void setSourceEncryptionContext(Map<String, String> sourceEncryptionContext) {
        this.sourceEncryptionContext = sourceEncryptionContext == null ? null : new SdkInternalMap<String, String>(sourceEncryptionContext);
    }

    public ReEncryptRequest withSourceEncryptionContext(Map<String, String> sourceEncryptionContext) {
        this.setSourceEncryptionContext(sourceEncryptionContext);
        return this;
    }

    public ReEncryptRequest addSourceEncryptionContextEntry(String key, String value) {
        if (null == this.sourceEncryptionContext) {
            this.sourceEncryptionContext = new SdkInternalMap();
        }
        if (this.sourceEncryptionContext.containsKey(key)) {
            throw new IllegalArgumentException("Duplicated keys (" + key.toString() + ") are provided.");
        }
        this.sourceEncryptionContext.put(key, value);
        return this;
    }

    public ReEncryptRequest clearSourceEncryptionContextEntries() {
        this.sourceEncryptionContext = null;
        return this;
    }

    public void setSourceKeyId(String sourceKeyId) {
        this.sourceKeyId = sourceKeyId;
    }

    public String getSourceKeyId() {
        return this.sourceKeyId;
    }

    public ReEncryptRequest withSourceKeyId(String sourceKeyId) {
        this.setSourceKeyId(sourceKeyId);
        return this;
    }

    public void setDestinationKeyId(String destinationKeyId) {
        this.destinationKeyId = destinationKeyId;
    }

    public String getDestinationKeyId() {
        return this.destinationKeyId;
    }

    public ReEncryptRequest withDestinationKeyId(String destinationKeyId) {
        this.setDestinationKeyId(destinationKeyId);
        return this;
    }

    public Map<String, String> getDestinationEncryptionContext() {
        if (this.destinationEncryptionContext == null) {
            this.destinationEncryptionContext = new SdkInternalMap();
        }
        return this.destinationEncryptionContext;
    }

    public void setDestinationEncryptionContext(Map<String, String> destinationEncryptionContext) {
        this.destinationEncryptionContext = destinationEncryptionContext == null ? null : new SdkInternalMap<String, String>(destinationEncryptionContext);
    }

    public ReEncryptRequest withDestinationEncryptionContext(Map<String, String> destinationEncryptionContext) {
        this.setDestinationEncryptionContext(destinationEncryptionContext);
        return this;
    }

    public ReEncryptRequest addDestinationEncryptionContextEntry(String key, String value) {
        if (null == this.destinationEncryptionContext) {
            this.destinationEncryptionContext = new SdkInternalMap();
        }
        if (this.destinationEncryptionContext.containsKey(key)) {
            throw new IllegalArgumentException("Duplicated keys (" + key.toString() + ") are provided.");
        }
        this.destinationEncryptionContext.put(key, value);
        return this;
    }

    public ReEncryptRequest clearDestinationEncryptionContextEntries() {
        this.destinationEncryptionContext = null;
        return this;
    }

    public void setSourceEncryptionAlgorithm(String sourceEncryptionAlgorithm) {
        this.sourceEncryptionAlgorithm = sourceEncryptionAlgorithm;
    }

    public String getSourceEncryptionAlgorithm() {
        return this.sourceEncryptionAlgorithm;
    }

    public ReEncryptRequest withSourceEncryptionAlgorithm(String sourceEncryptionAlgorithm) {
        this.setSourceEncryptionAlgorithm(sourceEncryptionAlgorithm);
        return this;
    }

    public ReEncryptRequest withSourceEncryptionAlgorithm(EncryptionAlgorithmSpec sourceEncryptionAlgorithm) {
        this.sourceEncryptionAlgorithm = sourceEncryptionAlgorithm.toString();
        return this;
    }

    public void setDestinationEncryptionAlgorithm(String destinationEncryptionAlgorithm) {
        this.destinationEncryptionAlgorithm = destinationEncryptionAlgorithm;
    }

    public String getDestinationEncryptionAlgorithm() {
        return this.destinationEncryptionAlgorithm;
    }

    public ReEncryptRequest withDestinationEncryptionAlgorithm(String destinationEncryptionAlgorithm) {
        this.setDestinationEncryptionAlgorithm(destinationEncryptionAlgorithm);
        return this;
    }

    public ReEncryptRequest withDestinationEncryptionAlgorithm(EncryptionAlgorithmSpec destinationEncryptionAlgorithm) {
        this.destinationEncryptionAlgorithm = destinationEncryptionAlgorithm.toString();
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

    public ReEncryptRequest withGrantTokens(String ... grantTokens) {
        if (this.grantTokens == null) {
            this.setGrantTokens(new SdkInternalList<String>(grantTokens.length));
        }
        for (String ele : grantTokens) {
            this.grantTokens.add(ele);
        }
        return this;
    }

    public ReEncryptRequest withGrantTokens(Collection<String> grantTokens) {
        this.setGrantTokens(grantTokens);
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (this.getCiphertextBlob() != null) {
            sb.append("CiphertextBlob: ").append(this.getCiphertextBlob()).append(",");
        }
        if (this.getSourceEncryptionContext() != null) {
            sb.append("SourceEncryptionContext: ").append(this.getSourceEncryptionContext()).append(",");
        }
        if (this.getSourceKeyId() != null) {
            sb.append("SourceKeyId: ").append(this.getSourceKeyId()).append(",");
        }
        if (this.getDestinationKeyId() != null) {
            sb.append("DestinationKeyId: ").append(this.getDestinationKeyId()).append(",");
        }
        if (this.getDestinationEncryptionContext() != null) {
            sb.append("DestinationEncryptionContext: ").append(this.getDestinationEncryptionContext()).append(",");
        }
        if (this.getSourceEncryptionAlgorithm() != null) {
            sb.append("SourceEncryptionAlgorithm: ").append(this.getSourceEncryptionAlgorithm()).append(",");
        }
        if (this.getDestinationEncryptionAlgorithm() != null) {
            sb.append("DestinationEncryptionAlgorithm: ").append(this.getDestinationEncryptionAlgorithm()).append(",");
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
        if (!(obj instanceof ReEncryptRequest)) {
            return false;
        }
        ReEncryptRequest other = (ReEncryptRequest)obj;
        if (other.getCiphertextBlob() == null ^ this.getCiphertextBlob() == null) {
            return false;
        }
        if (other.getCiphertextBlob() != null && !other.getCiphertextBlob().equals(this.getCiphertextBlob())) {
            return false;
        }
        if (other.getSourceEncryptionContext() == null ^ this.getSourceEncryptionContext() == null) {
            return false;
        }
        if (other.getSourceEncryptionContext() != null && !other.getSourceEncryptionContext().equals(this.getSourceEncryptionContext())) {
            return false;
        }
        if (other.getSourceKeyId() == null ^ this.getSourceKeyId() == null) {
            return false;
        }
        if (other.getSourceKeyId() != null && !other.getSourceKeyId().equals(this.getSourceKeyId())) {
            return false;
        }
        if (other.getDestinationKeyId() == null ^ this.getDestinationKeyId() == null) {
            return false;
        }
        if (other.getDestinationKeyId() != null && !other.getDestinationKeyId().equals(this.getDestinationKeyId())) {
            return false;
        }
        if (other.getDestinationEncryptionContext() == null ^ this.getDestinationEncryptionContext() == null) {
            return false;
        }
        if (other.getDestinationEncryptionContext() != null && !other.getDestinationEncryptionContext().equals(this.getDestinationEncryptionContext())) {
            return false;
        }
        if (other.getSourceEncryptionAlgorithm() == null ^ this.getSourceEncryptionAlgorithm() == null) {
            return false;
        }
        if (other.getSourceEncryptionAlgorithm() != null && !other.getSourceEncryptionAlgorithm().equals(this.getSourceEncryptionAlgorithm())) {
            return false;
        }
        if (other.getDestinationEncryptionAlgorithm() == null ^ this.getDestinationEncryptionAlgorithm() == null) {
            return false;
        }
        if (other.getDestinationEncryptionAlgorithm() != null && !other.getDestinationEncryptionAlgorithm().equals(this.getDestinationEncryptionAlgorithm())) {
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
        hashCode = 31 * hashCode + (this.getCiphertextBlob() == null ? 0 : this.getCiphertextBlob().hashCode());
        hashCode = 31 * hashCode + (this.getSourceEncryptionContext() == null ? 0 : this.getSourceEncryptionContext().hashCode());
        hashCode = 31 * hashCode + (this.getSourceKeyId() == null ? 0 : this.getSourceKeyId().hashCode());
        hashCode = 31 * hashCode + (this.getDestinationKeyId() == null ? 0 : this.getDestinationKeyId().hashCode());
        hashCode = 31 * hashCode + (this.getDestinationEncryptionContext() == null ? 0 : this.getDestinationEncryptionContext().hashCode());
        hashCode = 31 * hashCode + (this.getSourceEncryptionAlgorithm() == null ? 0 : this.getSourceEncryptionAlgorithm().hashCode());
        hashCode = 31 * hashCode + (this.getDestinationEncryptionAlgorithm() == null ? 0 : this.getDestinationEncryptionAlgorithm().hashCode());
        hashCode = 31 * hashCode + (this.getGrantTokens() == null ? 0 : this.getGrantTokens().hashCode());
        return hashCode;
    }

    @Override
    public ReEncryptRequest clone() {
        return (ReEncryptRequest)super.clone();
    }
}

