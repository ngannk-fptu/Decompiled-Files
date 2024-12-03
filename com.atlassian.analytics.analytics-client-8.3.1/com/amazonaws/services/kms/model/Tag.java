/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model;

import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.protocol.ProtocolMarshaller;
import com.amazonaws.protocol.StructuredPojo;
import com.amazonaws.services.kms.model.transform.TagMarshaller;
import java.io.Serializable;

public class Tag
implements Serializable,
Cloneable,
StructuredPojo {
    private String tagKey;
    private String tagValue;

    public void setTagKey(String tagKey) {
        this.tagKey = tagKey;
    }

    public String getTagKey() {
        return this.tagKey;
    }

    public Tag withTagKey(String tagKey) {
        this.setTagKey(tagKey);
        return this;
    }

    public void setTagValue(String tagValue) {
        this.tagValue = tagValue;
    }

    public String getTagValue() {
        return this.tagValue;
    }

    public Tag withTagValue(String tagValue) {
        this.setTagValue(tagValue);
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (this.getTagKey() != null) {
            sb.append("TagKey: ").append(this.getTagKey()).append(",");
        }
        if (this.getTagValue() != null) {
            sb.append("TagValue: ").append(this.getTagValue());
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
        if (!(obj instanceof Tag)) {
            return false;
        }
        Tag other = (Tag)obj;
        if (other.getTagKey() == null ^ this.getTagKey() == null) {
            return false;
        }
        if (other.getTagKey() != null && !other.getTagKey().equals(this.getTagKey())) {
            return false;
        }
        if (other.getTagValue() == null ^ this.getTagValue() == null) {
            return false;
        }
        return other.getTagValue() == null || other.getTagValue().equals(this.getTagValue());
    }

    public int hashCode() {
        int prime = 31;
        int hashCode = 1;
        hashCode = 31 * hashCode + (this.getTagKey() == null ? 0 : this.getTagKey().hashCode());
        hashCode = 31 * hashCode + (this.getTagValue() == null ? 0 : this.getTagValue().hashCode());
        return hashCode;
    }

    public Tag clone() {
        try {
            return (Tag)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new IllegalStateException("Got a CloneNotSupportedException from Object.clone() even though we're Cloneable!", e);
        }
    }

    @Override
    @SdkInternalApi
    public void marshall(ProtocolMarshaller protocolMarshaller) {
        TagMarshaller.getInstance().marshall(this, protocolMarshaller);
    }
}

