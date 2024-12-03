/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model;

import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.internal.SdkInternalList;
import com.amazonaws.services.kms.model.MessageType;
import com.amazonaws.services.kms.model.SigningAlgorithmSpec;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.List;

public class SignRequest
extends AmazonWebServiceRequest
implements Serializable,
Cloneable {
    private String keyId;
    private ByteBuffer message;
    private String messageType;
    private SdkInternalList<String> grantTokens;
    private String signingAlgorithm;

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getKeyId() {
        return this.keyId;
    }

    public SignRequest withKeyId(String keyId) {
        this.setKeyId(keyId);
        return this;
    }

    public void setMessage(ByteBuffer message) {
        this.message = message;
    }

    public ByteBuffer getMessage() {
        return this.message;
    }

    public SignRequest withMessage(ByteBuffer message) {
        this.setMessage(message);
        return this;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getMessageType() {
        return this.messageType;
    }

    public SignRequest withMessageType(String messageType) {
        this.setMessageType(messageType);
        return this;
    }

    public SignRequest withMessageType(MessageType messageType) {
        this.messageType = messageType.toString();
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

    public SignRequest withGrantTokens(String ... grantTokens) {
        if (this.grantTokens == null) {
            this.setGrantTokens(new SdkInternalList<String>(grantTokens.length));
        }
        for (String ele : grantTokens) {
            this.grantTokens.add(ele);
        }
        return this;
    }

    public SignRequest withGrantTokens(Collection<String> grantTokens) {
        this.setGrantTokens(grantTokens);
        return this;
    }

    public void setSigningAlgorithm(String signingAlgorithm) {
        this.signingAlgorithm = signingAlgorithm;
    }

    public String getSigningAlgorithm() {
        return this.signingAlgorithm;
    }

    public SignRequest withSigningAlgorithm(String signingAlgorithm) {
        this.setSigningAlgorithm(signingAlgorithm);
        return this;
    }

    public SignRequest withSigningAlgorithm(SigningAlgorithmSpec signingAlgorithm) {
        this.signingAlgorithm = signingAlgorithm.toString();
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (this.getKeyId() != null) {
            sb.append("KeyId: ").append(this.getKeyId()).append(",");
        }
        if (this.getMessage() != null) {
            sb.append("Message: ").append("***Sensitive Data Redacted***").append(",");
        }
        if (this.getMessageType() != null) {
            sb.append("MessageType: ").append(this.getMessageType()).append(",");
        }
        if (this.getGrantTokens() != null) {
            sb.append("GrantTokens: ").append(this.getGrantTokens()).append(",");
        }
        if (this.getSigningAlgorithm() != null) {
            sb.append("SigningAlgorithm: ").append(this.getSigningAlgorithm());
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
        if (!(obj instanceof SignRequest)) {
            return false;
        }
        SignRequest other = (SignRequest)obj;
        if (other.getKeyId() == null ^ this.getKeyId() == null) {
            return false;
        }
        if (other.getKeyId() != null && !other.getKeyId().equals(this.getKeyId())) {
            return false;
        }
        if (other.getMessage() == null ^ this.getMessage() == null) {
            return false;
        }
        if (other.getMessage() != null && !other.getMessage().equals(this.getMessage())) {
            return false;
        }
        if (other.getMessageType() == null ^ this.getMessageType() == null) {
            return false;
        }
        if (other.getMessageType() != null && !other.getMessageType().equals(this.getMessageType())) {
            return false;
        }
        if (other.getGrantTokens() == null ^ this.getGrantTokens() == null) {
            return false;
        }
        if (other.getGrantTokens() != null && !other.getGrantTokens().equals(this.getGrantTokens())) {
            return false;
        }
        if (other.getSigningAlgorithm() == null ^ this.getSigningAlgorithm() == null) {
            return false;
        }
        return other.getSigningAlgorithm() == null || other.getSigningAlgorithm().equals(this.getSigningAlgorithm());
    }

    public int hashCode() {
        int prime = 31;
        int hashCode = 1;
        hashCode = 31 * hashCode + (this.getKeyId() == null ? 0 : this.getKeyId().hashCode());
        hashCode = 31 * hashCode + (this.getMessage() == null ? 0 : this.getMessage().hashCode());
        hashCode = 31 * hashCode + (this.getMessageType() == null ? 0 : this.getMessageType().hashCode());
        hashCode = 31 * hashCode + (this.getGrantTokens() == null ? 0 : this.getGrantTokens().hashCode());
        hashCode = 31 * hashCode + (this.getSigningAlgorithm() == null ? 0 : this.getSigningAlgorithm().hashCode());
        return hashCode;
    }

    @Override
    public SignRequest clone() {
        return (SignRequest)super.clone();
    }
}

