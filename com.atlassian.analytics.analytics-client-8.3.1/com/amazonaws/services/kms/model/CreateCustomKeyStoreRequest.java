/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model;

import com.amazonaws.AmazonWebServiceRequest;
import java.io.Serializable;

public class CreateCustomKeyStoreRequest
extends AmazonWebServiceRequest
implements Serializable,
Cloneable {
    private String customKeyStoreName;
    private String cloudHsmClusterId;
    private String trustAnchorCertificate;
    private String keyStorePassword;

    public void setCustomKeyStoreName(String customKeyStoreName) {
        this.customKeyStoreName = customKeyStoreName;
    }

    public String getCustomKeyStoreName() {
        return this.customKeyStoreName;
    }

    public CreateCustomKeyStoreRequest withCustomKeyStoreName(String customKeyStoreName) {
        this.setCustomKeyStoreName(customKeyStoreName);
        return this;
    }

    public void setCloudHsmClusterId(String cloudHsmClusterId) {
        this.cloudHsmClusterId = cloudHsmClusterId;
    }

    public String getCloudHsmClusterId() {
        return this.cloudHsmClusterId;
    }

    public CreateCustomKeyStoreRequest withCloudHsmClusterId(String cloudHsmClusterId) {
        this.setCloudHsmClusterId(cloudHsmClusterId);
        return this;
    }

    public void setTrustAnchorCertificate(String trustAnchorCertificate) {
        this.trustAnchorCertificate = trustAnchorCertificate;
    }

    public String getTrustAnchorCertificate() {
        return this.trustAnchorCertificate;
    }

    public CreateCustomKeyStoreRequest withTrustAnchorCertificate(String trustAnchorCertificate) {
        this.setTrustAnchorCertificate(trustAnchorCertificate);
        return this;
    }

    public void setKeyStorePassword(String keyStorePassword) {
        this.keyStorePassword = keyStorePassword;
    }

    public String getKeyStorePassword() {
        return this.keyStorePassword;
    }

    public CreateCustomKeyStoreRequest withKeyStorePassword(String keyStorePassword) {
        this.setKeyStorePassword(keyStorePassword);
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (this.getCustomKeyStoreName() != null) {
            sb.append("CustomKeyStoreName: ").append(this.getCustomKeyStoreName()).append(",");
        }
        if (this.getCloudHsmClusterId() != null) {
            sb.append("CloudHsmClusterId: ").append(this.getCloudHsmClusterId()).append(",");
        }
        if (this.getTrustAnchorCertificate() != null) {
            sb.append("TrustAnchorCertificate: ").append(this.getTrustAnchorCertificate()).append(",");
        }
        if (this.getKeyStorePassword() != null) {
            sb.append("KeyStorePassword: ").append("***Sensitive Data Redacted***");
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
        if (!(obj instanceof CreateCustomKeyStoreRequest)) {
            return false;
        }
        CreateCustomKeyStoreRequest other = (CreateCustomKeyStoreRequest)obj;
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
        if (other.getKeyStorePassword() == null ^ this.getKeyStorePassword() == null) {
            return false;
        }
        return other.getKeyStorePassword() == null || other.getKeyStorePassword().equals(this.getKeyStorePassword());
    }

    public int hashCode() {
        int prime = 31;
        int hashCode = 1;
        hashCode = 31 * hashCode + (this.getCustomKeyStoreName() == null ? 0 : this.getCustomKeyStoreName().hashCode());
        hashCode = 31 * hashCode + (this.getCloudHsmClusterId() == null ? 0 : this.getCloudHsmClusterId().hashCode());
        hashCode = 31 * hashCode + (this.getTrustAnchorCertificate() == null ? 0 : this.getTrustAnchorCertificate().hashCode());
        hashCode = 31 * hashCode + (this.getKeyStorePassword() == null ? 0 : this.getKeyStorePassword().hashCode());
        return hashCode;
    }

    @Override
    public CreateCustomKeyStoreRequest clone() {
        return (CreateCustomKeyStoreRequest)super.clone();
    }
}

