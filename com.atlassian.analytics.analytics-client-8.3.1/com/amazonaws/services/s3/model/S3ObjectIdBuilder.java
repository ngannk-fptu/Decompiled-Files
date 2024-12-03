/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model;

import com.amazonaws.services.s3.model.S3ObjectId;
import java.io.Serializable;

public final class S3ObjectIdBuilder
implements Serializable {
    private String bucket;
    private String key;
    private String versionId;

    public S3ObjectIdBuilder() {
    }

    public S3ObjectIdBuilder(S3ObjectId id) {
        this.bucket = id.getBucket();
        this.key = id.getKey();
        this.versionId = id.getVersionId();
    }

    public String getBucket() {
        return this.bucket;
    }

    public String getKey() {
        return this.key;
    }

    public String getVersionId() {
        return this.versionId;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    public S3ObjectIdBuilder withBucket(String bucket) {
        this.bucket = bucket;
        return this;
    }

    public S3ObjectIdBuilder withKey(String key) {
        this.key = key;
        return this;
    }

    public S3ObjectIdBuilder withVersionId(String versionId) {
        this.versionId = versionId;
        return this;
    }

    public S3ObjectId build() {
        return new S3ObjectId(this.bucket, this.key, this.versionId);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        S3ObjectIdBuilder that = (S3ObjectIdBuilder)o;
        if (this.bucket != null ? !this.bucket.equals(that.bucket) : that.bucket != null) {
            return false;
        }
        if (this.key != null ? !this.key.equals(that.key) : that.key != null) {
            return false;
        }
        return this.versionId != null ? this.versionId.equals(that.versionId) : that.versionId == null;
    }

    public int hashCode() {
        int result = this.bucket != null ? this.bucket.hashCode() : 0;
        result = 31 * result + (this.key != null ? this.key.hashCode() : 0);
        result = 31 * result + (this.versionId != null ? this.versionId.hashCode() : 0);
        return result;
    }
}

