/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model;

import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.protocol.ProtocolMarshaller;
import com.amazonaws.protocol.StructuredPojo;
import com.amazonaws.services.kms.model.transform.MultiRegionKeyMarshaller;
import java.io.Serializable;

public class MultiRegionKey
implements Serializable,
Cloneable,
StructuredPojo {
    private String arn;
    private String region;

    public void setArn(String arn) {
        this.arn = arn;
    }

    public String getArn() {
        return this.arn;
    }

    public MultiRegionKey withArn(String arn) {
        this.setArn(arn);
        return this;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getRegion() {
        return this.region;
    }

    public MultiRegionKey withRegion(String region) {
        this.setRegion(region);
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (this.getArn() != null) {
            sb.append("Arn: ").append(this.getArn()).append(",");
        }
        if (this.getRegion() != null) {
            sb.append("Region: ").append(this.getRegion());
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
        if (!(obj instanceof MultiRegionKey)) {
            return false;
        }
        MultiRegionKey other = (MultiRegionKey)obj;
        if (other.getArn() == null ^ this.getArn() == null) {
            return false;
        }
        if (other.getArn() != null && !other.getArn().equals(this.getArn())) {
            return false;
        }
        if (other.getRegion() == null ^ this.getRegion() == null) {
            return false;
        }
        return other.getRegion() == null || other.getRegion().equals(this.getRegion());
    }

    public int hashCode() {
        int prime = 31;
        int hashCode = 1;
        hashCode = 31 * hashCode + (this.getArn() == null ? 0 : this.getArn().hashCode());
        hashCode = 31 * hashCode + (this.getRegion() == null ? 0 : this.getRegion().hashCode());
        return hashCode;
    }

    public MultiRegionKey clone() {
        try {
            return (MultiRegionKey)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new IllegalStateException("Got a CloneNotSupportedException from Object.clone() even though we're Cloneable!", e);
        }
    }

    @Override
    @SdkInternalApi
    public void marshall(ProtocolMarshaller protocolMarshaller) {
        MultiRegionKeyMarshaller.getInstance().marshall(this, protocolMarshaller);
    }
}

