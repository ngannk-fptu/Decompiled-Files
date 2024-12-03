/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model;

import com.amazonaws.AmazonWebServiceRequest;
import java.io.Serializable;

public class UpdateCustomKeyStoreRequest
extends AmazonWebServiceRequest
implements Serializable,
Cloneable {
    private String customKeyStoreId;
    private String newCustomKeyStoreName;
    private String keyStorePassword;
    private String cloudHsmClusterId;

    public void setCustomKeyStoreId(String customKeyStoreId) {
        this.customKeyStoreId = customKeyStoreId;
    }

    public String getCustomKeyStoreId() {
        return this.customKeyStoreId;
    }

    public UpdateCustomKeyStoreRequest withCustomKeyStoreId(String customKeyStoreId) {
        this.setCustomKeyStoreId(customKeyStoreId);
        return this;
    }

    public void setNewCustomKeyStoreName(String newCustomKeyStoreName) {
        this.newCustomKeyStoreName = newCustomKeyStoreName;
    }

    public String getNewCustomKeyStoreName() {
        return this.newCustomKeyStoreName;
    }

    public UpdateCustomKeyStoreRequest withNewCustomKeyStoreName(String newCustomKeyStoreName) {
        this.setNewCustomKeyStoreName(newCustomKeyStoreName);
        return this;
    }

    public void setKeyStorePassword(String keyStorePassword) {
        this.keyStorePassword = keyStorePassword;
    }

    public String getKeyStorePassword() {
        return this.keyStorePassword;
    }

    public UpdateCustomKeyStoreRequest withKeyStorePassword(String keyStorePassword) {
        this.setKeyStorePassword(keyStorePassword);
        return this;
    }

    public void setCloudHsmClusterId(String cloudHsmClusterId) {
        this.cloudHsmClusterId = cloudHsmClusterId;
    }

    public String getCloudHsmClusterId() {
        return this.cloudHsmClusterId;
    }

    public UpdateCustomKeyStoreRequest withCloudHsmClusterId(String cloudHsmClusterId) {
        this.setCloudHsmClusterId(cloudHsmClusterId);
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (this.getCustomKeyStoreId() != null) {
            sb.append("CustomKeyStoreId: ").append(this.getCustomKeyStoreId()).append(",");
        }
        if (this.getNewCustomKeyStoreName() != null) {
            sb.append("NewCustomKeyStoreName: ").append(this.getNewCustomKeyStoreName()).append(",");
        }
        if (this.getKeyStorePassword() != null) {
            sb.append("KeyStorePassword: ").append("***Sensitive Data Redacted***").append(",");
        }
        if (this.getCloudHsmClusterId() != null) {
            sb.append("CloudHsmClusterId: ").append(this.getCloudHsmClusterId());
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
        if (!(obj instanceof UpdateCustomKeyStoreRequest)) {
            return false;
        }
        UpdateCustomKeyStoreRequest other = (UpdateCustomKeyStoreRequest)obj;
        if (other.getCustomKeyStoreId() == null ^ this.getCustomKeyStoreId() == null) {
            return false;
        }
        if (other.getCustomKeyStoreId() != null && !other.getCustomKeyStoreId().equals(this.getCustomKeyStoreId())) {
            return false;
        }
        if (other.getNewCustomKeyStoreName() == null ^ this.getNewCustomKeyStoreName() == null) {
            return false;
        }
        if (other.getNewCustomKeyStoreName() != null && !other.getNewCustomKeyStoreName().equals(this.getNewCustomKeyStoreName())) {
            return false;
        }
        if (other.getKeyStorePassword() == null ^ this.getKeyStorePassword() == null) {
            return false;
        }
        if (other.getKeyStorePassword() != null && !other.getKeyStorePassword().equals(this.getKeyStorePassword())) {
            return false;
        }
        if (other.getCloudHsmClusterId() == null ^ this.getCloudHsmClusterId() == null) {
            return false;
        }
        return other.getCloudHsmClusterId() == null || other.getCloudHsmClusterId().equals(this.getCloudHsmClusterId());
    }

    public int hashCode() {
        int prime = 31;
        int hashCode = 1;
        hashCode = 31 * hashCode + (this.getCustomKeyStoreId() == null ? 0 : this.getCustomKeyStoreId().hashCode());
        hashCode = 31 * hashCode + (this.getNewCustomKeyStoreName() == null ? 0 : this.getNewCustomKeyStoreName().hashCode());
        hashCode = 31 * hashCode + (this.getKeyStorePassword() == null ? 0 : this.getKeyStorePassword().hashCode());
        hashCode = 31 * hashCode + (this.getCloudHsmClusterId() == null ? 0 : this.getCloudHsmClusterId().hashCode());
        return hashCode;
    }

    @Override
    public UpdateCustomKeyStoreRequest clone() {
        return (UpdateCustomKeyStoreRequest)super.clone();
    }
}

