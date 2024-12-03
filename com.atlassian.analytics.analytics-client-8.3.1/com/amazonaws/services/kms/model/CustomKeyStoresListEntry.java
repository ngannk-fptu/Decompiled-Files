/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model;

import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.protocol.ProtocolMarshaller;
import com.amazonaws.protocol.StructuredPojo;
import com.amazonaws.services.kms.model.ConnectionErrorCodeType;
import com.amazonaws.services.kms.model.ConnectionStateType;
import com.amazonaws.services.kms.model.transform.CustomKeyStoresListEntryMarshaller;
import java.io.Serializable;
import java.util.Date;

public class CustomKeyStoresListEntry
implements Serializable,
Cloneable,
StructuredPojo {
    private String customKeyStoreId;
    private String customKeyStoreName;
    private String cloudHsmClusterId;
    private String trustAnchorCertificate;
    private String connectionState;
    private String connectionErrorCode;
    private Date creationDate;

    public void setCustomKeyStoreId(String customKeyStoreId) {
        this.customKeyStoreId = customKeyStoreId;
    }

    public String getCustomKeyStoreId() {
        return this.customKeyStoreId;
    }

    public CustomKeyStoresListEntry withCustomKeyStoreId(String customKeyStoreId) {
        this.setCustomKeyStoreId(customKeyStoreId);
        return this;
    }

    public void setCustomKeyStoreName(String customKeyStoreName) {
        this.customKeyStoreName = customKeyStoreName;
    }

    public String getCustomKeyStoreName() {
        return this.customKeyStoreName;
    }

    public CustomKeyStoresListEntry withCustomKeyStoreName(String customKeyStoreName) {
        this.setCustomKeyStoreName(customKeyStoreName);
        return this;
    }

    public void setCloudHsmClusterId(String cloudHsmClusterId) {
        this.cloudHsmClusterId = cloudHsmClusterId;
    }

    public String getCloudHsmClusterId() {
        return this.cloudHsmClusterId;
    }

    public CustomKeyStoresListEntry withCloudHsmClusterId(String cloudHsmClusterId) {
        this.setCloudHsmClusterId(cloudHsmClusterId);
        return this;
    }

    public void setTrustAnchorCertificate(String trustAnchorCertificate) {
        this.trustAnchorCertificate = trustAnchorCertificate;
    }

    public String getTrustAnchorCertificate() {
        return this.trustAnchorCertificate;
    }

    public CustomKeyStoresListEntry withTrustAnchorCertificate(String trustAnchorCertificate) {
        this.setTrustAnchorCertificate(trustAnchorCertificate);
        return this;
    }

    public void setConnectionState(String connectionState) {
        this.connectionState = connectionState;
    }

    public String getConnectionState() {
        return this.connectionState;
    }

    public CustomKeyStoresListEntry withConnectionState(String connectionState) {
        this.setConnectionState(connectionState);
        return this;
    }

    public CustomKeyStoresListEntry withConnectionState(ConnectionStateType connectionState) {
        this.connectionState = connectionState.toString();
        return this;
    }

    public void setConnectionErrorCode(String connectionErrorCode) {
        this.connectionErrorCode = connectionErrorCode;
    }

    public String getConnectionErrorCode() {
        return this.connectionErrorCode;
    }

    public CustomKeyStoresListEntry withConnectionErrorCode(String connectionErrorCode) {
        this.setConnectionErrorCode(connectionErrorCode);
        return this;
    }

    public CustomKeyStoresListEntry withConnectionErrorCode(ConnectionErrorCodeType connectionErrorCode) {
        this.connectionErrorCode = connectionErrorCode.toString();
        return this;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getCreationDate() {
        return this.creationDate;
    }

    public CustomKeyStoresListEntry withCreationDate(Date creationDate) {
        this.setCreationDate(creationDate);
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (this.getCustomKeyStoreId() != null) {
            sb.append("CustomKeyStoreId: ").append(this.getCustomKeyStoreId()).append(",");
        }
        if (this.getCustomKeyStoreName() != null) {
            sb.append("CustomKeyStoreName: ").append(this.getCustomKeyStoreName()).append(",");
        }
        if (this.getCloudHsmClusterId() != null) {
            sb.append("CloudHsmClusterId: ").append(this.getCloudHsmClusterId()).append(",");
        }
        if (this.getTrustAnchorCertificate() != null) {
            sb.append("TrustAnchorCertificate: ").append(this.getTrustAnchorCertificate()).append(",");
        }
        if (this.getConnectionState() != null) {
            sb.append("ConnectionState: ").append(this.getConnectionState()).append(",");
        }
        if (this.getConnectionErrorCode() != null) {
            sb.append("ConnectionErrorCode: ").append(this.getConnectionErrorCode()).append(",");
        }
        if (this.getCreationDate() != null) {
            sb.append("CreationDate: ").append(this.getCreationDate());
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
        if (!(obj instanceof CustomKeyStoresListEntry)) {
            return false;
        }
        CustomKeyStoresListEntry other = (CustomKeyStoresListEntry)obj;
        if (other.getCustomKeyStoreId() == null ^ this.getCustomKeyStoreId() == null) {
            return false;
        }
        if (other.getCustomKeyStoreId() != null && !other.getCustomKeyStoreId().equals(this.getCustomKeyStoreId())) {
            return false;
        }
        if (other.getCustomKeyStoreName() == null ^ this.getCustomKeyStoreName() == null) {
            return false;
        }
        if (other.getCustomKeyStoreName() != null && !other.getCustomKeyStoreName().equals(this.getCustomKeyStoreName())) {
            return false;
        }
        if (other.getCloudHsmClusterId() == null ^ this.getCloudHsmClusterId() == null) {
            return false;
        }
        if (other.getCloudHsmClusterId() != null && !other.getCloudHsmClusterId().equals(this.getCloudHsmClusterId())) {
            return false;
        }
        if (other.getTrustAnchorCertificate() == null ^ this.getTrustAnchorCertificate() == null) {
            return false;
        }
        if (other.getTrustAnchorCertificate() != null && !other.getTrustAnchorCertificate().equals(this.getTrustAnchorCertificate())) {
            return false;
        }
        if (other.getConnectionState() == null ^ this.getConnectionState() == null) {
            return false;
        }
        if (other.getConnectionState() != null && !other.getConnectionState().equals(this.getConnectionState())) {
            return false;
        }
        if (other.getConnectionErrorCode() == null ^ this.getConnectionErrorCode() == null) {
            return false;
        }
        if (other.getConnectionErrorCode() != null && !other.getConnectionErrorCode().equals(this.getConnectionErrorCode())) {
            return false;
        }
        if (other.getCreationDate() == null ^ this.getCreationDate() == null) {
            return false;
        }
        return other.getCreationDate() == null || other.getCreationDate().equals(this.getCreationDate());
    }

    public int hashCode() {
        int prime = 31;
        int hashCode = 1;
        hashCode = 31 * hashCode + (this.getCustomKeyStoreId() == null ? 0 : this.getCustomKeyStoreId().hashCode());
        hashCode = 31 * hashCode + (this.getCustomKeyStoreName() == null ? 0 : this.getCustomKeyStoreName().hashCode());
        hashCode = 31 * hashCode + (this.getCloudHsmClusterId() == null ? 0 : this.getCloudHsmClusterId().hashCode());
        hashCode = 31 * hashCode + (this.getTrustAnchorCertificate() == null ? 0 : this.getTrustAnchorCertificate().hashCode());
        hashCode = 31 * hashCode + (this.getConnectionState() == null ? 0 : this.getConnectionState().hashCode());
        hashCode = 31 * hashCode + (this.getConnectionErrorCode() == null ? 0 : this.getConnectionErrorCode().hashCode());
        hashCode = 31 * hashCode + (this.getCreationDate() == null ? 0 : this.getCreationDate().hashCode());
        return hashCode;
    }

    public CustomKeyStoresListEntry clone() {
        try {
            return (CustomKeyStoresListEntry)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new IllegalStateException("Got a CloneNotSupportedException from Object.clone() even though we're Cloneable!", e);
        }
    }

    @Override
    @SdkInternalApi
    public void marshall(ProtocolMarshaller protocolMarshaller) {
        CustomKeyStoresListEntryMarshaller.getInstance().marshall(this, protocolMarshaller);
    }
}

