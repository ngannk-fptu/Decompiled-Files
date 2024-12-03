/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model;

import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.internal.SdkInternalList;
import com.amazonaws.services.kms.model.GrantConstraints;
import com.amazonaws.services.kms.model.GrantOperation;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public class CreateGrantRequest
extends AmazonWebServiceRequest
implements Serializable,
Cloneable {
    private String keyId;
    private String granteePrincipal;
    private String retiringPrincipal;
    private SdkInternalList<String> operations;
    private GrantConstraints constraints;
    private SdkInternalList<String> grantTokens;
    private String name;

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getKeyId() {
        return this.keyId;
    }

    public CreateGrantRequest withKeyId(String keyId) {
        this.setKeyId(keyId);
        return this;
    }

    public void setGranteePrincipal(String granteePrincipal) {
        this.granteePrincipal = granteePrincipal;
    }

    public String getGranteePrincipal() {
        return this.granteePrincipal;
    }

    public CreateGrantRequest withGranteePrincipal(String granteePrincipal) {
        this.setGranteePrincipal(granteePrincipal);
        return this;
    }

    public void setRetiringPrincipal(String retiringPrincipal) {
        this.retiringPrincipal = retiringPrincipal;
    }

    public String getRetiringPrincipal() {
        return this.retiringPrincipal;
    }

    public CreateGrantRequest withRetiringPrincipal(String retiringPrincipal) {
        this.setRetiringPrincipal(retiringPrincipal);
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

    public CreateGrantRequest withOperations(String ... operations) {
        if (this.operations == null) {
            this.setOperations(new SdkInternalList<String>(operations.length));
        }
        for (String ele : operations) {
            this.operations.add(ele);
        }
        return this;
    }

    public CreateGrantRequest withOperations(Collection<String> operations) {
        this.setOperations(operations);
        return this;
    }

    public CreateGrantRequest withOperations(GrantOperation ... operations) {
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

    public CreateGrantRequest withConstraints(GrantConstraints constraints) {
        this.setConstraints(constraints);
        return this;
    }

    public List<String> getGrantTokens() {
        if (this.grantTokens == null) {
            this.grantTokens = new SdkInternalList();
        }
        return this.grantTokens;
    }

    public void setGrantTokens(Collection<String> grantTokens) {
        if (grantTokens == null) {
            this.grantTokens = null;
            return;
        }
        this.grantTokens = new SdkInternalList<String>(grantTokens);
    }

    public CreateGrantRequest withGrantTokens(String ... grantTokens) {
        if (this.grantTokens == null) {
            this.setGrantTokens(new SdkInternalList<String>(grantTokens.length));
        }
        for (String ele : grantTokens) {
            this.grantTokens.add(ele);
        }
        return this;
    }

    public CreateGrantRequest withGrantTokens(Collection<String> grantTokens) {
        this.setGrantTokens(grantTokens);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public CreateGrantRequest withName(String name) {
        this.setName(name);
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (this.getKeyId() != null) {
            sb.append("KeyId: ").append(this.getKeyId()).append(",");
        }
        if (this.getGranteePrincipal() != null) {
            sb.append("GranteePrincipal: ").append(this.getGranteePrincipal()).append(",");
        }
        if (this.getRetiringPrincipal() != null) {
            sb.append("RetiringPrincipal: ").append(this.getRetiringPrincipal()).append(",");
        }
        if (this.getOperations() != null) {
            sb.append("Operations: ").append(this.getOperations()).append(",");
        }
        if (this.getConstraints() != null) {
            sb.append("Constraints: ").append(this.getConstraints()).append(",");
        }
        if (this.getGrantTokens() != null) {
            sb.append("GrantTokens: ").append(this.getGrantTokens()).append(",");
        }
        if (this.getName() != null) {
            sb.append("Name: ").append(this.getName());
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
        if (!(obj instanceof CreateGrantRequest)) {
            return false;
        }
        CreateGrantRequest other = (CreateGrantRequest)obj;
        if (other.getKeyId() == null ^ this.getKeyId() == null) {
            return false;
        }
        if (other.getKeyId() != null && !other.getKeyId().equals(this.getKeyId())) {
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
        if (other.getOperations() == null ^ this.getOperations() == null) {
            return false;
        }
        if (other.getOperations() != null && !other.getOperations().equals(this.getOperations())) {
            return false;
        }
        if (other.getConstraints() == null ^ this.getConstraints() == null) {
            return false;
        }
        if (other.getConstraints() != null && !other.getConstraints().equals(this.getConstraints())) {
            return false;
        }
        if (other.getGrantTokens() == null ^ this.getGrantTokens() == null) {
            return false;
        }
        if (other.getGrantTokens() != null && !other.getGrantTokens().equals(this.getGrantTokens())) {
            return false;
        }
        if (other.getName() == null ^ this.getName() == null) {
            return false;
        }
        return other.getName() == null || other.getName().equals(this.getName());
    }

    public int hashCode() {
        int prime = 31;
        int hashCode = 1;
        hashCode = 31 * hashCode + (this.getKeyId() == null ? 0 : this.getKeyId().hashCode());
        hashCode = 31 * hashCode + (this.getGranteePrincipal() == null ? 0 : this.getGranteePrincipal().hashCode());
        hashCode = 31 * hashCode + (this.getRetiringPrincipal() == null ? 0 : this.getRetiringPrincipal().hashCode());
        hashCode = 31 * hashCode + (this.getOperations() == null ? 0 : this.getOperations().hashCode());
        hashCode = 31 * hashCode + (this.getConstraints() == null ? 0 : this.getConstraints().hashCode());
        hashCode = 31 * hashCode + (this.getGrantTokens() == null ? 0 : this.getGrantTokens().hashCode());
        hashCode = 31 * hashCode + (this.getName() == null ? 0 : this.getName().hashCode());
        return hashCode;
    }

    @Override
    public CreateGrantRequest clone() {
        return (CreateGrantRequest)super.clone();
    }
}

