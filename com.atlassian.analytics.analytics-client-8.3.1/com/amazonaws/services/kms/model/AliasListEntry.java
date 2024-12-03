/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model;

import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.protocol.ProtocolMarshaller;
import com.amazonaws.protocol.StructuredPojo;
import com.amazonaws.services.kms.model.transform.AliasListEntryMarshaller;
import java.io.Serializable;
import java.util.Date;

public class AliasListEntry
implements Serializable,
Cloneable,
StructuredPojo {
    private String aliasName;
    private String aliasArn;
    private String targetKeyId;
    private Date creationDate;
    private Date lastUpdatedDate;

    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }

    public String getAliasName() {
        return this.aliasName;
    }

    public AliasListEntry withAliasName(String aliasName) {
        this.setAliasName(aliasName);
        return this;
    }

    public void setAliasArn(String aliasArn) {
        this.aliasArn = aliasArn;
    }

    public String getAliasArn() {
        return this.aliasArn;
    }

    public AliasListEntry withAliasArn(String aliasArn) {
        this.setAliasArn(aliasArn);
        return this;
    }

    public void setTargetKeyId(String targetKeyId) {
        this.targetKeyId = targetKeyId;
    }

    public String getTargetKeyId() {
        return this.targetKeyId;
    }

    public AliasListEntry withTargetKeyId(String targetKeyId) {
        this.setTargetKeyId(targetKeyId);
        return this;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getCreationDate() {
        return this.creationDate;
    }

    public AliasListEntry withCreationDate(Date creationDate) {
        this.setCreationDate(creationDate);
        return this;
    }

    public void setLastUpdatedDate(Date lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public Date getLastUpdatedDate() {
        return this.lastUpdatedDate;
    }

    public AliasListEntry withLastUpdatedDate(Date lastUpdatedDate) {
        this.setLastUpdatedDate(lastUpdatedDate);
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (this.getAliasName() != null) {
            sb.append("AliasName: ").append(this.getAliasName()).append(",");
        }
        if (this.getAliasArn() != null) {
            sb.append("AliasArn: ").append(this.getAliasArn()).append(",");
        }
        if (this.getTargetKeyId() != null) {
            sb.append("TargetKeyId: ").append(this.getTargetKeyId()).append(",");
        }
        if (this.getCreationDate() != null) {
            sb.append("CreationDate: ").append(this.getCreationDate()).append(",");
        }
        if (this.getLastUpdatedDate() != null) {
            sb.append("LastUpdatedDate: ").append(this.getLastUpdatedDate());
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
        if (!(obj instanceof AliasListEntry)) {
            return false;
        }
        AliasListEntry other = (AliasListEntry)obj;
        if (other.getAliasName() == null ^ this.getAliasName() == null) {
            return false;
        }
        if (other.getAliasName() != null && !other.getAliasName().equals(this.getAliasName())) {
            return false;
        }
        if (other.getAliasArn() == null ^ this.getAliasArn() == null) {
            return false;
        }
        if (other.getAliasArn() != null && !other.getAliasArn().equals(this.getAliasArn())) {
            return false;
        }
        if (other.getTargetKeyId() == null ^ this.getTargetKeyId() == null) {
            return false;
        }
        if (other.getTargetKeyId() != null && !other.getTargetKeyId().equals(this.getTargetKeyId())) {
            return false;
        }
        if (other.getCreationDate() == null ^ this.getCreationDate() == null) {
            return false;
        }
        if (other.getCreationDate() != null && !other.getCreationDate().equals(this.getCreationDate())) {
            return false;
        }
        if (other.getLastUpdatedDate() == null ^ this.getLastUpdatedDate() == null) {
            return false;
        }
        return other.getLastUpdatedDate() == null || other.getLastUpdatedDate().equals(this.getLastUpdatedDate());
    }

    public int hashCode() {
        int prime = 31;
        int hashCode = 1;
        hashCode = 31 * hashCode + (this.getAliasName() == null ? 0 : this.getAliasName().hashCode());
        hashCode = 31 * hashCode + (this.getAliasArn() == null ? 0 : this.getAliasArn().hashCode());
        hashCode = 31 * hashCode + (this.getTargetKeyId() == null ? 0 : this.getTargetKeyId().hashCode());
        hashCode = 31 * hashCode + (this.getCreationDate() == null ? 0 : this.getCreationDate().hashCode());
        hashCode = 31 * hashCode + (this.getLastUpdatedDate() == null ? 0 : this.getLastUpdatedDate().hashCode());
        return hashCode;
    }

    public AliasListEntry clone() {
        try {
            return (AliasListEntry)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new IllegalStateException("Got a CloneNotSupportedException from Object.clone() even though we're Cloneable!", e);
        }
    }

    @Override
    @SdkInternalApi
    public void marshall(ProtocolMarshaller protocolMarshaller) {
        AliasListEntryMarshaller.getInstance().marshall(this, protocolMarshaller);
    }
}

