/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model;

import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.Encryption;
import com.amazonaws.services.s3.model.MetadataEntry;
import com.amazonaws.services.s3.model.ObjectTagging;
import com.amazonaws.services.s3.model.StorageClass;
import java.io.Serializable;
import java.util.List;

public class S3Location
implements Serializable,
Cloneable {
    private String bucketName;
    private String prefix;
    private Encryption encryption;
    private String cannedACL;
    private AccessControlList accessControlList;
    private ObjectTagging tagging;
    private List<MetadataEntry> userMetadata;
    private String storageClass;

    public String getBucketName() {
        return this.bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public S3Location withBucketName(String bucketName) {
        this.setBucketName(bucketName);
        return this;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public S3Location withPrefix(String prefix) {
        this.setPrefix(prefix);
        return this;
    }

    public Encryption getEncryption() {
        return this.encryption;
    }

    public void setEncryption(Encryption encryption) {
        this.encryption = encryption;
    }

    public S3Location withEncryption(Encryption encryption) {
        this.setEncryption(encryption);
        return this;
    }

    public String getCannedACL() {
        return this.cannedACL;
    }

    public void setCannedACL(String cannedACL) {
        this.cannedACL = cannedACL;
    }

    public S3Location withCannedACL(String cannedACL) {
        this.setCannedACL(cannedACL);
        return this;
    }

    public S3Location withCannedACL(CannedAccessControlList cannedACL) {
        this.setCannedACL(cannedACL == null ? null : cannedACL.toString());
        return this;
    }

    public AccessControlList getAccessControlList() {
        return this.accessControlList;
    }

    public void setAccessControlList(AccessControlList accessControlList) {
        this.accessControlList = accessControlList;
    }

    public S3Location withAccessControlList(AccessControlList accessControlList) {
        this.setAccessControlList(accessControlList);
        return this;
    }

    public ObjectTagging getTagging() {
        return this.tagging;
    }

    public void setTagging(ObjectTagging tagging) {
        this.tagging = tagging;
    }

    public S3Location withTagging(ObjectTagging tagging) {
        this.setTagging(tagging);
        return this;
    }

    public List<MetadataEntry> getUserMetadata() {
        return this.userMetadata;
    }

    public void setUserMetadata(List<MetadataEntry> userMetadata) {
        this.userMetadata = userMetadata;
    }

    public S3Location withUserMetaData(List<MetadataEntry> userMetadata) {
        this.setUserMetadata(userMetadata);
        return this;
    }

    public String getStorageClass() {
        return this.storageClass;
    }

    public void setStorageClass(String storageClass) {
        this.storageClass = storageClass;
    }

    public S3Location withStorageClass(String storageClass) {
        this.setStorageClass(storageClass);
        return this;
    }

    public S3Location withStorageClass(StorageClass storageClass) {
        this.setStorageClass(storageClass == null ? null : storageClass.toString());
        return this;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof S3Location)) {
            return false;
        }
        S3Location other = (S3Location)obj;
        if (other.getBucketName() == null ^ this.getBucketName() == null) {
            return false;
        }
        if (other.getBucketName() != null && !other.getBucketName().equals(this.getBucketName())) {
            return false;
        }
        if (other.getPrefix() == null ^ this.getPrefix() == null) {
            return false;
        }
        if (other.getPrefix() != null && !other.getPrefix().equals(this.getPrefix())) {
            return false;
        }
        if (other.getEncryption() == null ^ this.getEncryption() == null) {
            return false;
        }
        if (other.getEncryption() != null && !other.getEncryption().equals(this.getEncryption())) {
            return false;
        }
        if (other.getCannedACL() == null ^ this.getCannedACL() == null) {
            return false;
        }
        if (other.getCannedACL() != null && !other.getCannedACL().equals(this.getCannedACL())) {
            return false;
        }
        if (other.getAccessControlList() == null ^ this.getAccessControlList() == null) {
            return false;
        }
        if (other.getAccessControlList() != null && !other.getAccessControlList().equals(this.getAccessControlList())) {
            return false;
        }
        if (other.getTagging() == null ^ this.getTagging() == null) {
            return false;
        }
        if (other.getTagging() != null && !other.getTagging().equals(this.getTagging())) {
            return false;
        }
        if (other.getUserMetadata() == null ^ this.getUserMetadata() == null) {
            return false;
        }
        if (other.getUserMetadata() != null && !other.getUserMetadata().equals(this.getUserMetadata())) {
            return false;
        }
        if (other.getStorageClass() == null ^ this.getStorageClass() == null) {
            return false;
        }
        return other.getStorageClass() == null || other.getStorageClass().equals(this.getStorageClass());
    }

    public int hashCode() {
        int prime = 31;
        int hashCode = 1;
        hashCode = 31 * hashCode + (this.getBucketName() == null ? 0 : this.getBucketName().hashCode());
        hashCode = 31 * hashCode + (this.getPrefix() == null ? 0 : this.getPrefix().hashCode());
        hashCode = 31 * hashCode + (this.getEncryption() == null ? 0 : this.getEncryption().hashCode());
        hashCode = 31 * hashCode + (this.getCannedACL() == null ? 0 : this.getCannedACL().hashCode());
        hashCode = 31 * hashCode + (this.getAccessControlList() == null ? 0 : this.getAccessControlList().hashCode());
        hashCode = 31 * hashCode + (this.getTagging() != null ? this.getTagging().hashCode() : 0);
        hashCode = 31 * hashCode + (this.getUserMetadata() != null ? this.getUserMetadata().hashCode() : 0);
        hashCode = 31 * hashCode + (this.getStorageClass() != null ? this.getStorageClass().hashCode() : 0);
        return hashCode;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (this.getBucketName() != null) {
            sb.append("BucketName: ").append(this.getBucketName()).append(",");
        }
        if (this.getPrefix() != null) {
            sb.append("Prefix: ").append(this.getPrefix()).append(",");
        }
        if (this.getEncryption() != null) {
            sb.append("Encryption: ").append(this.getEncryption()).append(",");
        }
        if (this.getCannedACL() != null) {
            sb.append("CannedACL: ").append(this.getCannedACL()).append(",");
        }
        if (this.getAccessControlList() != null) {
            sb.append("AccessControlList: ").append(this.getAccessControlList()).append(",");
        }
        if (this.getTagging() != null) {
            sb.append("Tagging: ").append(this.getTagging()).append(",");
        }
        if (this.getUserMetadata() != null) {
            sb.append("UserMetadata: ").append(this.getUserMetadata()).append(",");
        }
        if (this.getStorageClass() != null) {
            sb.append("StorageClass: ").append(this.getStorageClass());
        }
        sb.append("}");
        return sb.toString();
    }

    public S3Location clone() {
        try {
            return (S3Location)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new IllegalStateException("Got a CloneNotSupportedException from Object.clone() even though we're Cloneable!", e);
        }
    }
}

