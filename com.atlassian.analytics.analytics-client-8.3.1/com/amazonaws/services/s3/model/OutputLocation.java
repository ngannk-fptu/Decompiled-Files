/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model;

import com.amazonaws.services.s3.model.S3Location;
import java.io.Serializable;

public class OutputLocation
implements Serializable,
Cloneable {
    private S3Location s3;

    public S3Location getS3() {
        return this.s3;
    }

    public void setS3(S3Location s3) {
        this.s3 = s3;
    }

    public OutputLocation withS3(S3Location s3) {
        this.setS3(s3);
        return this;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof OutputLocation)) {
            return false;
        }
        OutputLocation other = (OutputLocation)obj;
        if (other.getS3() == null ^ this.getS3() == null) {
            return false;
        }
        return other.getS3() == null || other.getS3().equals(this.getS3());
    }

    public int hashCode() {
        int prime = 31;
        int hashCode = 1;
        hashCode = 31 * hashCode + (this.getS3() == null ? 0 : this.getS3().hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (this.getS3() != null) {
            sb.append("S3: ").append(this.getS3());
        }
        sb.append("}");
        return sb.toString();
    }

    public OutputLocation clone() {
        try {
            return (OutputLocation)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new IllegalStateException("Got a CloneNotSupportedException from Object.clone() even though we're Cloneable!", e);
        }
    }
}

