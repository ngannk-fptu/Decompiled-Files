/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model;

import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.internal.SdkInternalList;
import com.amazonaws.protocol.ProtocolMarshaller;
import com.amazonaws.protocol.StructuredPojo;
import com.amazonaws.services.kms.model.GrantConstraints;
import com.amazonaws.services.kms.model.GrantOperation;
import com.amazonaws.services.kms.model.transform.GrantListEntryMarshaller;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class GrantListEntry
implements Serializable,
Cloneable,
StructuredPojo {
    private String keyId;
    private String grantId;
    private String name;
    private Date creationDate;
    private String granteePrincipal;
    private String retiringPrincipal;
    private String issuingAccount;
    private SdkInternalList<String> operations;
    private GrantConstraints constraints;

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getKeyId() {
        return this.keyId;
    }

    public GrantListEntry withKeyId(String keyId) {
        this.setKeyId(keyId);
        return this;
    }

    public void setGrantId(String grantId) {
        this.grantId = grantId;
    }

    public String getGrantId() {
        return this.grantId;
    }

    public GrantListEntry withGrantId(String grantId) {
        this.setGrantId(grantId);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public GrantListEntry withName(String name) {
        this.setName(name);
        return this;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getCreationDate() {
        return this.creationDate;
    }

    public GrantListEntry withCreationDate(Date creationDate) {
        this.setCreationDate(creationDate);
        return this;
    }

    public void setGranteePrincipal(String granteePrincipal) {
        this.granteePrincipal = granteePrincipal;
    }

    public String getGranteePrincipal() {
        return this.granteePrincipal;
    }

    public GrantListEntry withGranteePrincipal(String granteePrincipal) {
        this.setGranteePrincipal(granteePrincipal);
        return this;
    }

    public void setRetiringPrincipal(String retiringPrincipal) {
        this.retiringPrincipal = retiringPrincipal;
    }

    public String getRetiringPrincipal() {
        return this.retiringPrincipal;
    }

    public GrantListEntry withRetiringPrincipal(String retiringPrincipal) {
        this.setRetiringPrincipal(retiringPrincipal);
        return this;
    }

    public void setIssuingAccount(String issuingAccount) {
        this.issuingAccount = issuingAccount;
    }

    public String getIssuingAccount() {
        return this.issuingAccount;
    }

    public GrantListEntry withIssuingAccount(String issuingAccount) {
        this.setIssuingAccount(issuingAccount);
        return this;
    }

    public List<String> getOperations() {
        if (this.operations == null) {
            this.operations = new SdkInternalList();
        }
        return this.operations;
    }

    public void setOperations(Collection<String> operations) {
        if (operations == null) {
            this.operations = null;
            return;
        }
        this.operations = new SdkInternalList<String>(operations);
    }

    public GrantListEntry withOperations(String ... operations) {
        if (this.operations == null) {
            this.setOperations(new SdkInternalList<String>(operations.length));
        }
        for (String ele : operations) {
            this.operations.add(ele);
        }
        return this;
    }

    public GrantListEntry withOperations(Collection<String> operations) {
        this.setOperations(operations);
        return this;
    }

    public GrantListEntry withOperations(GrantOperation ... operations) {
        SdkInternalList<String> operationsCopy = new SdkInternalList<String>(operations.length);
        for (GrantOperation value : operations) {
            operationsCopy.add(value.toString());
        }
        if (this.getOperations() == null) {
            this.setOperations(operationsCopy);
        } else {
            this.getOperations().addAll(operationsCopy);
        }
        return this;
    }

    public void setConstraints(GrantConstraints constraints) {
        this.constraints = constraints;
    }

    public GrantConstraints getConstraints() {
        return this.constraints;
    }

    public GrantListEntry withConstraints(GrantConstraints constraints) {
        this.setConstraints(constraints);
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (this.getKeyId() != null) {
            sb.append("KeyId: ").append(this.getKeyId()).append(",");
        }
        if (this.getGrantId() != null) {
            sb.append("GrantId: ").append(this.getGrantId()).append(",");
        }
        if (this.getName() != null) {
            sb.append("Name: ").append(this.getName()).append(",");
        }
        if (this.getCreationDate() != null) {
            sb.append("CreationDate: ").append(this.getCreationDate()).append(",");
        }
        if (this.getGranteePrincipal() != null) {
            sb.append("GranteePrincipal: ").append(this.getGranteePrincipal()).append(",");
        }
        if (this.getRetiringPrincipal() != null) {
            sb.append("RetiringPrincipal: ").append(this.getRetiringPrincipal()).append(",");
        }
        if (this.getIssuingAccount() != null) {
            sb.append("IssuingAccount: ").append(this.getIssuingAccount()).append(",");
        }
        if (this.getOperations() != null) {
            sb.append("Operations: ").append(this.getOperations()).append(",");
        }
        if (this.getConstraints() != null) {
            sb.append("Constraints: ").append(this.getConstraints());
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
        if (!(obj instanceof GrantListEntry)) {
            return false;
        }
        GrantListEntry other = (GrantListEntry)obj;
        if (other.getKeyId() == null ^ this.getKeyId() == null) {
            return false;
        }
        if (other.getKeyId() != null && !other.getKeyId().equals(this.getKeyId())) {
            return false;
        }
        if (other.getGrantId() == null ^ this.getGrantId() == null) {
            return false;
        }
        if (other.getGrantId() != null && !other.getGrantId().equals(this.getGrantId())) {
            return false;
        }
        if (other.getName() == null ^ this.getName() == null) {
            return false;
        }
        if (other.getName() != null && !other.getName().equals(this.getName())) {
            return false;
        }
        if (other.getCreationDate() == null ^ this.getCreationDate() == null) {
            return false;
        }
        if (other.getCreationDate() != null && !other.getCreationDate().equals(this.getCreationDate())) {
            return false;
        }
        if (other.getGranteePrincipal() == null ^ this.getGranteePrincipal() == null) {
            return false;
        }
        if (other.getGranteePrincipal() != null && !other.getGranteePrincipal().equals(this.getGranteePrincipal())) {
            return false;
        }
        if (other.getRetiringPrincipal() == null ^ this.getRetiringPrincipal() == null) {
            return false;
        }
        if (other.getRetiringPrincipal() != null && !other.getRetiringPrincipal().equals(this.getRetiringPrincipal())) {
            return false;
        }
        if (other.getIssuingAccount() == null ^ this.getIssuingAccount() == null) {
            return false;
        }
        if (other.getIssuingAccount() != null && !other.getIssuingAccount().equals(this.getIssuingAccount())) {
            return false;
        }
        if (other.getOperations() == null ^ this.getOperations() == null) {
            return false;
        }
        if (other.getOperations() != null && !other.getOperations().equals(this.getOperations())) {
            return false;
        }
        if (other.getConstraints() == null ^ this.getConstraints() == null) {
            return false;
        }
        return other.getConstraints() == null || other.getConstraints().equals(this.getConstraints());
    }

    public int hashCode() {
        int prime = 31;
        int hashCode = 1;
        hashCode = 31 * hashCode + (this.getKeyId() == null ? 0 : this.getKeyId().hashCode());
        hashCode = 31 * hashCode + (this.getGrantId() == null ? 0 : this.getGrantId().hashCode());
        hashCode = 31 * hashCode + (this.getName() == null ? 0 : this.getName().hashCode());
        hashCode = 31 * hashCode + (this.getCreationDate() == null ? 0 : this.getCreationDate().hashCode());
        hashCode = 31 * hashCode + (this.getGranteePrincipal() == null ? 0 : this.getGranteePrincipal().hashCode());
        hashCode = 31 * hashCode + (this.getRetiringPrincipal() == null ? 0 : this.getRetiringPrincipal().hashCode());
        hashCode = 31 * hashCode + (this.getIssuingAccount() == null ? 0 : this.getIssuingAccount().hashCode());
        hashCode = 31 * hashCode + (this.getOperations() == null ? 0 : this.getOperations().hashCode());
        hashCode = 31 * hashCode + (this.getConstraints() == null ? 0 : this.getConstraints().hashCode());
        return hashCode;
    }

    public GrantListEntry clone() {
        try {
            return (GrantListEntry)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new IllegalStateException("Got a CloneNotSupportedException from Object.clone() even though we're Cloneable!", e);
        }
    }

    @Override
    @SdkInternalApi
    public void marshall(ProtocolMarshaller protocolMarshaller) {
        GrantListEntryMarshaller.getInstance().marshall(this, protocolMarshaller);
    }
}

